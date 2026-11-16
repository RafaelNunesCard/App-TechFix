package com.example.techfix.ui.payment

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.techfix.data.CardPrefs
import com.example.techfix.data.SavedCard
import com.example.techfix.ui.orders.DetailHeader
import com.example.techfix.ui.theme.TechFixAccentBrush
import com.example.techfix.ui.theme.TechFixAccentSolid
import com.example.techfix.ui.theme.TechFixBackground
import com.example.techfix.ui.theme.TechFixBorder
import com.example.techfix.ui.theme.TechFixGreen
import com.example.techfix.ui.theme.TechFixGreenBg
import com.example.techfix.ui.theme.TechFixRed
import com.example.techfix.ui.theme.TechFixRedBg
import com.example.techfix.ui.theme.TechFixSurface
import com.example.techfix.ui.theme.TechFixTextPrimary
import com.example.techfix.ui.theme.TechFixTextSecondary
import com.example.techfix.ui.theme.TechFixTextTertiary
import com.example.techfix.ui.theme.TechFixWarning
import com.example.techfix.ui.theme.TechFixWarningBg
import java.util.Calendar

private const val CARD_PREFIX = "card:"

private data class OtherMethod(
    val id: String,
    val name: String,
    val hint: String,
    val icon: ImageVector,
    val tint: Color,
    val bg: Color
)

private val otherMethods = listOf(
    OtherMethod("pix", "Pix", "Pagamento instantâneo", Icons.Outlined.QrCode2, TechFixGreen, TechFixGreenBg),
    OtherMethod("cash", "Dinheiro", "Pague ao técnico", Icons.Outlined.Payments, TechFixWarning, TechFixWarningBg),
    OtherMethod("wallet", "Carteira TechFix", "Saldo R$ 0,00", Icons.Outlined.AccountBalanceWallet, TechFixRed, TechFixRedBg)
)

/** Só texto: não desenhamos logos de bandeiras. */
private fun brandOf(digits: String): String = when {
    digits.startsWith("4") -> "Visa"
    digits.startsWith("5") || digits.startsWith("2") -> "Mastercard"
    else -> "Cartão"
}

/**
 * Formas de pagamento (apenas preferência, sem cobrança real).
 * O valor salvo é "pix", "cash", "wallet" ou "card:<id do cartão>".
 */
