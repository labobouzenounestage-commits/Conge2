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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
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
import com.example.data.model.UserEntity
import com.example.ui.components.StatusBadge
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.AppStrings
import com.example.ui.theme.BorderLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    allRequests: List<LeaveRequestEntity>,
    allUsers: List<UserEntity>,
    lang: AppLanguage,
    modifier: Modifier = Modifier
) {
    var selectedUserId by remember { mutableStateOf<Long?>(null) } // null means ALL
    var dropdownExpanded by remember { mutableStateOf(false) }

    val filteredRequests = remember(allRequests, selectedUserId) {
        if (selectedUserId == null) allRequests else allRequests.filter { it.userId == selectedUserId }
    }

    val selectedUserName = remember(allUsers, selectedUserId) {
        if (selectedUserId == null) {
            if (lang == AppLanguage.ARABIC) "جميع الموظفين (الكل)" else "Tous les employés"
        } else {
            allUsers.firstOrNull { it.id == selectedUserId }?.fullName ?: ""
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = if (lang == AppLanguage.ARABIC) "📅 تقويم العطلات العام (Calandre)" else "📅 Calendrier Général des Congés",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (lang == AppLanguage.ARABIC)
                    "عرض شامل لجميع فترات عطلات الموظفين مع خاصية التصفية المباشرة."
                else
                    "Vue d'ensemble de tous les congés planifiés avec filtre par employé.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Filter Bar
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, BorderLight)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    ExposedDropdownMenuBox(
                        expanded = dropdownExpanded,
                        onExpandedChange = { dropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedUserName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(if (lang == AppLanguage.ARABIC) "🔍 تصفية حسب الموظف" else "🔍 Filtrer par employé") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth()
                                .testTag("calendar_filter_dropdown")
                        )
                        ExposedDropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(if (lang == AppLanguage.ARABIC) "جميع الموظفين (الكل)" else "Tous les employés") },
                                onClick = {
                                    selectedUserId = null
                                    dropdownExpanded = false
                                }
                            )
                            allUsers.forEach { user ->
                                DropdownMenuItem(
                                    text = { Text(user.fullName) },
                                    onClick = {
                                        selectedUserId = user.id
                                        dropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        if (filteredRequests.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (lang == AppLanguage.ARABIC) "لا توجد عطلات مسجلة بالتقويم" else "Aucun congé planifié",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(filteredRequests, key = { it.id }) { req ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("calendar_item_${req.id}"),
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
                                text = "👤 ${req.userName}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            StatusBadge(status = req.status, lang = lang)
                        }

                        Text(
                            text = "${req.type.icon} ${AppStrings.leaveTypeName(req.type, lang)}: من ${req.startDate} إلى ${req.endDate}  (${req.days} ${if (lang == AppLanguage.ARABIC) "يوم" else "j"})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = if (lang == AppLanguage.ARABIC)
                                "الموظف البديل: ${req.substituteName} • تاريخ التقديم: ${req.createdAt}"
                            else
                                "Remplaçant: ${req.substituteName} • Soumis: ${req.createdAt}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
