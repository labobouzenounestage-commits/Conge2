package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import com.example.ui.screens.dashboard.DashboardScreen
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EmailLogEntity
import com.example.data.model.LeaveRequestEntity
import com.example.data.model.LeaveType
import com.example.data.model.NotificationEntity
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.ui.components.RoleBadge
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.AppStrings
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.BluePrimaryDark
import com.example.ui.theme.DangerRed
import com.example.ui.viewmodel.MainTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScaffold(
    currentUser: UserEntity,
    currentTab: MainTab,
    lang: AppLanguage,
    allUsers: List<UserEntity>,
    allRequests: List<LeaveRequestEntity>,
    myRequests: List<LeaveRequestEntity>,
    pendingSubstituteRequests: List<LeaveRequestEntity>,
    pendingManagerRequests: List<LeaveRequestEntity>,
    notifications: List<NotificationEntity>,
    emailLogs: List<EmailLogEntity>,
    unreadNotifCount: Int,
    pendingManagerCount: Int,
    snackbarMessage: String?,
    onSelectTab: (MainTab) -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onSubmitLeaveRequest: (LeaveType, String, String, Int, String, Long) -> Unit,
    onAcceptSubstitute: (Long) -> Unit,
    onRejectSubstitute: (Long) -> Unit,
    onApproveRequest: (Long) -> Unit,
    onRejectRequest: (Long) -> Unit,
    onDeleteAllRequests: () -> Unit,
    onUpdateBalance: (LeaveType, Int) -> Unit,
    onAdminAddUser: (String, String, String, String, String, UserRole) -> Unit,
    onAdminEditUser: (UserEntity, UserRole, String?, Int, Int, Int) -> Unit,
    onAdminDeleteUser: (Long) -> Unit,
    onMarkAllNotificationsRead: () -> Unit,
    onSendLowBalanceReminders: () -> Unit,
    onOpenEmailClient: (android.content.Context, EmailLogEntity) -> Unit,
    onLogout: () -> Unit,
    onClearSnackbar: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessage) {
        if (!snackbarMessage.isNullOrBlank()) {
            snackbarHostState.showSnackbar(snackbarMessage)
            onClearSnackbar()
        }
    }

    val isRtl = lang == AppLanguage.ARABIC
    val layoutDirection = if (isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

    val isManagerOrAdmin = currentUser.role == UserRole.ADMIN || currentUser.role == UserRole.MANAGER

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = currentUser.fullName,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                RoleBadge(role = currentUser.role, lang = lang)
                            }
                            Text(
                                text = AppStrings.appTitle(lang),
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    },
                    actions = {
                        // Quick Language Toggle
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.15f))
                                .clickable {
                                    val nextLang = if (lang == AppLanguage.ARABIC) AppLanguage.FRENCH else AppLanguage.ARABIC
                                    onLanguageChange(nextLang)
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${lang.flag} ${lang.code.uppercase()}",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        IconButton(
                            onClick = onLogout,
                            modifier = Modifier.testTag("header_logout_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = AppStrings.logout(lang),
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = BluePrimaryDark,
                        titleContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    // 0. Dashboard
                    NavigationBarItem(
                        selected = currentTab == MainTab.DASHBOARD,
                        onClick = { onSelectTab(MainTab.DASHBOARD) },
                        icon = { Icon(Icons.Default.BarChart, contentDescription = null) },
                        label = { Text(AppStrings.menuDashboard(lang), fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = BluePrimary, indicatorColor = BluePrimary.copy(alpha = 0.12f)),
                        modifier = Modifier.testTag("nav_dashboard")
                    )

                    // 1. New Request (Only Employee & Admin)
                    if (currentUser.role != UserRole.MANAGER) {
                        NavigationBarItem(
                            selected = currentTab == MainTab.NEW_REQUEST,
                            onClick = { onSelectTab(MainTab.NEW_REQUEST) },
                            icon = { Icon(Icons.Default.AddCircle, contentDescription = null) },
                            label = { Text(AppStrings.menuNewRequest(lang), fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(selectedIconColor = BluePrimary, indicatorColor = BluePrimary.copy(alpha = 0.12f)),
                            modifier = Modifier.testTag("nav_new_request")
                        )
                    }

                    // 2. My Requests & Balances
                    NavigationBarItem(
                        selected = currentTab == MainTab.MY_REQUESTS,
                        onClick = { onSelectTab(MainTab.MY_REQUESTS) },
                        icon = {
                            if (pendingSubstituteRequests.isNotEmpty()) {
                                BadgedBox(badge = {
                                    Badge(containerColor = DangerRed) {
                                        Text("${pendingSubstituteRequests.size}")
                                    }
                                }) {
                                    Icon(Icons.Default.Description, contentDescription = null)
                                }
                            } else {
                                Icon(Icons.Default.Description, contentDescription = null)
                            }
                        },
                        label = { Text(AppStrings.menuMyRequests(lang), fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = BluePrimary, indicatorColor = BluePrimary.copy(alpha = 0.12f)),
                        modifier = Modifier.testTag("nav_my_requests")
                    )

                    // 3. Calendar
                    NavigationBarItem(
                        selected = currentTab == MainTab.CALENDAR,
                        onClick = { onSelectTab(MainTab.CALENDAR) },
                        icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                        label = { Text(AppStrings.menuCalendar(lang), fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = BluePrimary, indicatorColor = BluePrimary.copy(alpha = 0.12f)),
                        modifier = Modifier.testTag("nav_calendar")
                    )

                    // 4. Demandes (Manager & Admin only!)
                    if (isManagerOrAdmin) {
                        NavigationBarItem(
                            selected = currentTab == MainTab.DEMANDES,
                            onClick = { onSelectTab(MainTab.DEMANDES) },
                            icon = {
                                if (pendingManagerCount > 0) {
                                    BadgedBox(badge = {
                                        Badge(containerColor = DangerRed) {
                                            Text("$pendingManagerCount")
                                        }
                                    }) {
                                        Icon(Icons.Default.Inbox, contentDescription = null)
                                    }
                                } else {
                                    Icon(Icons.Default.Inbox, contentDescription = null)
                                }
                            },
                            label = { Text(AppStrings.menuDemandes(lang), fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(selectedIconColor = BluePrimary, indicatorColor = BluePrimary.copy(alpha = 0.12f)),
                            modifier = Modifier.testTag("nav_demandes")
                        )

                        // 5. Employees (Manager & Admin only!)
                        NavigationBarItem(
                            selected = currentTab == MainTab.EMPLOYEES,
                            onClick = { onSelectTab(MainTab.EMPLOYEES) },
                            icon = { Icon(Icons.Default.People, contentDescription = null) },
                            label = { Text(AppStrings.menuEmployees(lang), fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(selectedIconColor = BluePrimary, indicatorColor = BluePrimary.copy(alpha = 0.12f)),
                            modifier = Modifier.testTag("nav_employees")
                        )
                    }

                    // 6. Notifications & Email Center
                    NavigationBarItem(
                        selected = currentTab == MainTab.NOTIFICATIONS,
                        onClick = { onSelectTab(MainTab.NOTIFICATIONS) },
                        icon = {
                            if (unreadNotifCount > 0) {
                                BadgedBox(badge = {
                                    Badge(containerColor = DangerRed) {
                                        Text("$unreadNotifCount")
                                    }
                                }) {
                                    Icon(Icons.Default.Notifications, contentDescription = null)
                                }
                            } else {
                                Icon(Icons.Default.Notifications, contentDescription = null)
                            }
                        },
                        label = { Text(AppStrings.menuNotifications(lang), fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = BluePrimary, indicatorColor = BluePrimary.copy(alpha = 0.12f)),
                        modifier = Modifier.testTag("nav_notifications")
                    )
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentTab) {
                    MainTab.DASHBOARD -> {
                        DashboardScreen(
                            currentUser = currentUser,
                            allRequests = allRequests,
                            allUsers = allUsers,
                            lang = lang
                        )
                    }
                    MainTab.NEW_REQUEST -> {
                        NewRequestScreen(
                            currentUser = currentUser,
                            allUsers = allUsers,
                            lang = lang,
                            onSubmit = { type, start, end, days, reason, subId ->
                                onSubmitLeaveRequest(type, start, end, days, reason, subId)
                            }
                        )
                    }
                    MainTab.MY_REQUESTS -> {
                        MyRequestsScreen(
                            currentUser = currentUser,
                            myRequests = myRequests,
                            pendingSubstituteRequests = pendingSubstituteRequests,
                            lang = lang,
                            onAcceptSubstitute = onAcceptSubstitute,
                            onRejectSubstitute = onRejectSubstitute,
                            onUpdateBalance = onUpdateBalance
                        )
                    }
                    MainTab.CALENDAR -> {
                        CalendarScreen(
                            allRequests = allRequests,
                            allUsers = allUsers,
                            lang = lang
                        )
                    }
                    MainTab.DEMANDES -> {
                        if (isManagerOrAdmin) {
                            DemandesScreen(
                                userRole = currentUser.role,
                                pendingRequests = pendingManagerRequests,
                                lang = lang,
                                onApprove = onApproveRequest,
                                onReject = onRejectRequest,
                                onDeleteAll = onDeleteAllRequests
                            )
                        } else {
                            // Non-manager fallback
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Access Restricted")
                            }
                        }
                    }
                    MainTab.EMPLOYEES -> {
                        if (isManagerOrAdmin) {
                            EmployeesScreen(
                                currentUser = currentUser,
                                allUsers = allUsers,
                                lang = lang,
                                onAddUser = onAdminAddUser,
                                onEditUser = onAdminEditUser,
                                onDeleteUser = onAdminDeleteUser
                            )
                        } else {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Access Restricted")
                            }
                        }
                    }
                    MainTab.NOTIFICATIONS -> {
                        NotificationsAndEmailScreen(
                            currentUser = currentUser,
                            notifications = notifications,
                            emailLogs = emailLogs,
                            lang = lang,
                            onMarkAllRead = onMarkAllNotificationsRead,
                            onSendLowBalanceReminders = onSendLowBalanceReminders,
                            onOpenEmailClient = onOpenEmailClient
                        )
                    }
                }
            }
        }
    }
}
