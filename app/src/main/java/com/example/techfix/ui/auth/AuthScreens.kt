package com.example.techfix.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ---- Design tokens (same palette as the onboarding flow, kept local to this package) ----
private val ScreenBackground = Color(0xFF121212)
private val FieldBackground = Color(0xFF1C1C1E)
private val FieldBorderIdle = Color(0xFF2C2C2E)
private val AccentStart = Color(0xFFFF8A3D) // orange
private val AccentEnd = Color(0xFFFF3D68)   // pink/red
private val TextPrimary = Color(0xFFFFFFFF)
private val TextSecondary = Color(0xFFA0A0A5)
private val TextLabel = Color(0xFF8E8E93)

private val AccentGradient = Brush.linearGradient(listOf(AccentStart, AccentEnd))

// =========================================================================================
// LOGIN SCREEN
// =========================================================================================

@Composable
fun LoginScreen(
    onForgotPassword: () -> Unit,
    onLogin: (email: String, password: String) -> Unit,
    onSocialLogin: (SocialProvider) -> Unit,
    onNavigateToSignUp: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Surface(modifier = Modifier.fillMaxSize(), color = ScreenBackground) {
        Column(modifier = Modifier.fillMaxSize()) {

            AuthGradientHeader(
                title = "Bem-vindo de volta!",
                subtitle = "Conectando quem precisa com quem resolve."
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "Acesse sua Conta",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Insira suas credenciais para acompanhar seus chamados.",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                AuthTextField(
                    label = "E-MAIL",
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "seu.email@techfix.com",
                    trailingIcon = Icons.Outlined.Email,
                    keyboardType = KeyboardType.Email
                )

                Spacer(modifier = Modifier.height(16.dp))

                AuthTextField(
                    label = "SENHA",
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Digite sua senha de acesso",
                    trailingIcon = Icons.Outlined.Lock,
                    isPassword = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Esqueceu sua senha?",
                    color = AccentEnd,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onForgotPassword)
                )

                Spacer(modifier = Modifier.height(20.dp))

                PrimaryGradientButton(
                    text = "ENTRAR",
                    onClick = { onLogin(email, password) }
                )

                Spacer(modifier = Modifier.height(24.dp))

                DividerWithLabel(text = "ou entre com")

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    // NOTE: swap these generic icons for the real brand assets
                    // (X/Twitter, Facebook, Apple) — Material Icons has no
                    // trademarked logos, so placeholders are used here.
                    SocialIconButton(
                        icon = Icons.Outlined.Public,
                        onClick = { onSocialLogin(SocialProvider.X) }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    SocialIconButton(
                        icon = Icons.Outlined.Language,
                        onClick = { onSocialLogin(SocialProvider.FACEBOOK) }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    SocialIconButton(
                        icon = Icons.Outlined.Star,
                        onClick = { onSocialLogin(SocialProvider.APPLE) }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Não tem uma conta? ",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Cadastrar-se",
                        color = AccentEnd,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable(onClick = onNavigateToSignUp)
                    )
                }
            }
        }
    }
}

enum class SocialProvider { X, FACEBOOK, APPLE }

// =========================================================================================
// SIGN UP SCREEN (step 1 of N — basic info)
// =========================================================================================

