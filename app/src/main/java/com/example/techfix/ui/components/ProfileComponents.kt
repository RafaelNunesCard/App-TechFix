package com.example.techfix.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import coil.compose.AsyncImage
import com.example.techfix.R
import com.example.techfix.model.ServiceHistoryItem
import com.example.techfix.model.ServiceStatus
import com.example.techfix.ui.theme.TechFixAccentBrush
import com.example.techfix.ui.theme.TechFixAccentSolid
import com.example.techfix.ui.theme.TechFixBorder
import com.example.techfix.ui.theme.TechFixChipBackground
import com.example.techfix.ui.theme.TechFixDimens
import com.example.techfix.ui.theme.TechFixGreen
import com.example.techfix.ui.theme.TechFixGreenBg
import com.example.techfix.ui.theme.TechFixPillShape
import com.example.techfix.ui.theme.TechFixRedBg
import com.example.techfix.ui.theme.TechFixSurface
import com.example.techfix.ui.theme.TechFixSurfaceVariant
import com.example.techfix.ui.theme.TechFixTextPrimary
import com.example.techfix.ui.theme.TechFixTextSecondary
import com.example.techfix.ui.theme.TechFixTextTertiary
import com.example.techfix.ui.theme.TechFixWarning
import com.example.techfix.ui.theme.TechFixWarningBg

// Regras deste arquivo (padrão do grupo): cores só de Color.kt, texto só de
// MaterialTheme.typography (Sora), cantos só de MaterialTheme.shapes / TechFixPillShape
// e tamanhos só de TechFixDimens. Nada de Color(0x...), fontSize ou "NN.dp" soltos.

/** Card padrão (fundo #16151B, borda branca a 8%, raio 16). */
@Composable
fun TechFixCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = TechFixSurface,
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(TechFixDimens.BorderWidth, TechFixBorder),
    ) {
        Column(modifier = Modifier.padding(TechFixDimens.CardPadding), content = content)
    }
}

/** Título de seção, com uma ação opcional à direita (ex.: "Editar"). */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge)
        if (actionLabel != null && onAction != null) {
            Text(
                text = actionLabel,
                style = MaterialTheme.typography.labelLarge,
                color = TechFixAccentSolid,
                modifier = Modifier
                    .clip(TechFixPillShape)
                    .clickable(role = Role.Button, onClick = onAction)
                    .padding(horizontal = TechFixDimens.Space8, vertical = TechFixDimens.Space4)
            )
        }
    }
}

/** Indicador do topo do perfil (Concluídos / Avaliação / Confiança). */
@Composable
fun StatPill(
    icon: ImageVector,
    iconTint: Color,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = TechFixSurface,
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(TechFixDimens.BorderWidth, TechFixBorder),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(TechFixDimens.Space12),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(TechFixDimens.IconTile)
                    .clip(MaterialTheme.shapes.small)
                    .background(TechFixSurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(TechFixDimens.IconMedium)
                )
            }
            Spacer(Modifier.height(TechFixDimens.Space10))
            Text(value, style = MaterialTheme.typography.headlineSmall, maxLines = 1)
            Text(
                label,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/** Chip somente leitura (categorias de interesse). */
@Composable
fun InterestChip(text: String) {
    Surface(
        color = TechFixChipBackground,
        shape = TechFixPillShape,
        border = BorderStroke(TechFixDimens.BorderWidth, TechFixBorder)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = TechFixTextPrimary,
            modifier = Modifier.padding(
                horizontal = TechFixDimens.Space12,
                vertical = TechFixDimens.Space8
            )
        )
    }
}

/** Linha de "Preferências de Atendimento". */
@Composable
fun PreferenceRow(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = TechFixDimens.Space6)
    ) {
        Icon(
            Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = TechFixAccentSolid,
            modifier = Modifier.size(TechFixDimens.IconMedium)
        )
        Spacer(Modifier.width(TechFixDimens.Space12))
        Text(text, style = MaterialTheme.typography.bodyLarge)
    }
}

/** Barra de progresso fina e animada. [brush] (opcional) vence [progressColor]. */
@Composable
fun TechFixProgressBar(
    progress: Int,
    modifier: Modifier = Modifier,
    trackColor: Color = TechFixSurfaceVariant,
    progressColor: Color = TechFixGreen,
    brush: Brush? = null
) {
    val fraction by animateFloatAsState(
        targetValue = progress.coerceIn(0, 100) / 100f,
        label = "progress"
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(TechFixDimens.ProgressHeight)
            .clip(TechFixPillShape)
            .background(trackColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction = fraction)
                .fillMaxHeight()
                .clip(TechFixPillShape)
                .background(brush ?: SolidColor(progressColor))
        )
    }
}

/** Selo de status do serviço. */
@Composable
fun StatusBadge(status: ServiceStatus) {
    val (background, content) = when (status) {
        ServiceStatus.CONCLUIDO -> TechFixGreenBg to TechFixGreen
        ServiceStatus.EM_ANDAMENTO -> TechFixRedBg to TechFixAccentSolid
        ServiceStatus.AGUARDANDO_PECAS -> TechFixWarningBg to TechFixWarning
    }
    Surface(color = background, shape = TechFixPillShape) {
        Text(
            status.label,
            style = MaterialTheme.typography.labelMedium,
            color = content,
            modifier = Modifier.padding(
                horizontal = TechFixDimens.Space10,
                vertical = TechFixDimens.Space4
            )
        )
    }
}

