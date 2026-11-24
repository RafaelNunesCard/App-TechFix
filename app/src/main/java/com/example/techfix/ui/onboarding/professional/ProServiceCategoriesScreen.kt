package com.example.techfix.ui.onboarding.professional

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

internal data class ProServiceCategory(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val specialties: List<String>
)

internal val proServiceCategories = listOf(
    ProServiceCategory(
        "technology", "Tecnologia", Icons.Outlined.Computer,
        listOf("Reparo de computadores", "Troca de tela de celular", "Instalação de redes", "Recuperação de dados", "Configuração de Smart TV")
    ),
    ProServiceCategory(
        "construction", "Construção", Icons.Outlined.Handyman,
        listOf("Alvenaria", "Assentamento de pisos", "Instalação de drywall", "Reformas residenciais")
    ),
    ProServiceCategory(
        "electrical", "Elétrica", Icons.Outlined.Bolt,
        listOf("Instalação elétrica residencial", "Troca de quadro elétrico", "Casa inteligente", "Reparo de ar-condicionado")
    ),
    ProServiceCategory(
        "plumbing", "Hidráulica", Icons.Outlined.Plumbing,
        listOf("Conserto de vazamentos", "Desentupimento", "Instalação de torneiras", "Manutenção de encanamento")
    ),
    ProServiceCategory(
        "cleaning", "Limpeza", Icons.Outlined.CleaningServices,
        listOf("Limpeza residencial", "Limpeza pós-obra", "Higienização de estofados", "Limpeza de vidros")
    ),
    ProServiceCategory(
        "beauty", "Beleza", Icons.Outlined.ContentCut,
        listOf("Corte de cabelo", "Manicure e pedicure", "Maquiagem", "Design de sobrancelhas")
    ),
    ProServiceCategory(
        "education", "Educação", Icons.Outlined.School,
        listOf("Reforço escolar", "Aulas de idiomas", "Aulas de informática", "Preparação para provas")
    ),
    ProServiceCategory(
        "transportation", "Transporte", Icons.Outlined.LocalShipping,
        listOf("Fretes", "Mudanças", "Entregas locais", "Transporte de móveis")
    ),
    ProServiceCategory(
        "maintenance", "Manutenção", Icons.Outlined.Build,
        listOf("Montagem de móveis", "Pequenos reparos", "Manutenção de eletrodomésticos", "Instalação de prateleiras")
    ),
    ProServiceCategory(
        "painting", "Pintura", Icons.Outlined.FormatPaint,
        listOf("Pintura interna", "Pintura externa", "Acabamento em paredes", "Pintura de portas e janelas")
    )
)

/**
 * Professional onboarding — step 1 of 5.
 * Multi-select the primary service categories the professional offers.
 */
@Composable
fun ProServiceCategoriesScreen(
    totalSteps: Int = 5,
    currentStep: Int = 1,
    initialSelectedIds: Set<String> = emptySet(),
    onBack: () -> Unit,
    onContinue: (Set<String>) -> Unit
) {
    var selectedIds by remember(initialSelectedIds) { mutableStateOf(initialSelectedIds) }

    Surface(modifier = Modifier.fillMaxSize(), color = BackgroundDark) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
            Spacer(modifier = Modifier.height(24.dp))
            ProStepProgressBar(totalSteps = totalSteps, currentStep = currentStep)
            Spacer(modifier = Modifier.height(16.dp))
            ProStepHeader(
                step = currentStep,
                totalSteps = totalSteps,
                title = "Que serviço você oferece?",
                subtitle = "Escolha as principais categorias de serviço que você oferece. Pode marcar mais de uma."
            )
            Spacer(modifier = Modifier.height(20.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(proServiceCategories) { category ->
                    CategoryTile(
                        icon = category.icon,
                        label = category.label,
                        selected = category.id in selectedIds,
                        onClick = {
                            selectedIds = if (category.id in selectedIds) {
                                selectedIds - category.id
                            } else {
                                selectedIds + category.id
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ProBottomNavBar {
                ProBackButton(onClick = onBack)
                ProPrimaryButton(
                    text = "Continuar",
                    enabled = selectedIds.isNotEmpty(),
                    modifier = Modifier.weight(1f),
                    onClick = { onContinue(selectedIds) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun ProServiceCategoriesScreenPreview() {
    MaterialTheme { ProServiceCategoriesScreen(onBack = {}, onContinue = {}) }
}
