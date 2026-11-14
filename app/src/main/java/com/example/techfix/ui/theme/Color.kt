package com.example.techfix.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// CORES DO TECHFIX (valores do Figma). É AQUI que se muda uma cor:
// as telas devem usar estes nomes e nunca um Color(0x...) solto.

// ---- Fundos ----
val TechFixBackground = Color(0xFF0D0C10)        // fundo das telas
val TechFixSurface = Color(0xFF16151B)           // cards
val TechFixSurfaceVariant = Color(0xFF1D1C23)    // campos de texto, áreas em destaque
val TechFixChipBackground = Color(0xFF211F28)    // chips e botões secundários

// Mantido porque ProdutoScreen e ServicosScreen usam este nome
val BackColor = TechFixBackground

// ---- Destaque (coral) ----
val TechFixAccentStart = Color(0xFFFF5F56)
val TechFixAccentEnd = Color(0xFFFF8A75)
val TechFixAccentSolid = Color(0xFFFF5F56)

// Gradiente dos botões principais
val TechFixAccentBrush = Brush.horizontalGradient(
    colors = listOf(TechFixAccentStart, TechFixAccentEnd)
)

// ---- Estados ----
val TechFixGreen = Color(0xFF4ADE80)             // sucesso / concluído
val TechFixGreenBg = Color(0x1F4ADE80)           // fundo de selo verde (12%)
val TechFixWarning = Color(0xFFFACC15)           // aguardando
val TechFixWarningBg = Color(0x1FFACC15)
val TechFixRed = Color(0xFFF87171)               // erro / recusar
val TechFixRedBg = Color(0x1FF87171)

// ---- Textos ----
val TechFixTextPrimary = Color(0xFFFFFFFF)
val TechFixTextSecondary = Color(0xA6FFFFFF)     // branco 65%
val TechFixTextTertiary = Color(0x80FFFFFF)      // branco 50%

// ---- Linhas e bordas ----
val TechFixBorder = Color(0x14FFFFFF)            // branco 8%, borda dos cards
val TechFixDivider = Color(0x14FFFFFF)