@Composable
fun SignUpBasicInfoScreen(
    totalSteps: Int = 2,
    currentStep: Int = 1,
    onContinue: (SignUpBasicInfo) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val passwordsMatch = confirmPassword.isEmpty() || confirmPassword == password
    val canContinue = fullName.isNotBlank() && email.isNotBlank() &&
        password.isNotBlank() && confirmPassword == password

    Surface(modifier = Modifier.fillMaxSize(), color = ScreenBackground) {
        Column(modifier = Modifier.fillMaxSize()) {

            AuthGradientHeader(
                title = "Crie sua Conta",
                subtitle = "Na TechFix, clientes encontram profissionais e profissionais novas oportunidades."
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                SignUpStepProgressBar(totalSteps = totalSteps, currentStep = currentStep)

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Informações Básicas",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Insira os dados da conta para prosseguir com o seu cadastro.",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                AuthTextField(
                    label = "NOME COMPLETO",
                    value = fullName,
                    onValueChange = { fullName = it },
                    placeholder = "Digite seu nome completo",
                    trailingIcon = Icons.Outlined.Person
                )

                Spacer(modifier = Modifier.height(16.dp))

                AuthTextField(
                    label = "E-MAIL",
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "seu.email@exemplo.com",
                    trailingIcon = Icons.Outlined.Email,
                    keyboardType = KeyboardType.Email
                )

                Spacer(modifier = Modifier.height(16.dp))

                AuthTextField(
                    label = "SENHA",
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Crie uma senha forte",
                    trailingIcon = Icons.Outlined.Lock,
                    isPassword = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                AuthTextField(
                    label = "CONFIRMAR SENHA",
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = "Confirme a senha criada",
                    trailingIcon = Icons.Outlined.Lock,
                    isPassword = true,
                    isError = !passwordsMatch,
                    errorMessage = "As senhas não coincidem"
                )

                Spacer(modifier = Modifier.height(20.dp))

                PrimaryGradientButton(
                    text = "CONTINUAR",
                    enabled = canContinue,
                    trailingIcon = Icons.Default.ArrowForward,
                    onClick = {
                        onContinue(SignUpBasicInfo(fullName, email, password))
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Já possui acesso? ",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Fazer Login",
                        color = AccentEnd,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable(onClick = onNavigateToLogin)
                    )
                }
            }
        }
    }
}

data class SignUpBasicInfo(
    val fullName: String,
    val email: String,
    val password: String
)

// =========================================================================================
// SHARED COMPONENTS
// =========================================================================================

@Composable
private fun AuthGradientHeader(title: String, subtitle: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .background(AccentGradient)
            .padding(horizontal = 24.dp, vertical = 28.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🛠", fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Row {
                    Text(
                        text = "Tech",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Fix",
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = title,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun SignUpStepProgressBar(totalSteps: Int, currentStep: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        repeat(totalSteps) { index ->
            val isFilled = index < currentStep
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (isFilled) AccentEnd else Color(0xFF3A3A3C))
            )
        }
    }
}

@Composable
private fun AuthTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    trailingIcon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    Column {
        Text(
            text = label,
            color = TextLabel,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        val borderColor = if (isError) AccentEnd else FieldBorderIdle

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
            placeholder = {
                Text(text = placeholder, color = TextSecondary.copy(alpha = 0.6f), fontSize = 14.sp)
            },
            trailingIcon = {
                Icon(imageVector = trailingIcon, contentDescription = null, tint = TextSecondary)
            },
            singleLine = true,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(
                keyboardType = if (isPassword) KeyboardType.Password else keyboardType
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = FieldBackground,
                unfocusedContainerColor = FieldBackground,
                errorContainerColor = FieldBackground,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = AccentEnd,
                unfocusedBorderColor = borderColor,
                errorBorderColor = AccentEnd,
                cursorColor = AccentEnd
            ),
            isError = isError
        )

        if (isError && errorMessage != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = errorMessage, color = AccentEnd, fontSize = 11.sp)
        }
    }
}

@Composable
private fun PrimaryGradientButton(
    text: String,
    enabled: Boolean = true,
    trailingIcon: ImageVector? = null,
    onClick: () -> Unit
) {
    val backgroundModifier = if (enabled) {
        Modifier.background(AccentGradient)
    } else {
        Modifier.background(Color(0xFF3A3A3C))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(26.dp))
            .then(backgroundModifier)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = text,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            )
            if (trailingIcon != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun DividerWithLabel(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = FieldBorderIdle)
        Text(
            text = text,
            color = TextSecondary,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = FieldBorderIdle)
    }
}

@Composable
private fun SocialIconButton(icon: ImageVector, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(FieldBackground)
            .border(BorderStroke(1.dp, FieldBorderIdle), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(20.dp))
    }
}

// =========================================================================================
// PREVIEWS
// =========================================================================================

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreen(
            onForgotPassword = {},
            onLogin = { _, _ -> },
            onSocialLogin = {},
            onNavigateToSignUp = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun SignUpScreenPreview() {
    MaterialTheme {
        SignUpBasicInfoScreen(
            onContinue = {},
            onNavigateToLogin = {}
        )
    }
}
