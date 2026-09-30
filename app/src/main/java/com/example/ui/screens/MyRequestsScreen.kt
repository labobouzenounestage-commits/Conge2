package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LeaveRequestEntity
import com.example.data.model.LeaveType
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.ui.components.BalanceCard
import com.example.ui.components.StatusBadge
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.AppStrings
import com.example.ui.theme.BorderLight
import com.example.ui.theme.DangerRed
import com.example.ui.theme.SuccessGreen

@Composable
fun MyRequestsScreen(
    currentUser: UserEntity,
    myRequests: List<LeaveRequestEntity>,
    pendingSubstituteRequests: List<LeaveRequestEntity>,
    lang: AppLanguage,
    onAcceptSubstitute: (requestId: Long) -> Unit,
    onRejectSubstitute: (requestId: Long) -> Unit,
    onUpdateBalance: (type: LeaveType, newDays: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val isAdmin = currentUser.role == UserRole.ADMIN
    var editingBalanceType by remember { mutableStateOf<LeaveType?>(null) }
    var currentBalanceToEdit by remember { mutableStateOf(0) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = AppStrings.myRequestsTitle(lang),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (isAdmin) AppStrings.myRequestsSubAdmin(lang) else AppStrings.myRequestsSub(lang),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Three Balance Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BalanceCard(
                    type = LeaveType.ANNUAL,
                    balance = currentUser.balanceAnnual,
                    isAdmin = isAdmin,
                    lang = lang,
                    onClick = {
                        currentBalanceToEdit = currentUser.balanceAnnual
                        editingBalanceType = LeaveType.ANNUAL
                    },
                    modifier = Modifier.weight(1f)
                )

                BalanceCard(
                    type = LeaveType.SICK,
                    balance = currentUser.balanceSick,
                    isAdmin = isAdmin,
                    lang = lang,
                    onClick = {
                        currentBalanceToEdit = currentUser.balanceSick
                        editingBalanceType = LeaveType.SICK
                    },
                    modifier = Modifier.weight(1f)
                )

                BalanceCard(
                    type = LeaveType.RTT,
                    balance = currentUser.balanceRtt,
                    isAdmin = isAdmin,
                    lang = lang,
                    onClick = {
                        currentBalanceToEdit = currentUser.balanceRtt
                        editingBalanceType = LeaveType.RTT
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Section: Pending Substitute Requests assigned to current user
        if (pendingSubstituteRequests.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("substitute_requests_panel"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFDF2F8)),
                    border = BorderStroke(1.5.dp, Color(0xFFF472B6))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "🌸 ${AppStrings.substituteInboxTitle(lang)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF9D174D)
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFDB2777))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "${pendingSubstituteRequests.size}",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        pendingSubstituteRequests.forEach { req ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, BorderLight)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "${req.userName} طلب تعويضك عن عطلته",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "• ${AppStrings.leaveTypeName(req.type, lang)} (${req.days} ${if (lang == AppLanguage.ARABIC) "يوم" else "j"})\n• من ${req.startDate} إلى ${req.endDate}\n• السبب: ${req.reason.ifBlank { if (lang == AppLanguage.ARABIC) "لم يحدد" else "Non spécifié" }}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        OutlinedButton(
                                            onClick = { onRejectSubstitute(req.id) },
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.testTag("reject_substitute_${req.id}")
                                        ) {
                                            Text(AppStrings.rejectSubstitute(lang), fontSize = 12.sp)
                                        }
                                        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                                        Button(
                                            onClick = { onAcceptSubstitute(req.id) },
                                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.testTag("accept_substitute_${req.id}")
                                        ) {
                                            Text(AppStrings.acceptSubstitute(lang), fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section: My Requests History
        item {
            Text(
                text = AppStrings.myRequestsHistory(lang),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (myRequests.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (lang == AppLanguage.ARABIC) "لا توجد طلبات سابقة" else "Aucune demande trouvée",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(myRequests, key = { it.id }) { req ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("request_item_${req.id}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, BorderLight),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${req.type.icon} ${AppStrings.leaveTypeName(req.type, lang)}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            StatusBadge(status = req.status, lang = lang)
                        }

                        Text(
                            text = "📅 ${req.startDate} ⬅️ ${req.endDate}  (${req.days} ${if (lang == AppLanguage.ARABIC) "يوم" else "jours"})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = if (lang == AppLanguage.ARABIC)
                                "الموظف البديل: ${req.substituteName} | تاريخ الإرسال: ${req.createdAt}"
                            else
                                "Remplaçant: ${req.substituteName} | Envoyé le: ${req.createdAt}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (req.reason.isNotBlank()) {
                            Text(
                                text = "💬 ${req.reason}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }

    editingBalanceType?.let { type ->
        EditBalanceDialog(
            type = type,
            currentBalance = currentBalanceToEdit,
            lang = lang,
            onDismiss = { editingBalanceType = null },
            onSave = { newBal ->
                onUpdateBalance(type, newBal)
                editingBalanceType = null
            }
        )
    }
}
