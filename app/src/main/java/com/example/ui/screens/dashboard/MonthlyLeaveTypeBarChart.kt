package com.example.ui.screens.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LeaveRequestEntity
import com.example.data.model.LeaveStatus
import com.example.data.model.LeaveType
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.AppStrings
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.BorderLight

data class MonthLeaveData(
    val monthIndex: Int, // 0..11
    val monthNameAr: String,
    val monthNameFr: String,
    val annualDays: Int,
    val sickDays: Int,
    val rttDays: Int
) {
    val totalDays: Int
        get() = annualDays + sickDays + rttDays
}

@Composable
fun MonthlyLeaveTypeBarChart(
    requests: List<LeaveRequestEntity>,
    lang: AppLanguage,
    modifier: Modifier = Modifier
) {
    val monthNamesAr = remember {
        listOf("جانفي", "فيفري", "مارس", "أفريل", "ماي", "جوان", "جويلية", "أوت", "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر")
    }
    val monthNamesFr = remember {
        listOf("Jan", "Fév", "Mar", "Avr", "Mai", "Juin", "Juil", "Août", "Sep", "Oct", "Nov", "Déc")
    }

    // Aggregate leave days per month for approved & pending requests
    val monthlyData = remember(requests) {
        val annualMap = IntArray(12)
        val sickMap = IntArray(12)
        val rttMap = IntArray(12)

        for (req in requests) {
            // Only count approved or pending requests
            if (req.status == LeaveStatus.REJECTED || req.status == LeaveStatus.REJECTED_SUBSTITUTE) continue

            // Parse month from startDate YYYY-MM-DD
            val parts = req.startDate.split("-")
            if (parts.size >= 2) {
                val m = (parts[1].toIntOrNull() ?: 1) - 1
                if (m in 0..11) {
                    when (req.type) {
                        LeaveType.ANNUAL -> annualMap[m] += req.days
                        LeaveType.SICK -> sickMap[m] += req.days
                        LeaveType.RTT -> rttMap[m] += req.days
                    }
                }
            }
        }

        (0..11).map { index ->
            MonthLeaveData(
                monthIndex = index,
                monthNameAr = monthNamesAr[index],
                monthNameFr = monthNamesFr[index],
                annualDays = annualMap[index],
                sickDays = sickMap[index],
                rttDays = rttMap[index]
            )
        }
    }

    val maxDays = remember(monthlyData) {
        val maxInList = monthlyData.maxOfOrNull { it.totalDays } ?: 0
        if (maxInList <= 0) 10 else maxInList + (maxInList / 4) + 1
    }

    var selectedMonth by remember { mutableStateOf<MonthLeaveData?>(null) }

    val colorAnnual = Color(0xFF2563EB) // Royal Blue
    val colorSick = Color(0xFF0D9488)   // Teal Green
    val colorRtt = Color(0xFFD97706)    // Amber Orange

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("monthly_leave_type_chart"),
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = AppStrings.chartMonthlyTypesTitle(lang),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (lang == AppLanguage.ARABIC) "المجموع التراكمي للأيام حسب كل شهر" else "Jours cumulés par mois et catégorie",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Legend Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendIndicator(label = AppStrings.legendAnnual(lang), color = colorAnnual)
                LegendIndicator(label = AppStrings.legendSick(lang), color = colorSick)
                LegendIndicator(label = AppStrings.legendRtt(lang), color = colorRtt)
            }

            // Scrollable Bar Chart Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
                    .padding(horizontal = 8.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    monthlyData.forEach { data ->
                        val isSelected = selectedMonth?.monthIndex == data.monthIndex
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .width(38.dp)
                                .clickable { selectedMonth = if (isSelected) null else data }
                        ) {
                            // Total days label on top of bar
                            if (data.totalDays > 0) {
                                Text(
                                    text = "${data.totalDays}j",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) BluePrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                Spacer(modifier = Modifier.height(14.dp))
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            // Stacked Bar Canvas
                            Canvas(modifier = Modifier.size(width = 24.dp, height = 130.dp)) {
                                val canvasHeight = size.height
                                val barWidth = size.width
                                val scale = canvasHeight / maxDays.toFloat()

                                val hAnnual = data.annualDays * scale
                                val hSick = data.sickDays * scale
                                val hRtt = data.rttDays * scale

                                val corner = CornerRadius(4.dp.toPx(), 4.dp.toPx())

                                if (data.totalDays == 0) {
                                    // Empty placeholder base
                                    drawRoundRect(
                                        color = Color.LightGray.copy(alpha = 0.25f),
                                        topLeft = Offset(0f, canvasHeight - 6.dp.toPx()),
                                        size = Size(barWidth, 6.dp.toPx()),
                                        cornerRadius = corner
                                    )
                                } else {
                                    var currentY = canvasHeight

                                    // Annual portion (bottom)
                                    if (hAnnual > 0) {
                                        currentY -= hAnnual
                                        drawRoundRect(
                                            color = colorAnnual,
                                            topLeft = Offset(0f, currentY),
                                            size = Size(barWidth, hAnnual),
                                            cornerRadius = corner
                                        )
                                    }

                                    // Sick portion (middle)
                                    if (hSick > 0) {
                                        currentY -= hSick
                                        drawRoundRect(
                                            color = colorSick,
                                            topLeft = Offset(0f, currentY),
                                            size = Size(barWidth, hSick),
                                            cornerRadius = corner
                                        )
                                    }

                                    // RTT portion (top)
                                    if (hRtt > 0) {
                                        currentY -= hRtt
                                        drawRoundRect(
                                            color = colorRtt,
                                            topLeft = Offset(0f, currentY),
                                            size = Size(barWidth, hRtt),
                                            cornerRadius = corner
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Month Label
                            val mLabel = if (lang == AppLanguage.ARABIC) data.monthNameAr else data.monthNameFr
                            Text(
                                text = mLabel,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) BluePrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Month Breakdown inspection card when tapped
            selectedMonth?.let { month ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(BluePrimary.copy(alpha = 0.08f))
                        .padding(10.dp)
                ) {
                    val mTitle = if (lang == AppLanguage.ARABIC) month.monthNameAr else month.monthNameFr
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🗓️ $mTitle: ${month.totalDays} ${if (lang == AppLanguage.ARABIC) "يوم إجازة إجمالي" else "jours au total"}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = BluePrimary
                        )

                        Text(
                            text = "🏖️ ${month.annualDays}j  •  🩺 ${month.sickDays}j  •  ⏱️ ${month.rttDays}j",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendIndicator(label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
