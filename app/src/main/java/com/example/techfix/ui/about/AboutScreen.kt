package com.example.techfix.ui.about

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * Tela "Sobre Nós": uma lista rolável (LazyColumn) com as 6 seções do
 * Figma (definidas em AboutSections.kt) + rodapé, e a roda de
 * navegação (CircularNavWheel, em AboutComponents.kt) flutuando por
 * cima, grudada na borda direita, sincronizada com o scroll nos dois
 * sentidos, e que pode encolher/expandir através do botão de olho.
 *
 * @param onBack chamado ao tocar no botão de voltar (canto superior
 *        esquerdo) — quem chama essa tela decide o que fazer
 *        (normalmente, popBackStack() no NavHost do MainActivity.kt).
 */
@Composable
fun AboutScreen(onBack: () -> Unit = {}) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val currentSectionIndex by remember {
        derivedStateOf { listState.firstVisibleItemIndex.coerceIn(0, aboutSections.lastIndex) }
    }

    // Controla se a roda está mostrando as fatias (expandida) ou só
    // o botão de olho (encolhida). Começa expandida, igual ao Figma.
    var wheelExpanded by remember { mutableStateOf(true) }

    // Quando a roda encolhe, ela também precisa se aproximar da borda
    // direita — senão fica um espaço vazio enorme no lugar onde o
    // anel grande estava. Por isso animamos esse deslocamento junto
    // com o tamanho da roda (que é animado dentro de CircularNavWheel,
    // em AboutComponents.kt).
    val wheelOffsetX by animateDpAsState(
        targetValue = if (wheelExpanded) 90.dp else 12.dp,
        label = "wheelOffsetX"
    )

    Surface(modifier = Modifier.fillMaxSize(), color = AboutBackgroundDark) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                // Cada "item" é uma das 6 seções, na MESMA ordem de
                // "aboutSections" (em AboutComponents.kt) — essa ordem
                // é o que faz o índice do scroll bater com o índice
                // da fatia certa na roda.
                items(aboutSections) { section ->
                    when (section.id) {
                        "company" -> OurCompanySection()
                        "why_us" -> WhyChooseSection()
                        "values" -> OurValuesSection()
                        "services" -> OurServicesSection()
                        "how_we_work" -> HowWeWorkSection()
                        "team" -> OurTeamSection()
                    }
                }

                item { AboutFooterSection() }
            }

            CircularNavWheel(
                selectedIndex = currentSectionIndex,
                onSectionSelected = { index ->
                    coroutineScope.launch {
                        listState.animateScrollToItem(index)
                    }
                },
                expanded = wheelExpanded,
                onToggleExpanded = { wheelExpanded = !wheelExpanded },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = wheelOffsetX)
            )

            // Botão de voltar flutuante, fixo no canto superior
            // esquerdo (fora do LazyColumn, direto no Box — por isso
            // não rola junto com o conteúdo).
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0x99000000))
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color.White
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0D0D0D)
@Composable
private fun AboutScreenPreview() {
    MaterialTheme {
        AboutScreen()
    }
}
