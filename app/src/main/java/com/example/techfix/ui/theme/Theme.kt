package com.example.techfix.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val TechFixDarkColorScheme = darkColorScheme(
    background = TechFixBackground,
    surface = TechFixSurface,
    surfaceVariant = TechFixSurfaceVariant,
    surfaceContainerLowest = TechFixBackground,
    surfaceContainerLow = TechFixSurface,
    surfaceContainer = TechFixSurface,
    surfaceContainerHigh = TechFixSurfaceVariant,
    surfaceContainerHighest = TechFixChipBackground,
    primary = TechFixAccentSolid,
    onPrimary = Color.White,
    secondary = TechFixGreen,
    error = TechFixRed,
    onBackground = TechFixTextPrimary,
    onSurface = TechFixTextPrimary,
    onSurfaceVariant = TechFixTextSecondary,
    outline = TechFixBorder,
    outlineVariant = TechFixBorder,
)

/**
 * Tema único do app. A MainActivity (e os @Preview) usam ESTA função.
 * Cores ficam em Color.kt, fonte e estilos em Type.kt, cantos em Shape.kt.
 */
@Composable
fun TechFixTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = TechFixDarkColorScheme,
        typography = TechFixTypography,
        shapes = TechFixShapes,
        content = content
    )
}
