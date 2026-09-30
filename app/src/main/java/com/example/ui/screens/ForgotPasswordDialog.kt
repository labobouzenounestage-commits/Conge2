package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp
import com.example.ui.i18n.AppLanguage

@Composable
fun ForgotPasswordDialog(
    lang: AppLanguage,
    onDismiss: () -> Unit,
    onReset: (email: String, newPass: String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (lang == AppLanguage.ARABIC) "🔑 استعادة وتغيير كلمة المرور" else "🔑 Récupération de mot de passe",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = if (lang == AppLanguage.ARABIC)
                        "أدخل البريد الإلكتروني المسجل في النظام وكلمة المرور الجديدة:"
                    else
                        "Entrez l'email du compte et votre nouveau mot de passe :"
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; errorText = null },
                    label = { Text(if (lang == AppLanguage.ARABIC) "البريد الإلكتروني" else "Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("forgot_email_input")
                )

                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it; errorText = null },
                    label = { Text(if (lang == AppLanguage.ARABIC) "كلمة المرور الجديدة" else "Nouveau mot de passe") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("forgot_new_password_input")
                )

                if (errorText != null) {
                    Text(
                        text = errorText ?: "",
                        color = androidx.compose.material3.MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (email.isBlank() || newPassword.isBlank()) {
                        errorText = if (lang == AppLanguage.ARABIC) "يرجى ملء كافة الحقول" else "Veuillez remplir tous les champs"
                    } else {
                        onReset(email.trim(), newPassword)
                    }
                },
                modifier = Modifier.testTag("forgot_password_submit_button")
            ) {
                Text(if (lang == AppLanguage.ARABIC) "تحديث كلمة المرور" else "Mettre à jour")
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
