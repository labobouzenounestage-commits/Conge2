package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.AppStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEditUserDialog(
    user: UserEntity,
    lang: AppLanguage,
    onDismiss: () -> Unit,
    onSave: (updatedRole: UserRole, newPass: String?, annual: Int, sick: Int, rtt: Int) -> Unit
) {
    var selectedRole by remember { mutableStateOf(user.role) }
    var roleDropdownExpanded by remember { mutableStateOf(false) }
    var newPassword by remember { mutableStateOf("") }
    var annualText by remember { mutableStateOf(user.balanceAnnual.toString()) }
    var sickText by remember { mutableStateOf(user.balanceSick.toString()) }
    var rttText by remember { mutableStateOf(user.balanceRtt.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (lang == AppLanguage.ARABIC)
                    "⚙️ تعديل بيانات: ${user.fullName}"
                else
                    "⚙️ Modifier : ${user.fullName}",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Role Selector
                ExposedDropdownMenuBox(
                    expanded = roleDropdownExpanded,
                    onExpandedChange = { roleDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = AppStrings.roleLabel(selectedRole, lang),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(if (lang == AppLanguage.ARABIC) "الدور والصلاحيات" else "Rôle et droits") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                            .testTag("admin_role_select")
                    )
                    ExposedDropdownMenu(
                        expanded = roleDropdownExpanded,
                        onDismissRequest = { roleDropdownExpanded = false }
                    ) {
                        UserRole.entries.forEach { role ->
                            DropdownMenuItem(
                                text = { Text(AppStrings.roleLabel(role, lang)) },
                                onClick = {
                                    selectedRole = role
                                    roleDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text(if (lang == AppLanguage.ARABIC) "كلمة المرور الجديدة (اختياري)" else "Nouveau mot de passe (optionnel)") },
                    placeholder = { Text(if (lang == AppLanguage.ARABIC) "اترك فارغاً للإبقاء على الحالية" else "Laisser vide pour conserver") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("admin_edit_password_input")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = annualText,
                        onValueChange = { annualText = it.filter { ch -> ch.isDigit() } },
                        label = { Text(if (lang == AppLanguage.ARABIC) "سنوي" else "Annuel") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("admin_edit_annual_input")
                    )
                    OutlinedTextField(
                        value = sickText,
                        onValueChange = { sickText = it.filter { ch -> ch.isDigit() } },
                        label = { Text(if (lang == AppLanguage.ARABIC) "مرضي" else "Maladie") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("admin_edit_sick_input")
                    )
                    OutlinedTextField(
                        value = rttText,
                        onValueChange = { rttText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("RTT") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("admin_edit_rtt_input")
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        selectedRole,
                        newPassword.trim().ifEmpty { null },
                        annualText.toIntOrNull() ?: user.balanceAnnual,
                        sickText.toIntOrNull() ?: user.balanceSick,
                        rttText.toIntOrNull() ?: user.balanceRtt
                    )
                },
                modifier = Modifier.testTag("admin_save_user_button")
            ) {
                Text(if (lang == AppLanguage.ARABIC) "حفظ التغييرات" else "Enregistrer")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(if (lang == AppLanguage.ARABIC) "إلغاء" else "Annuler")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}