@Composable
fun PaymentMethodsScreen(
    initialMethod: String,
    onBack: () -> Unit,
    onSave: (String) -> Unit
) {
    val context = LocalContext.current
    var selected by remember { mutableStateOf(initialMethod) }
    var adding by remember { mutableStateOf(false) }
    val savedCards = remember { mutableStateListOf<SavedCard>().apply { addAll(CardPrefs.list(context)) } }
    // Cartões com "Salvar para próximas compras" desligado: valem só enquanto esta tela está aberta.
    val sessionCards = remember { mutableStateListOf<SavedCard>() }
    val allCards = savedCards + sessionCards

    BackHandler(enabled = adding) { adding = false }

    if (adding) {
        NewCardScreen(
            onBack = { adding = false },
            onAdd = { card, keep ->
                if (keep) {
                    CardPrefs.add(context, card)
                    savedCards.add(card)
                } else {
                    sessionCards.add(card)
                }
                selected = CARD_PREFIX + card.id
                adding = false
            }
        )
        return
    }

    val selectedCard = allCards.firstOrNull { CARD_PREFIX + it.id == selected }

    Surface(modifier = Modifier.fillMaxSize(), color = TechFixBackground) {
        Column(modifier = Modifier.fillMaxSize()) {
            DetailHeader(title = "Formas de Pagamento", onBack = onBack)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Cartão em destaque (o escolhido, ou o primeiro salvo)
                val shown = selectedCard ?: allCards.firstOrNull()
                if (shown != null) {
                    CardPreview(
                        brand = shown.brand,
                        numberText = "••••  ••••  ••••  ${shown.last4}",
                        holder = shown.holder.uppercase(),
                        expiry = shown.expiry,
                        badge = if (shown.id == selectedCard?.id) "Selecionado" else null
                    )
                } else {
                    CardPreview(
                        brand = "Cartão",
                        numberText = "••••  ••••  ••••  ••••",
                        holder = "NENHUM CARTÃO SALVO",
                        expiry = "MM/AA",
                        badge = null
                    )
                }

                SectionLabel("Cartões salvos")

                if (allCards.isEmpty()) {
                    Text("Você ainda não adicionou cartões.", color = TechFixTextTertiary, fontSize = 13.sp)
                }
                allCards.forEach { card ->
                    val isSelected = CARD_PREFIX + card.id == selected
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(TechFixSurface)
                            .border(1.dp, if (isSelected) TechFixAccentSolid.copy(alpha = 0.6f) else TechFixBorder, RoundedCornerShape(16.dp))
                            .clickable { selected = CARD_PREFIX + card.id }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconTile(Icons.Outlined.CreditCard, TechFixTextSecondary, Color(0x14FFFFFF))
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("${card.brand} •••• ${card.last4}", color = TechFixTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text("Validade ${card.expiry}", color = TechFixTextSecondary, fontSize = 12.sp)
                        }
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .clickable {
                                    CardPrefs.remove(context, card.id)
                                    savedCards.removeAll { it.id == card.id }
                                    sessionCards.removeAll { it.id == card.id }
                                    if (selected == CARD_PREFIX + card.id) selected = ""
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.DeleteOutline, contentDescription = "Remover cartão", tint = TechFixTextTertiary, modifier = Modifier.size(18.dp))
                        }
                        RadioButton(
                            selected = isSelected,
                            onClick = { selected = CARD_PREFIX + card.id },
                            colors = RadioButtonDefaults.colors(selectedColor = TechFixAccentSolid)
                        )
                    }
                }

                // Botão tracejado "Adicionar cartão"
                val dashColor = TechFixTextTertiary
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { adding = true }
                        .drawBehind {
                            drawRoundRect(
                                color = dashColor,
                                cornerRadius = CornerRadius(14.dp.toPx()),
                                style = Stroke(
                                    width = 1.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f))
                                )
                            )
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, tint = TechFixAccentSolid, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Adicionar cartão", color = TechFixAccentSolid, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }

                SectionLabel("Outras formas")

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(TechFixSurface)
                        .border(1.dp, TechFixBorder, RoundedCornerShape(16.dp))
                ) {
                    otherMethods.forEach { method ->
                        val isSelected = method.id == selected
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selected = method.id }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconTile(method.icon, method.tint, method.bg)
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(method.name, color = TechFixTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                Text(method.hint, color = TechFixTextSecondary, fontSize = 12.sp)
                            }
                            if (isSelected) {
                                Icon(Icons.Filled.CheckCircle, contentDescription = "Selecionado", tint = TechFixAccentSolid, modifier = Modifier.size(22.dp))
                            } else {
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = TechFixTextTertiary, modifier = Modifier.size(22.dp))
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(TechFixSurface)
                        .padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(Icons.Outlined.VerifiedUser, contentDescription = null, tint = TechFixGreen, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "Demonstração: nenhum pagamento real é processado. Só guardamos a bandeira, os 4 últimos dígitos, o nome e a validade. O pagamento é combinado com o profissional após o serviço.",
                        color = TechFixTextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }

                Spacer(Modifier.height(4.dp))
            }

            PrimaryButton(
                text = "Salvar alterações",
                onClick = { onSave(selected.ifBlank { "pix" }) },
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp).navigationBarsPadding()
            )
        }
    }
}

// =========================================================================================
// NOVO CARTÃO
// =========================================================================================

