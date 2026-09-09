package com.example.techfix.ui.onboarding.professional

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ---- Shared design tokens for the professional onboarding flow ----
val BackgroundDark = Color(0xFF121212)
val CardDark = Color(0xFF1C1C1E)
val CardBorderIdle = Color(0xFF2C2C2E)
val ChipBackground = Color(0xFF2A2A2C)
val AccentStart = Color(0xFFFF8A3D) // orange
val AccentEnd = Color(0xFFFF3D68)   // pink/red
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFA0A0A5)
val TextLabel = Color(0xFF8E8E93)

val AccentGradient = Brush.horizontalGradient(listOf(AccentStart, AccentEnd))

@Composable
fun ProStepProgressBar(totalSteps: Int, currentStep: Int) {
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
fun ProStepHeader(step: Int, totalSteps: Int, title: String, subtitle: String) {
    Text(
        text = "STEP $step OF $totalSteps",
        color = AccentEnd,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.sp
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = title,
        color = TextPrimary,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 30.sp
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = subtitle,
        color = TextSecondary,
        fontSize = 13.sp,
        lineHeight = 18.sp
    )
}

@Composable
fun ProBackButton(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .height(52.dp)
            .clip(RoundedCornerShape(26.dp))
            .clickable(onClick = onClick),
        color = CardDark,
        shape = RoundedCornerShape(26.dp),
        border = BorderStroke(1.dp, CardBorderIdle)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Back", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun ProPrimaryButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    showArrow: Boolean = true,
    onClick: () -> Unit
) {
    val backgroundModifier = if (enabled) Modifier.background(AccentGradient)
    else Modifier.background(Color(0xFF3A3A3C))

    Box(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(26.dp))
            .then(backgroundModifier)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (showArrow) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(text = text, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun ProBottomNavBar(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

/** A 2-column category tile with an icon, label, and a small dot badge when selected. */
@Composable
fun CategoryTile(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (selected) AccentEnd else CardBorderIdle
    val iconTint = if (selected) AccentEnd else TextSecondary

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        color = CardDark,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.5.dp, borderColor)
    ) {
        Box(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = label, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
            if (selected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(AccentEnd)
                )
            }
        }
    }
}

/** A rounded pill used for multi-select specialty chips (with optional checkmark). */
@Composable
fun SpecialtyChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (selected) AccentEnd else CardBorderIdle
    val backgroundColor = if (selected) Color(0xFF2A1418) else ChipBackground
    val textColor = if (selected) TextPrimary else TextSecondary

    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        color = backgroundColor,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.5.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            if (selected) {
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Check,
                    contentDescription = null,
                    tint = AccentEnd,
                    modifier = Modifier.size(13.dp)
                )
            }
        }
    }
}
