package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.MaterialTheme
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
import com.example.data.model.UserRole
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.AppStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAddUserDialog(
    lang: AppLanguage,
    onDismiss: () -> Unit,
    onAdd: (firstName: String, lastName: String, username: String, pass: String, email: String, role: UserRole) -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var role by remember { mutableStateOf(UserRole.EMPLOYEE) }
    var roleExpanded by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (lang == AppLanguage.ARABIC) "➕ إضافة موظف جديد إلى النظام" else "➕ Ajouter un nouvel employé",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = firstName,
                        onValueChange = { firstName = it; errorMsg = null },
                        label = { Text(if (lang == AppLanguage.ARABIC) "الاسم الأول" else "Prénom") },
                        modifier = Modifier.weight(1f).testTag("add_user_firstname")
                    )
                    OutlinedTextField(
                        value = lastName,
                        onValueChange = { lastName = it; errorMsg = null },
                        label = { Text(if (lang == AppLanguage.ARABIC) "اللقب" else "Nom") },
                        modifier = Modifier.weight(1f).testTag("add_user_lastname")
                    )
                }

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it; errorMsg = null },
                    label = { Text(if (lang == AppLanguage.ARABIC) "اسم المستخدم" else "Nom d'utilisateur") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_user_username")
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; errorMsg = null },
                    label = { Text(if (lang == AppLanguage.ARABIC) "كلمة المرور" else "Mot de passe") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_user_password")
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(if (lang == AppLanguage.ARABIC) "البريد الإلكتروني (اختياري)" else "Email (Optionnel)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_user_email")
                )

                ExposedDropdownMenuBox(
                    expanded = roleExpanded,
                    onExpandedChange = { roleExpanded = it }
                ) {
                    OutlinedTextField(
                        value = AppStrings.roleLabel(role, lang),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(if (lang == AppLanguage.ARABIC) "الصلاحية / الدور" else "Rôle") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleExpanded) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                            .testTag("add_user_role_select")
                    )
                    ExposedDropdownMenu(
                        expanded = roleExpanded,
                        onDismissRequest = { roleExpanded = false }
                    ) {
                        UserRole.entries.forEach { r ->
                            DropdownMenuItem(
                                text = { Text(AppStrings.roleLabel(r, lang)) },
                                onClick = {
                                    role = r
                                    roleExpanded = false
                                }
                            )
                        }
                    }
                }

                if (errorMsg != null) {
                    Text(
                        text = errorMsg ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (firstName.isBlank() || lastName.isBlank() || username.isBlank() || password.isBlank()) {
                        errorMsg = if (lang == AppLanguage.ARABIC) "يرجى تعبئة الحقول الأساسية" else "Veuillez remplir les champs obligatoires"
                    } else {
                        val computedEmail = email.trim().ifEmpty { "${username.trim().lowercase()}@company.com" }
                        onAdd(firstName.trim(), lastName.trim(), username.trim().lowercase(), password, computedEmail, role)
                    }
                },
                modifier = Modifier.testTag("submit_add_user_button")
            ) {
                Text(if (lang == AppLanguage.ARABIC) "إنشاء الحساب" else "Créer le compte")
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
