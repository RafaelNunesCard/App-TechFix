package com.example.techfix.ui.about

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.item
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
 * sentidos:
 * - Rolar a tela manualmente atualiza qual fatia fica destacada.
 * - Tocar numa fatia rola a tela até a seção correspondente.
 *
 * @param onBack chamado quando o usuário toca no botão de voltar no
 *        canto superior esquerdo — quem chama essa tela (o NavHost no
 *        MainActivity.kt) decide o que fazer (normalmente, popBackStack()).
 */
@Composable
fun AboutScreen(onBack: () -> Unit = {}) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val currentSectionIndex by remember {
        derivedStateOf { listState.firstVisibleItemIndex.coerceIn(0, aboutSections.lastIndex) }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = AboutBackgroundDark) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
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
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = 90.dp)
            )

            // Botão de voltar flutuante, fixo no canto superior esquerdo
            // (não rola junto com o conteúdo, porque está fora do
            // LazyColumn, direto no Box).
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
