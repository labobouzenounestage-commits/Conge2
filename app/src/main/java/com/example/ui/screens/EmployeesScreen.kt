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
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.ui.components.RoleBadge
import com.example.ui.i18n.AppLanguage
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.BorderLight
import com.example.ui.theme.DangerRed

@Composable
fun EmployeesScreen(
    currentUser: UserEntity,
    allUsers: List<UserEntity>,
    lang: AppLanguage,
    onAddUser: (firstName: String, lastName: String, username: String, pass: String, email: String, role: UserRole) -> Unit,
    onEditUser: (user: UserEntity, role: UserRole, newPass: String?, annual: Int, sick: Int, rtt: Int) -> Unit,
    onDeleteUser: (userId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val isFullAdmin = currentUser.role == UserRole.ADMIN
    var showAddDialog by remember { mutableStateOf(false) }
    var userToEdit by remember { mutableStateOf<UserEntity?>(null) }
    var userToDelete by remember { mutableStateOf<UserEntity?>(null) }

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
                        text = if (lang == AppLanguage.ARABIC) "👤 معلومات العمال والموظفين" else "👤 Gestion du Personnel",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (lang == AppLanguage.ARABIC)
                            "عرض قائمة وسجل الموظفين وأرصدتهم في النظام."
                        else
                            "Liste des employés enregistrés et soldes de congés.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (isFullAdmin) {
                    Button(
                        onClick = { showAddDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("add_employee_button")
                    ) {
                        Text(if (lang == AppLanguage.ARABIC) "➕ إضافة موظف" else "➕ Ajouter", fontSize = 12.sp)
                    }
                }
            }
        }

        items(allUsers, key = { it.id }) { user ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("employee_card_${user.id}"),
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
                            text = user.fullName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        RoleBadge(role = user.role, lang = lang)
                    }

                    Text(
                        text = "📧 ${user.email}  |  👤 @${user.username}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Balances preview
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "🏖️ ${if (lang == AppLanguage.ARABIC) "سنوي:" else "Annuel:"} ${user.balanceAnnual}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (user.balanceAnnual <= 5) DangerRed else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "🩺 ${if (lang == AppLanguage.ARABIC) "مرضي:" else "Maladie:"} ${user.balanceSick}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "⏱️ RTT: ${user.balanceRtt}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Admin Actions
                    if (isFullAdmin) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = { userToEdit = user },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("edit_user_button_${user.id}")
                            ) {
                                Text(if (lang == AppLanguage.ARABIC) "⚙️ تعديل" else "⚙️ Modifier", fontSize = 11.sp)
                            }

                            if (user.id != currentUser.id) {
                                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                                OutlinedButton(
                                    onClick = { userToDelete = user },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("delete_user_button_${user.id}")
                                ) {
                                    Text(if (lang == AppLanguage.ARABIC) "🗑️ حذف" else "🗑️ Supprimer", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AdminAddUserDialog(
            lang = lang,
            onDismiss = { showAddDialog = false },
            onAdd = { firstName, lastName, username, pass, email, role ->
                onAddUser(firstName, lastName, username, pass, email, role)
                showAddDialog = false
            }
        )
    }

    userToEdit?.let { target ->
        AdminEditUserDialog(
            user = target,
            lang = lang,
            onDismiss = { userToEdit = null },
            onSave = { updatedRole, newPass, annual, sick, rtt ->
                onEditUser(target, updatedRole, newPass, annual, sick, rtt)
                userToEdit = null
            }
        )
    }

    userToDelete?.let { target ->
        AlertDialog(
            onDismissRequest = { userToDelete = null },
            title = {
                Text(
                    text = if (lang == AppLanguage.ARABIC) "تأكيد حذف الموظف" else "Confirmer la suppression",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (lang == AppLanguage.ARABIC)
                        "هل أنت متأكد من حذف الموظف ${target.fullName} (@${target.username}) من النظام نهائياً؟"
                    else
                        "Voulez-vous vraiment supprimer définitivement ${target.fullName} (@${target.username}) ?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteUser(target.id)
                        userToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                ) {
                    Text(if (lang == AppLanguage.ARABIC) "حذف الموظف" else "Supprimer")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { userToDelete = null }) {
                    Text(if (lang == AppLanguage.ARABIC) "إلغاء" else "Annuler")
                }
            }
        )
    }
}
