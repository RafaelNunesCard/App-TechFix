package com.example.techfix.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Raios de canto do Figma: 10 (ícones), 16 (cards), 20 (cards grandes), pílula (botões)
val TechFixShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

// Botões em formato de pílula
val TechFixPillShape = RoundedCornerShape(percent = 50)
