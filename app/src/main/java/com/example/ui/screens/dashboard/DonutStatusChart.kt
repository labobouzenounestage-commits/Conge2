package com.example.ui.screens.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.AppStrings
import com.example.ui.theme.BorderLight
import com.example.ui.theme.DangerRed
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber

@Composable
fun DonutStatusChart(
    approvedCount: Int,
    pendingCount: Int,
    rejectedCount: Int,
    lang: AppLanguage,
    modifier: Modifier = Modifier
) {
    val total = approvedCount + pendingCount + rejectedCount
    val safeTotal = if (total == 0) 1 else total

    val approvedPct = (approvedCount * 100f) / safeTotal
    val pendingPct = (pendingCount * 100f) / safeTotal
    val rejectedPct = (rejectedCount * 100f) / safeTotal

    val approvedSweep = (approvedCount.toFloat() / safeTotal) * 360f
    val pendingSweep = (pendingCount.toFloat() / safeTotal) * 360f
    val rejectedSweep = (rejectedCount.toFloat() / safeTotal) * 360f

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("donut_status_chart"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = AppStrings.chartStatusTitle(lang),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Donut Canvas
                Box(
                    modifier = Modifier.size(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(140.dp)) {
                        val strokeWidth = 26.dp.toPx()
                        val arcSize = size.width - strokeWidth

                        if (total == 0) {
                            drawArc(
                                color = Color.LightGray.copy(alpha = 0.5f),
                                startAngle = 0f,
                                sweepAngle = 360f,
                                useCenter = false,
                                topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                                size = Size(arcSize, arcSize),
                                style = Stroke(width = strokeWidth)
                            )
                        } else {
                            var currentStart = -90f

                            // Approved Arc (Green)
                            if (approvedSweep > 0) {
                                drawArc(
                                    color = SuccessGreen,
                                    startAngle = currentStart,
                                    sweepAngle = approvedSweep - if (pendingSweep > 0 || rejectedSweep > 0) 2f else 0f,
                                    useCenter = false,
                                    topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                                    size = Size(arcSize, arcSize),
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                )
                                currentStart += approvedSweep
                            }

                            // Pending Arc (Amber)
                            if (pendingSweep > 0) {
                                drawArc(
                                    color = WarningAmber,
                                    startAngle = currentStart,
                                    sweepAngle = pendingSweep - if (rejectedSweep > 0 || approvedSweep > 0) 2f else 0f,
                                    useCenter = false,
                                    topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                                    size = Size(arcSize, arcSize),
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                )
                                currentStart += pendingSweep
                            }

                            // Rejected Arc (Red)
                            if (rejectedSweep > 0) {
                                drawArc(
                                    color = DangerRed,
                                    startAngle = currentStart,
                                    sweepAngle = rejectedSweep - if (approvedSweep > 0 || pendingSweep > 0) 2f else 0f,
                                    useCenter = false,
                                    topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                                    size = Size(arcSize, arcSize),
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                )
                            }
                        }
                    }

                    // Center Metrics
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (total > 0) "${approvedPct.toInt()}%" else "0%",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (lang == AppLanguage.ARABIC) "نسبة القبول" else "Approuvées",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Legend & Metrics Column
                Column(
                    modifier = Modifier.padding(start = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ChartLegendItem(
                        label = AppStrings.legendApproved(lang),
                        count = approvedCount,
                        percentage = approvedPct,
                        color = SuccessGreen
                    )
                    ChartLegendItem(
                        label = AppStrings.legendPending(lang),
                        count = pendingCount,
                        percentage = pendingPct,
                        color = WarningAmber
                    )
                    ChartLegendItem(
                        label = AppStrings.legendRejected(lang),
                        count = rejectedCount,
                        percentage = rejectedPct,
                        color = DangerRed
                    )
                }
            }
        }
    }
}

@Composable
private fun ChartLegendItem(
    label: String,
    count: Int,
    percentage: Float,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Column {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "$count (${percentage.toInt()}%)",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
