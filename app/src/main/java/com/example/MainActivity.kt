package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.email.EmailService
import com.example.data.local.AppDatabase
import com.example.data.repository.LeaveRepository
import com.example.ui.i18n.AppLanguage
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MainAppScaffold
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.LeaveViewModel
import com.example.ui.viewmodel.LeaveViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)
        val emailService = EmailService(database.emailLogDao())
        val repository = LeaveRepository(
            userDao = database.userDao(),
            leaveRequestDao = database.leaveRequestDao(),
            notificationDao = database.notificationDao(),
            emailLogDao = database.emailLogDao(),
            emailService = emailService
        )
        val viewModelFactory = LeaveViewModelFactory(repository)
        val viewModel: LeaveViewModel by viewModels { viewModelFactory }

        setContent {
            MyApplicationTheme {
                LeaveApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun LeaveApp(viewModel: LeaveViewModel) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val language by viewModel.language.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val allRequests by viewModel.allRequests.collectAsStateWithLifecycle()
    val myRequests by viewModel.myRequests.collectAsStateWithLifecycle()
    val pendingSubstituteRequests by viewModel.pendingSubstituteRequests.collectAsStateWithLifecycle()
    val pendingManagerRequests by viewModel.pendingManagerRequests.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val emailLogs by viewModel.allEmailLogs.collectAsStateWithLifecycle()
    val unreadNotifCount by viewModel.unreadNotifCount.collectAsStateWithLifecycle()
    val pendingManagerCount by viewModel.pendingManagerCount.collectAsStateWithLifecycle()
    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()

    var loginError by remember { mutableStateOf<String?>(null) }

    val layoutDirection = if (language == AppLanguage.ARABIC) LayoutDirection.Rtl else LayoutDirection.Ltr

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Surface(modifier = Modifier.fillMaxSize()) {
            val user = currentUser
            if (user == null) {
                LoginScreen(
                    currentLang = language,
                    onLanguageChange = { viewModel.setLanguage(it) },
                    onLogin = { id, pass ->
                        viewModel.login(id, pass) { ok, err ->
                            loginError = if (ok) null else err
                        }
                    },
                    onQuickLogin = { u, p ->
                        loginError = null
                        viewModel.quickLogin(u, p)
                    },
                    onResetPassword = { email, newPass ->
                        viewModel.resetPassword(email, newPass) { }
                    },
                    errorMessage = loginError
                )
            } else {
                MainAppScaffold(
                    currentUser = user,
                    currentTab = currentTab,
                    lang = language,
                    allUsers = allUsers,
                    allRequests = allRequests,
                    myRequests = myRequests,
                    pendingSubstituteRequests = pendingSubstituteRequests,
                    pendingManagerRequests = pendingManagerRequests,
                    notifications = notifications,
                    emailLogs = emailLogs,
                    unreadNotifCount = unreadNotifCount,
                    pendingManagerCount = pendingManagerCount,
                    snackbarMessage = snackbarMessage,
                    onSelectTab = { viewModel.selectTab(it) },
                    onLanguageChange = { viewModel.setLanguage(it) },
                    onSubmitLeaveRequest = { type, start, end, days, reason, subId ->
                        viewModel.submitLeaveRequest(
                            type = type,
                            startDate = start,
                            endDate = end,
                            days = days,
                            reason = reason,
                            substituteId = subId,
                            onSuccess = {},
                            onError = {}
                        )
                    },
                    onAcceptSubstitute = { viewModel.processSubstituteDecision(it, accepted = true) },
                    onRejectSubstitute = { viewModel.processSubstituteDecision(it, accepted = false) },
                    onApproveRequest = { viewModel.processManagerDecision(it, approved = true) },
                    onRejectRequest = { viewModel.processManagerDecision(it, approved = false) },
                    onDeleteAllRequests = { viewModel.deleteAllRequests() },
                    onUpdateBalance = { type, newDays -> viewModel.updateCurrentUserBalance(type, newDays) },
                    onAdminAddUser = { firstName, lastName, username, pass, email, role ->
                        viewModel.adminAddUser(firstName, lastName, username, pass, email, role)
                    },
                    onAdminEditUser = { targetUser, role, pass, annual, sick, rtt ->
                        viewModel.adminUpdateUser(targetUser, role, pass, annual, sick, rtt)
                    },
                    onAdminDeleteUser = { viewModel.adminDeleteUser(it) },
                    onMarkAllNotificationsRead = { viewModel.markNotificationsRead() },
                    onSendLowBalanceReminders = { viewModel.triggerLowBalanceYearEndReminders() },
                    onOpenEmailClient = { ctx, log -> viewModel.openEmailApp(ctx, log) },
                    onLogout = { viewModel.logout() },
                    onClearSnackbar = { viewModel.clearSnackbar() }
                )
            }
        }
    }
}
