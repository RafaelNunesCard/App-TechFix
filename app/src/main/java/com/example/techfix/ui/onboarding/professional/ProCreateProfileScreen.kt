package com.example.techfix.ui.onboarding.professional

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.CameraAlt
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

data class ProProfileInfo(
    val fullName: String,
    val shortBio: String,
    val portfolioItemCount: Int
)

/**
 * Professional onboarding — step 4 of 5.
 * Photo, name, short bio/pitch, and portfolio items.
 *
 * NOTE: photo upload and portfolio image picking are left as TODOs —
 * wire them to your image picker / storage of choice (e.g. Photo Picker API + Firebase Storage).
 */
@Composable
fun ProCreateProfileScreen(
    totalSteps: Int = 5,
    currentStep: Int = 4,
    onBack: () -> Unit,
    onContinue: (ProProfileInfo) -> Unit,
    onUploadPhoto: () -> Unit = {},
    onAddPortfolioItem: () -> Unit = {}
) {
    var fullName by remember { mutableStateOf("") }
    var shortBio by remember { mutableStateOf("") }
    var portfolioItemCount by remember { mutableStateOf(0) }

    val canContinue = fullName.isNotBlank() && shortBio.isNotBlank()

    Surface(modifier = Modifier.fillMaxSize(), color = BackgroundDark) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
            Spacer(modifier = Modifier.height(24.dp))
            ProStepProgressBar(totalSteps = totalSteps, currentStep = currentStep)
            Spacer(modifier = Modifier.height(16.dp))
            ProStepHeader(
                step = currentStep,
                totalSteps = totalSteps,
                title = "Create your profile",
                subtitle = "How should clients see you? Build your professional presence below with a real photo."
            )
            Spacer(modifier = Modifier.height(20.dp))

            Surface(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                color = CardDark,
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, CardBorderIdle)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onUploadPhoto)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(ChipBackground)
                                .border(BorderStroke(1.dp, CardBorderIdle), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CameraAlt,
                                contentDescription = null,
                                tint = AccentEnd,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = "Upload Profile Photo", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text(text = "JPG or PNG, max 5MB.", color = TextLabel, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    FieldLabel(text = "Full Name")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Digite seu nome completo", color = TextSecondary.copy(alpha = 0.6f), fontSize = 13.sp) },
                        singleLine = true,
                        colors = proFieldColors()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    FieldLabel(text = "Short Biography / Pitch")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = shortBio,
                        onValueChange = { shortBio = it },
                        modifier = Modifier.fillMaxWidth().height(96.dp),
                        placeholder = {
                            Text(
                                "Técnico especializado em manutenção de computadores e smartphones com mais de...",
                                color = TextSecondary.copy(alpha = 0.6f),
                                fontSize = 13.sp
                            )
                        },
                        colors = proFieldColors()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    FieldLabel(text = "Portfolio Items (Upload Jobs)")
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        repeat(portfolioItemCount) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(ChipBackground)
                                    .border(BorderStroke(1.dp, CardBorderIdle), RoundedCornerShape(10.dp))
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF2A1418))
                                .border(BorderStroke(1.5.dp, AccentEnd), RoundedCornerShape(10.dp))
                                .clickable {
                                    portfolioItemCount++
                                    onAddPortfolioItem()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Adicionar item", tint = AccentEnd)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ProBottomNavBar {
                ProBackButton(onClick = onBack)
                ProPrimaryButton(
                    text = "Continue",
                    enabled = canContinue,
                    modifier = Modifier.weight(1f),
                    onClick = { onContinue(ProProfileInfo(fullName, shortBio, portfolioItemCount)) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(text = text.uppercase(), color = TextLabel, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.5.sp)
}

@Composable
private fun proFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = ChipBackground,
    unfocusedContainerColor = ChipBackground,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    focusedBorderColor = AccentEnd,
    unfocusedBorderColor = CardBorderIdle,
    cursorColor = AccentEnd
)

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun ProCreateProfileScreenPreview() {
    MaterialTheme { ProCreateProfileScreen(onBack = {}, onContinue = {}) }
}
