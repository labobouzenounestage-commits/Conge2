package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LeaveRequestEntity
import com.example.data.model.UserRole
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.AppStrings
import com.example.ui.theme.BorderLight
import com.example.ui.theme.DangerRed
import com.example.ui.theme.SuccessGreen

@Composable
fun DemandesScreen(
    userRole: UserRole,
    pendingRequests: List<LeaveRequestEntity>,
    lang: AppLanguage,
    onApprove: (requestId: Long) -> Unit,
    onReject: (requestId: Long) -> Unit,
    onDeleteAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (lang == AppLanguage.ARABIC) "📥 استقبال معالجة الطلبات" else "📥 Traitement des Demandes",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (lang == AppLanguage.ARABIC)
                            "الطلبات الجاهزة لقرار الإدارة بعد موافقة الموظف البديل."
                        else
                            "Demandes en attente d'approbation administrative.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (userRole == UserRole.ADMIN) {
                    OutlinedButton(
                        onClick = { showDeleteConfirmDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("delete_all_requests_button")
                    ) {
                        Text(AppStrings.deleteAllRequests(lang), fontSize = 11.sp)
                    }
                }
            }
        }

        if (pendingRequests.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (lang == AppLanguage.ARABIC)
                            "لا توجد طلبات معلقة بانتظار قرار الإدارة حالياً"
                        else
                            "Aucune demande en attente de validation",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(pendingRequests, key = { it.id }) { req ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("demande_item_${req.id}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, BorderLight),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "👤 ${req.userName}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "${req.type.icon} ${AppStrings.leaveTypeName(req.type, lang)}",
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 13.sp
                            )
                        }

                        Text(
                            text = "🗓️ من ${req.startDate} إلى ${req.endDate}  (${req.days} ${if (lang == AppLanguage.ARABIC) "يوم" else "jours"})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = if (lang == AppLanguage.ARABIC)
                                "الموظف البديل: ${req.substituteName} (وافق على التعويض) • ${req.createdAt}"
                            else
                                "Remplaçant: ${req.substituteName} (Remplacement validé) • ${req.createdAt}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (req.reason.isNotBlank()) {
                            Text(
                                text = "💬 السبب: ${req.reason}",
                                fontSize = 12.sp
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = { onReject(req.id) },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("reject_request_button_${req.id}")
                            ) {
                                Text(AppStrings.rejectBtn(lang), fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                            Button(
                                onClick = { onApprove(req.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("approve_request_button_${req.id}")
                            ) {
                                Text(AppStrings.approveBtn(lang), fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = {
                Text(
                    text = if (lang == AppLanguage.ARABIC) "تأكيد حذف كل الطلبات" else "Confirmer la suppression",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (lang == AppLanguage.ARABIC)
                        "هل أنت متأكد من رغبتك في حذف جميع الطلبات من السجل بالكامل؟ هذا الإجراء لا يمكن التراجع عنه."
                    else
                        "Êtes-vous sûr de vouloir supprimer définitivement toutes les demandes ?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteAll()
                        showDeleteConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                ) {
                    Text(if (lang == AppLanguage.ARABIC) "حذف الكل" else "Supprimer tout")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text(if (lang == AppLanguage.ARABIC) "إلغاء" else "Annuler")
                }
            }
        )
    }
}
