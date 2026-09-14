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
    allInterestOptions: List<String> = listOf(
        "Reparo de Notebooks", "Troca de Tela", "Upgrade", "Preventiva",
        "Dados", "Redes", "Software", "Hardware"
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

    // Preferências como toggles (switch). Convertendo a lista fixa em pares (texto -> estado).
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
                title = { Text("Editar Perfil", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar", tint = TechFixTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TechFixBackground)
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
                        model = initialProfile.avatarUrl,
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
                    "Alterar foto",
                    color = TechFixAccentSolid,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable(onClick = onChangePhotoClick)
                )
            }

            // Dados básicos
            TechFixCard {
                Text("Dados Básicos", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(12.dp))
                TechFixTextField(label = "Nome completo", value = name, onValueChange = { name = it })
                Spacer(Modifier.height(12.dp))
                TechFixTextField(
                    label = "Localização",
                    value = location,
                    onValueChange = { location = it },
                    leadingIcon = Icons.Filled.LocationOn
                )
            }

            // Sobre mim
            TechFixCard {
                Text("Sobre Mim", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(12.dp))
                TechFixTextField(
                    label = "Conte um pouco sobre você",
                    value = bio,
                    onValueChange = { bio = it },
                    singleLine = false,
                    minLines = 3
                )
            }

            // Categorias de interesse (seleção múltipla)
            TechFixCard {
                Text("Categorias de Interesse", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(4.dp))
                Text(
                    "Toque para selecionar ou remover",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(12.dp))
                SelectableChipsFlow(
                    options = allInterestOptions,
                    selected = selectedInterests,
                    onToggle = { option ->
                        if (selectedInterests.contains(option)) selectedInterests.remove(option)
                        else selectedInterests.add(option)
                    }
                )
            }

            // Preferências de atendimento (switches)
            TechFixCard {
                Text("Preferências de Atendimento", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                preferenceStates.keys.toList().forEach { pref ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(pref, style = MaterialTheme.typography.bodyLarge, fontSize = 14.sp, modifier = Modifier.weight(1f))
                        Switch(
                            checked = preferenceStates[pref] == true,
                            onCheckedChange = { preferenceStates[pref] = it },
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
                        interestCategories = selectedInterests.toList(),
                        preferences = preferenceStates.filterValues { it }.keys.toList()
                    )
                    onSave(updated)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = TechFixAccentSolid)
            ) {
                Text("Salvar Alterações")
            }

            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50)
            ) {
                Text("Cancelar")
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
        label = { Text(label) },
        leadingIcon = leadingIcon?.let { icon ->
            { Icon(icon, contentDescription = null, tint = TechFixTextTertiary) }
        },
        singleLine = singleLine,
        minLines = minLines,
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TechFixAccentSolid,
            unfocusedBorderColor = TechFixDivider,
            focusedLabelColor = TechFixAccentSolid,
            unfocusedLabelColor = TechFixTextTertiary,
            focusedTextColor = TechFixTextPrimary,
            unfocusedTextColor = TechFixTextPrimary,
            cursorColor = TechFixAccentSolid
        )
    )
}

/** Chips seletíveis (toca para marcar/desmarcar) usados na edição de categorias. */
@Composable
private fun SelectableChipsFlow(
    options: List<String>,
    selected: List<String>,
    onToggle: (String) -> Unit,
    itemsPerRow: Int = 2
) {
    val chunks = options.chunked(itemsPerRow)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        chunks.forEach { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowItems.forEach { option ->
                    val isSelected = selected.contains(option)
                    Surface(
                        color = if (isSelected) TechFixAccentSolid else TechFixChipBackground,
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.clickable { onToggle(option) }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            if (isSelected) {
                                Icon(
                                    Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                            }
                            Text(
                                option,
                                color = if (isSelected) Color.White else TechFixTextPrimary,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
