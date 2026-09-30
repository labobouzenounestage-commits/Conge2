package com.example.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.EmailLogEntity
import com.example.data.model.LeaveRequestEntity
import com.example.data.model.LeaveType
import com.example.data.model.NotificationEntity
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.data.repository.LeaveRepository
import com.example.ui.i18n.AppLanguage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MainTab {
    DASHBOARD,
    NEW_REQUEST,
    MY_REQUESTS,
    CALENDAR,
    DEMANDES,
    EMPLOYEES,
    NOTIFICATIONS
}

@OptIn(ExperimentalCoroutinesApi::class)
class LeaveViewModel(
    private val repository: LeaveRepository
) : ViewModel() {

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _language = MutableStateFlow(AppLanguage.ARABIC)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _currentTab = MutableStateFlow(MainTab.NEW_REQUEST)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    val allUsers: StateFlow<List<UserEntity>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRequests: StateFlow<List<LeaveRequestEntity>> = repository.allRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allEmailLogs: StateFlow<List<EmailLogEntity>> = repository.allEmailLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Reactively refresh current user from DB whenever user list updates
    init {
        viewModelScope.launch {
            repository.allUsers.collect { users ->
                val current = _currentUser.value ?: return@collect
                val updated = users.firstOrNull { it.id == current.id }
                if (updated != null && updated != current) {
                    _currentUser.value = updated
                }
            }
        }
    }

    // Requests for current user
    val myRequests: StateFlow<List<LeaveRequestEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getRequestsByUser(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Pending substitute requests where current user is chosen as substitute
    val pendingSubstituteRequests: StateFlow<List<LeaveRequestEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getPendingSubstituteRequests(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Pending manager requests (only relevant for Manager and Admin)
    val pendingManagerRequests: StateFlow<List<LeaveRequestEntity>> = repository.getPendingManagerRequests()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Notifications for current user
    val notifications: StateFlow<List<NotificationEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getNotificationsForUser(user.id, user.role) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotifCount: StateFlow<Int> = notifications.combine(_currentUser) { list, _ ->
        list.count { !it.read }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val pendingManagerCount: StateFlow<Int> = pendingManagerRequests.combine(_currentUser) { list, user ->
        if (user?.role == UserRole.ADMIN || user?.role == UserRole.MANAGER) list.size else 0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun setLanguage(lang: AppLanguage) {
        _language.value = lang
    }

    fun selectTab(tab: MainTab) {
        _currentTab.value = tab
    }

    fun login(identifier: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val user = repository.login(identifier, pass)
            if (user != null) {
                _currentUser.value = user
                _currentTab.value = MainTab.DASHBOARD
                onResult(true, null)
            } else {
                val err = if (_language.value == AppLanguage.ARABIC)
                    "اسم المستخدم أو كلمة المرور غير صحيحة"
                else
                    "Identifiants de connexion incorrects"
                onResult(false, err)
            }
        }
    }

    fun quickLogin(username: String, pass: String) {
        login(username, pass) { _, _ -> }
    }

    fun logout() {
        _currentUser.value = null
        _currentTab.value = MainTab.NEW_REQUEST
    }

    fun submitLeaveRequest(
        type: LeaveType,
        startDate: String,
        endDate: String,
        days: Int,
        reason: String,
        substituteId: Long,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val result = repository.submitLeaveRequest(
                requester = user,
                type = type,
                startDate = startDate,
                endDate = endDate,
                days = days,
                reason = reason,
                substituteId = substituteId
            )
            result.fold(
                onSuccess = {
                    val msg = if (_language.value == AppLanguage.ARABIC)
                        "تم إرسال طلب العطلة بنجاح وإشعار الموظف البديل والمدراء عبر البريد!"
                    else
                        "Demande soumise avec succès, email envoyé aux managers !"
                    _snackbarMessage.value = msg
                    _currentTab.value = MainTab.MY_REQUESTS
                    onSuccess()
                },
                onFailure = { err ->
                    onError(err.message ?: "Error")
                }
            )
        }
    }

    fun processSubstituteDecision(requestId: Long, accepted: Boolean) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.processSubstituteDecision(requestId, accepted, user)
            val msg = if (accepted) {
                if (_language.value == AppLanguage.ARABIC) "تم قبول التعويض وإرسال الطلب للإدارة" else "Remplacement accepté, transmis aux managers"
            } else {
                if (_language.value == AppLanguage.ARABIC) "تم رفض التعويض" else "Remplacement refusé"
            }
            _snackbarMessage.value = msg
        }
    }

    fun processManagerDecision(requestId: Long, approved: Boolean) {
        val user = _currentUser.value ?: return
        if (user.role != UserRole.ADMIN && user.role != UserRole.MANAGER) return

        viewModelScope.launch {
            repository.processManagerDecision(requestId, approved, user)
            val msg = if (approved) {
                if (_language.value == AppLanguage.ARABIC) "تمت الموافقة على الطلب وخصم الأيام وإرسال بريد للموظف!" else "Demande approuvée, solde déduit et email envoyé !"
            } else {
                if (_language.value == AppLanguage.ARABIC) "تم رفض الطلب وإرسال بريد للموظف!" else "Demande refusée et email envoyé !"
            }
            _snackbarMessage.value = msg
        }
    }

    fun deleteAllRequests() {
        val user = _currentUser.value ?: return
        if (user.role != UserRole.ADMIN) return

        viewModelScope.launch {
            repository.deleteAllRequests()
            _snackbarMessage.value = if (_language.value == AppLanguage.ARABIC)
                "تم حذف كافة الطلبات من السجل"
            else
                "Toutes les demandes ont été supprimées"
        }
    }

    fun updateCurrentUserBalance(type: LeaveType, newDays: Int) {
        val user = _currentUser.value ?: return
        if (user.role != UserRole.ADMIN) return

        val annual = if (type == LeaveType.ANNUAL) newDays else user.balanceAnnual
        val sick = if (type == LeaveType.SICK) newDays else user.balanceSick
        val rtt = if (type == LeaveType.RTT) newDays else user.balanceRtt

        viewModelScope.launch {
            repository.updateBalances(user.id, annual, sick, rtt)
            _snackbarMessage.value = if (_language.value == AppLanguage.ARABIC)
                "تم تحديث الرصيد بنجاح"
            else
                "Solde mis à jour avec succès"
        }
    }

    fun adminUpdateUser(targetUser: UserEntity, newRole: UserRole, newPass: String?, annual: Int, sick: Int, rtt: Int) {
        val admin = _currentUser.value ?: return
        if (admin.role != UserRole.ADMIN) return

        viewModelScope.launch {
            val updated = targetUser.copy(
                role = newRole,
                password = newPass ?: targetUser.password,
                balanceAnnual = annual,
                balanceSick = sick,
                balanceRtt = rtt
            )
            repository.updateUser(updated)
            _snackbarMessage.value = if (_language.value == AppLanguage.ARABIC)
                "تم حفظ بيانات الموظف بنجاح"
            else
                "Employé mis à jour avec succès"
        }
    }

    fun adminAddUser(firstName: String, lastName: String, username: String, pass: String, email: String, role: UserRole) {
        val admin = _currentUser.value ?: return
        if (admin.role != UserRole.ADMIN) return

        viewModelScope.launch {
            val newUser = UserEntity(
                firstName = firstName,
                lastName = lastName,
                username = username,
                password = pass,
                email = email,
                role = role,
                balanceAnnual = 30,
                balanceSick = 15,
                balanceRtt = 5
            )
            repository.insertUser(newUser)
            _snackbarMessage.value = if (_language.value == AppLanguage.ARABIC)
                "تم إضافة الموظف الجديد بنجاح"
            else
                "Nouvel employé ajouté avec succès"
        }
    }

    fun adminDeleteUser(userId: Long) {
        val admin = _currentUser.value ?: return
        if (admin.role != UserRole.ADMIN || userId == admin.id) return

        viewModelScope.launch {
            repository.deleteUser(userId)
            _snackbarMessage.value = if (_language.value == AppLanguage.ARABIC)
                "تم حذف الموظف من النظام"
            else
                "Employé supprimé"
        }
    }

    fun resetPassword(email: String, newPass: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val ok = repository.resetPasswordByEmail(email, newPass)
            if (ok) {
                _snackbarMessage.value = if (_language.value == AppLanguage.ARABIC)
                    "تم تحديث كلمة المرور بنجاح!"
                else
                    "Mot de passe mis à jour avec succès !"
            }
            onResult(ok)
        }
    }

    fun markNotificationsRead() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.markAllNotificationsRead(user.id, user.role)
        }
    }

    fun triggerLowBalanceYearEndReminders() {
        viewModelScope.launch {
            val count = repository.sendLowBalanceReminders(thresholdDays = 5)
            _snackbarMessage.value = if (_language.value == AppLanguage.ARABIC)
                "تم إرسال رسائل تذكير نهاية السنة إلى $count موظف بأرصدة منخفضة!"
            else
                "$count rappels de fin d'année ont été envoyés par email !"
        }
    }

    fun openEmailApp(context: Context, log: EmailLogEntity) {
        repository.openEmailClient(context, log)
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }
}
