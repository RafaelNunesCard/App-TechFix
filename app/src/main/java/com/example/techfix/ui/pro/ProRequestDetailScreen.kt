package com.example.techfix.ui.pro

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
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
import com.example.techfix.data.local.ServiceReviewEntity
import com.example.techfix.ui.components.OrderReviewCard
import com.example.techfix.ui.orders.DetailCard
import com.example.techfix.ui.orders.DetailHeader
import com.example.techfix.ui.orders.InfoRow
import com.example.techfix.ui.orders.OrderDetail
import com.example.techfix.ui.orders.OrderStatus
import com.example.techfix.ui.orders.SectionTitle
import com.example.techfix.ui.orders.TECHFIX_FEE_CENTS
import com.example.techfix.ui.orders.formatCents
import com.example.techfix.ui.theme.TechFixAccentBrush
import com.example.techfix.ui.theme.TechFixBackground
import com.example.techfix.ui.theme.TechFixChipBackground
import com.example.techfix.ui.theme.TechFixGreen
import com.example.techfix.ui.theme.TechFixRed
import com.example.techfix.ui.theme.TechFixRedBg
import com.example.techfix.ui.theme.TechFixTextPrimary
import com.example.techfix.ui.theme.TechFixTextSecondary

private val refuseReasons = listOf(
    "Sem disponibilidade",
    "Fora da minha região",
    "Serviço não atendido",
    "Outro"
)

/** Detalhe de uma solicitação, do ponto de vista do PROFISSIONAL. */
@Composable
fun ProRequestDetailScreen(
    detail: OrderDetail?,
    onBack: () -> Unit,
    onAdvance: () -> Unit,
    onRefuse: () -> Unit,
    onOpenChat: () -> Unit,
    submittedReview: ServiceReviewEntity? = null,
    canReview: Boolean = false,
    onSubmitReview: suspend (Int, String) -> Boolean = { _, _ -> false }
) {
    var showRefuse by remember { mutableStateOf(false) }
    var reason by remember { mutableStateOf(refuseReasons.first()) }

    if (showRefuse) {
        AlertDialog(
            onDismissRequest = { showRefuse = false },
            title = { Text("Recusar solicitação?") },
            text = {
                Column {
                    refuseReasons.forEach { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { reason = option },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = reason == option, onClick = { reason = option })
                            Text(option)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showRefuse = false
                    onRefuse()
                }) { Text("Confirmar recusa") }
            },
            dismissButton = { TextButton(onClick = { showRefuse = false }) { Text("Voltar") } }
        )
    }

    Surface(modifier = Modifier.fillMaxSize(), color = TechFixBackground) {
        Column(modifier = Modifier.fillMaxSize().imePadding()) {
            DetailHeader(
                title = if (detail != null) "Solicitação #${detail.id}" else "Solicitação",
                onBack = onBack
            )

            if (detail == null) {
                Text(
                    "Carregando solicitação...",
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
                DetailCard {
                    Text(detail.status.label, color = TechFixGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text(detail.personName, color = TechFixTextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Cliente", color = TechFixTextSecondary, fontSize = 12.sp)
                }

                DetailCard {
                    SectionTitle("Serviço")
                    Spacer(Modifier.height(10.dp))
                    InfoRow("Item ou área", detail.deviceName)
                    InfoRow("Serviço", detail.service)
                    if (detail.problem.isNotBlank()) InfoRow("Descrição do cliente", detail.problem)
                }

                DetailCard {
                    SectionTitle("Data e local")
                    Spacer(Modifier.height(10.dp))
                    if (detail.schedule.isNotBlank()) InfoRow("Quando", detail.schedule)
                    if (detail.location.isNotBlank()) InfoRow("Onde", detail.location)
                }

                if (canReview && detail.status == OrderStatus.COMPLETED) {
                    OrderReviewCard("Avaliar cliente", submittedReview, onSubmitReview)
                }

                DetailCard {
                    SectionTitle("Você receberá")
                    Spacer(Modifier.height(10.dp))
                    val price = detail.priceCents
                    if (price == null) {
                        Text("Valor a combinar com o cliente.", color = TechFixTextSecondary, fontSize = 13.sp)
                    } else {
                        InfoRow("Valor do serviço", formatCents(price))
                        InfoRow("Taxa TechFix", formatCents(TECHFIX_FEE_CENTS))
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Líquido ${formatCents(price - TECHFIX_FEE_CENTS)}",
                            color = TechFixGreen,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(percent = 50))
                        .background(TechFixChipBackground)
                        .clickable(onClick = onOpenChat),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Enviar mensagem ao cliente", color = TechFixTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(8.dp))
            }

            val label = advanceLabel(detail.status)
            if (label != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (detail.status == OrderStatus.PENDING) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .clip(RoundedCornerShape(percent = 50))
                                .background(TechFixRedBg)
                                .clickable { showRefuse = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Recusar", color = TechFixRed, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .weight(if (detail.status == OrderStatus.PENDING) 1.4f else 1f)
                            .height(52.dp)
                            .clip(RoundedCornerShape(percent = 50))
                            .background(TechFixAccentBrush)
                            .clickable(onClick = onAdvance),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(label, color = TechFixTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
