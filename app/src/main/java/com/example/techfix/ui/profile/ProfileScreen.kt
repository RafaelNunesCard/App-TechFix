@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.example.techfix.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import com.example.techfix.R
import com.example.techfix.model.ProfileOptions
import com.example.techfix.model.TechFixInfo
import com.example.techfix.model.UserProfile
import com.example.techfix.ui.components.EmptyState
import com.example.techfix.ui.components.InterestChip
import com.example.techfix.ui.components.PreferenceRow
import com.example.techfix.ui.components.ProfileAvatar
import com.example.techfix.ui.components.SectionHeader
import com.example.techfix.ui.components.ServiceHistoryCard
import com.example.techfix.ui.components.StatPill
import com.example.techfix.ui.components.TechFixCard
import com.example.techfix.ui.components.TechFixPrimaryButton
import com.example.techfix.ui.components.TechFixProgressBar
import com.example.techfix.ui.components.TechFixSecondaryButton
import com.example.techfix.ui.components.TechFixWordmark
import com.example.techfix.ui.theme.TechFixAccentBrush
import com.example.techfix.ui.theme.TechFixAccentSolid
import com.example.techfix.ui.theme.TechFixBackground
import com.example.techfix.ui.theme.TechFixDimens
import com.example.techfix.ui.theme.TechFixGreen
import com.example.techfix.ui.theme.TechFixGreenBg
import com.example.techfix.ui.theme.TechFixPillShape
import com.example.techfix.ui.theme.TechFixRed
import com.example.techfix.ui.theme.TechFixSurfaceVariant
import com.example.techfix.ui.theme.TechFixTextPrimary
import com.example.techfix.ui.theme.TechFixTextSecondary
import com.example.techfix.ui.theme.TechFixTextTertiary
import com.example.techfix.ui.theme.TechFixWarning
import java.util.Locale

/**
 * Tela de Perfil (aba "Perfil"). É "burra": recebe o perfil pronto e só avisa dos cliques.
 * O tema (TechFixTheme) vem da MainActivity.
 *
 * Parâmetros novos (todos opcionais, o MainActivity antigo continua compilando):
 * - [onOpenDbViewer]: 5 toques seguidos na logo abrem o visualizador do banco (demonstração).
 * - [isProfessional]: conta de profissional não vê o que é só de cliente
 *   (Solicitar Serviço, categorias, preferências e fidelidade).
 * - [bottomBar]: barra inferior (Início / Explorar / Pedidos / Perfil) passada pelo MainActivity.
 */
