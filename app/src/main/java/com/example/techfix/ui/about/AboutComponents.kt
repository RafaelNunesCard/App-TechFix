package com.example.techfix.ui.about

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

// Constantes da roda, extraídas pra ficar fácil de ajustar depois sem
// caçar números "mágicos" espalhados pelo meio do desenho.
private val WheelExpandedDiameter = 260.dp
private val WheelCollapsedDiameter = 60.dp
private const val InnerRadiusRatio = 0.55f // quão grande é o "buraco" vazio do meio, em relação ao raio total
private const val SelectedExtraThickness = 18f
private const val SliceGapDegrees = 2f
private const val LabelRadiusDp = 92

/**
 * Uma das 6 seções da página. "id" identifica a seção no código;
 * "shortLabel" é o texto curto que aparece dentro da fatia da roda.
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
 * A roda de navegação: um ANEL dividido em 6 fatias, com um botão
 * redondo no centro (ícone de olho) que encolhe/expande ela.
 *
 * @param expanded true = mostra o anel com todas as fatias; false =
 *        encolhe pra só o botão de olho, bem pequeno
 * @param onToggleExpanded chamado ao tocar no botão de olho
 */
@Composable
fun CircularNavWheel(
    selectedIndex: Int,
    onSectionSelected: (Int) -> Unit,
    expanded: Boolean,
    onToggleExpanded: () -> Unit,
    modifier: Modifier = Modifier,
    sections: List<AboutSectionInfo> = aboutSections
) {
    // "animateDpAsState" faz o tamanho da roda mudar SUAVEMENTE (com
    // animação) ao expandir/encolher, em vez de "pular" de um valor
    // pro outro de uma vez.
    val diameter by animateDpAsState(
        targetValue = if (expanded) WheelExpandedDiameter else WheelCollapsedDiameter,
        label = "wheelDiameter"
    )
    val sliceAngle = 360f / sections.size

    Box(modifier = modifier.size(diameter), contentAlignment = Alignment.Center) {
        // O anel, os rótulos e o "ACTIVE 0X/06" só existem enquanto a
        // roda está expandida — assim, quando encolhida, não fica
        // nem desenhado, nem reagindo a toque à toa.
        if (expanded) {
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
                            val innerRadius = outerRadius * InnerRadiusRatio

                            // Só reage a toques DENTRO do anel — ignora o
                            // buraco vazio do centro (onde fica o botão de
                            // olho) e a área fora do círculo.
                            if (distance in innerRadius..outerRadius) {
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
                val innerRadius = outerRadius * InnerRadiusRatio
                val baseThickness = outerRadius - innerRadius
                val midRadius = (outerRadius + innerRadius) / 2f
                val ovalSize = Size(midRadius * 2f, midRadius * 2f)
                val ovalTopLeft = Offset(
                    (size.width - ovalSize.width) / 2f,
                    (size.height - ovalSize.height) / 2f
                )

                sections.forEachIndexed { index, _ ->
                    val isSelected = index == selectedIndex
                    // A fatia selecionada fica mais "grossa" — cresce
                    // tanto pra dentro quanto pra fora do anel.
                    val thickness = if (isSelected) baseThickness + SelectedExtraThickness else baseThickness

                    drawArc(
                        color = if (isSelected) AboutAccentOrange else AboutCardDark,
                        startAngle = index * sliceAngle,
                        sweepAngle = sliceAngle - SliceGapDegrees,
                        useCenter = false,
                        topLeft = ovalTopLeft,
                        size = ovalSize,
                        style = Stroke(width = thickness)
                    )
                }
            }

            // Rótulo de texto de cada fatia, no ângulo médio dela.
            sections.forEachIndexed { index, section ->
                val isSelected = index == selectedIndex
                val midAngleDeg = index * sliceAngle + sliceAngle / 2f
                val midAngleRad = Math.toRadians(midAngleDeg.toDouble())

                val offsetXDp = (cos(midAngleRad) * LabelRadiusDp).toInt().dp
                val offsetYDp = (sin(midAngleRad) * LabelRadiusDp).toInt().dp

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

            // "ACTIVE 0X/06", um pouco acima do botão de olho, dentro
            // do buraco vazio do anel.
            Text(
                text = "ACTIVE ${(selectedIndex + 1).toString().padStart(2, '0')}/" +
                    sections.size.toString().padStart(2, '0'),
                color = AboutAccentOrange,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = (-20).dp)
            )
        }

        // O botão de olho: existe SEMPRE, expandida ou não — é ele que
        // controla o estado. Quando a roda encolhe, ele é a ÚNICA
        // coisa visível (um botãozinho redondo flutuando na borda).
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(AboutAccentOrange)
                .clickable { onToggleExpanded() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                // Olho aberto = mostrando as fatias; olho fechado = escondidas.
                imageVector = if (expanded) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                contentDescription = if (expanded) "Esconder seções" else "Mostrar seções",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