/** Card de um serviço do histórico. */
@Composable
fun ServiceHistoryCard(item: ServiceHistoryItem, modifier: Modifier = Modifier) {
    TechFixCard(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(TechFixDimens.IconTileLarge)
                    .clip(MaterialTheme.shapes.small)
                    .background(TechFixSurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Devices,
                    contentDescription = null,
                    tint = TechFixTextSecondary,
                    modifier = Modifier.size(TechFixDimens.IconLarge)
                )
            }
            Spacer(Modifier.width(TechFixDimens.Space12))
            Column(Modifier.weight(1f)) {
                Text(item.deviceName, style = MaterialTheme.typography.titleMedium)
                Text(item.description, style = MaterialTheme.typography.bodyMedium, maxLines = 2)
            }
        }

        Spacer(Modifier.height(TechFixDimens.Space16))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Status do Serviço", style = MaterialTheme.typography.bodyMedium)
            StatusBadge(item.status)
        }

        Spacer(Modifier.height(TechFixDimens.Space12))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Progresso", style = MaterialTheme.typography.bodyMedium)
            Text("${item.progress}%", style = MaterialTheme.typography.labelLarge)
        }
        Spacer(Modifier.height(TechFixDimens.Space8))
        TechFixProgressBar(
            progress = item.progress,
            progressColor = TechFixGreen,
            brush = if (item.status == ServiceStatus.CONCLUIDO) null else TechFixAccentBrush
        )

        Spacer(Modifier.height(TechFixDimens.Space16))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(item.dateLabel, style = MaterialTheme.typography.bodySmall)
                Text(item.date, style = MaterialTheme.typography.labelLarge)
            }
            if (item.warranty != null) {
                Column(horizontalAlignment = Alignment.End) {
                    Text("GARANTIA TÉCNICA", style = MaterialTheme.typography.bodySmall)
                    Text(
                        item.warranty,
                        style = MaterialTheme.typography.labelLarge,
                        color = TechFixGreen
                    )
                }
            }
        }
    }
}

/** Estado vazio (nunca inventa dado: só explica o que vai aparecer ali). */
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(TechFixDimens.IconTileLarge)
                .clip(MaterialTheme.shapes.small)
                .background(TechFixSurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = TechFixTextSecondary,
                modifier = Modifier.size(TechFixDimens.IconLarge)
            )
        }
        Spacer(Modifier.height(TechFixDimens.Space12))
        Text(title, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
        Spacer(Modifier.height(TechFixDimens.Space4))
        Text(description, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
        if (actionLabel != null && onAction != null) {
            Spacer(Modifier.height(TechFixDimens.Space16))
            TechFixSecondaryButton(
                text = actionLabel,
                onClick = onAction,
                contentColor = TechFixAccentSolid,
                borderColor = TechFixAccentSolid
            )
        }
    }
}

/** Botão principal: gradiente coral e formato de pílula. */
@Composable
fun TechFixPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    trailingIcon: ImageVector? = null
) {
    val background: Brush = if (enabled) TechFixAccentBrush else SolidColor(TechFixSurfaceVariant)
    val contentColor = if (enabled) TechFixTextPrimary else TechFixTextTertiary

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(TechFixDimens.ButtonHeight)
            .clip(TechFixPillShape)
            .background(background)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text, style = MaterialTheme.typography.labelLarge, color = contentColor)
            if (trailingIcon != null) {
                Spacer(Modifier.width(TechFixDimens.Space8))
                Icon(
                    trailingIcon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(TechFixDimens.IconMedium)
                )
            }
        }
    }
}

/** Botão secundário com contorno (também usado para "Sair da conta", em vermelho). */
@Composable
fun TechFixSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    contentColor: Color = TechFixTextPrimary,
    borderColor: Color = TechFixBorder
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(TechFixDimens.ButtonHeight),
        shape = TechFixPillShape,
        border = BorderStroke(TechFixDimens.BorderWidth, borderColor),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = contentColor)
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(TechFixDimens.IconMedium))
            Spacer(Modifier.width(TechFixDimens.Space8))
        }
        Text(text, style = MaterialTheme.typography.labelLarge, color = contentColor)
    }
}

/** Foto de perfil com aro em gradiente. [model] aceita URL/Uri em texto ou um drawable. */
@Composable
fun ProfileAvatar(
    model: Any,
    modifier: Modifier = Modifier,
    size: Dp = TechFixDimens.AvatarProfile
) {
    AsyncImage(
        model = model,
        contentDescription = "Foto de perfil",
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(size)
            .border(TechFixDimens.AvatarRing, TechFixAccentBrush, CircleShape)
            .padding(TechFixDimens.AvatarRing + TechFixDimens.AvatarRingGap)
            .clip(CircleShape)
            .background(TechFixSurfaceVariant)
    )
}

/** Logo "TechFix" (PNG transparente) para a barra superior. */
@Composable
fun TechFixWordmark(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.logo_techfix_wordmark),
        contentDescription = "TechFix",
        contentScale = ContentScale.Fit,
        modifier = modifier.height(TechFixDimens.TopBarLogoHeight)
    )
}