@Composable
fun ProfileScreen(
    profile: UserProfile,
    onMenuClick: () -> Unit = {},
    onEditProfileClick: () -> Unit = {},
    onRequestServiceClick: () -> Unit = {},
    onSeeAllHistoryClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onOpenDbViewer: () -> Unit = {},
    isProfessional: Boolean = false,
    bottomBar: @Composable () -> Unit = {},
) {
    // Toque 5x seguidos na logo abre a tela do banco de dados (para a demonstração).
    var logoTaps by remember { mutableStateOf(0) }
    var lastTapAt by remember { mutableStateOf(0L) }
    val logoInteraction = remember { MutableInteractionSource() }
    val onLogoClick: () -> Unit = {
        val now = System.currentTimeMillis()
        logoTaps = if (now - lastTapAt < ProfileOptions.DB_VIEWER_TAP_WINDOW_MS) logoTaps + 1 else 1
        lastTapAt = now
        if (logoTaps >= ProfileOptions.DB_VIEWER_TAPS) {
            logoTaps = 0
            onOpenDbViewer()
        }
    }

    Scaffold(
        containerColor = TechFixBackground,
        topBar = {
            TopAppBar(
                title = {
                    TechFixWordmark(
                        modifier = Modifier.clickable(
                            interactionSource = logoInteraction,
                            indication = null,
                            onClick = onLogoClick
                        )
                    )
                },
                actions = {
                    IconButton(onClick = onMenuClick) {
                        Icon(
                            Icons.Filled.Menu,
                            contentDescription = "Menu",
                            tint = TechFixTextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TechFixBackground,
                    scrolledContainerColor = TechFixBackground
                )
            )
        },
        bottomBar = bottomBar
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(
                start = TechFixDimens.Space16,
                end = TechFixDimens.Space16,
                top = TechFixDimens.Space8,
                bottom = TechFixDimens.Space24
            ),
            verticalArrangement = Arrangement.spacedBy(TechFixDimens.Space16)
        ) {
            item(key = "header") {
                ProfileHeaderCard(
                    profile = profile,
                    isProfessional = isProfessional,
                    onEditProfileClick = onEditProfileClick,
                    onRequestServiceClick = onRequestServiceClick
                )
            }

            item(key = "stats") { StatsRow(profile) }

            item(key = "about") {
                TechFixCard {
                    SectionHeader(
                        title = "Sobre Mim",
                        actionLabel = if (profile.bio.isBlank()) "Adicionar" else "Editar",
                        onAction = onEditProfileClick
                    )
                    Spacer(Modifier.height(TechFixDimens.Space8))
                    if (profile.bio.isBlank()) {
                        Text(
                            "Conte um pouco sobre você e o que espera dos atendimentos.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TechFixTextTertiary
                        )
                    } else {
                        Text(profile.bio, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }

            if (!isProfessional) {
                item(key = "interests") {
                    TechFixCard {
                        SectionHeader(
                            title = "Categorias de Interesse",
                            actionLabel = if (profile.interestCategories.isEmpty()) "Adicionar" else "Editar",
                            onAction = onEditProfileClick
                        )
                        Spacer(Modifier.height(TechFixDimens.Space12))
                        if (profile.interestCategories.isEmpty()) {
                            Text(
                                "Escolha os tipos de serviço que mais te interessam.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TechFixTextTertiary
                            )
                        } else {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(TechFixDimens.Space8),
                                verticalArrangement = Arrangement.spacedBy(TechFixDimens.Space8)
                            ) {
                                profile.interestCategories.forEach { InterestChip(it) }
                            }
                        }
                    }
                }

                item(key = "preferences") {
                    TechFixCard {
                        SectionHeader(
                            title = "Preferências de Atendimento",
                            actionLabel = if (profile.preferences.isEmpty()) "Adicionar" else "Editar",
                            onAction = onEditProfileClick
                        )
                        Spacer(Modifier.height(TechFixDimens.Space8))
                        if (profile.preferences.isEmpty()) {
                            Text(
                                "Diga como prefere ser atendido: presencial, horários, orçamento…",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TechFixTextTertiary
                            )
                        } else {
                            profile.preferences.forEach { PreferenceRow(it) }
                        }
                    }
                }

                item(key = "trust") { TrustCard(profile) }
            }

            item(key = "history_header") {
                SectionHeader(
                    title = if (isProfessional) "Pedidos Recebidos" else "Histórico de Serviços",
                    actionLabel = if (profile.serviceHistory.size > ProfileOptions.HISTORY_PREVIEW_COUNT) "Ver todos" else null,
                    onAction = onSeeAllHistoryClick
                )
            }

            if (profile.serviceHistory.isEmpty()) {
                item(key = "history_empty") {
                    TechFixCard {
                        EmptyState(
                            icon = Icons.Filled.Build,
                            title = if (isProfessional) "Nenhum pedido ainda" else "Nenhum serviço ainda",
                            description = if (isProfessional) {
                                "Os pedidos que os clientes enviarem para você aparecem aqui."
                            } else {
                                "Quando você fizer um pedido, o acompanhamento e a garantia aparecem aqui."
                            },
                            actionLabel = if (isProfessional) null else "Solicitar Serviço",
                            onAction = if (isProfessional) null else onRequestServiceClick
                        )
                    }
                }
            } else {
                itemsIndexed(
                    items = profile.serviceHistory.take(ProfileOptions.HISTORY_PREVIEW_COUNT),
                    key = { index, _ -> "service_$index" }
                ) { _, service ->
                    ServiceHistoryCard(item = service)
                }
            }

            item(key = "logout") {
                TechFixSecondaryButton(
                    text = "Sair da conta",
                    onClick = onLogoutClick,
                    icon = Icons.AutoMirrored.Filled.Logout,
                    contentColor = TechFixRed,
                    borderColor = TechFixRed
                )
            }

            item(key = "footer") {
                Text(
                    TechFixInfo.SUPPORT_LINE,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = TechFixDimens.Space8)
                )
            }
        }
    }
}

@Composable
private fun ProfileHeaderCard(
    profile: UserProfile,
    isProfessional: Boolean,
    onEditProfileClick: () -> Unit,
    onRequestServiceClick: () -> Unit
) {
    TechFixCard {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileAvatar(model = profile.avatarUrl.ifBlank { R.drawable.placeholder_perfil })

            Spacer(Modifier.height(TechFixDimens.Space12))
            if (profile.name.isNotBlank()) {
                Text(
                    profile.name,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
            } else {
                // Sem nome ainda: não inventamos um, convidamos a pessoa a preencher.
                Text(
                    "Adicione seu nome",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TechFixAccentSolid,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .clickable(onClick = onEditProfileClick)
                        .padding(horizontal = TechFixDimens.Space8, vertical = TechFixDimens.Space4)
                )
            }

            if (profile.isVerified) {
                Spacer(Modifier.height(TechFixDimens.Space4))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Verified,
                        contentDescription = null,
                        tint = TechFixGreen,
                        modifier = Modifier.size(TechFixDimens.IconSmall)
                    )
                    Spacer(Modifier.width(TechFixDimens.Space4))
                    Text(
                        "Perfil Verificado",
                        style = MaterialTheme.typography.labelMedium,
                        color = TechFixGreen
                    )
                }
            }

            Spacer(Modifier.height(TechFixDimens.Space8))
            InfoLine(
                icon = Icons.Filled.LocationOn,
                text = profile.location.ifBlank { "Localização não informada" },
                muted = profile.location.isBlank()
            )
            if (profile.memberSince.isNotBlank()) {
                Spacer(Modifier.height(TechFixDimens.Space4))
                InfoLine(icon = Icons.Filled.DateRange, text = profile.memberSince, muted = false)
            }

            Spacer(Modifier.height(TechFixDimens.Space20))
            TechFixSecondaryButton(
                text = "Editar Perfil",
                onClick = onEditProfileClick,
                icon = Icons.Filled.Edit
            )
            if (!isProfessional) {
                Spacer(Modifier.height(TechFixDimens.Space10))
                TechFixPrimaryButton(
                    text = "Solicitar Serviço",
                    onClick = onRequestServiceClick,
                    trailingIcon = Icons.AutoMirrored.Filled.ArrowForward
                )
            }
        }
    }
}

