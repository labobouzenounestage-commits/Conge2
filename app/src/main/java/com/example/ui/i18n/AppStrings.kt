package com.example.ui.i18n

import com.example.data.model.LeaveStatus
import com.example.data.model.LeaveType
import com.example.data.model.UserRole

object AppStrings {

    fun appTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "🏢 نظام إدارة العطلات المتكامل"
        AppLanguage.FRENCH -> "🏢 Système de Gestion des Congés"
    }

    fun login(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "تسجيل الدخول"
        AppLanguage.FRENCH -> "Connexion"
    }

    fun loginSubtitle(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "نظام متكامل لإدارة العطلات وصلاحيات الموظفين والإدارة"
        AppLanguage.FRENCH -> "Gestion intégrée des congés et des autorisations d'équipe"
    }

    fun loginIdLabel(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "اسم المستخدم أو البريد الإلكتروني"
        AppLanguage.FRENCH -> "Nom d'utilisateur ou Email"
    }

    fun passwordLabel(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "كلمة المرور"
        AppLanguage.FRENCH -> "Mot de passe"
    }

    fun loginBtn(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "تسجيل الدخول"
        AppLanguage.FRENCH -> "Se connecter"
    }

    fun logout(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "تسجيل الخروج"
        AppLanguage.FRENCH -> "Déconnexion"
    }

    fun forgotPassLink(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "🔑 نسيت كلمة المرور؟"
        AppLanguage.FRENCH -> "🔑 Mot de passe oublié ?"
    }

    fun quickTestAccounts(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "حسابات تجريبية سريعة بنقرة واحدة:"
        AppLanguage.FRENCH -> "Comptes de test rapide en un clic :"
    }

    // Tabs
    fun menuDashboard(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "المؤشرات"
        AppLanguage.FRENCH -> "Dashboard"
    }

    fun menuNewRequest(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "طلب جديد"
        AppLanguage.FRENCH -> "Demande"
    }

    fun menuMyRequests(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "أرصدتي وطلباتي"
        AppLanguage.FRENCH -> "Mes Soldes"
    }

    fun menuCalendar(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "التقويم"
        AppLanguage.FRENCH -> "Calendrier"
    }

    fun menuDemandes(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "استقبال الطلبات"
        AppLanguage.FRENCH -> "Traitement"
    }

    fun menuEmployees(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "معلومات العمال"
        AppLanguage.FRENCH -> "Personnel"
    }

    fun menuNotifications(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "الإشعارات والبريد"
        AppLanguage.FRENCH -> "Notifs & Emails"
    }

    // New Request Screen
    fun newRequestTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "تقديم طلب عطلة جديد"
        AppLanguage.FRENCH -> "Soumettre une Demande de Congé"
    }

    fun leaveTypeLabel(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "نوع العطلة"
        AppLanguage.FRENCH -> "Type de congé"
    }

    fun startDateLabel(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "تاريخ البداية"
        AppLanguage.FRENCH -> "Date de début"
    }

    fun endDateLabel(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "تاريخ النهاية"
        AppLanguage.FRENCH -> "Date de fin"
    }

    fun daysCountLabel(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "عدد الأيام المحسوبة"
        AppLanguage.FRENCH -> "Nombre de jours calculés"
    }

    fun reasonLabel(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "السبب أو الملاحظات"
        AppLanguage.FRENCH -> "Motif ou remarques"
    }

    fun substituteLabel(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "الموظف البديل (إلزامي)"
        AppLanguage.FRENCH -> "Employé remplaçant (Obligatoire)"
    }

    fun substitutePrompt(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "-- اختر الموظف الذي سيعوضك --"
        AppLanguage.FRENCH -> "-- Choisir un remplaçant --"
    }

    fun submitBtn(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "إرسال طلب العطلة"
        AppLanguage.FRENCH -> "Envoyer la demande"
    }

    // Balances
    fun myRequestsTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "أرصدتي وحالة الطلبات"
        AppLanguage.FRENCH -> "Mes Soldes & Historique"
    }

    fun cardAnnual(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "🏖️ رصيد العطلة السنوية"
        AppLanguage.FRENCH -> "🏖️ Solde Annuel"
    }

    fun cardSick(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "🩺 رصيد العطلة المرضية"
        AppLanguage.FRENCH -> "🩺 Solde Maladie"
    }

    fun cardRTT(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "⏱️ رصيد الاسترجاع RTT"
        AppLanguage.FRENCH -> "⏱️ Solde RTT"
    }

    fun myRequestsSub(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "أرصدة العطلات الحالية الخاصة بك:"
        AppLanguage.FRENCH -> "Vos soldes actuels de congés disponibles :"
    }

    fun myRequestsSubAdmin(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "انقر على أي بطاقة لتعديل رصيد الأيام مباشرة (خاص بالمسؤول):"
        AppLanguage.FRENCH -> "Cliquez sur une carte pour modifier le solde directement (Admin) :"
    }

    fun myRequestsHistory(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "سجل الطلبات الخاص بي"
        AppLanguage.FRENCH -> "Mon historique de demandes"
    }

    fun substituteInboxTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "🌸 طلبات التعويض الواردة إليّ"
        AppLanguage.FRENCH -> "🌸 Demandes de remplacement reçues"
    }

    fun acceptSubstitute(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "✅ قبول التعويض"
        AppLanguage.FRENCH -> "✅ Accepter"
    }

    fun rejectSubstitute(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "❌ اعتذار عن التعويض"
        AppLanguage.FRENCH -> "❌ Décliner"
    }

    fun approveBtn(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "✅ قبول الطلب"
        AppLanguage.FRENCH -> "✅ Approuver"
    }

    fun rejectBtn(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "❌ رفض الطلب"
        AppLanguage.FRENCH -> "❌ Refuser"
    }

    fun deleteAllRequests(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "🗑️ مسح كل الطلبات"
        AppLanguage.FRENCH -> "🗑️ Tout supprimer"
    }

    // Role text
    fun roleLabel(role: UserRole, lang: AppLanguage) = when (role) {
        UserRole.ADMIN -> if (lang == AppLanguage.ARABIC) "مسؤول كامل (Admin)" else "Admin Complet"
        UserRole.MANAGER -> if (lang == AppLanguage.ARABIC) "مدير محدود (Manager)" else "Manager Limité"
        UserRole.EMPLOYEE -> if (lang == AppLanguage.ARABIC) "موظف" else "Employé"
    }

    // Status text
    fun statusLabel(status: LeaveStatus, lang: AppLanguage): String = when (status) {
        LeaveStatus.PENDING_SUBSTITUTE -> if (lang == AppLanguage.ARABIC) "في انتظار موافقة البديل" else "En attente remplaçant"
        LeaveStatus.PENDING_MANAGER -> if (lang == AppLanguage.ARABIC) "في انتظار الموافقة النهائية" else "En attente validation"
        LeaveStatus.APPROVED -> if (lang == AppLanguage.ARABIC) "موافق عليه" else "Approuvé"
        LeaveStatus.REJECTED -> if (lang == AppLanguage.ARABIC) "مرفوض" else "Refusé"
        LeaveStatus.REJECTED_SUBSTITUTE -> if (lang == AppLanguage.ARABIC) "رفض البديل التعويض" else "Remplacement refusé"
    }

    fun leaveTypeName(type: LeaveType, lang: AppLanguage): String = when (type) {
        LeaveType.ANNUAL -> if (lang == AppLanguage.ARABIC) "🏖️ سنوية" else "🏖️ Annuel"
        LeaveType.SICK -> if (lang == AppLanguage.ARABIC) "🩺 مرضية" else "🩺 Maladie"
        LeaveType.RTT -> if (lang == AppLanguage.ARABIC) "⏱️ استرجاع (RTT)" else "⏱️ RTT"
    }

    fun daysUnit(count: Int, lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> if (count in 3..10) "$count أيام" else "$count يوم"
        AppLanguage.FRENCH -> if (count > 1) "$count jours" else "$count jour"
    }

    // Email section
    fun emailCenterTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "✉️ مركز إشعارات البريد الإلكتروني"
        AppLanguage.FRENCH -> "✉️ Centre des Notifications Email"
    }

    fun sendLowBalanceRemindersBtn(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "🔔 إرسال تذكير نهاية السنة للأرصدة المنخفضة (≤ 5 أيام)"
        AppLanguage.FRENCH -> "🔔 Envoyer rappels fin d'année (Solde ≤ 5 jours)"
    }

    fun openInEmailApp(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "📧 فتح في تطبيق البريد"
        AppLanguage.FRENCH -> "📧 Ouvrir dans l'app email"
    }

    // Dashboard & Analytics
    fun dashboardTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "📊 لوحة متابعة مؤشرات واتجاهات العطلات"
        AppLanguage.FRENCH -> "📊 Tableau de Bord & Tendances des Congés"
    }

    fun dashboardSubtitle(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "رؤية بيانية شاملة لحالة الطلبات وتوزيع أنواع العطلات شهرياً"
        AppLanguage.FRENCH -> "Visualisation analytique des demandes et répartition mensuelle"
    }

    fun statTotalRequests(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "إجمالي الطلبات"
        AppLanguage.FRENCH -> "Total Demandes"
    }

    fun statApprovalRate(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "نسبة القبول"
        AppLanguage.FRENCH -> "Taux d'Approbation"
    }

    fun statPendingCount(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "طلبات قيد المتابعة"
        AppLanguage.FRENCH -> "En Cours de Traitement"
    }

    fun statTotalDaysTaken(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "مجموع أيام الإجازات"
        AppLanguage.FRENCH -> "Jours de Congé Pris"
    }

    fun chartStatusTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "حالة الطلبات: الموافق عليها مقابل المعلقة والمرفوضة"
        AppLanguage.FRENCH -> "Statut : Approuvées vs En attente vs Refusées"
    }

    fun chartMonthlyTypesTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "توزيع أنواع العطلات المأخوذة شهرياً (بالأيام)"
        AppLanguage.FRENCH -> "Types de congés pris par mois (en jours)"
    }

    fun chartMonthlyVolumeTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "تطور حجم الطلبات على مدار العام"
        AppLanguage.FRENCH -> "Évolution mensuelle du volume des demandes"
    }

    fun legendApproved(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "موافق عليها"
        AppLanguage.FRENCH -> "Approuvées"
    }

    fun legendPending(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "قيد الانتظار"
        AppLanguage.FRENCH -> "En attente"
    }

    fun legendRejected(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "مرفوضة"
        AppLanguage.FRENCH -> "Refusées"
    }

    fun legendAnnual(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "سنوية 🏖️"
        AppLanguage.FRENCH -> "Annuel 🏖️"
    }

    fun legendSick(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "مرضية 🩺"
        AppLanguage.FRENCH -> "Maladie 🩺"
    }

    fun legendRtt(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "استرجاع RTT ⏱️"
        AppLanguage.FRENCH -> "RTT ⏱️"
    }

    fun employeeLeaveConsumption(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "استهلاك أرصدة الموظفين (الأيام المتبقية)"
        AppLanguage.FRENCH -> "Consommation des soldes employés"
    }

    fun scopeAllCompany(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "🏢 جميع موظفي المؤسسة"
        AppLanguage.FRENCH -> "🏢 Toute l'entreprise"
    }

    fun scopePersonalOnly(lang: AppLanguage) = when (lang) {
        AppLanguage.ARABIC -> "👤 طلباتي الشخصية فقط"
        AppLanguage.FRENCH -> "👤 Mes demandes uniquement"
    }
}
