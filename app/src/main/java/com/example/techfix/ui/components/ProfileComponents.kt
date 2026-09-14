package com.example.techfix.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.techfix.model.ServiceHistoryItem
import com.example.techfix.model.ServiceStatus
import com.example.techfix.ui.theme.*

/** Card genérico escuro com cantos arredondados, usado em todas as seções da tela. */
@Composable
fun TechFixCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = TechFixSurface,
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

/** Um dos três indicadores no topo do perfil: Concluídos / Avaliação / Confiança. */
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
        shape = RoundedCornerShape(14.dp),
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text(
                    label.uppercase(),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

/** Chip usado em "Categorias de Interesse". */
@Composable
fun InterestChip(text: String) {
    Surface(
        color = TechFixChipBackground,
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.85f),
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

/** Linha usada em "Preferências de Atendimento". */
@Composable
fun PreferenceRow(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(TechFixAccentSolid)
        )
        Spacer(Modifier.width(12.dp))
        Text(text, style = MaterialTheme.typography.bodyLarge, fontSize = 14.sp, color = Color.White.copy(alpha = 0.85f))
    }
}

/** Barra de progresso fina customizada (usada na fidelidade e no histórico de serviços). */
@Composable
fun TechFixProgressBar(
    progress: Int,
    modifier: Modifier = Modifier,
    trackColor: Color = TechFixSurfaceVariant,
    progressColor: Color = TechFixGreen
) {
    val clamped = progress.coerceIn(0, 100)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(50))
            .background(trackColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction = clamped / 100f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(50))
                .background(progressColor)
        )
    }
}

/** Badge de status (Concluído / Em Andamento / Aguardando Peças). */
@Composable
fun StatusBadge(status: ServiceStatus) {
    val (bg, fg) = when (status) {
        ServiceStatus.CONCLUIDO -> TechFixGreenBg to TechFixGreen
        ServiceStatus.EM_ANDAMENTO -> Color(0xFF3A2418) to TechFixAccentSolid
        ServiceStatus.AGUARDANDO_PECAS -> Color(0xFF33301A) to Color(0xFFE0C441)
    }
    Surface(color = bg, shape = RoundedCornerShape(50)) {
        Text(
            status.label,
            color = fg,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

/** Card de item do histórico de serviços (ex.: MacBook Pro 16, iPhone 14 Pro). */
@Composable
fun ServiceHistoryCard(item: ServiceHistoryItem, modifier: Modifier = Modifier) {
    TechFixCard(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(TechFixSurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Devices,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(item.deviceName, style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                Text(
                    item.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.6f),
                    maxLines = 2
                )
            }
        }

        Spacer(Modifier.height(14.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Status do Serviço", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.6f))
            StatusBadge(item.status)
        }

        Spacer(Modifier.height(10.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Progresso", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.6f))
            Text(
                "${item.progress}%",
                style = MaterialTheme.typography.labelLarge,
                color = Color.White
            )
        }
        Spacer(Modifier.height(6.dp))
        TechFixProgressBar(
            progress = item.progress,
            progressColor = if (item.status == ServiceStatus.CONCLUIDO) TechFixGreen else TechFixAccentSolid
        )

        Spacer(Modifier.height(14.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(item.dateLabel, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.5f))
                Text(item.date, style = MaterialTheme.typography.labelLarge, fontSize = 13.sp, color = Color.White.copy(alpha = 0.85f))
            }
            if (item.warranty != null) {
                Column(horizontalAlignment = Alignment.End) {
                    Text("GARANTIA TÉCNICA", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.5f))
                    Text(
                        item.warranty,
                        color = TechFixGreen,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}