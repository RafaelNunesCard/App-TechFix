package com.example.techfix.ui.request

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.techfix.ui.orders.DetailHeader
import com.example.techfix.ui.orders.TECHFIX_FEE_CENTS
import com.example.techfix.ui.orders.formatCents
import com.example.techfix.ui.theme.TechFixAccentBrush
import com.example.techfix.ui.theme.TechFixAccentSolid
import com.example.techfix.ui.theme.TechFixBackground
import com.example.techfix.ui.theme.TechFixBorder
import com.example.techfix.ui.theme.TechFixChipBackground
import com.example.techfix.ui.theme.TechFixGreen
import com.example.techfix.ui.theme.TechFixGreenBg
import com.example.techfix.ui.theme.TechFixRed
import com.example.techfix.ui.theme.TechFixSurface
import com.example.techfix.ui.theme.TechFixTextPrimary
import com.example.techfix.ui.theme.TechFixTextSecondary
import com.example.techfix.ui.theme.TechFixTextTertiary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/** Tudo que o cliente preencheu no formulário. */
data class RequestForm(
    val serviceName: String,
    val servicePriceCents: Int,
    val device: String,
    val problem: String,
    val scheduleLabel: String,
    val locationLabel: String
)

private data class ServiceOption(val name: String, val priceCents: Int)

private val serviceOptions = listOf(
    ServiceOption("Troca de tela", 12000),
    ServiceOption("Formatação e backup", 9000),
    ServiceOption("Upgrade de hardware", 15000)
)

private val timeOptions = listOf("09:00", "11:00", "14:00", "16:00")

private const val HOME_VISIT = "Na minha casa"
private const val BRING_TO_TECH = "Levar ao técnico"

/** Rótulos dos próximos dias: Hoje, Amanhã e os dois seguintes (ex: "Sex 14"). */
private fun dayLabels(): List<String> {
    val format = SimpleDateFormat("EEE dd", Locale("pt", "BR"))
    return (0..3).map { offset ->
        when (offset) {
            0 -> "Hoje"
            1 -> "Amanhã"
            else -> {
                val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, offset) }
                format.format(cal.time).replace(".", "").replaceFirstChar { it.uppercase() }
            }
        }
    }
}

