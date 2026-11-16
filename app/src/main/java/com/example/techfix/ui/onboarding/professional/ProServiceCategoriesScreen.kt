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

data class ProServiceCategory(val id: String, val label: String, val icon: ImageVector)

private val proServiceCategories = listOf(
    ProServiceCategory("technology", "Tecnologia", Icons.Outlined.Computer),
    ProServiceCategory("construction", "Construção", Icons.Outlined.Handyman),
    ProServiceCategory("electrical", "Elétrica", Icons.Outlined.Bolt),
    ProServiceCategory("plumbing", "Hidráulica", Icons.Outlined.Plumbing),
    ProServiceCategory("cleaning", "Limpeza", Icons.Outlined.CleaningServices),
    ProServiceCategory("beauty", "Beleza", Icons.Outlined.ContentCut),
    ProServiceCategory("education", "Educação", Icons.Outlined.School),
    ProServiceCategory("transportation", "Transporte", Icons.Outlined.LocalShipping),
    ProServiceCategory("maintenance", "Manutenção", Icons.Outlined.Build),
    ProServiceCategory("painting", "Pintura", Icons.Outlined.FormatPaint)
)

/**
 * Professional onboarding — step 1 of 5.
 * Multi-select the primary service categories the professional offers.
 */
@Composable
fun ProServiceCategoriesScreen(
    totalSteps: Int = 5,
    currentStep: Int = 1,
    onBack: () -> Unit,
    onContinue: (Set<String>) -> Unit
) {
    var selectedIds by remember { mutableStateOf(setOf("technology", "electrical", "maintenance")) }

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
