package com.example.techfix.ui.onboarding.professional

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Professional onboarding — final step (profile ready / publish).
 */
@Composable
fun ProProfileReadyScreen(
    fullName: String,
    bio: String,
    specialties: List<String>,
    serviceRadiusKm: Int,
    availabilityLabel: String,
    onEditInfo: () -> Unit,
    onPublish: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize(), color = BackgroundDark) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            CheckBadge()

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Your profile is ready!",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Verified clients can now search and book your tech services immediately. Check your profile preview card:",
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            ProfilePreviewCard(
                fullName = fullName,
                bio = bio,
                specialties = specialties,
                serviceRadiusKm = serviceRadiusKm,
                availabilityLabel = availabilityLabel
            )

            Spacer(modifier = Modifier.weight(1f))

            ProBottomNavBar {
                Surface(
                    modifier = Modifier
                        .height(52.dp)
                        .clip(RoundedCornerShape(26.dp))
                        .clickable(onClick = onEditInfo),
                    color = CardDark,
                    shape = RoundedCornerShape(26.dp),
                    border = BorderStroke(1.dp, CardBorderIdle)
                ) {
                    Box(modifier = Modifier.padding(horizontal = 24.dp), contentAlignment = Alignment.Center) {
                        Text(text = "Edit Info", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    }
                }
                ProPrimaryButton(
                    text = "PUBLISH MY PROFILE",
                    showArrow = false,
                    modifier = Modifier.weight(1f),
                    onClick = onPublish
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun CheckBadge() {
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color(0xFF2A1418))
                .border(BorderStroke(1.5.dp, AccentEnd), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = AccentEnd, modifier = Modifier.size(26.dp))
        }
    }
}

@Composable
private fun ProfilePreviewCard(
    fullName: String,
    bio: String,
    specialties: List<String>,
    serviceRadiusKm: Int,
    availabilityLabel: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = CardDark,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, CardBorderIdle)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(AccentGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = fullName.trim().firstOrNull()?.toString() ?: "P",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = fullName, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = "5.0 • Verified Professional", color = AccentEnd, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "ABOUT / BIO", color = TextLabel, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.5.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = bio, color = TextSecondary, fontSize = 13.sp, lineHeight = 18.sp)

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "SPECIALTIES OFFERED", color = TextLabel, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.5.sp)
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                specialties.forEach { specialty ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(ChipBackground)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(text = specialty, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Icon(imageVector = Icons.Outlined.LocationOn, contentDescription = null, tint = AccentEnd, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Within $serviceRadiusKm km", color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(16.dp))
                Icon(imageVector = Icons.Outlined.CalendarToday, contentDescription = null, tint = AccentEnd, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = availabilityLabel, color = TextSecondary, fontSize = 12.sp)
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun ProProfileReadyScreenPreview() {
    MaterialTheme {
        ProProfileReadyScreen(
            fullName = "Carlos Eduardo",
            bio = "Especialista certificado em consertos de computadores de mesa, laptops corporativos e celulares.",
            specialties = listOf("Computer Repair", "Phone Repair", "Wiring"),
            serviceRadiusKm = 15,
            availabilityLabel = "Mon-Fri Availability",
            onEditInfo = {},
            onPublish = {}
        )
    }
}
