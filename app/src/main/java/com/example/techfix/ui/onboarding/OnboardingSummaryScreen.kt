package com.example.techfix.ui.onboarding

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
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ---- Design tokens (same palette used across the onboarding flow) ----
private val BackgroundDark = Color(0xFF121212)
private val CardDark = Color(0xFF1C1C1E)
private val CardBorderIdle = Color(0xFF2C2C2E)
private val ChipBackground = Color(0xFF2A2A2C)
private val AccentStart = Color(0xFFFF8A3D) // orange
private val AccentEnd = Color(0xFFFF3D68)   // pink/red
private val TextPrimary = Color(0xFFFFFFFF)
private val TextSecondary = Color(0xFFA0A0A5)
private val TextLabel = Color(0xFF8E8E93)

private val AccentGradient = Brush.horizontalGradient(listOf(AccentStart, AccentEnd))

/**
 * Final onboarding step: recap of everything the user chose
 * (interests + matching priority) before entering the app.
 */
@Composable
fun OnboardingSummaryScreen(
    selectedInterests: List<String>,
    matchingPriorityLabel: String,
    totalSteps: Int = 3,
    onStartExploring: () -> Unit,
    onBackToPreferences: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize(), color = BackgroundDark) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            StepProgressBar(totalSteps = totalSteps, currentStep = totalSteps)

            Spacer(modifier = Modifier.height(32.dp))

            CheckBadge()

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "All set!",
                color = TextPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Your customization profile has been securely recorded. You're ready to find top verified specialists.",
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            SummaryCard(
                selectedInterests = selectedInterests,
                matchingPriorityLabel = matchingPriorityLabel
            )

            Spacer(modifier = Modifier.weight(1f))

            PrimaryGradientButton(
                text = "START EXPLORING",
                icon = Icons.Default.ArrowForward,
                onClick = onStartExploring
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Back to Preferences",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onBackToPreferences)
                    .padding(vertical = 12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun StepProgressBar(totalSteps: Int, currentStep: Int) {
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
private fun CheckBadge() {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(Color(0xFF2A1418))
            .border(BorderStroke(1.5.dp, AccentEnd), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = AccentEnd,
            modifier = Modifier.size(30.dp)
        )
    }
}

@Composable
private fun SummaryCard(
    selectedInterests: List<String>,
    matchingPriorityLabel: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = CardDark,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, CardBorderIdle)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2A1418)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🛡", fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "ONBOARDING SUMMARY",
                    color = AccentEnd,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "SELECTED INTERESTS",
                color = TextLabel,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            InterestChipsRow(interests = selectedInterests)

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "PRIMARY MATCHING PRIORITY",
                color = TextLabel,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.WorkspacePremium,
                    contentDescription = null,
                    tint = AccentEnd,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = matchingPriorityLabel,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun InterestChipsRow(interests: List<String>) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        interests.forEach { interest ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(ChipBackground)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = interest,
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun PrimaryGradientButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(AccentGradient)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun OnboardingSummaryScreenPreview() {
    MaterialTheme {
        OnboardingSummaryScreen(
            selectedInterests = listOf("Computers", "Phones", "Plumbing"),
            matchingPriorityLabel = "Highest Rated Professionals first",
            onStartExploring = {},
            onBackToPreferences = {}
        )
    }
}
