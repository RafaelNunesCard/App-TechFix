package com.example.techfix.ui.profile

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.techfix.model.UserProfile
import com.example.techfix.ui.theme.TechFixTheme

private fun editPreviewProfile(name: String, filled: Boolean) = UserProfile(
    name = name,
    avatarUrl = "",
    isVerified = false,
    location = if (filled) "São Paulo, SP" else "",
    memberSince = "",
    completedServices = 0,
    rating = 0.0,
    confidencePercent = 0,
    bio = if (filled) "Gosto de manter meus equipamentos em dia." else "",
    interestCategories = if (filled) listOf("Computadores", "Celulares") else emptyList(),
    preferences = if (filled) listOf("Prefere orçamento prévio") else emptyList(),
    loyaltyLabel = "Novo por aqui",
    loyaltyPercent = 0,
    serviceHistory = emptyList()
)

@Preview(name = "Editar Perfil - vazio", showBackground = true, widthDp = 380, heightDp = 900)
@Composable
private fun EditProfileEmptyPreview() {
    TechFixTheme { EditProfileScreen(initialProfile = editPreviewProfile(name = "", filled = false)) }
}

@Preview(name = "Editar Perfil - preenchido", showBackground = true, widthDp = 380, heightDp = 900)
@Composable
private fun EditProfileFilledPreview() {
    TechFixTheme { EditProfileScreen(initialProfile = editPreviewProfile(name = "Maria Souza", filled = true)) }
}
