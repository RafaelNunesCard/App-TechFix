package com.example.techfix.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.techfix.R
import kotlinx.coroutines.delay

private val Orange = Color(0xFFFF8A3D)
private val Pink = Color(0xFFFF3D68)
private const val TAGLINE = "Conectando quem precisa com quem resolve"

/**
 * Abertura animada: duas bolinhas (quem precisa e quem resolve) se aproximam,
 * se encontram numa onda de luz e a logo aparece. Depois a frase é "digitada".
 */
@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val currentOnFinished by rememberUpdatedState(onFinished)
    val t = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        t.animateTo(1f, tween(durationMillis = 2_600, easing = LinearEasing))
        delay(500L)
        currentOnFinished()
    }

    SplashContent(t = t.value)
}

/** Trecho [a, b] da animação, de 0 a 1. */
private fun segment(t: Float, a: Float, b: Float): Float = ((t - a) / (b - a)).coerceIn(0f, 1f)

@Composable
private fun SplashContent(t: Float) {
    val dotsP = FastOutSlowInEasing.transform(segment(t, 0.00f, 0.38f))
    val dotsFade = 1f - segment(t, 0.38f, 0.46f)
    val ringP = FastOutSlowInEasing.transform(segment(t, 0.38f, 0.85f))
    val ring2P = FastOutSlowInEasing.transform(segment(t, 0.46f, 0.95f))
    val logoP = FastOutSlowInEasing.transform(segment(t, 0.36f, 0.66f))
    val tagP = segment(t, 0.62f, 0.98f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0C10))
    ) {
        Box(
            modifier = Modifier
                .safeDrawingPadding()
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = (-40).dp)
                    .size(320.dp),
                contentAlignment = Alignment.Center
            ) {
                // Brilho atrás da logo.
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Pink.copy(alpha = 0.28f * logoP), Orange.copy(alpha = 0.10f * logoP), Color.Transparent),
                            center = center,
                            radius = size.width * 0.5f
                        ),
                        radius = size.width * 0.5f,
                        center = center
                    )

                    // Bolinhas se aproximando.
                    val half = size.width / 2f
                    val dist = half * 0.95f * (1f - dotsP)
                    val dotRadius = 9.dp.toPx()
                    if (dotsFade > 0f) {
                        drawCircle(Orange.copy(alpha = dotsFade), dotRadius, Offset(center.x - dist, center.y))
                        drawCircle(Pink.copy(alpha = dotsFade), dotRadius, Offset(center.x + dist, center.y))
                    }

                    // Ondas depois do encontro.
                    if (ringP > 0f) {
                        drawCircle(
                            brush = Brush.sweepGradient(listOf(Orange, Pink, Orange), center = center),
                            radius = half * ringP,
                            center = center,
                            alpha = (1f - ringP),
                            style = Stroke(width = 3.dp.toPx())
                        )
                    }
                    if (ring2P > 0f) {
                        drawCircle(
                            brush = Brush.sweepGradient(listOf(Pink, Orange, Pink), center = center),
                            radius = half * ring2P,
                            center = center,
                            alpha = (1f - ring2P) * 0.7f,
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }
                }

                Image(
                    painter = painterResource(id = R.drawable.splash_techfix_logo),
                    contentDescription = "TechFix",
                    modifier = Modifier
                        .width(220.dp)
                        .aspectRatio(1.5f)
                        .graphicsLayer {
                            alpha = logoP
                            val s = 0.7f + 0.3f * logoP
                            scaleX = s
                            scaleY = s
                        }
                )
            }

            // Frase "digitada" (o resto fica transparente para não deslocar o texto).
            val typed = (tagP * TAGLINE.length).toInt().coerceIn(0, TAGLINE.length)
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = Color(0xFFD4D4D8))) { append(TAGLINE.take(typed)) }
                    withStyle(SpanStyle(color = Color.Transparent)) { append(TAGLINE.drop(typed)) }
                },
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = 170.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun SplashScreenPreview() {
    SplashContent(t = 0.8f)
}
