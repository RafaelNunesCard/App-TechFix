package com.example.techfix.ui.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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

data class ServiceCategory(
    val id: String,
    val label: String,
    val icon: ImageVector
)

private val allCategories = listOf(
    ServiceCategory("computers", "Computers", Icons.Outlined.Computer),
    ServiceCategory("phones", "Phones", Icons.Outlined.Smartphone),
    ServiceCategory("wifi", "Wi-Fi & Networks", Icons.Outlined.Wifi),
    ServiceCategory("electrical", "Electrical", Icons.Outlined.Bolt),
    ServiceCategory("plumbing", "Plumbing", Icons.Outlined.Plumbing),
    ServiceCategory("maintenance", "Maintenance", Icons.Outlined.Build),
    ServiceCategory("painting", "Painting", Icons.Outlined.FormatPaint),
    ServiceCategory("photography", "Photography", Icons.Outlined.CameraAlt)
)

/**
 * Onboarding step 1 of 3: category selection.
 * Lets the user multi-select the service categories they're interested in.
 */
@Composable
fun CategorySelectionScreen(
    totalSteps: Int = 3,
    currentStep: Int = 1,
    onBack: () -> Unit,
    onContinue: (Set<String>) -> Unit
) {
    var selectedIds by remember { mutableStateOf(setOf("computers", "phones", "plumbing")) }

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
                text = "What are you looking for?",
                color = TextPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 32.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Select the categories that interest you. We'll curate the top technicians and professionals in these zones.",
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(allCategories) { category ->
                    CategoryChip(
                        category = category,
                        selected = category.id in selectedIds,
                        onClick = {
                            selectedIds = if (category.id in selectedIds) {
                                selectedIds - category.id
                            } else {
                                selectedIds + category.id
                            }
                        }
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
                    enabled = selectedIds.isNotEmpty(),
                    modifier = Modifier.weight(1f),
                    onClick = { onContinue(selectedIds) }
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
private fun CategoryChip(
    category: ServiceCategory,
    selected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (selected) AccentEnd else CardBorderIdle
    val iconColor = if (selected) AccentEnd else TextSecondary

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        color = CardDark,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.5.dp, borderColor)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = category.label,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            if (selected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(AccentEnd)
                )
            }
        }
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
private fun CategorySelectionScreenPreview() {
    MaterialTheme {
        CategorySelectionScreen(onBack = {}, onContinue = {})
    }
}
