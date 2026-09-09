package com.example.techfix.ui.onboarding.professional

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val experienceOptions = listOf("1-2 years", "3-5 years", "5-10 years", "10+ years")
private val weekDays = listOf("M", "T", "W", "T", "F", "S", "S")
private val weekDayIds = listOf("mon", "tue", "wed", "thu", "fri", "sat", "sun")

data class ProWorkDetails(
    val experienceLevel: String,
    val serviceRadiusKm: Int,
    val availableDayIds: Set<String>
)

/**
 * Professional onboarding — step 3 of 5.
 * Experience level, max travel distance, and weekly availability.
 */
@Composable
fun ProWorkDetailsScreen(
    totalSteps: Int = 5,
    currentStep: Int = 3,
    onBack: () -> Unit,
    onContinue: (ProWorkDetails) -> Unit
) {
    var experienceLevel by remember { mutableStateOf("3-5 years") }
    var radiusKm by remember { mutableStateOf(15f) }
    var availableDays by remember { mutableStateOf(setOf("mon", "tue", "wed", "thu", "fri")) }

    Surface(modifier = Modifier.fillMaxSize(), color = BackgroundDark) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
            Spacer(modifier = Modifier.height(24.dp))
            ProStepProgressBar(totalSteps = totalSteps, currentStep = currentStep)
            Spacer(modifier = Modifier.height(16.dp))
            ProStepHeader(
                step = currentStep,
                totalSteps = totalSteps,
                title = "Tell us about your work",
                subtitle = "Set your typical experience, working radius, and the days you are available to receive client orders."
            )
            Spacer(modifier = Modifier.height(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                SectionCard(title = "Experience Level") {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        experienceOptions.forEach { option ->
                            ExperiencePill(
                                label = option,
                                selected = option == experienceLevel,
                                onClick = { experienceLevel = option }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                SectionCard(title = "Service Area Radius") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Maximum travel distance", color = TextSecondary, fontSize = 12.sp)
                        Text(
                            text = "${radiusKm.toInt()} km",
                            color = AccentEnd,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Slider(
                        value = radiusKm,
                        onValueChange = { radiusKm = it },
                        valueRange = 1f..50f,
                        colors = SliderDefaults.colors(
                            thumbColor = AccentEnd,
                            activeTrackColor = AccentEnd,
                            inactiveTrackColor = CardBorderIdle
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                SectionCard(title = "Weekly Availability") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        weekDays.forEachIndexed { index, label ->
                            val dayId = weekDayIds[index]
                            DayToggle(
                                label = label,
                                selected = dayId in availableDays,
                                onClick = {
                                    availableDays = if (dayId in availableDays) {
                                        availableDays - dayId
                                    } else {
                                        availableDays + dayId
                                    }
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ProBottomNavBar {
                ProBackButton(onClick = onBack)
                ProPrimaryButton(
                    text = "Continue",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onContinue(ProWorkDetails(experienceLevel, radiusKm.toInt(), availableDays))
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = CardDark,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, CardBorderIdle)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun ExperiencePill(label: String, selected: Boolean, onClick: () -> Unit) {
    val borderColor = if (selected) AccentEnd else CardBorderIdle
    val backgroundColor = if (selected) AccentEnd else ChipBackground
    val textColor = if (selected) Color.White else TextSecondary

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(backgroundColor)
            .border(BorderStroke(1.dp, borderColor), RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(text = label, color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun DayToggle(label: String, selected: Boolean, onClick: () -> Unit) {
    val borderColor = if (selected) AccentEnd else CardBorderIdle
    val textColor = if (selected) AccentEnd else TextSecondary

    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(if (selected) Color(0xFF2A1418) else ChipBackground)
            .border(BorderStroke(1.5.dp, borderColor), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, color = textColor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun ProWorkDetailsScreenPreview() {
    MaterialTheme { ProWorkDetailsScreen(onBack = {}, onContinue = {}) }
}