@Composable
private fun NewCardScreen(
    onBack: () -> Unit,
    onAdd: (SavedCard, Boolean) -> Unit
) {
    var number by remember { mutableStateOf("") }   // já formatado "1234 5678 ..."
    var holder by remember { mutableStateOf("") }
    var expiry by remember { mutableStateOf("") }   // "MM/AA"
    var cvv by remember { mutableStateOf("") }
    var keep by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    val digits = number.filter { it.isDigit() }

    fun submit() {
        val month = expiry.take(2).toIntOrNull() ?: 0
        val year = expiry.drop(3).toIntOrNull() ?: -1
        val now = Calendar.getInstance()
        val currentYear = now.get(Calendar.YEAR) % 100
        val currentMonth = now.get(Calendar.MONTH) + 1
        error = when {
            digits.length < 13 -> "Informe o número completo do cartão."
            holder.isBlank() -> "Informe o nome impresso no cartão."
            expiry.length != 5 || month !in 1..12 -> "Validade inválida. Use MM/AA."
            year < currentYear || (year == currentYear && month < currentMonth) -> "Este cartão está vencido."
            cvv.length < 3 -> "Informe o CVV (3 ou 4 dígitos)."
            else -> null
        }
        if (error != null) return
        // O número completo e o CVV nunca saem desta função.
        onAdd(
            SavedCard(
                id = System.currentTimeMillis().toString(),
                brand = brandOf(digits),
                last4 = digits.takeLast(4),
                holder = holder.trim(),
                expiry = expiry
            ),
            keep
        )
    }

    Surface(modifier = Modifier.fillMaxSize(), color = TechFixBackground) {
        Column(modifier = Modifier.fillMaxSize().imePadding()) {
            DetailHeader(title = "Novo Cartão", onBack = onBack)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                val padded = digits.padEnd(16, '•').chunked(4).joinToString("  ")
                CardPreview(
                    brand = brandOf(digits),
                    numberText = padded,
                    holder = holder.uppercase().ifBlank { "NOME IMPRESSO NO CARTÃO" },
                    expiry = expiry.ifBlank { "MM/AA" },
                    badge = null
                )

                CardField(
                    label = "Número do cartão",
                    value = number,
                    placeholder = "•••• •••• •••• ••••",
                    keyboardType = KeyboardType.Number,
                    onChange = { raw ->
                        number = raw.filter { it.isDigit() }.take(16).chunked(4).joinToString(" ")
                    }
                )
                CardField(
                    label = "Nome impresso",
                    value = holder,
                    placeholder = "Como aparece no cartão",
                    keyboardType = KeyboardType.Text,
                    onChange = { holder = it.take(26) }
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CardField(
                        label = "Validade (MM/AA)",
                        value = expiry,
                        placeholder = "MM/AA",
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.weight(1f),
                        onChange = { raw ->
                            val d = raw.filter { it.isDigit() }.take(4)
                            expiry = if (d.length > 2) d.take(2) + "/" + d.drop(2) else d
                        }
                    )
                    CardField(
                        label = "CVV",
                        value = cvv,
                        placeholder = "•••",
                        keyboardType = KeyboardType.NumberPassword,
                        visual = PasswordVisualTransformation(),
                        modifier = Modifier.weight(1f),
                        onChange = { cvv = it.filter { c -> c.isDigit() }.take(4) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Salvar para próximas compras", color = TechFixTextPrimary, fontSize = 14.sp, modifier = Modifier.weight(1f))
                    Switch(
                        checked = keep,
                        onCheckedChange = { keep = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = TechFixAccentSolid,
                            uncheckedThumbColor = TechFixTextSecondary,
                            uncheckedTrackColor = TechFixBorder
                        )
                    )
                }

                val currentError = error
                if (currentError != null) {
                    Text(currentError, color = TechFixRed, fontSize = 13.sp)
                }
            }

            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp).navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PrimaryButton(text = "Adicionar cartão", onClick = { submit() }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Lock, contentDescription = null, tint = TechFixTextTertiary, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Número completo e CVV não são armazenados", color = TechFixTextTertiary, fontSize = 12.sp)
                }
            }
        }
    }
}

// =========================================================================================
// COMPONENTES
// =========================================================================================

@Composable
private fun SectionLabel(text: String) {
    Text(text, color = TechFixTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
}

@Composable
private fun IconTile(icon: ImageVector, tint: Color, bg: Color) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bg),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun PrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(percent = 50))
            .background(TechFixAccentBrush)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = TechFixTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun CardField(
    label: String,
    value: String,
    placeholder: String,
    keyboardType: KeyboardType,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    visual: VisualTransformation = VisualTransformation.None
) {
    Column(modifier = modifier) {
        Text(label, color = TechFixTextPrimary, fontSize = 13.sp)
        Spacer(Modifier.height(8.dp))
        BasicTextField(
            value = value,
            onValueChange = onChange,
            singleLine = true,
            visualTransformation = visual,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            textStyle = TextStyle(color = TechFixTextPrimary, fontSize = 15.sp),
            cursorBrush = SolidColor(TechFixAccentSolid),
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { inner ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(TechFixSurface)
                        .border(1.dp, TechFixBorder, RoundedCornerShape(14.dp))
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isEmpty()) {
                        Text(placeholder, color = TechFixTextTertiary, fontSize = 15.sp)
                    }
                    inner()
                }
            }
        )
    }
}

/** Cartão ilustrativo. Bandeira só em texto (sem logo). */
@Composable
private fun CardPreview(
    brand: String,
    numberText: String,
    holder: String,
    expiry: String,
    badge: String?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF23232C), Color(0xFF141418))))
            .border(1.dp, TechFixBorder, RoundedCornerShape(22.dp))
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.CreditCard, contentDescription = null, tint = TechFixTextPrimary, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(10.dp))
            Text(brand, color = TechFixTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            if (badge != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(TechFixGreenBg)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(badge, color = TechFixGreen, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .size(width = 38.dp, height = 28.dp)
                .clip(RoundedCornerShape(6.dp))
                .border(1.5.dp, Color(0xFFB8A27A), RoundedCornerShape(6.dp))
        )

        Spacer(Modifier.height(18.dp))
        Text(numberText, color = TechFixTextPrimary, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)

        Spacer(Modifier.height(18.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f)) {
                Text("TITULAR", color = TechFixTextTertiary, fontSize = 10.sp)
                Spacer(Modifier.height(2.dp))
                Text(holder, color = TechFixTextPrimary, fontSize = 13.sp, maxLines = 1)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("VALIDADE", color = TechFixTextTertiary, fontSize = 10.sp)
                Spacer(Modifier.height(2.dp))
                Text(expiry, color = TechFixTextPrimary, fontSize = 13.sp)
            }
        }
    }
}
