package com.example.techfix.ui.onboarding.professional

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class SpecialtyGroup(
    val categoryId: String,
    val categoryLabel: String,
    val specialties: List<String>
)

// NOTE: mirrors the categories chosen in step 1 — swap for a real lookup
// (e.g. keyed off the ids the user picked) once specialties come from a backend.
private val allSpecialtyGroups = listOf(
    SpecialtyGroup(
        categoryId = "technology",
        categoryLabel = "Tecnologia",
        specialties = listOf("Reparo de computadores", "Troca de tela de celular", "Instalação de redes", "Recuperação de dados", "Configuração de Smart TV")
    ),
    SpecialtyGroup(
        categoryId = "electrical",
        categoryLabel = "Elétrica",
        specialties = listOf("Instalação elétrica residencial", "Troca de quadro elétrico", "Casa inteligente", "Reparo de ar-condicionado")
    )
)

/**
 * Professional onboarding — step 2 of 5.
 * Multi-select specific specialties within each previously chosen category.
 */
@Composable
fun ProSpecialtiesScreen(
    selectedCategoryIds: Set<String> = setOf("technology", "electrical"),
    totalSteps: Int = 5,
    currentStep: Int = 2,
    onBack: () -> Unit,
    onContinue: (Set<String>) -> Unit
) {
    var selectedSpecialties by remember {
        mutableStateOf(
            setOf("Reparo de computadores", "Troca de tela de celular", "Recuperação de dados", "Instalação elétrica residencial")
        )
    }

    val visibleGroups = allSpecialtyGroups.filter { it.categoryId in selectedCategoryIds }

    Surface(modifier = Modifier.fillMaxSize(), color = BackgroundDark) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
            Spacer(modifier = Modifier.height(24.dp))
            ProStepProgressBar(totalSteps = totalSteps, currentStep = currentStep)
            Spacer(modifier = Modifier.height(16.dp))
            ProStepHeader(
                step = currentStep,
                totalSteps = totalSteps,
                title = "Quais são suas especialidades?",
                subtitle = "Separamos especialidades de acordo com as categorias escolhidas. Marque as que combinam com você."
            )
            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                visibleGroups.forEach { group ->
                    Text(
                        text = group.categoryLabel,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        group.specialties.forEach { specialty ->
                            SpecialtyChip(
                                label = specialty,
                                selected = specialty in selectedSpecialties,
                                onClick = {
                                    selectedSpecialties = if (specialty in selectedSpecialties) {
                                        selectedSpecialties - specialty
                                    } else {
                                        selectedSpecialties + specialty
                                    }
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            ProBottomNavBar {
                ProBackButton(onClick = onBack)
                ProPrimaryButton(
                    text = "Continuar",
                    enabled = selectedSpecialties.isNotEmpty(),
                    modifier = Modifier.weight(1f),
                    onClick = { onContinue(selectedSpecialties) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun ProSpecialtiesScreenPreview() {
    MaterialTheme { ProSpecialtiesScreen(onBack = {}, onContinue = {}) }
}
