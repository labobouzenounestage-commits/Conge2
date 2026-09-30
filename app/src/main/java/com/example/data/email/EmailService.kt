package com.example.data.email

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.data.local.EmailLogDao
import com.example.data.model.EmailLogEntity
import com.example.data.model.LeaveType

class EmailService(
    private val emailLogDao: EmailLogDao
) {
    companion object {
        const val DEFAULT_HR_EMAIL = "Labo.bouzenoune.stage@gmail.com"
    }

    suspend fun sendLeaveStatusEmail(
        toEmail: String,
        recipientName: String,
        isApproved: Boolean,
        leaveType: LeaveType,
        days: Int,
        startDate: String,
        endDate: String,
        remainingBalance: Int
    ) {
        val subject = if (isApproved) {
            "✅ تمت الموافقة على طلب العطلة الخاص بك / Demande de congé approuvée"
        } else {
            "❌ تم رفض طلب العطلة الخاص بك / Demande de congé refusée"
        }

        val body = if (isApproved) {
            """
            مرحباً $recipientName،
            
            يسعدنا إبلاغك بأنه تمت الموافقة على طلب العطلة الخاص بك:
            • نوع العطلة: ${leaveType.code} (${leaveType.name})
            • الفترة: من $startDate إلى $endDate
            • عدد الأيام: $days يوم
            • الرصيد المتبقي: $remainingBalance يوم
            
            نتمنى لك قضاء عطلة سعيدة!
            إدارة الموارد البشرية
            
            ---
            Bonjour $recipientName,
            Votre demande de congé (${leaveType.name}, $days jours) du $startDate au $endDate a été approuvée.
            Solde restant: $remainingBalance jours.
            """.trimIndent()
        } else {
            """
            مرحباً $recipientName،
            
            نأسف لإبلاغك بأنه قد تم رفض طلب العطلة التالي:
            • نوع العطلة: ${leaveType.code}
            • الفترة: من $startDate إلى $endDate
            • عدد الأيام: $days يوم
            
            يرجى مراجعة الإدارة لمزيد من التفاصيل.
            إدارة الموارد البشرية
            
            ---
            Bonjour $recipientName,
            Votre demande de congé du $startDate au $endDate n'a pas pu être acceptée. Veuillez contacter votre responsable.
            """.trimIndent()
        }

        val log = EmailLogEntity(
            toEmail = toEmail,
            recipientName = recipientName,
            subject = subject,
            body = body,
            triggerType = if (isApproved) "REQUEST_APPROVED" else "REQUEST_REJECTED",
            status = "SENT"
        )
        emailLogDao.insertEmailLog(log)
    }

    suspend fun sendNewRequestManagerNotification(
        managerEmails: List<String>,
        requesterName: String,
        leaveType: LeaveType,
        days: Int,
        startDate: String,
        endDate: String,
        substituteName: String,
        reason: String
    ) {
        val subject = "📥 طلب عطلة جديد للمراجعة: $requesterName / Nouvelle demande de congé"
        val body = """
        إلى السادة المدراء والمسؤولين،
        
        تم تقديم طلب عطلة جديد يتطلب مراجعتكم وقراركم:
        • الموظف مقدم الطلب: $requesterName
        • نوع العطلة: ${leaveType.code}
        • المدة: $days يوم (من $startDate إلى $endDate)
        • الموظف البديل المكلف: $substituteName
        • السبب: ${if (reason.isNotBlank()) reason else "لم يحدد"}
        
        يرجى تسجيل الدخول إلى النظام للموافقة أو الرفض.
        نظام إدارة العطلات
        
        ---
        Nouvelle demande de congé soumise par $requesterName ($days jours, du $startDate au $endDate).
        Remplaçant désigné: $substituteName.
        """.trimIndent()

        val recipients = (managerEmails + DEFAULT_HR_EMAIL).distinct().filter { it.isNotBlank() }
        for (email in recipients) {
            val log = EmailLogEntity(
                toEmail = email,
                recipientName = "Manager / HR Admin",
                subject = subject,
                body = body,
                triggerType = "REQUEST_SUBMITTED",
                status = "SENT"
            )
            emailLogDao.insertEmailLog(log)
        }
    }

    suspend fun sendLowBalanceReminderEmail(
        toEmail: String,
        recipientName: String,
        annualBalance: Int
    ) {
        val subject = "⚠️ تذكير: رصيد العطلات السنوية المتبقي / Rappel solde de congés"
        val body = """
        عزيزي الموظف $recipientName،
        
        نحيطكم علماً بأنه مع اقتراب نهاية السنة التقويمية (خلال الشهر القادم)، فإن رصيد عطلاتكم السنوية المتبقي هو:
        👉 $annualBalance يوم فقط.
        
        يرجى التخطيط لأي أيام متبقية وتقديم طلباتكم في أقرب وقت لتفادي ضغط نهاية العام وضمان تنظيم المناوبة والعمل.
        
        مع أطيب التحيات،
        قسم إدارة الموارد البشرية
        
        ---
        Cher(e) $recipientName,
        Rappel de fin d'année: votre solde de congé annuel restant est de $annualBalance jours.
        Merci d'anticiper vos demandes avant la clôture annuelle.
        """.trimIndent()

        val log = EmailLogEntity(
            toEmail = toEmail,
            recipientName = recipientName,
            subject = subject,
            body = body,
            triggerType = "LOW_BALANCE_REMINDER",
            status = "SENT"
        )
        emailLogDao.insertEmailLog(log)
    }

    fun openEmailClient(context: Context, toEmail: String, subject: String, body: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(toEmail))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        runCatching {
            context.startActivity(Intent.createChooser(intent, "إرسال عبر تطبيق البريد / Envoyer par email"))
        }
    }
}
