package com.example.techfix.ui.orders

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.techfix.ui.theme.TechFixAccentBrush
import com.example.techfix.ui.theme.TechFixAccentSolid
import com.example.techfix.ui.theme.TechFixBackground
import com.example.techfix.ui.theme.TechFixBorder
import com.example.techfix.ui.theme.TechFixChipBackground
import com.example.techfix.ui.theme.TechFixGreen
import com.example.techfix.ui.theme.TechFixGreenBg
import com.example.techfix.ui.theme.TechFixRed
import com.example.techfix.ui.theme.TechFixRedBg
import com.example.techfix.ui.theme.TechFixSurface
import com.example.techfix.ui.theme.TechFixTextPrimary
import com.example.techfix.ui.theme.TechFixTextSecondary
import com.example.techfix.ui.theme.TechFixTextTertiary
import java.text.NumberFormat
import java.util.Locale

/** Taxa fixa da TechFix, em centavos (R$ 9,90). */
const val TECHFIX_FEE_CENTS = 990

/** Dados de um pedido para as telas de detalhe (cliente e profissional). */
data class OrderDetail(
    val id: String,
    val deviceName: String,
    val service: String,
    val problem: String,
    /** Cliente: nome do técnico. Profissional: nome do cliente. */
    val personName: String,
    val status: OrderStatus,
    val schedule: String,
    val location: String,
    val priceCents: Int?
)

fun formatCents(cents: Int?): String =
    if (cents == null) "A combinar"
    else NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(cents / 100.0)

