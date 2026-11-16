package com.example.techfix.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.techfix.ui.theme.TechFixAccentBrush
import com.example.techfix.ui.theme.TechFixBackground
import com.example.techfix.ui.theme.TechFixBorder
import com.example.techfix.ui.theme.TechFixRed
import com.example.techfix.ui.theme.TechFixSurface
import com.example.techfix.ui.theme.TechFixTextPrimary
import com.example.techfix.ui.theme.TechFixTextSecondary
import com.example.techfix.ui.theme.TechFixTextTertiary
import kotlinx.coroutines.launch

/**
 * Tela "Esqueci minha senha".
 *
 * Como o app guarda tudo só no celular (sem servidor de e-mail), a pessoa
 * informa o e-mail da conta e já escolhe a nova senha aqui mesmo.
 *
 * [onResetPassword] devolve null se deu certo, ou a mensagem de erro.
 */
@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit,
    onResetPassword: suspend (email: String, newPassword: String) -> String?,
    onDone: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun submit() {
        error = when {
            email.isBlank() -> "Informe o e-mail da sua conta."
            password.length < 6 -> "A nova senha precisa ter pelo menos 6 caracteres."
            password != confirm -> "As senhas não coincidem."
            else -> null
        }
        if (error != null) return
        loading = true
        scope.launch {
            val result = onResetPassword(email, password)
            loading = false
            if (result == null) onDone() else error = result
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TechFixBackground)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Voltar",
                tint = TechFixTextPrimary
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Esqueceu sua senha?",
            color = TechFixTextPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Informe o e-mail da sua conta e escolha uma nova senha.",
            color = TechFixTextSecondary,
            fontSize = 14.sp
        )

        Spacer(Modifier.height(28.dp))

        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            ForgotField(
                value = email,
                onValueChange = { email = it },
                label = "E-mail",
                keyboardType = KeyboardType.Email
            )
            ForgotField(
                value = password,
                onValueChange = { password = it },
                label = "Nova senha",
                keyboardType = KeyboardType.Password,
                isPassword = true
            )
            ForgotField(
                value = confirm,
                onValueChange = { confirm = it },
                label = "Confirmar nova senha",
                keyboardType = KeyboardType.Password,
                isPassword = true
            )
        }

        val currentError = error
        if (currentError != null) {
            Spacer(Modifier.height(12.dp))
            Text(text = currentError, color = TechFixRed, fontSize = 13.sp)
        }

        Spacer(Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(percent = 50))
                .background(TechFixAccentBrush)
                .clickable(enabled = !loading) { submit() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (loading) "Salvando..." else "Redefinir senha",
                color = TechFixTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Voltar para o login",
            color = TechFixTextTertiary,
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable(onClick = onBack)
                .padding(8.dp)
        )
    }
}

@Composable
private fun ForgotField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType,
    isPassword: Boolean = false
) {
    var visible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = { visible = !visible }) {
                    Icon(
                        imageVector = if (visible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        contentDescription = if (visible) "Esconder senha" else "Mostrar senha",
                        tint = TechFixTextSecondary
                    )
                }
            }
        } else null,
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        visualTransformation = if (isPassword && !visible) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TechFixTextPrimary,
            unfocusedTextColor = TechFixTextPrimary,
            focusedContainerColor = TechFixSurface,
            unfocusedContainerColor = TechFixSurface,
            focusedBorderColor = TechFixTextSecondary,
            unfocusedBorderColor = TechFixBorder,
            focusedLabelColor = TechFixTextSecondary,
            unfocusedLabelColor = TechFixTextTertiary,
            cursorColor = TechFixTextPrimary
        )
    )
}
