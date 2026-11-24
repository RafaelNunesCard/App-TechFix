package com.example.techfix.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.techfix.R
import com.example.techfix.data.local.ServiceReview
import com.example.techfix.model.SampleData
import com.example.techfix.model.UserProfile
import com.example.techfix.ui.components.*
import com.example.techfix.ui.theme.*
import kotlin.math.roundToInt

/**
 * Tela de Perfil do Cliente (mobile-customer-profile), replicando o protótipo Figma
 * "IBM - TechFix". Recebe callbacks para as ações principais, deixando a tela "burra"
 * (sem lógica de navegação/negócio embutida).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    profile: UserProfile = SampleData.carlosProfile,
    isProfessional: Boolean = true,
    onMenuClick: () -> Unit = {},
    onEditProfileClick: () -> Unit = {},
    onRequestServiceClick: () -> Unit = {},
    onSeeAllHistoryClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onOpenDbViewer: () -> Unit = {},
    onNavItemSelected: (HomeNavItem) -> Unit = {},
    receivedReviews: List<ServiceReview> = emptyList()
) {
    // Toque 5x seguidos na logo abre a tela do banco de dados (para a demonstração).
    val logoTaps = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(0) }
    val lastTapAt = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(0L) }
    Scaffold(
        containerColor = TechFixBackground,
        bottomBar = { TechFixBottomNav(selected = HomeNavItem.PROFILE, onItemSelected = onNavItemSelected) },
        topBar = {
            TechFixTopBar(
                onMenuClick = onMenuClick,
                onLogoClick = {
                    val now = System.currentTimeMillis()
                    logoTaps.value = if (now - lastTapAt.value < 800) logoTaps.value + 1 else 1
                    lastTapAt.value = now
                    if (logoTaps.value >= 5) {
                        logoTaps.value = 0
                        onOpenDbViewer()
                    }
                }
            )
        }
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

            if (isProfessional) {
                item { StatsRow(profile.completedServices, receivedReviews) }
                item {
                    TechFixCard {
                        Text(
                            "Sobre Mim",
                            style = MaterialTheme.typography.titleLarge,
                            color = TechFixTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(profile.bio, style = MaterialTheme.typography.bodyLarge, color = TechFixTextSecondary)
                    }
                }
            }

            if (isProfessional && profile.interestCategories.isNotEmpty()) {
                item {
                    TechFixCard {
                        Text(
                            "Categorias de interesse",
                            style = MaterialTheme.typography.titleLarge,
                            color = TechFixTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(12.dp))
                        FlowRowChips(items = profile.interestCategories)
                    }
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
                ConfidenceCard(isProfessional, profile.loyaltyLabel, receivedReviews)
            }

            if (isProfessional) {
                item {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Histórico de Serviços",
                            style = MaterialTheme.typography.titleLarge,
                            color = TechFixTextPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
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
            }

            item {
                OutlinedButton(
                    onClick = onLogoutClick,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(50),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TechFixRed),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TechFixRed)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Sair da conta", fontWeight = FontWeight.SemiBold)
                }
            }

        }
    }
}

@Composable
internal fun ProfileMenuDialog(
    onDismissRequest: () -> Unit,
    onNotificationsClick: () -> Unit,
    onPaymentClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = TechFixSurface,
            border = BorderStroke(1.dp, TechFixBorder)
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Menu",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.headlineSmall,
                        color = TechFixTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = onDismissRequest,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = TechFixChipBackground,
                            contentColor = TechFixTextSecondary
                        )
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = "Fechar menu")
                    }
                }

                ProfileMenuOption("Notificações", Icons.Outlined.Notifications, onNotificationsClick)
                ProfileMenuOption("Formas de pagamento", Icons.Outlined.CreditCard, onPaymentClick)

                HorizontalDivider(Modifier.padding(top = 4.dp), color = TechFixDivider)
                TextButton(
                    onClick = onLogoutClick,
                    modifier = Modifier.align(Alignment.Start),
                    colors = ButtonDefaults.textButtonColors(contentColor = TechFixRed)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Sair da conta", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun ProfileMenuOption(label: String, icon: ImageVector, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = TechFixSurfaceVariant,
        border = BorderStroke(1.dp, TechFixBorder)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(36.dp)
                    .background(TechFixChipBackground, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = TechFixAccentSolid, modifier = Modifier.size(20.dp))
            }
            Text(
                label,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                color = TechFixTextPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Icon(
                Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = TechFixTextTertiary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TechFixTopBar(onMenuClick: () -> Unit, onLogoClick: () -> Unit) {
    TopAppBar(
        title = {
            Image(
                painter = painterResource(R.drawable.logo_techfix),
                contentDescription = "TechFix",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .height(34.dp)
                    .clickable(
                        interactionSource = androidx.compose.runtime.remember {
                            androidx.compose.foundation.interaction.MutableInteractionSource()
                        },
                        indication = null,
                        onClick = onLogoClick
                    )
            )
        },
        actions = {
            OutlinedButton(
                onClick = onMenuClick,
                modifier = Modifier.padding(end = 16.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = TechFixSurfaceVariant,
                    contentColor = TechFixTextPrimary
                ),
                border = BorderStroke(1.dp, TechFixBorder),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Icon(
                    Icons.Filled.Menu,
                    contentDescription = null,
                    tint = TechFixAccentSolid,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text("Menu", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
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
                model = profile.avatarUrl.ifBlank { R.drawable.placeholder_perfil },
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
private fun ConfidenceCard(isProfessional: Boolean, newAccountLabel: String, reviews: List<ServiceReview>) {
    TechFixCard {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                "Indicador de Confiança",
                style = MaterialTheme.typography.titleLarge,
                color = TechFixTextPrimary,
                fontWeight = FontWeight.Bold
            )
            if (reviews.isEmpty()) {
                if (isProfessional) {
                    Surface(color = TechFixGreenBg, shape = RoundedCornerShape(50)) {
                        Text(
                            newAccountLabel,
                            color = TechFixGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
                Text("Nenhuma avaliação recebida ainda.", color = TechFixTextPrimary)
                Text(
                    if (isProfessional) "Clientes podem avaliar seu atendimento após um pedido concluído."
                    else "Profissionais podem avaliar você após um pedido concluído.",
                    color = TechFixTextSecondary
                )
            } else {
                ReviewSummary(reviews)
                ReviewListButton(reviews, title = "Avaliações recebidas", label = "Ver avaliações recebidas")
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StatsRow(completedServices: Int, reviews: List<ServiceReview>) {
    val average = if (reviews.isEmpty()) null else reviews.map { it.stars }.average()
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val spacing = 10.dp
        val minWidth = (92 * LocalDensity.current.fontScale.coerceAtLeast(1f)).dp
        val columns = when {
            maxWidth >= minWidth * 3 + spacing * 2 -> 3
            maxWidth >= minWidth * 2 + spacing -> 2
            else -> 1
        }
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalArrangement = Arrangement.spacedBy(spacing),
            maxItemsInEachRow = columns
        ) {
            StatPill(
                icon = Icons.Filled.CheckCircle,
                iconTint = TechFixGreen,
                label = "Concluídos",
                value = completedServices.toString(),
                modifier = Modifier.weight(1f)
            )
            StatPill(
                icon = Icons.Filled.Star,
                iconTint = TechFixWarning,
                label = "Avaliação",
                value = average?.let { "${formatReviewScore(it)} ★" } ?: "—",
                modifier = Modifier.weight(1f)
            )
            StatPill(
                icon = Icons.Filled.Shield,
                iconTint = TechFixAccentSolid,
                label = "Confiança",
                value = average?.let { "${(it * 20).roundToInt()}%" } ?: "—",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlowRowChips(items: List<String>) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEach { InterestChip(it) }
    }
}
