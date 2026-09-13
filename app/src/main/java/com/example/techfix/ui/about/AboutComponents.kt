package com.example.techfix.ui.about

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// ---- Design tokens só da página "Sobre Nós" (cores do Figma) ----
val AboutBackgroundDark = Color(0xFF0D0D0D)
val AboutCardDark = Color(0xFF1A1A1A)
val AboutAccentOrange = Color(0xFFFF6A2B)
val AboutTextPrimary = Color(0xFFFFFFFF)
val AboutTextSecondary = Color(0xFF9B9B9B)

/**
 * Uma das 6 seções da página. "id" identifica a seção no código;
 * "shortLabel" é o texto curto que aparece dentro da fatia da roda
 * (por isso é mais curto que o título completo da seção).
 */
data class AboutSectionInfo(val id: String, val shortLabel: String)

val aboutSections = listOf(
    AboutSectionInfo("company", "Our Company"),
    AboutSectionInfo("why_us", "Why Us"),
    AboutSectionInfo("values", "Our Values"),
    AboutSectionInfo("services", "Services"),
    AboutSectionInfo("how_we_work", "How We Work"),
    AboutSectionInfo("team", "Our Team")
)

/**
 * A roda de navegação: um ANEL (não um círculo cheio — por isso o
 * centro fica vazio) dividido em 6 fatias, uma por seção. Ela fica
 * ancorada perto da borda direita da tela; só a parte esquerda do
 * anel aparece de verdade, o resto "sangra" pra fora da tela — é
 * assim que criamos o efeito "parcialmente fora da tela" do Figma
 * sem precisar de nenhum truque especial, só posicionamento.
 *
 * CONCEITOS NOVOS AQUI (pra quem nunca usou Canvas no Compose):
 *
 * - "Canvas" é uma área onde VOCÊ desenha formas manualmente
 *   (arcos, linhas, círculos) usando coordenadas em pixels — é o
 *   oposto de usar componentes prontos como Text ou Button.
 * - "pointerInput" + "detectTapGestures" são como a gente "escuta"
 *   toques manualmente dentro de um Canvas, recebendo a posição
 *   exata (x, y) de onde o dedo tocou.
 * - Pra saber EM QUAL FATIA o usuário tocou, e pra saber ONDE
 *   desenhar o texto de cada fatia, usamos trigonometria (seno e
 *   cosseno) — a mesma matemática que posiciona os ponteiros de
 *   um relógio analógico em volta do centro dele.
 */
@Composable
fun CircularNavWheel(
    selectedIndex: Int,
    onSectionSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    sections: List<AboutSectionInfo> = aboutSections
) {
    val diameter = 260.dp
    val sliceAngle = 360f / sections.size

    Box(modifier = modifier.size(diameter), contentAlignment = Alignment.Center) {
        Canvas(
            modifier = Modifier
                .size(diameter)
                .pointerInput(sections.size) {
                    detectTapGestures { tapOffset ->
                        val centerPx = Offset(size.width / 2f, size.height / 2f)
                        val dx = tapOffset.x - centerPx.x
                        val dy = tapOffset.y - centerPx.y
                        val distance = sqrt(dx * dx + dy * dy)

                        val outerRadius = size.width / 2f
                        val innerRadius = outerRadius * 0.55f

                        // Só reage a toques DENTRO do anel — ignora o
                        // buraco vazio do centro e a área fora do círculo.
                        if (distance in innerRadius..outerRadius) {
                            // atan2 devolve, em radianos, o ângulo do
                            // toque em relação ao centro do círculo.
                            var angleDeg = Math.toDegrees(atan2(dy, dx).toDouble())
                            if (angleDeg < 0) angleDeg += 360.0
                            val index = (angleDeg / sliceAngle).toInt()
                                .coerceIn(0, sections.size - 1)
                            onSectionSelected(index)
                        }
                    }
                }
        ) {
            val outerRadius = size.minDimension / 2f
            val innerRadius = outerRadius * 0.55f
            val baseThickness = outerRadius - innerRadius
            // O raio "do meio" do anel: é nele que o traço (Stroke) fica
            // centralizado ao desenhar o arco.
            val midRadius = (outerRadius + innerRadius) / 2f
            val ovalSize = Size(midRadius * 2f, midRadius * 2f)
            val ovalTopLeft = Offset(
                (size.width - ovalSize.width) / 2f,
                (size.height - ovalSize.height) / 2f
            )

            sections.forEachIndexed { index, _ ->
                val isSelected = index == selectedIndex
                // A fatia selecionada fica mais "grossa" — isso cria o
                // efeito de destaque/expansão pedido no design (ela
                // cresce tanto pra dentro quanto pra fora do anel).
                val thickness = if (isSelected) baseThickness + 18f else baseThickness

                drawArc(
                    color = if (isSelected) AboutAccentOrange else AboutCardDark,
                    startAngle = index * sliceAngle,
                    sweepAngle = sliceAngle - 2f, // o "-2" cria um respiro entre as fatias
                    useCenter = false,
                    topLeft = ovalTopLeft,
                    size = ovalSize,
                    style = Stroke(width = thickness)
                )
            }
        }

        // O texto no centro VAZIO do anel: "ACTIVE 01/06".
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "ACTIVE", color = AboutTextSecondary, fontSize = 10.sp, letterSpacing = 1.sp)
            Text(
                text = "${(selectedIndex + 1).toString().padStart(2, '0')}/${sections.size.toString().padStart(2, '0')}",
                color = AboutAccentOrange,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // O rótulo de texto de cada fatia, posicionado no ângulo médio
        // dela usando seno/cosseno.
        sections.forEachIndexed { index, section ->
            val isSelected = index == selectedIndex
            val midAngleDeg = index * sliceAngle + sliceAngle / 2f
            val midAngleRad = Math.toRadians(midAngleDeg.toDouble())
            val labelRadiusDp = 92 // a que distância do centro o texto fica (dentro do anel)

            // cos/sin devolvem um valor entre -1 e 1; multiplicando pelo
            // raio, isso vira um deslocamento em x/y a partir do centro
            // — exatamente como posicionar o ponteiro de um relógio.
            val offsetXDp = (cos(midAngleRad) * labelRadiusDp).toInt().dp
            val offsetYDp = (sin(midAngleRad) * labelRadiusDp).toInt().dp

            Text(
                text = section.shortLabel,
                color = if (isSelected) Color.White else AboutTextSecondary,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = offsetXDp, y = offsetYDp)
            )
        }
    }
}
