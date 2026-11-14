package com.example.techfix.ui.payment

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.techfix.ui.orders.DetailHeader
import com.example.techfix.ui.theme.TechFixAccentBrush
import com.example.techfix.ui.theme.TechFixAccentSolid
import com.example.techfix.ui.theme.TechFixBackground
import com.example.techfix.ui.theme.TechFixSurface
import com.example.techfix.ui.theme.TechFixTextPrimary
import com.example.techfix.ui.theme.TechFixTextSecondary
import com.example.techfix.ui.theme.TechFixTextTertiary

private data class PaymentMethod(val id: String, val name: String, val hint: String)

private val methods = listOf(
    PaymentMethod("pix", "Pix", "Pagamento instantâneo"),
    PaymentMethod("cash", "Dinheiro", "Pague ao técnico"),
    PaymentMethod("card", "Cartão", "Na maquininha do técnico"),
    PaymentMethod("wallet", "Carteira TechFix", "Saldo R$ 0,00")
)

/** Escolha da forma de pagamento preferida (apenas uma preferência, sem cobrança real). */
@Composable
fun PaymentMethodsScreen(
    initialMethod: String,
    onBack: () -> Unit,
    onSave: (String) -> Unit
) {
    var selected by remember { mutableStateOf(initialMethod) }

    Surface(modifier = Modifier.fillMaxSize(), color = TechFixBackground) {
        Column(modifier = Modifier.fillMaxSize()) {
            DetailHeader(title = "Formas de pagamento", onBack = onBack)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Como você prefere pagar?", color = TechFixTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)

                methods.forEach { method ->
                    val isSelected = method.id == selected
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) TechFixAccentSolid.copy(alpha = 0.08f) else TechFixSurface)
                            .clickable { selected = method.id }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(method.name, color = TechFixTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text(method.hint, color = TechFixTextSecondary, fontSize = 12.sp)
                        }
                        RadioButton(
                            selected = isSelected,
                            onClick = { selected = method.id },
                            colors = RadioButtonDefaults.colors(selectedColor = TechFixAccentSolid)
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))
                Text(
                    "Demonstração: este app não processa pagamentos reais nem guarda dados de cartão. " +
                        "O pagamento é combinado com o profissional após a conclusão do serviço.",
                    color = TechFixTextTertiary,
                    fontSize = 12.sp
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .height(52.dp)
                    .clip(RoundedCornerShape(percent = 50))
                    .background(TechFixAccentBrush)
                    .clickable { onSave(selected) },
                contentAlignment = Alignment.Center
            ) {
                Text("Salvar alterações", color = TechFixTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
