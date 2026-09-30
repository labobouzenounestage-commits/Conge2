package com.example.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.model.LeaveType
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.AppStrings

@Composable
fun EditBalanceDialog(
    type: LeaveType,
    currentBalance: Int,
    lang: AppLanguage,
    onDismiss: () -> Unit,
    onSave: (newBalance: Int) -> Unit
) {
    var balanceText by remember { mutableStateOf(currentBalance.toString()) }

    val title = when (type) {
        LeaveType.ANNUAL -> AppStrings.cardAnnual(lang)
        LeaveType.SICK -> AppStrings.cardSick(lang)
        LeaveType.RTT -> AppStrings.cardRTT(lang)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (lang == AppLanguage.ARABIC) "تعديل رصيد $title" else "Modifier le solde $title",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (lang == AppLanguage.ARABIC)
                        "أدخل عدد الأيام الجديد للرصيد المتاح:"
                    else
                        "Entrez le nouveau nombre de jours disponibles :"
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = balanceText,
                    onValueChange = { balanceText = it.filter { ch -> ch.isDigit() } },
                    label = { Text(if (lang == AppLanguage.ARABIC) "عدد الأيام" else "Jours") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_balance_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newVal = balanceText.toIntOrNull() ?: currentBalance
                    onSave(newVal)
                },
                modifier = Modifier.testTag("save_balance_button")
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