@Composable
private fun InfoLine(icon: ImageVector, text: String, muted: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            icon,
            contentDescription = null,
            tint = TechFixTextTertiary,
            modifier = Modifier.size(TechFixDimens.IconSmall)
        )
        Spacer(Modifier.width(TechFixDimens.Space6))
        Text(
            text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (muted) TechFixTextTertiary else TechFixTextSecondary
        )
    }
}

@Composable
private fun StatsRow(profile: UserProfile) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(TechFixDimens.Space12)
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
            iconTint = TechFixWarning,
            label = "Avaliação",
            value = if (profile.rating > 0.0) {
                "%.1f".format(Locale.forLanguageTag("pt-BR"), profile.rating)
            } else "—",
            modifier = Modifier.weight(1f)
        )
        StatPill(
            icon = Icons.Filled.Shield,
            iconTint = TechFixAccentSolid,
            label = "Confiança",
            value = if (profile.confidencePercent > 0) "${profile.confidencePercent}%" else "—",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun TrustCard(profile: UserProfile) {
    val hasScore = profile.loyaltyPercent > 0
    TechFixCard {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Indicador de Confiança", style = MaterialTheme.typography.titleLarge)
            Surface(
                color = if (hasScore) TechFixGreenBg else TechFixSurfaceVariant,
                shape = TechFixPillShape
            ) {
                Text(
                    profile.loyaltyLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (hasScore) TechFixGreen else TechFixTextSecondary,
                    modifier = Modifier.padding(
                        horizontal = TechFixDimens.Space10,
                        vertical = TechFixDimens.Space4
                    )
                )
            }
        }
        Spacer(Modifier.height(TechFixDimens.Space12))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Fidelidade", style = MaterialTheme.typography.bodyMedium)
            Text("${profile.loyaltyPercent}%", style = MaterialTheme.typography.labelLarge)
        }
        Spacer(Modifier.height(TechFixDimens.Space8))
        TechFixProgressBar(
            progress = profile.loyaltyPercent,
            progressColor = TechFixGreen,
            brush = if (hasScore) null else TechFixAccentBrush
        )
    }
}
