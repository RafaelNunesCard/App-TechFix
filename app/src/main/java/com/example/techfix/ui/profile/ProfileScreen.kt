package com.example.techfix.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.techfix.R
import com.example.techfix.model.SampleData
import com.example.techfix.model.UserProfile
import com.example.techfix.ui.components.*
import com.example.techfix.ui.theme.*

/**
 * Tela de Perfil do Cliente (mobile-customer-profile), replicando o protótipo Figma
 * "IBM - TechFix". Recebe callbacks para as ações principais, deixando a tela "burra"
 * (sem lógica de navegação/negócio embutida).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    profile: UserProfile = SampleData.carlosProfile,
    onMenuClick: () -> Unit = {},
    onEditProfileClick: () -> Unit = {},
    onRequestServiceClick: () -> Unit = {},
    onSeeAllHistoryClick: () -> Unit = {},
) {
    Scaffold(
        containerColor = TechFixBackground,
        topBar = { TechFixTopBar(onMenuClick = onMenuClick) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item { Spacer(Modifier.height(4.dp)) }

            item {
                ProfileHeaderCard(
                    profile = profile,
                    onEditProfileClick = onEditProfileClick,
                    onRequestServiceClick = onRequestServiceClick
                )
            }

            item { StatsRow(profile = profile) }

            item {
                TechFixCard {
                    Text("Sobre Mim", style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text(profile.bio, style = MaterialTheme.typography.bodyLarge, color = Color.White.copy(alpha = 0.7f))
                }
            }

            item {
                TechFixCard {
                    Text("Categorias de Interesse", style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    FlowRowChips(items = profile.interestCategories)
                }
            }

            item {
                TechFixCard {
                    Text("Preferências de Atendimento", style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    profile.preferences.forEach { PreferenceRow(it) }
                }
            }

            item {
                TechFixCard {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Indicador de Confiança", style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
                        Surface(color = TechFixGreenBg, shape = RoundedCornerShape(50)) {
                            Text(
                                profile.loyaltyLabel,
                                color = TechFixGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Fidelidade", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.6f))
                        Text("${profile.loyaltyPercent}%", style = MaterialTheme.typography.labelLarge, color = Color.White)
                    }
                    Spacer(Modifier.height(6.dp))
                    TechFixProgressBar(progress = profile.loyaltyPercent)
                }
            }

            item {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Histórico de Serviços", style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
                    Text(
                        "Ver todos",
                        color = TechFixAccentSolid,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        modifier = Modifier.clickable(onClick = onSeeAllHistoryClick)
                    )
                }
            }

            items(profile.serviceHistory) { item ->
                ServiceHistoryCard(item = item)
            }

            item {
                Spacer(Modifier.height(8.dp))
                Text(
                    "TechFix • Suporte Especializado • CNPJ 12.345.678/0001-90",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TechFixTopBar(onMenuClick: () -> Unit) {
    TopAppBar(
        title = {
            Image(
                painter = painterResource(R.drawable.logo_techfix),
                contentDescription = "TechFix",
                contentScale = ContentScale.Fit,
                modifier = Modifier.height(34.dp)
            )
        },
        actions = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = TechFixTextPrimary)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = TechFixBackground)
    )
}

@Composable
private fun ProfileHeaderCard(
    profile: UserProfile,
    onEditProfileClick: () -> Unit,
    onRequestServiceClick: () -> Unit
) {
    TechFixCard {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = profile.avatarUrl,
                contentDescription = "Foto de perfil",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(84.dp)
                    .clip(CircleShape)
                    .background(TechFixSurfaceVariant)
            )

            Spacer(Modifier.height(12.dp))
            Text(profile.name, style = MaterialTheme.typography.headlineSmall, color = Color.White, fontWeight = FontWeight.Bold)

            if (profile.isVerified) {
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = TechFixGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Perfil Verificado", color = TechFixGreen, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = TechFixTextTertiary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    "${profile.location}  •  ${profile.memberSince}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.55f)
                )
            }

            Spacer(Modifier.height(16.dp))
            OutlinedButton(
                onClick = onEditProfileClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50)
            ) {
                Text("Editar Perfil", color = Color.White)
            }

            Spacer(Modifier.height(10.dp))
            Button(
                onClick = onRequestServiceClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = TechFixAccentSolid)
            ) {
                Text("Solicitar Serviço")
                Spacer(Modifier.width(6.dp))
                Icon(Icons.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun StatsRow(profile: UserProfile) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatPill(
            icon = Icons.Filled.CheckCircle,
            iconTint = TechFixGreen,
            label = "Concluídos",
            value = profile.completedServices.toString(),
            modifier = Modifier.weight(1f)
        )
        StatPill(
            icon = Icons.Filled.Star,
            iconTint = Color(0xFFE0C441),
            label = "Avaliação",
            value = "${profile.rating} ★",
            modifier = Modifier.weight(1f)
        )
        StatPill(
            icon = Icons.Filled.Shield,
            iconTint = TechFixAccentSolid,
            label = "Confiança",
            value = "${profile.confidencePercent}%",
            modifier = Modifier.weight(1f)
        )
    }
}

/** Simula um FlowRow (quebra de linha automática) sem depender da lib experimental. */
@Composable
private fun FlowRowChips(items: List<String>, itemsPerRow: Int = 3) {
    val chunks = items.chunked(itemsPerRow)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        chunks.forEach { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowItems.forEach { InterestChip(it) }
            }
        }
    }
}