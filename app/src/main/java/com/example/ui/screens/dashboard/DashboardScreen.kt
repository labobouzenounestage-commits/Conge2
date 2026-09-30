package com.example.ui.screens.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.InsertChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LeaveRequestEntity
import com.example.data.model.LeaveStatus
import com.example.data.model.LeaveType
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.ui.components.RoleBadge
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.AppStrings
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.BorderLight
import com.example.ui.theme.DangerRed
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber

@Composable
fun DashboardScreen(
    currentUser: UserEntity,
    allRequests: List<LeaveRequestEntity>,
    allUsers: List<UserEntity>,
    lang: AppLanguage,
    modifier: Modifier = Modifier
) {
    val isManagerOrAdmin = currentUser.role == UserRole.ADMIN || currentUser.role == UserRole.MANAGER

    // Filter scope: ALL company vs PERSONAL
    var showAllScope by remember { mutableStateOf(isManagerOrAdmin) }

    val filteredRequests = remember(allRequests, currentUser, showAllScope) {
        if (showAllScope) allRequests else allRequests.filter { it.userId == currentUser.id }
    }

    // Status counts
    val approvedCount = remember(filteredRequests) {
        filteredRequests.count { it.status == LeaveStatus.APPROVED }
    }
    val pendingCount = remember(filteredRequests) {
        filteredRequests.count { it.status == LeaveStatus.PENDING_MANAGER || it.status == LeaveStatus.PENDING_SUBSTITUTE }
    }
    val rejectedCount = remember(filteredRequests) {
        filteredRequests.count { it.status == LeaveStatus.REJECTED || it.status == LeaveStatus.REJECTED_SUBSTITUTE }
    }

    val totalRequests = filteredRequests.size
    val approvalRate = remember(totalRequests, approvedCount) {
        if (totalRequests > 0) ((approvedCount * 100f) / totalRequests).toInt() else 0
    }

    val totalDaysApproved = remember(filteredRequests) {
        filteredRequests.filter { it.status == LeaveStatus.APPROVED }.sumOf { it.days }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("dashboard_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = AppStrings.dashboardTitle(lang),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = AppStrings.dashboardSubtitle(lang),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Scope Switcher (Company vs Personal) for Managers/Admins
        if (isManagerOrAdmin) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = showAllScope,
                        onClick = { showAllScope = true },
                        label = { Text(AppStrings.scopeAllCompany(lang), fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BluePrimary.copy(alpha = 0.12f),
                            selectedLabelColor = BluePrimary
                        ),
                        modifier = Modifier.testTag("scope_chip_all")
                    )

                    FilterChip(
                        selected = !showAllScope,
                        onClick = { showAllScope = false },
                        label = { Text(AppStrings.scopePersonalOnly(lang), fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BluePrimary.copy(alpha = 0.12f),
                            selectedLabelColor = BluePrimary
                        ),
                        modifier = Modifier.testTag("scope_chip_personal")
                    )
                }
            }
        }

        // 4 KPI Summary Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                KpiStatCard(
                    title = AppStrings.statTotalRequests(lang),
                    value = "$totalRequests",
                    subtitle = if (lang == AppLanguage.ARABIC) "مقدمة بالنظام" else "Déposées",
                    icon = Icons.Default.InsertChart,
                    color = BluePrimary,
                    tag = "kpi_total_requests",
                    modifier = Modifier.weight(1f)
                )

                KpiStatCard(
                    title = AppStrings.statApprovalRate(lang),
                    value = "$approvalRate%",
                    subtitle = if (lang == AppLanguage.ARABIC) "$approvedCount مقبولة" else "$approvedCount validées",
                    icon = Icons.Default.CheckCircle,
                    color = SuccessGreen,
                    tag = "kpi_approval_rate",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                KpiStatCard(
                    title = AppStrings.statPendingCount(lang),
                    value = "$pendingCount",
                    subtitle = if (lang == AppLanguage.ARABIC) "تتطلب قرار" else "À traiter",
                    icon = Icons.Default.HourglassTop,
                    color = WarningAmber,
                    tag = "kpi_pending_count",
                    modifier = Modifier.weight(1f)
                )

                KpiStatCard(
                    title = AppStrings.statTotalDaysTaken(lang),
                    value = "${totalDaysApproved}j",
                    subtitle = if (lang == AppLanguage.ARABIC) "أيام موافق عليها" else "Jours approuvés",
                    icon = Icons.Default.BeachAccess,
                    color = Color(0xFF7C3AED),
                    tag = "kpi_total_days",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Chart 1: Donut Status Chart (Pending vs Approved vs Rejected)
        item {
            DonutStatusChart(
                approvedCount = approvedCount,
                pendingCount = pendingCount,
                rejectedCount = rejectedCount,
                lang = lang
            )
        }

        // Chart 2: Monthly Leave Types Bar Chart (Annual, Sick, RTT per month)
        item {
            MonthlyLeaveTypeBarChart(
                requests = filteredRequests,
                lang = lang
            )
        }

        // Chart 3: Monthly Volume Trend Line/Area Chart
        item {
            MonthlyVolumeTrendChart(
                requests = filteredRequests,
                lang = lang
            )
        }

        // Employee Annual Balance Consumption (for Management)
        if (isManagerOrAdmin && showAllScope) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("employee_balance_consumption_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, BorderLight),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = AppStrings.employeeLeaveConsumption(lang),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        allUsers.filter { it.role == UserRole.EMPLOYEE }.forEach { emp ->
                            val consumed = maxOf(0, 30 - emp.balanceAnnual)
                            val progress = (consumed.toFloat() / 30f).coerceIn(0f, 1f)

                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = emp.fullName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "${emp.balanceAnnual} / 30 ${if (lang == AppLanguage.ARABIC) "يوم متبقي" else "j restants"}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (emp.balanceAnnual <= 5) DangerRed else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                LinearProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = if (emp.balanceAnnual <= 5) DangerRed else BluePrimary,
                                    trackColor = Color(0xFFF1F5F9)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
