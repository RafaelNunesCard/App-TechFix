package com.example.techfix.ui.about

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.item
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * Tela "Sobre Nós": uma lista rolável (LazyColumn) com as 6 seções do
 * Figma (definidas em AboutSections.kt) + rodapé, e a roda de
 * navegação (CircularNavWheel, em AboutComponents.kt) flutuando por
 * cima, grudada na borda direita, sincronizada com o scroll nos dois
 * sentidos:
 * - Rolar a tela manualmente atualiza qual fatia fica destacada.
 * - Tocar numa fatia rola a tela até a seção correspondente.
 */
@Composable
fun AboutScreen() {
    // "rememberLazyListState" guarda (e sobrevive a recomposições) a
    // posição de scroll da lista — é o que nos permite tanto LER em
    // que ponto o usuário está, quanto MANDAR a lista rolar sozinha.
    val listState = rememberLazyListState()

    // "animateScrollToItem" é uma função "suspend" — só pode ser chamada
    // de dentro de uma corrotina. "rememberCoroutineScope" nos dá um
    // escopo pra abrir uma corrotina a partir de um clique (que não é
    // suspend por natureza).
    val coroutineScope = rememberCoroutineScope()

    // "derivedStateOf" recalcula esse valor só quando o resultado muda
    // de verdade — evita redesenhar a roda a cada pixel rolado, e só
    // atualiza quando o item visível no topo realmente muda de índice.
    // OBS: essa é uma aproximação simples (pega o primeiro item visível
    // da lista). Se depois você achar que a troca de destaque acontece
    // "cedo" ou "tarde" demais durante o scroll, este é o lugar pra ajustar.
    val currentSectionIndex by remember {
        derivedStateOf { listState.firstVisibleItemIndex.coerceIn(0, aboutSections.lastIndex) }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = AboutBackgroundDark) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                // Cada "item" do LazyColumn é uma das 6 seções, na MESMA
                // ordem da lista "aboutSections" (em AboutComponents.kt)
                // — essa ordem é o que faz o índice do scroll bater com
                // o índice da fatia certa na roda.
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

                // O rodapé fica depois das 6 seções, fora da contagem
                // que a roda usa (por isso é um "item" extra solto, e
                // não faz parte da lista "aboutSections").
                item { AboutFooterSection() }
            }

            CircularNavWheel(
                selectedIndex = currentSectionIndex,
                onSectionSelected = { index ->
                    coroutineScope.launch {
                        listState.animateScrollToItem(index)
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    // Empurra a roda pra direita, fazendo boa parte dela
                    // "sangrar" pra fora da tela — só a fatia esquerda
                    // do anel fica visível de verdade.
                    .offset(x = 90.dp)
            )
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
