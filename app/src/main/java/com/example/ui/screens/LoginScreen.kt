package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.AppStrings
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.BluePrimaryDark
import com.example.ui.theme.BorderLight
import com.example.ui.theme.RoleAdminBg
import com.example.ui.theme.RoleAdminColor
import com.example.ui.theme.RoleEmployeeBg
import com.example.ui.theme.RoleEmployeeColor
import com.example.ui.theme.RoleManagerBg
import com.example.ui.theme.RoleManagerColor

@Composable
fun LoginScreen(
    currentLang: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    onLogin: (identifier: String, pass: String) -> Unit,
    onQuickLogin: (username: String, pass: String) -> Unit,
    onResetPassword: (email: String, newPass: String) -> Unit,
    errorMessage: String?,
    modifier: Modifier = Modifier
) {
    var identifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showForgotDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        BluePrimary.copy(alpha = 0.08f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 480.dp)
                .fillMaxWidth()
                .testTag("login_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Language Switcher Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (currentLang == AppLanguage.ARABIC) "نظام إدارة العطلات" else "Gestion Congés",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = BluePrimary
                    )

                    // Language toggles
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(2.dp)
                    ) {
                        AppLanguage.entries.forEach { lang ->
                            val isSelected = lang == currentLang
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isSelected) BluePrimary else Color.Transparent)
                                    .clickable { onLanguageChange(lang) }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = "${lang.flag} ${lang.displayName}",
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // App Brand Icon
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(BluePrimaryDark)
                        .border(3.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_leave_icon),
                        contentDescription = "App Icon",
                        modifier = Modifier.size(54.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = AppStrings.login(currentLang),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = AppStrings.loginSubtitle(currentLang),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Username / Email
                OutlinedTextField(
                    value = identifier,
                    onValueChange = { identifier = it },
                    label = { Text(AppStrings.loginIdLabel(currentLang)) },
                    placeholder = { Text("nadir, Labo.bouzenoune.stage@gmail.com, admin") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = BluePrimary)
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_id_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Password
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(AppStrings.passwordLabel(currentLang)) },
                    placeholder = { Text("••••••••") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = BluePrimary)
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle password"
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_password_input")
                )

                if (!errorMessage.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Submit Login Button
                Button(
                    onClick = { onLogin(identifier.trim(), password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("login_submit_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = AppStrings.loginBtn(currentLang),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                TextButton(
                    onClick = { showForgotDialog = true },
                    modifier = Modifier.testTag("forgot_password_button")
                ) {
                    Text(
                        text = AppStrings.forgotPassLink(currentLang),
                        fontSize = 12.sp,
                        color = BluePrimary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Quick Profile Testing Shortcuts
                Text(
                    text = AppStrings.quickTestAccounts(currentLang),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    QuickAccountPill(
                        title = "👑 Nadir (Full Admin)",
                        subtitle = "nadir / Labo.bouzenoune.stage@gmail.com (Pass: nadir@1990)",
                        bgColor = RoleAdminBg,
                        textColor = RoleAdminColor,
                        onClick = {
                            identifier = "Labo.bouzenoune.stage@gmail.com"
                            password = "nadir@1990"
                            onQuickLogin("Labo.bouzenoune.stage@gmail.com", "nadir@1990")
                        },
                        tag = "quick_login_nadir"
                    )

                    QuickAccountPill(
                        title = "👔 Admin (Manager)",
                        subtitle = "admin / admin@123",
                        bgColor = RoleManagerBg,
                        textColor = RoleManagerColor,
                        onClick = {
                            identifier = "admin"
                            password = "admin@123"
                            onQuickLogin("admin", "admin@123")
                        },
                        tag = "quick_login_manager"
                    )

                    QuickAccountPill(
                        title = "👷 Ahmed (Employee)",
                        subtitle = "ahmed / 123",
                        bgColor = RoleEmployeeBg,
                        textColor = RoleEmployeeColor,
                        onClick = {
                            identifier = "ahmed"
                            password = "123"
                            onQuickLogin("ahmed", "123")
                        },
                        tag = "quick_login_ahmed"
                    )

                    QuickAccountPill(
                        title = "👩 Fatima (Low Balance Employee)",
                        subtitle = "fatima / 123 (Annual: 4 days)",
                        bgColor = RoleEmployeeBg,
                        textColor = RoleEmployeeColor,
                        onClick = {
                            identifier = "fatima"
                            password = "123"
                            onQuickLogin("fatima", "123")
                        },
                        tag = "quick_login_fatima"
                    )
                }
            }
        }
    }

    if (showForgotDialog) {
        ForgotPasswordDialog(
            lang = currentLang,
            onDismiss = { showForgotDialog = false },
            onReset = { email, newPass ->
                onResetPassword(email, newPass)
                showForgotDialog = false
            }
        )
    }
}

@Composable
private fun QuickAccountPill(
    title: String,
    subtitle: String,
    bgColor: Color,
    textColor: Color,
    onClick: () -> Unit,
    tag: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .border(1.dp, textColor.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag(tag)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = title,
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Text(
                    text = subtitle,
                    color = textColor.copy(alpha = 0.8f),
                    fontSize = 10.sp
                )
            }
            Text(
                text = "⚡ دخول فوري",
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
