package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LeaveStatus
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.AppStrings
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DangerRedBg
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenBg
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberBg

@Composable
fun StatusBadge(
    status: LeaveStatus,
    lang: AppLanguage,
    modifier: Modifier = Modifier
) {
    val (textColor, bgColor, borderColor) = when (status) {
        LeaveStatus.APPROVED -> Triple(SuccessGreen, SuccessGreenBg, SuccessGreen.copy(alpha = 0.3f))
        LeaveStatus.REJECTED, LeaveStatus.REJECTED_SUBSTITUTE -> Triple(DangerRed, DangerRedBg, DangerRed.copy(alpha = 0.3f))
        LeaveStatus.PENDING_MANAGER, LeaveStatus.PENDING_SUBSTITUTE -> Triple(WarningAmber, WarningAmberBg, WarningAmber.copy(alpha = 0.3f))
    }

    Box(
        modifier = modifier
            .testTag("status_badge_${status.name.lowercase()}")
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = AppStrings.statusLabel(status, lang),
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
