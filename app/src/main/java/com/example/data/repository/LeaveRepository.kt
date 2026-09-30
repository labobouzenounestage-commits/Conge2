package com.example.data.repository

import android.content.Context
import com.example.data.email.EmailService
import com.example.data.local.EmailLogDao
import com.example.data.local.LeaveRequestDao
import com.example.data.local.NotificationDao
import com.example.data.local.UserDao
import com.example.data.model.EmailLogEntity
import com.example.data.model.LeaveRequestEntity
import com.example.data.model.LeaveStatus
import com.example.data.model.LeaveType
import com.example.data.model.NotificationEntity
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LeaveRepository(
    private val userDao: UserDao,
    private val leaveRequestDao: LeaveRequestDao,
    private val notificationDao: NotificationDao,
    private val emailLogDao: EmailLogDao,
    private val emailService: EmailService
) {

    val allUsers: Flow<List<UserEntity>> = userDao.getAllUsers()
    val allRequests: Flow<List<LeaveRequestEntity>> = leaveRequestDao.getAllRequests()
    val allEmailLogs: Flow<List<EmailLogEntity>> = emailLogDao.getAllEmailLogs()

    suspend fun login(identifier: String, pass: String): UserEntity? {
        val user = userDao.findByIdentifier(identifier.trim()) ?: return null
        return if (user.password == pass) user else null
    }

    suspend fun getUserById(id: Long): UserEntity? = userDao.getUserById(id)

    fun getRequestsByUser(userId: Long): Flow<List<LeaveRequestEntity>> =
        leaveRequestDao.getRequestsByUser(userId)

    fun getPendingSubstituteRequests(substituteId: Long): Flow<List<LeaveRequestEntity>> =
        leaveRequestDao.getPendingSubstituteRequests(substituteId)

    fun getPendingManagerRequests(): Flow<List<LeaveRequestEntity>> =
        leaveRequestDao.getPendingManagerRequests()

    fun getNotificationsForUser(userId: Long, role: UserRole): Flow<List<NotificationEntity>> =
        notificationDao.getNotificationsForUser(userId, role.name)

    suspend fun markAllNotificationsRead(userId: Long, role: UserRole) {
        notificationDao.markAllAsReadForUser(userId, role.name)
    }

    suspend fun resetPasswordByEmail(email: String, newPass: String): Boolean {
        val user = userDao.findByIdentifier(email.trim()) ?: return false
        userDao.updatePassword(user.id, newPass)
        return true
    }

    suspend fun submitLeaveRequest(
        requester: UserEntity,
        type: LeaveType,
        startDate: String,
        endDate: String,
        days: Int,
        reason: String,
        substituteId: Long
    ): Result<Long> {
        val currentBalance = when (type) {
            LeaveType.ANNUAL -> requester.balanceAnnual
            LeaveType.SICK -> requester.balanceSick
            LeaveType.RTT -> requester.balanceRtt
        }

        if (days <= 0) {
            return Result.failure(IllegalArgumentException("عدد الأيام غير صالح / Nombre de jours invalide"))
        }

        if (days > currentBalance) {
            return Result.failure(IllegalArgumentException("الرصيد المتاح غير كافٍ ($currentBalance يوم) / Solde insuffisant ($currentBalance jours)"))
        }

        val substitute = userDao.getUserById(substituteId)
            ?: return Result.failure(IllegalArgumentException("الموظف البديل غير موجود / Remplaçant introuvable"))

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val formattedDate = sdf.format(Date())

        val request = LeaveRequestEntity(
            userId = requester.id,
            userName = requester.fullName,
            userEmail = requester.email,
            type = type,
            startDate = startDate,
            endDate = endDate,
            days = days,
            reason = reason,
            substituteId = substitute.id,
            substituteName = substitute.fullName,
            status = LeaveStatus.PENDING_SUBSTITUTE,
            createdAt = formattedDate
        )

        val id = leaveRequestDao.insertRequest(request)

        // Notify Substitute in-app
        notificationDao.insertNotification(
            NotificationEntity(
                targetUserId = substitute.id,
                messageAr = "طلب تعويض جديد من ${requester.fullName} للفترة من $startDate إلى $endDate ($days يوم). يرجى الرد بالقبول أو الرفض.",
                messageFr = "Demande de remplacement de ${requester.fullName} du $startDate au $endDate ($days j).",
                read = false
            )
        )

        // Email Managers about the newly submitted leave request
        val managers = userDao.getUsersByRole(UserRole.MANAGER) + userDao.getUsersByRole(UserRole.ADMIN)
        val managerEmails = managers.map { it.email }.filter { it.isNotBlank() }
        emailService.sendNewRequestManagerNotification(
            managerEmails = managerEmails,
            requesterName = requester.fullName,
            leaveType = type,
            days = days,
            startDate = startDate,
            endDate = endDate,
            substituteName = substitute.fullName,
            reason = reason
        )

        return Result.success(id)
    }

    suspend fun processSubstituteDecision(
        requestId: Long,
        accepted: Boolean,
        substituteUser: UserEntity
    ) {
        val request = leaveRequestDao.getRequestById(requestId) ?: return
        if (request.status != LeaveStatus.PENDING_SUBSTITUTE || request.substituteId != substituteUser.id) {
            return
        }

        val newStatus = if (accepted) LeaveStatus.PENDING_MANAGER else LeaveStatus.REJECTED_SUBSTITUTE
        leaveRequestDao.updateStatus(requestId, newStatus)

        // Notify requester about substitute decision
        val msgAr = if (accepted) {
            "وافق البديل ${substituteUser.fullName} على تعويضك. الطلب الآن بانتظار موافقة الإدارة."
        } else {
            "اعتذر البديل ${substituteUser.fullName} عن التعويض. تم إلغاء الطلب."
        }
        val msgFr = if (accepted) {
            "${substituteUser.fullName} a accepté le remplacement. En attente de validation finale."
        } else {
            "${substituteUser.fullName} a décliné le remplacement."
        }

        notificationDao.insertNotification(
            NotificationEntity(
                targetUserId = request.userId,
                messageAr = msgAr,
                messageFr = msgFr,
                read = false
            )
        )

        // If accepted, notify managers
        if (accepted) {
            notificationDao.insertNotification(
                NotificationEntity(
                    targetRole = "MANAGER",
                    messageAr = "طلب عطلة جديد جاهز للموافقة الإدارية: ${request.userName} ($msgAr)",
                    messageFr = "Demande prête pour approbation: ${request.userName}",
                    read = false
                )
            )
        }
    }

    suspend fun processManagerDecision(
        requestId: Long,
        approved: Boolean,
        managerUser: UserEntity
    ) {
        val request = leaveRequestDao.getRequestById(requestId) ?: return
        val newStatus = if (approved) LeaveStatus.APPROVED else LeaveStatus.REJECTED
        leaveRequestDao.updateStatus(requestId, newStatus)

        val requester = userDao.getUserById(request.userId)

        var remainingAnnual = requester?.balanceAnnual ?: 0
        if (approved && requester != null) {
            val updatedUser = when (request.type) {
                LeaveType.ANNUAL -> {
                    val newBal = maxOf(0, requester.balanceAnnual - request.days)
                    remainingAnnual = newBal
                    requester.copy(balanceAnnual = newBal)
                }
                LeaveType.SICK -> requester.copy(balanceSick = maxOf(0, requester.balanceSick - request.days))
                LeaveType.RTT -> requester.copy(balanceRtt = maxOf(0, requester.balanceRtt - request.days))
            }
            userDao.updateUser(updatedUser)
        }

        // Notify requester in-app
        val actionAr = if (approved) "قبول" else "رفض"
        val actionFr = if (approved) "approuvée" else "refusée"
        notificationDao.insertNotification(
            NotificationEntity(
                targetUserId = request.userId,
                messageAr = "تم $actionAr طلب العطلة الخاص بك (${request.type.code}) من قبل ${managerUser.fullName}.",
                messageFr = "Votre demande de congé (${request.type.name}) a été $actionFr.",
                read = false
            )
        )

        // Send Email to Employee on approval / rejection
        if (requester != null && requester.email.isNotBlank()) {
            emailService.sendLeaveStatusEmail(
                toEmail = requester.email,
                recipientName = requester.fullName,
                isApproved = approved,
                leaveType = request.type,
                days = request.days,
                startDate = request.startDate,
                endDate = request.endDate,
                remainingBalance = remainingAnnual
            )
        }
    }

    suspend fun sendLowBalanceReminders(thresholdDays: Int = 5): Int {
        val allUsersList = userDao.getUsersByRole(UserRole.EMPLOYEE)
        var count = 0
        for (user in allUsersList) {
            if (user.balanceAnnual <= thresholdDays && user.email.isNotBlank()) {
                emailService.sendLowBalanceReminderEmail(
                    toEmail = user.email,
                    recipientName = user.fullName,
                    annualBalance = user.balanceAnnual
                )
                notificationDao.insertNotification(
                    NotificationEntity(
                        targetUserId = user.id,
                        messageAr = "تنبيه نهاية السنة: رصيدك السنوي المتبقي هو ${user.balanceAnnual} يوم فقط. يرجى التخطيط لعطلتك.",
                        messageFr = "Rappel de fin d'année: votre solde annuel est de ${user.balanceAnnual} jours.",
                        read = false
                    )
                )
                count++
            }
        }
        return count
    }

    suspend fun insertUser(user: UserEntity): Long = userDao.insertUser(user)

    suspend fun updateUser(user: UserEntity) = userDao.updateUser(user)

    suspend fun updateBalances(userId: Long, annual: Int, sick: Int, rtt: Int) {
        userDao.updateBalances(userId, annual, sick, rtt)
    }

    suspend fun deleteUser(userId: Long) {
        userDao.deleteUserById(userId)
    }

    suspend fun deleteAllRequests() {
        leaveRequestDao.deleteAllRequests()
    }

    fun openEmailClient(context: Context, log: EmailLogEntity) {
        emailService.openEmailClient(context, log.toEmail, log.subject, log.body)
    }
}
