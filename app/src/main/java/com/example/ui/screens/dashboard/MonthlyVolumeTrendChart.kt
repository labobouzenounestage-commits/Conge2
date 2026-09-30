package com.example.ui.screens.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LeaveRequestEntity
import com.example.data.model.LeaveStatus
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.AppStrings
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.BorderLight

@Composable
fun MonthlyVolumeTrendChart(
    requests: List<LeaveRequestEntity>,
    lang: AppLanguage,
    modifier: Modifier = Modifier
) {
    val monthLabels = remember {
        listOf("Jan", "Fév", "Mar", "Avr", "Mai", "Juin", "Juil", "Août", "Sep", "Oct", "Nov", "Déc")
    }

    // Monthly counts of requests
    val monthlyCounts = remember(requests) {
        val counts = FloatArray(12)
        for (req in requests) {
            val parts = req.startDate.split("-")
            if (parts.size >= 2) {
                val m = (parts[1].toIntOrNull() ?: 1) - 1
                if (m in 0..11) {
                    counts[m] += 1f
                }
            }
        }
        counts
    }

    val maxCount = remember(monthlyCounts) {
        val m = monthlyCounts.maxOrNull() ?: 0f
        if (m <= 0f) 5f else m + 1f
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("monthly_volume_trend_chart"),
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
            Text(
                text = AppStrings.chartMonthlyVolumeTitle(lang),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Smooth Trend Line Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 12.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxWidth().height(125.dp)) {
                    val w = size.width
                    val h = size.height
                    val stepX = w / 11f

                    // Gridlines
                    val gridLines = 3
                    for (i in 0..gridLines) {
                        val y = h - (h / gridLines) * i
                        drawLine(
                            color = Color.LightGray.copy(alpha = 0.35f),
                            start = Offset(0f, y),
                            end = Offset(w, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // Build line and fill paths
                    val linePath = Path()
                    val fillPath = Path()

                    val points = (0..11).map { index ->
                        val count = monthlyCounts[index]
                        val x = index * stepX
                        val y = h - (count / maxCount) * h
                        Offset(x, y)
                    }

                    if (points.isNotEmpty()) {
                        linePath.moveTo(points[0].x, points[0].y)
                        fillPath.moveTo(points[0].x, h)
                        fillPath.lineTo(points[0].x, points[0].y)

                        for (i in 1 until points.size) {
                            val prev = points[i - 1]
                            val curr = points[i]
                            val cx = (prev.x + curr.x) / 2f
                            linePath.cubicTo(cx, prev.y, cx, curr.y, curr.x, curr.y)
                            fillPath.cubicTo(cx, prev.y, cx, curr.y, curr.x, curr.y)
                        }

                        fillPath.lineTo(points.last().x, h)
                        fillPath.close()

                        // Draw gradient fill
                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    BluePrimary.copy(alpha = 0.35f),
                                    BluePrimary.copy(alpha = 0.02f)
                                )
                            )
                        )

                        // Draw line
                        drawPath(
                            path = linePath,
                            color = BluePrimary,
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )

                        // Draw dots at points
                        for (pt in points) {
                            drawCircle(
                                color = Color.White,
                                radius = 4.dp.toPx(),
                                center = pt
                            )
                            drawCircle(
                                color = BluePrimary,
                                radius = 3.dp.toPx(),
                                center = pt
                            )
                        }
                    }
                }
            }

            // Month labels under the chart
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("Jan", "Mar", "Mai", "Juil", "Sep", "Nov").forEach { label ->
                    Text(
                        text = label,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
