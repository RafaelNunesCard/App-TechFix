package com.example.techfix.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.techfix.R

// Fonte oficial do app: Sora (arquivos em res/font)
val SoraFamily = FontFamily(
    Font(R.font.sora_regular, FontWeight.Normal),
    Font(R.font.sora_bold, FontWeight.Bold)
)

// Estilos de texto do app. Todos usam a Sora.
val TechFixTypography = Typography(
    headlineMedium = TextStyle(fontFamily = SoraFamily, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TechFixTextPrimary),
    headlineSmall = TextStyle(fontFamily = SoraFamily, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TechFixTextPrimary),
    titleLarge = TextStyle(fontFamily = SoraFamily, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TechFixTextPrimary),
    titleMedium = TextStyle(fontFamily = SoraFamily, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TechFixTextPrimary),
    titleSmall = TextStyle(fontFamily = SoraFamily, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TechFixTextPrimary),
    bodyLarge = TextStyle(fontFamily = SoraFamily, fontSize = 15.sp, fontWeight = FontWeight.Normal, color = TechFixTextPrimary),
    bodyMedium = TextStyle(fontFamily = SoraFamily, fontSize = 13.sp, fontWeight = FontWeight.Normal, color = TechFixTextSecondary),
    bodySmall = TextStyle(fontFamily = SoraFamily, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TechFixTextTertiary),
    labelLarge = TextStyle(fontFamily = SoraFamily, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TechFixTextPrimary),
    labelMedium = TextStyle(fontFamily = SoraFamily, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TechFixTextSecondary),
    labelSmall = TextStyle(fontFamily = SoraFamily, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TechFixTextTertiary),
)
