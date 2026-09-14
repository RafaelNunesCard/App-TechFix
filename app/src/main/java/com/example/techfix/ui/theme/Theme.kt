package com.example.techfix.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Paleta baseada no protótipo Figma (IBM - TechFix)
val TechFixBackground = Color(0xFF121212)
val TechFixSurface = Color(0xFF1E1E1E)
val TechFixSurfaceVariant = Color(0xFF232326)
val TechFixChipBackground = Color(0xFF2A2A2D)

val TechFixAccentStart = Color(0xFFFF6B4D)
val TechFixAccentEnd = Color(0xFFFF4D6D)
val TechFixAccentSolid = Color(0xFFFF5A45)

val TechFixGreen = Color(0xFF34C759)
val TechFixGreenBg = Color(0xFF16321F)

val TechFixTextPrimary = Color(0xFFFFFFFF)
val TechFixTextSecondary = Color(0xFFA0A0A5)
val TechFixTextTertiary = Color(0xFF6E6E73)
val TechFixDivider = Color(0xFF2C2C2E)

private val TechFixDarkColorScheme = darkColorScheme(
    background = TechFixBackground,
    surface = TechFixSurface,
    surfaceVariant = TechFixSurfaceVariant,
    primary = TechFixAccentSolid,
    onPrimary = Color.White,
    onBackground = TechFixTextPrimary,
    onSurface = TechFixTextPrimary,
    onSurfaceVariant = TechFixTextSecondary,
    outline = TechFixDivider,
)

val TechFixTypography = Typography(
    headlineSmall = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TechFixTextPrimary),
    titleLarge = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TechFixTextPrimary),
    titleMedium = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TechFixTextPrimary),
    bodyLarge = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal, color = TechFixTextPrimary),
    bodyMedium = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal, color = TechFixTextSecondary),
    bodySmall = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TechFixTextTertiary),
    labelLarge = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TechFixTextPrimary),
)

@Composable
fun TechFixTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = TechFixDarkColorScheme,
        typography = TechFixTypography,
        content = content
    )
}
