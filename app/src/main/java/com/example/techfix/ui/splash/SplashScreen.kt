package com.example.techfix.ui.splash

import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.techfix.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val currentOnFinished by rememberUpdatedState(onFinished)

    // Progresso visual da barra.
    var progress by remember { mutableFloatStateOf(0f) }

    // Anima a barra de 0% até 100% em 2 segundos.
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(
            durationMillis = 2_000
        ),
        label = "splash_progress"
    )

    LaunchedEffect(Unit) {
        // Inicia o carregamento.
        progress = 1f

        // Aguarda o mesmo tempo da animação.
        delay(2_000L)

        // Navega para a próxima tela.
        currentOnFinished()
    }

    SplashContent(
        progress = animatedProgress
    )
}

@Composable
private fun SplashContent(
    progress: Float = 0f
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111111))
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            val screenHeight = maxHeight
            val glowSize = maxWidth * 1.2f
            val logoWidth = (maxWidth * 0.66f).coerceAtMost(280.dp)

            // Brilho estático atrás da logo.
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = screenHeight * 0.12f)
                    .size(glowSize)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0x553F202A),
                                Color(0x222D1C16),
                                Color.Transparent
                            )
                        )
                    )
            )

            Image(
                painter = painterResource(
                    id = R.drawable.splash_techfix_logo
                ),
                contentDescription = "TechFix",
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = screenHeight * 0.27f)
                    .width(logoWidth)
                    .aspectRatio(1.5f)
            )

            Text(
                text = "Manutenção e reparo na palma da sua mão",
                color = Color(0xFFCCCCCC),
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = screenHeight * 0.53f)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = screenHeight * 0.77f)
            ) {
                // Barra de carregamento.
                Box(
                    modifier = Modifier
                        .width(110.dp)
                        .height(9.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFF333333))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(50))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFFFFAD32),
                                        Color(0xFFFF624E),
                                        Color(0xFFFF1683)
                                    )
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (progress >= 1f) {
                        "Concluído!"
                    } else {
                        "Carregando..."
                    },
                    color = Color(0xFF999999),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// Prévia visual.
@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 800
)
@Composable
private fun SplashScreenPreview() {
    SplashContent(progress = 0.5f)
}
