package com.example.techfix.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.techfix.model.SampleData
import com.example.techfix.model.UserProfile
import com.example.techfix.ui.components.TechFixCard
import com.example.techfix.ui.theme.*

/**
 * Tela de Edição de Perfil. Mantém um estado interno editável (nome, localização,
 * bio, categorias selecionadas e preferências) e só devolve o UserProfile atualizado
 * quando o usuário confirma em "Salvar Alterações".
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    initialProfile: UserProfile = SampleData.carlosProfile,
    isProfessional: Boolean = true,
    allInterestOptions: List<String> = listOf(
        "Reparo de Notebooks",
        "Troca de Tela",
        "Upgrade",
        "Preventiva",
        "Dados",
        "Redes",
        "Software",
        "Hardware"
    ),
    onBackClick: () -> Unit = {},
    onSave: (UserProfile) -> Unit = {},
    onCancel: () -> Unit = {},
    onChangePhotoClick: () -> Unit = {},
) {
    var name by remember { mutableStateOf(initialProfile.name) }
    var location by remember { mutableStateOf(initialProfile.location) }
    var bio by remember { mutableStateOf(initialProfile.bio) }

    val selectedInterests = remember {
        mutableStateListOf(*initialProfile.interestCategories.toTypedArray())
    }

    // Preferências como toggles (switch).
    val preferenceStates = remember {
        mutableStateMapOf<String, Boolean>().apply {
            listOf(
                "Atendimento presencial preferido",
                "Horário flexível (manhã ou tarde)",
                "Prefere orçamento prévio",
                "Comunicação via app"
            ).forEach { pref ->
                put(pref, initialProfile.preferences.contains(pref))
            }
        }
    }

    Scaffold(
        containerColor = TechFixBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Editar Perfil",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = TechFixTextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TechFixBackground
                )
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Spacer(Modifier.height(4.dp))

            // Avatar editável
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(contentAlignment = Alignment.BottomEnd) {

                    AsyncImage(
                        model = initialProfile.avatarUrl.ifBlank { com.example.techfix.R.drawable.placeholder_perfil },
                        contentDescription = "Foto de perfil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(TechFixSurfaceVariant)
                    )

                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(TechFixAccentSolid)
                            .clickable(onClick = onChangePhotoClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.CameraAlt,
                            contentDescription = "Alterar foto",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Alterar foto",
                    color = TechFixAccentSolid,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable(onClick = onChangePhotoClick)
                )
            }

            // Dados básicos
            TechFixCard {

                Text(
                    text = "Dados Básicos",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(12.dp))

                TechFixTextField(
                    label = "Nome completo",
                    value = name,
                    onValueChange = { name = it }
                )

                Spacer(Modifier.height(12.dp))

                TechFixTextField(
                    label = "Localização",
                    value = location,
                    onValueChange = { location = it },
                    leadingIcon = Icons.Filled.LocationOn
                )
            }

            if (isProfessional) {
                TechFixCard {
                    Text(
                        text = "Sobre Mim",
                        style = MaterialTheme.typography.titleLarge,
                        color = TechFixTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(12.dp))
                    TechFixTextField(
                        label = "Conte um pouco sobre você",
                        value = bio,
                        onValueChange = { bio = it },
                        singleLine = false,
                        minLines = 3
                    )
                }

                // Categorias de interesse
                TechFixCard {

                    Text(
                        text = "Categorias de Interesse",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "Toque para selecionar ou remover",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.5f)
                    )

                    Spacer(Modifier.height(12.dp))

                    SelectableChipsFlow(
                        options = (initialProfile.interestCategories + allInterestOptions).distinct(),
                        selected = selectedInterests,
                        onToggle = { option ->
                            if (selectedInterests.contains(option)) {
                                selectedInterests.remove(option)
                            } else {
                                selectedInterests.add(option)
                            }
                        }
                    )
                }
            }

            // Preferências de atendimento
            TechFixCard {

                Text(
                    text = "Preferências de Atendimento",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(8.dp))

                preferenceStates.keys.toList().forEach { pref ->

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = pref,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f)
                        )

                        Switch(
                            checked = preferenceStates[pref] == true,
                            onCheckedChange = {
                                preferenceStates[pref] = it
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = TechFixAccentSolid,
                                uncheckedTrackColor = TechFixSurfaceVariant
                            )
                        )
                    }
                }
            }

            // Ações
            Button(
                onClick = {

                    val updated = initialProfile.copy(
                        name = name,
                        location = location,
                        bio = bio,
                        interestCategories = if (isProfessional) selectedInterests.toList() else emptyList(),
                        preferences = preferenceStates
                            .filterValues { it }
                            .keys
                            .toList()
                    )

                    onSave(updated)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TechFixAccentSolid
                )
            ) {
                Text(
                    text = "Salvar Alterações",
                    color = Color.White
                )
            }

            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Cancelar",
                    color = Color.White
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun TechFixTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,

        label = {
            Text(
                text = label
            )
        },

        leadingIcon = leadingIcon?.let { icon ->
            {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = TechFixTextTertiary
                )
            }
        },

        singleLine = singleLine,
        minLines = minLines,

        modifier = Modifier.fillMaxWidth(),

        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TechFixAccentSolid,
            unfocusedBorderColor = TechFixDivider,

            focusedLabelColor = TechFixAccentSolid,
            unfocusedLabelColor = Color.White.copy(alpha = 0.5f),

            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,

            cursorColor = TechFixAccentSolid
        )
    )
}

/**
 * Chips seletíveis usados na edição de categorias.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SelectableChipsFlow(
    options: List<String>,
    selected: List<String>,
    onToggle: (String) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            FilterChip(
                selected = option in selected,
                onClick = { onToggle(option) },
                label = { Text(option) },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = TechFixChipBackground,
                    labelColor = TechFixTextSecondary,
                    selectedContainerColor = TechFixAccentSolid,
                    selectedLabelColor = TechFixTextPrimary
                )
            )
        }
    }
}
