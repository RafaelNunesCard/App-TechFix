package com.example.techfix.ui.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Handyman
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ---- Design tokens (matched to the Figma dark theme) ----
private val BackgroundDark = Color(0xFF121212)
private val CardDark = Color(0xFF1C1C1E)
private val CardBorderIdle = Color(0xFF2C2C2E)
private val AccentStart = Color(0xFFFF8A3D) // orange
private val AccentEnd = Color(0xFFFF3D68)   // pink/red
private val TextPrimary = Color(0xFFFFFFFF)
private val TextSecondary = Color(0xFFA0A0A5)

private val AccentGradient = Brush.horizontalGradient(listOf(AccentStart, AccentEnd))

enum class UserPathway {
    NEEDS_SERVICE,
    OFFERS_SERVICE
}

/**
 * Onboarding screen where the user chooses whether they want to
 * request a service or offer their professional services on TechFix.
 */
@Composable
fun PathwaySelectionScreen(
    onContinue: (UserPathway) -> Unit
) {
    var selectedPathway by remember { mutableStateOf<UserPathway?>(UserPathway.NEEDS_SERVICE) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Logo + app name
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(AccentGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Build,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "TechFix",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "How do you want to use TechFix?",
                color = TextPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 34.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Choose your pathway. You can always change your profile preferences or register as a professional later.",
                color = TextSecondary,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            PathwayCard(
                icon = Icons.Outlined.Handyman,
                title = "I need a service",
                description = "Find verified local specialists for computer repair, technical network setups, plumbing, and more.",
                selected = selectedPathway == UserPathway.NEEDS_SERVICE,
                onClick = { selectedPathway = UserPathway.NEEDS_SERVICE }
            )

            Spacer(modifier = Modifier.height(16.dp))

            PathwayCard(
                icon = Icons.Outlined.Build,
                title = "I want to offer my services",
                description = "Get client requests, showcase your portfolio, and expand your service business securely on the platform.",
                selected = selectedPathway == UserPathway.OFFERS_SERVICE,
                onClick = { selectedPathway = UserPathway.OFFERS_SERVICE }
            )

            Spacer(modifier = Modifier.weight(1f))

            ContinueButton(
                enabled = selectedPathway != null,
                onClick = { selectedPathway?.let(onContinue) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Home indicator spacing (matches the Figma bottom bar)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFF3A3A3C))
                )
            }
        }
    }
}

@Composable
private fun PathwayCard(
    icon: ImageVector,
    title: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (selected) AccentEnd else CardBorderIdle

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        color = CardDark,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF2A2A2C)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            SelectionIndicator(selected = selected)
        }
    }
}

@Composable
private fun SelectionIndicator(selected: Boolean) {
    val baseModifier = Modifier
        .size(22.dp)
        .clip(CircleShape)

    if (selected) {
        Box(
            modifier = baseModifier.background(AccentEnd),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = Color.White,
                modifier = Modifier.size(13.dp)
            )
        }
    } else {
        Box(
            modifier = baseModifier
                .background(Color.Transparent)
                .then(Modifier),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .border(BorderStroke(1.5.dp, CardBorderIdle), CircleShape)
            )
        }
    }
}

@Composable
private fun ContinueButton(
    enabled: Boolean,
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
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp))
            .then(backgroundModifier)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Continue",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun PathwaySelectionScreenPreview() {
    MaterialTheme {
        PathwaySelectionScreen(onContinue = {})
    }
}
