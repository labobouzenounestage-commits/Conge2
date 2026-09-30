package com.example.ui.screens

import android.app.DatePickerDialog
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LeaveType
import com.example.data.model.UserEntity
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.AppStrings
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.BorderLight
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewRequestScreen(
    currentUser: UserEntity,
    allUsers: List<UserEntity>,
    lang: AppLanguage,
    onSubmit: (type: LeaveType, start: String, end: String, days: Int, reason: String, substituteId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedType by remember { mutableStateOf(LeaveType.ANNUAL) }
    var typeDropdownExpanded by remember { mutableStateOf(false) }

    val calendar = remember { Calendar.getInstance() }
    val sdf = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    var startDate by remember {
        mutableStateOf(sdf.format(calendar.time))
    }
    var endDate by remember {
        val nextDay = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 2) }
        mutableStateOf(sdf.format(nextDay.time))
    }

    var reason by remember { mutableStateOf("") }

    // List eligible substitutes (other users who are employees or colleagues)
    val eligibleSubstitutes = remember(allUsers, currentUser) {
        allUsers.filter { it.id != currentUser.id }
    }
    var selectedSubstituteId by remember {
        mutableStateOf<Long?>(eligibleSubstitutes.firstOrNull()?.id)
    }
    var substituteDropdownExpanded by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Calculate days difference
    val calculatedDays by remember(startDate, endDate) {
        derivedStateOf {
            runCatching {
                val d1 = sdf.parse(startDate)
                val d2 = sdf.parse(endDate)
                if (d1 != null && d2 != null && !d2.before(d1)) {
                    val diff = d2.time - d1.time
                    (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1).toInt()
                } else {
                    0
                }
            }.getOrDefault(0)
        }
    }

    val currentBalanceForType = when (selectedType) {
        LeaveType.ANNUAL -> currentUser.balanceAnnual
        LeaveType.SICK -> currentUser.balanceSick
        LeaveType.RTT -> currentUser.balanceRtt
    }

    fun showDatePicker(initialDate: String, onDateSelected: (String) -> Unit) {
        val cal = Calendar.getInstance()
        runCatching {
            sdf.parse(initialDate)?.let { cal.time = it }
        }
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val chosen = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }
                onDateSelected(sdf.format(chosen.time))
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 640.dp)
                .fillMaxWidth()
                .testTag("new_request_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, BorderLight),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = AppStrings.newRequestTitle(lang),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Current available balance indicator
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BluePrimary.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (lang == AppLanguage.ARABIC) "الرصيد المتاح لهذا النوع:" else "Solde disponible :",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "$currentBalanceForType ${if (lang == AppLanguage.ARABIC) "يوم" else "jours"}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = BluePrimary
                        )
                    }
                }

                // Leave Type selector
                ExposedDropdownMenuBox(
                    expanded = typeDropdownExpanded,
                    onExpandedChange = { typeDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = AppStrings.leaveTypeName(selectedType, lang),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(AppStrings.leaveTypeLabel(lang)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                            .testTag("select_leave_type")
                    )
                    ExposedDropdownMenu(
                        expanded = typeDropdownExpanded,
                        onDismissRequest = { typeDropdownExpanded = false }
                    ) {
                        LeaveType.entries.forEach { t ->
                            DropdownMenuItem(
                                text = { Text(AppStrings.leaveTypeName(t, lang)) },
                                onClick = {
                                    selectedType = t
                                    typeDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Dates Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = startDate,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(AppStrings.startDateLabel(lang)) },
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker(startDate) { startDate = it } }) {
                                Icon(Icons.Default.CalendarMonth, contentDescription = "Select Start Date", tint = BluePrimary)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_start_date")
                    )

                    OutlinedTextField(
                        value = endDate,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(AppStrings.endDateLabel(lang)) },
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker(endDate) { endDate = it } }) {
                                Icon(Icons.Default.CalendarMonth, contentDescription = "Select End Date", tint = BluePrimary)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_end_date")
                    )
                }

                // Days count display
                OutlinedTextField(
                    value = if (calculatedDays > 0) AppStrings.daysUnit(calculatedDays, lang) else "0",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(AppStrings.daysCountLabel(lang)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_days_count")
                )

                // Mandatory Substitute Selector
                val selectedSubUser = eligibleSubstitutes.firstOrNull { it.id == selectedSubstituteId }
                ExposedDropdownMenuBox(
                    expanded = substituteDropdownExpanded,
                    onExpandedChange = { substituteDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedSubUser?.fullName ?: AppStrings.substitutePrompt(lang),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(AppStrings.substituteLabel(lang)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = substituteDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                            .testTag("select_substitute")
                    )
                    ExposedDropdownMenu(
                        expanded = substituteDropdownExpanded,
                        onDismissRequest = { substituteDropdownExpanded = false }
                    ) {
                        eligibleSubstitutes.forEach { emp ->
                            DropdownMenuItem(
                                text = { Text("${emp.fullName} (${emp.username})") },
                                onClick = {
                                    selectedSubstituteId = emp.id
                                    substituteDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Reason / Notes
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text(AppStrings.reasonLabel(lang)) },
                    minLines = 3,
                    maxLines = 4,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_leave_reason")
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = {
                        if (calculatedDays <= 0) {
                            errorMessage = if (lang == AppLanguage.ARABIC)
                                "يرجى اختيار تاريخ نهاية بعد تاريخ البداية"
                            else
                                "La date de fin doit être après la date de début"
                            return@Button
                        }
                        if (calculatedDays > currentBalanceForType) {
                            errorMessage = if (lang == AppLanguage.ARABIC)
                                "الرصيد لا يكفي! المتاح: $currentBalanceForType يوم"
                            else
                                "Solde insuffisant ! Disponible : $currentBalanceForType jours"
                            return@Button
                        }
                        val subId = selectedSubstituteId
                        if (subId == null) {
                            errorMessage = if (lang == AppLanguage.ARABIC)
                                "يرجى تحديد الموظف البديل"
                            else
                                "Veuillez sélectionner un remplaçant"
                            return@Button
                        }

                        errorMessage = null
                        onSubmit(selectedType, startDate, endDate, calculatedDays, reason, subId)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_leave_request_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = AppStrings.submitBtn(lang),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