/** Detalhe do pedido, do ponto de vista do CLIENTE. */
@Composable
fun OrderDetailScreen(
    detail: OrderDetail?,
    onBack: () -> Unit,
    onCancelOrder: () -> Unit,
    onOpenChat: () -> Unit
) {
    var confirmCancel by remember { mutableStateOf(false) }

    if (confirmCancel) {
        AlertDialog(
            onDismissRequest = { confirmCancel = false },
            title = { Text("Cancelar pedido?") },
            text = { Text("O profissional será avisado e o pedido será encerrado.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmCancel = false
                    onCancelOrder()
                }) { Text("Cancelar pedido") }
            },
            dismissButton = { TextButton(onClick = { confirmCancel = false }) { Text("Voltar") } }
        )
    }

    Surface(modifier = Modifier.fillMaxSize(), color = TechFixBackground) {
        Column(modifier = Modifier.fillMaxSize()) {
            DetailHeader(title = if (detail != null) "Pedido #${detail.id}" else "Pedido", onBack = onBack)

            if (detail == null) {
                Text(
                    "Carregando pedido...",
                    color = TechFixTextSecondary,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(20.dp)
                )
                return@Column
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                DetailStatusCard(detail)
                if (detail.status != OrderStatus.CANCELED) TimelineCard(detail.status)

                DetailCard {
                    SectionTitle("Profissional")
                    Spacer(Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(TechFixChipBackground),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                detail.personName.trim().firstOrNull()?.uppercase() ?: "?",
                                color = TechFixTextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(detail.personName, color = TechFixTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                DetailCard {
                    SectionTitle("Detalhes do serviço")
                    Spacer(Modifier.height(10.dp))
                    InfoRow("Aparelho", detail.deviceName)
                    InfoRow("Serviço", detail.service)
                    if (detail.schedule.isNotBlank()) InfoRow("Quando", detail.schedule)
                    if (detail.location.isNotBlank()) InfoRow("Onde", detail.location)
                    if (detail.problem.isNotBlank()) InfoRow("Descrição", detail.problem)
                }

                PaymentCard(detail.priceCents)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(percent = 50))
                        .background(TechFixAccentBrush)
                        .clickable(onClick = onOpenChat),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Falar com o profissional", color = TechFixTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }

                val canCancel = detail.status == OrderStatus.PENDING || detail.status == OrderStatus.APPROVED
                if (canCancel) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(percent = 50))
                            .background(TechFixRedBg)
                            .clickable { confirmCancel = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Cancelar pedido", color = TechFixRed, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun DetailStatusCard(detail: OrderDetail) {
    val headline = when (detail.status) {
        OrderStatus.PENDING -> "Aguardando o profissional aceitar"
        OrderStatus.APPROVED -> "Pedido aceito"
        OrderStatus.IN_PROGRESS -> "Serviço em andamento"
        OrderStatus.WAITING_PARTS -> "Aguardando peças"
        OrderStatus.COMPLETED -> "Serviço concluído"
        OrderStatus.CANCELED -> "Pedido cancelado"
    }
    val color = when (detail.status) {
        OrderStatus.COMPLETED -> TechFixGreen
        OrderStatus.CANCELED -> TechFixTextTertiary
        else -> TechFixAccentSolid
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.10f))
            .padding(16.dp)
    ) {
        Text(detail.status.label, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        Text(headline, color = TechFixTextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        if (detail.schedule.isNotBlank()) {
            Spacer(Modifier.height(4.dp))
            Text("Previsto: ${detail.schedule}", color = TechFixTextSecondary, fontSize = 13.sp)
        }
    }
}

@Composable
private fun TimelineCard(status: OrderStatus) {
    val steps = listOf("Solicitado", "Aceito", "Em atendimento", "Concluído")
    val doneCount = when (status) {
        OrderStatus.PENDING -> 1
        OrderStatus.APPROVED -> 2
        OrderStatus.IN_PROGRESS, OrderStatus.WAITING_PARTS -> 3
        OrderStatus.COMPLETED -> 4
        OrderStatus.CANCELED -> 0
    }
    DetailCard {
        SectionTitle("Andamento")
        Spacer(Modifier.height(12.dp))
        steps.forEachIndexed { index, title ->
            val done = index < doneCount
            val current = index == doneCount
            val dotColor = when {
                done -> TechFixGreen
                current -> TechFixAccentSolid
                else -> TechFixChipBackground
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(dotColor),
                    contentAlignment = Alignment.Center
                ) {
                    if (done) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = null,
                            tint = TechFixBackground,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        title,
                        color = if (done || current) TechFixTextPrimary else TechFixTextTertiary,
                        fontSize = 14.sp,
                        fontWeight = if (current) FontWeight.Bold else FontWeight.Medium
                    )
                    if (current) {
                        Text(
                            if (status == OrderStatus.WAITING_PARTS && index == 2) "Aguardando peças" else "Próxima etapa",
                            color = TechFixTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
            if (index < steps.lastIndex) {
                Box(
                    modifier = Modifier
                        .padding(start = 11.dp)
                        .width(2.dp)
                        .height(18.dp)
                        .background(if (done) TechFixGreen else TechFixChipBackground)
                )
            }
        }
    }
}

@Composable
internal fun PaymentCard(priceCents: Int?) {
    DetailCard {
        SectionTitle("Pagamento")
        Spacer(Modifier.height(10.dp))
        if (priceCents == null) {
            Text("Valor a combinar com o profissional.", color = TechFixTextSecondary, fontSize = 13.sp)
        } else {
            InfoRow("Serviço", formatCents(priceCents))
            InfoRow("Taxa TechFix", formatCents(TECHFIX_FEE_CENTS))
            Spacer(Modifier.height(6.dp))
            Row {
                Text("Total", color = TechFixTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text(
                    formatCents(priceCents + TECHFIX_FEE_CENTS),
                    color = TechFixTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text("Pagamento após a conclusão do serviço.", color = TechFixTextTertiary, fontSize = 11.sp)
    }
}

@Composable
internal fun DetailHeader(title: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(TechFixSurface)
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Voltar",
                tint = TechFixTextPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.width(14.dp))
        Text(title, color = TechFixTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
internal fun DetailCard(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = TechFixSurface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, TechFixBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) { content() }
    }
}

@Composable
internal fun SectionTitle(text: String) {
    Text(text, color = TechFixTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
}

@Composable
internal fun InfoRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 5.dp)) {
        Text(label, color = TechFixTextTertiary, fontSize = 11.sp)
        Text(value, color = TechFixTextPrimary, fontSize = 14.sp)
    }
}
               }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        title,
                        color = if (done || current) TechFixTextPrimary else TechFixTextTertiary,
                        fontSize = 14.sp,
                        fontWeight = if (current) FontWeight.Bold else FontWeight.Medium
                    )
                    if (current) {
                        Text(
                            if (status == OrderStatus.WAITING_PARTS && index == 2) "Aguardando peças" else "Próxima etapa",
                            color = TechFixTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
            if (index < steps.lastIndex) {
                Box(
                    modifier = Modifier
                        .padding(start = 11.dp)
                        .width(2.dp)
                        .height(18.dp)
                        .background(if (done) TechFixGreen else TechFixChipBackground)
                )
            }
        }
    }
}

@Composable
internal fun PaymentCard(priceCents: Int?) {
    DetailCard {
        SectionTitle("Pagamento")
        Spacer(Modifier.height(10.dp))
        if (priceCents == null) {
            Text("Valor a combinar com o profissional.", color = TechFixTextSecondary, fontSize = 13.sp)
        } else {
            InfoRow("Serviço", formatCents(priceCents))
            InfoRow("Taxa TechFix", formatCents(TECHFIX_FEE_CENTS))
            Spacer(Modifier.height(6.dp))
            Row {
                Text("Total", color = TechFixTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text(
                    formatCents(priceCents + TECHFIX_FEE_CENTS),
                    color = TechFixTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text("Pagamento após a conclusão do serviço.", color = TechFixTextTertiary, fontSize = 11.sp)
    }
}

@Composable
internal fun DetailHeader(title: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(TechFixSurface)
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Voltar",
                tint = TechFixTextPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.width(14.dp))
        Text(title, color = TechFixTextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
internal fun DetailCard(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = TechFixSurface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, TechFixBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) { content() }
    }
}

@Composable
internal fun SectionTitle(text: String) {
    Text(text, color = TechFixTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
}

@Composable
internal fun InfoRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 5.dp)) {
        Text(label, color = TechFixTextTertiary, fontSize = 11.sp)
        Text(value, color = TechFixTextPrimary, fontSize = 14.sp)
    }
}
