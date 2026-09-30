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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.AppStrings
import com.example.ui.theme.RoleAdminBg
import com.example.ui.theme.RoleAdminColor
import com.example.ui.theme.RoleEmployeeBg
import com.example.ui.theme.RoleEmployeeColor
import com.example.ui.theme.RoleManagerBg
import com.example.ui.theme.RoleManagerColor

@Composable
fun RoleBadge(
    role: UserRole,
    lang: AppLanguage,
    modifier: Modifier = Modifier
) {
    val (textColor, bgColor, borderColor) = when (role) {
        UserRole.ADMIN -> Triple(RoleAdminColor, RoleAdminBg, RoleAdminColor.copy(alpha = 0.3f))
        UserRole.MANAGER -> Triple(RoleManagerColor, RoleManagerBg, RoleManagerColor.copy(alpha = 0.3f))
        UserRole.EMPLOYEE -> Triple(RoleEmployeeColor, RoleEmployeeBg, RoleEmployeeColor.copy(alpha = 0.3f))
    }

    Box(
        modifier = modifier
            .testTag("role_badge_${role.name.lowercase()}")
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = AppStrings.roleLabel(role, lang),
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
