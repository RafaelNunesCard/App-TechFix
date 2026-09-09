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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ---- Design tokens (shared with the rest of the TechFix onboarding flow) ----
private val BackgroundDark = Color(0xFF121212)
private val CardDark = Color(0xFF1C1C1E)
private val CardBorderIdle = Color(0xFF2C2C2E)
private val AccentStart = Color(0xFFFF8A3D) // orange
private val AccentEnd = Color(0xFFFF3D68)   // pink/red
private val TextPrimary = Color(0xFFFFFFFF)
private val TextSecondary = Color(0xFFA0A0A5)

private val AccentGradient = Brush.horizontalGradient(listOf(AccentStart, AccentEnd))

data class MatchPriority(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector
)

private val allPriorities = listOf(
    MatchPriority(
        id = "fastest_response",
        title = "Fastest Response",
        description = "Prioritize specialists with immediate availabilities.",
        icon = Icons.Outlined.Bolt
    ),
    MatchPriority(
        id = "best_price",
        title = "Best Price Match",
        description = "Sort options based on competitive pricing and offers.",
        icon = Icons.Outlined.Sell
    ),
    MatchPriority(
        id = "highest_rated",
        title = "Highest Rated",
        description = "Filter strictly by certified professionals with 4.8+ ratings.",
        icon = Icons.Outlined.WorkspacePremium
    ),
    MatchPriority(
        id = "nearest_distance",
        title = "Nearest Distance",
        description = "Discover physical proximity helpers located nearby.",
        icon = Icons.Outlined.LocationOn
    )
)

/**
 * Onboarding step 2 of 3: single-select matching priority.
 * Lets the user choose the algorithm TechFix should optimize for
 * when ranking professionals.
 */
@Composable
fun MatchPriorityScreen(
    totalSteps: Int = 3,
    currentStep: Int = 2,
    onBack: () -> Unit,
    onContinue: (String) -> Unit
) {
    var selectedId by remember { mutableStateOf("highest_rated") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            StepProgressBar(totalSteps = totalSteps, currentStep = currentStep)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "STEP $currentStep OF $totalSteps",
                color = AccentEnd,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "How do you prefer to find help?",
                color = TextPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 32.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tell us your priority algorithm so we can sort and match the best fit for your specific lifestyle.",
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                allPriorities.forEach { priority ->
                    PriorityRow(
                        priority = priority,
                        selected = priority.id == selectedId,
                        onClick = { selectedId = priority.id }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BackButton(onClick = onBack)

                ContinueButton(
                    enabled = selectedId.isNotEmpty(),
                    modifier = Modifier.weight(1f),
                    onClick = { onContinue(selectedId) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
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

            Spacer(modifier = Modifier.height(12.dp))
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
private fun PriorityRow(
    priority: MatchPriority,
    selected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (selected) AccentEnd else CardBorderIdle
    val iconTint = if (selected) AccentEnd else TextSecondary

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
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF2A2A2C)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = priority.icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = priority.title,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = priority.description,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            RadioIndicator(selected = selected)
        }
    }
}

@Composable
private fun RadioIndicator(selected: Boolean) {
    val ringModifier = Modifier
        .size(20.dp)
        .clip(CircleShape)

    if (selected) {
        Box(
            modifier = ringModifier.background(AccentEnd),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            )
        }
    } else {
        Box(
            modifier = ringModifier.border(BorderStroke(1.5.dp, CardBorderIdle), CircleShape)
        )
    }
}

@Composable
private fun BackButton(onClick: () -> Unit) {
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
            Text(
                text = "Back",
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ContinueButton(
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val backgroundModifier = if (enabled) {
        Modifier.background(AccentGradient)
    } else {
        Modifier.background(Color(0xFF3A3A3C))
    }

    Box(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(26.dp))
            .then(backgroundModifier)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Continue",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun MatchPriorityScreenPreview() {
    MaterialTheme {
        MatchPriorityScreen(onBack = {}, onContinue = {})
    }
}