@Composable
fun RequestServiceScreen(
    professionalName: String,
    professionalSpecialty: String,
    onBack: () -> Unit,
    onSubmit: (RequestForm) -> Unit,
    onViewOrders: () -> Unit,
    onGoHome: () -> Unit
) {
    var sent by remember { mutableStateOf(false) }
    var selectedService by remember { mutableStateOf(serviceOptions.first()) }
    var device by remember { mutableStateOf("") }
    var problem by remember { mutableStateOf("") }
    val days = remember { dayLabels() }
    var selectedDay by remember { mutableStateOf(days.first()) }
    var selectedTime by remember { mutableStateOf(timeOptions[2]) }
    var place by remember { mutableStateOf(HOME_VISIT) }
    var address by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Surface(modifier = Modifier.fillMaxSize(), color = TechFixBackground) {
        if (sent) {
            SuccessPanel(professionalName, onViewOrders, onGoHome)
            return@Surface
        }

        Column(modifier = Modifier.fillMaxSize()) {
            DetailHeader(title = "Solicitar Serviço", onBack = onBack)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FormCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(TechFixAccentBrush),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                professionalName.trim().firstOrNull()?.uppercase() ?: "?",
                                color = TechFixTextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(professionalName, color = TechFixTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text(professionalSpecialty, color = TechFixTextSecondary, fontSize = 12.sp)
                        }
                    }
                }

                Label("Qual serviço você precisa?")
                serviceOptions.forEach { option ->
                    val selected = option == selectedService
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (selected) TechFixAccentSolid.copy(alpha = 0.08f) else TechFixSurface)
                            .clickable { selectedService = option }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(option.name, color = TechFixTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text("a partir de ${formatCents(option.priceCents)}", color = TechFixTextSecondary, fontSize = 12.sp)
                        }
                        if (selected) {
                            Icon(Icons.Filled.Check, contentDescription = "Selecionado", tint = TechFixAccentSolid)
                        }
                    }
                }

                Label("Qual aparelho?")
                FormField(
                    value = device,
                    onValueChange = { device = it },
                    placeholder = "Ex: iPhone 13, Notebook Dell"
                )

                Label("Descreva o problema")
                FormField(
                    value = problem,
                    onValueChange = { if (it.length <= 300) problem = it },
                    placeholder = "Conte o que está acontecendo, modelo do aparelho, etc.",
                    singleLine = false,
                    minHeight = 120
                )
                Text("${problem.length}/300", color = TechFixTextTertiary, fontSize = 11.sp)

                Label("Quando?")
                ChipRow(options = days, selected = selectedDay, onSelect = { selectedDay = it })
                ChipRow(options = timeOptions, selected = selectedTime, onSelect = { selectedTime = it })

                Label("Onde?")
                ChipRow(options = listOf(HOME_VISIT, BRING_TO_TECH), selected = place, onSelect = { place = it })
                if (place == HOME_VISIT) {
                    FormField(
                        value = address,
                        onValueChange = { address = it },
                        placeholder = "Endereço (rua, número, bairro)"
                    )
                }

                FormCard {
                    Text("Resumo", color = TechFixTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))
                    SummaryRow("Serviço", formatCents(selectedService.priceCents))
                    SummaryRow("Taxa TechFix", formatCents(TECHFIX_FEE_CENTS))
                    Spacer(Modifier.height(6.dp))
                    SummaryRow("Total", formatCents(selectedService.priceCents + TECHFIX_FEE_CENTS), bold = true)
                    Spacer(Modifier.height(8.dp))
                    Text("Pagamento após a conclusão do serviço.", color = TechFixTextTertiary, fontSize = 11.sp)
                }

                val currentError = error
                if (currentError != null) {
                    Text(currentError, color = TechFixRed, fontSize = 13.sp)
                }
                Spacer(Modifier.height(4.dp))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TechFixSurface)
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Total", color = TechFixTextSecondary, fontSize = 11.sp)
                    Text(
                        formatCents(selectedService.priceCents + TECHFIX_FEE_CENTS),
                        color = TechFixTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .height(52.dp)
                        .clip(RoundedCornerShape(percent = 50))
                        .background(TechFixAccentBrush)
                        .clickable {
                            error = when {
                                device.isBlank() -> "Informe qual é o aparelho."
                                place == HOME_VISIT && address.isBlank() -> "Informe o endereço do atendimento."
                                else -> null
                            }
                            if (error == null) {
                                onSubmit(
                                    RequestForm(
                                        serviceName = selectedService.name,
                                        servicePriceCents = selectedService.priceCents,
                                        device = device.trim(),
                                        problem = problem.trim(),
                                        scheduleLabel = "$selectedDay, $selectedTime",
                                        locationLabel = if (place == HOME_VISIT) "Na casa do cliente - ${address.trim()}" else BRING_TO_TECH
                                    )
                                )
                                sent = true
                            }
                        }
                        .padding(horizontal = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Enviar Solicitação", color = TechFixTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun SuccessPanel(professionalName: String, onViewOrders: () -> Unit, onGoHome: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(TechFixGreenBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Check, contentDescription = null, tint = TechFixGreen, modifier = Modifier.size(36.dp))
        }
        Spacer(Modifier.height(20.dp))
        Text("Solicitação enviada!", color = TechFixTextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(
            "${professionalName.trim().split(" ").firstOrNull().orEmpty()} vai analisar o seu pedido.",
            color = TechFixTextSecondary,
            fontSize = 14.sp
        )
        Spacer(Modifier.height(28.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(percent = 50))
                .background(TechFixAccentBrush)
                .clickable(onClick = onViewOrders),
            contentAlignment = Alignment.Center
        ) {
            Text("Ver meus pedidos", color = TechFixTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(12.dp))
        Text(
            "Voltar ao início",
            color = TechFixTextSecondary,
            fontSize = 14.sp,
            modifier = Modifier
                .clickable(onClick = onGoHome)
                .padding(12.dp)
        )
    }
}

@Composable
private fun Label(text: String) {
    Text(text, color = TechFixTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
}

@Composable
private fun FormCard(content: @Composable () -> Unit) {
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
private fun SummaryRow(label: String, value: String, bold: Boolean = false) {
    Row(modifier = Modifier.padding(vertical = 3.dp)) {
        Text(
            label,
            color = if (bold) TechFixTextPrimary else TechFixTextSecondary,
            fontSize = if (bold) 15.sp else 13.sp,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )
        Text(
            value,
            color = TechFixTextPrimary,
            fontSize = if (bold) 18.sp else 13.sp,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun ChipRow(options: List<String>, selected: String, onSelect: (String) -> Unit) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            val isSelected = option == selected
            Box(
                modifier = Modifier
                    .height(40.dp)
                    .clip(RoundedCornerShape(percent = 50))
                    .background(if (isSelected) TechFixAccentSolid.copy(alpha = 0.16f) else TechFixChipBackground)
                    .clickable { onSelect(option) }
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    option,
                    color = if (isSelected) TechFixAccentSolid else TechFixTextSecondary,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun FormField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    singleLine: Boolean = true,
    minHeight: Int = 0
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = TechFixTextTertiary, fontSize = 13.sp) },
        singleLine = singleLine,
        modifier = Modifier
            .fillMaxWidth()
            .height(if (minHeight > 0) minHeight.dp else 56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TechFixTextPrimary,
            unfocusedTextColor = TechFixTextPrimary,
            focusedContainerColor = TechFixSurface,
            unfocusedContainerColor = TechFixSurface,
            focusedBorderColor = TechFixTextSecondary,
            unfocusedBorderColor = TechFixBorder,
            cursorColor = Color.White
        )
    )
}
