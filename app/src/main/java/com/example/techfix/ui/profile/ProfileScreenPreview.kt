package com.example.techfix.ui.profile

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.techfix.model.ServiceHistoryItem
import com.example.techfix.model.ServiceStatus
import com.example.techfix.model.UserProfile
import com.example.techfix.ui.theme.TechFixTheme

// Dados usados SOMENTE nos previews do Android Studio (não entram no app).
private fun previewProfile(
    name: String = "Maria Souza",
    filled: Boolean = false
) = UserProfile(
    name = name,
    avatarUrl = "",
    isVerified = false,
    location = if (filled) "São Paulo, SP" else "",
    memberSince = "Membro desde Novembro de 2026",
    completedServices = if (filled) 1 else 0,
    rating = 0.0,
    confidencePercent = 0,
    bio = if (filled) "Gosto de manter meus equipamentos em dia." else "",
    interestCategories = if (filled) listOf("Computadores", "Celulares", "Wi-Fi e Redes") else emptyList(),
    preferences = if (filled) listOf("Prefere orçamento prévio") else emptyList(),
    loyaltyLabel = "Novo por aqui",
    loyaltyPercent = 0,
    serviceHistory = if (filled) listOf(
        ServiceHistoryItem(
            deviceName = "Notebook",
            description = "Troca de bateria",
            status = ServiceStatus.CONCLUIDO,
            progress = 100,
            dateLabel = "FINALIZADO EM",
            date = "14/11/2026",
            warranty = "Válida por 90 dias"
        )
    ) else emptyList()
)

@Preview(name = "Perfil - conta nova", showBackground = true, widthDp = 380, heightDp = 900)
@Composable
private fun ProfileNewAccountPreview() {
    TechFixTheme { ProfileScreen(profile = previewProfile()) }
}

@Preview(name = "Perfil - preenchido", showBackground = true, widthDp = 380, heightDp = 1200)
@Composable
private fun ProfileFilledPreview() {
    TechFixTheme { ProfileScreen(profile = previewProfile(filled = true)) }
}

@Preview(name = "Perfil - profissional", showBackground = true, widthDp = 380, heightDp = 900)
@Composable
private fun ProfileProfessionalPreview() {
    TechFixTheme { ProfileScreen(profile = previewProfile(filled = true), isProfessional = true) }
}

@Preview(name = "Perfil - sem nome", showBackground = true, widthDp = 380, heightDp = 900)
@Composable
private fun ProfileNoNamePreview() {
    TechFixTheme { ProfileScreen(profile = previewProfile(name = "")) }
}
