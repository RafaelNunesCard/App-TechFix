@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.example.techfix.ui.profile

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.example.techfix.R
import com.example.techfix.model.ProfileOptions
import com.example.techfix.model.UserProfile
import com.example.techfix.ui.components.ProfileAvatar
import com.example.techfix.ui.components.TechFixCard
import com.example.techfix.ui.components.TechFixPrimaryButton
import com.example.techfix.ui.theme.TechFixAccentBrush
import com.example.techfix.ui.theme.TechFixAccentSolid
import com.example.techfix.ui.theme.TechFixBackground
import com.example.techfix.ui.theme.TechFixBorder
import com.example.techfix.ui.theme.TechFixChipBackground
import com.example.techfix.ui.theme.TechFixDimens
import com.example.techfix.ui.theme.TechFixDivider
import com.example.techfix.ui.theme.TechFixPillShape
import com.example.techfix.ui.theme.TechFixRed
import com.example.techfix.ui.theme.TechFixSurface
import com.example.techfix.ui.theme.TechFixSurfaceVariant
import com.example.techfix.ui.theme.TechFixTextPrimary
import com.example.techfix.ui.theme.TechFixTextSecondary
import com.example.techfix.ui.theme.TechFixTextTertiary

/**
 * Tela de Editar Perfil. Trabalha num rascunho e só devolve o perfil atualizado quando a
 * pessoa toca em "Salvar Alterações". Sair com alterações pendentes pede confirmação.
 * A foto é tratada pelo MainActivity ([onChangePhotoClick]); aqui só é exibida.
 * O tema (TechFixTheme) vem da MainActivity.
 */
@Composable
fun EditProfileScreen(
    initialProfile: UserProfile,
    allInterestOptions: List<String> = ProfileOptions.interests,
    onBackClick: () -> Unit = {},
    onSave: (UserProfile) -> Unit = {},
    onCancel: () -> Unit = {},
    onChangePhotoClick: () -> Unit = {},
) {
    val focusManager = LocalFocusManager.current

    var name by remember { mutableStateOf(initialProfile.name) }
    var location by remember { mutableStateOf(initialProfile.location) }
    var bio by remember { mutableStateOf(initialProfile.bio) }
    var interests by remember { mutableStateOf(initialProfile.interestCategories.toSet()) }
    var preferences by remember { mutableStateOf(initialProfile.preferences.toSet()) }
    var nameTouched by remember { mutableStateOf(false) }
    var showDiscardDialog by remember { mutableStateOf(false) }

    val trimmedName = name.trim()
    val nameError: String? = when {
        trimmedName.isEmpty() -> "Informe seu nome"
        trimmedName.length < 2 -> "O nome precisa ter ao menos 2 letras"
        else -> null
    }
    val isDirty = trimmedName != initialProfile.name.trim() ||
        location.trim() != initialProfile.location.trim() ||
        bio.trim() != initialProfile.bio.trim() ||
        interests != initialProfile.interestCategories.toSet() ||
        preferences != initialProfile.preferences.toSet()
    val canSave = isDirty && nameError == null

    val requestBack: () -> Unit = {
        if (isDirty) showDiscardDialog = true else onBackClick()
    }
    val save: () -> Unit = {
        nameTouched = true
        if (canSave) {
            focusManager.clearFocus()
            onSave(
                initialProfile.copy(
                    name = trimmedName,
                    location = location.trim(),
                    bio = bio.trim(),
                    interestCategories = allInterestOptions.filter { it in interests } +
                        interests.filter { it !in allInterestOptions },
                    preferences = ProfileOptions.preferences.filter { it in preferences } +
                        preferences.filter { it !in ProfileOptions.preferences }
                )
            )
        }
    }

    // Com alterações pendentes, o botão "voltar" do celular também pergunta antes de sair.
    BackHandler(enabled = isDirty) { showDiscardDialog = true }

    Scaffold(
        containerColor = TechFixBackground,
        topBar = {
            TopAppBar(
                title = { Text("Editar Perfil", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = requestBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = TechFixTextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TechFixBackground,
                    scrolledContainerColor = TechFixBackground
                )
            )
        },
        bottomBar = {
            Surface(color = TechFixBackground) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .imePadding()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(TechFixDimens.BorderWidth)
                            .background(TechFixDivider)
                    )
                    Column(
                        modifier = Modifier.padding(
                            horizontal = TechFixDimens.Space16,
                            vertical = TechFixDimens.Space12
                        )
                    ) {
                        TechFixPrimaryButton(
                            text = "Salvar Alterações",
                            onClick = save,
                            enabled = canSave
                        )
                        TextButton(
                            onClick = requestBack,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "Cancelar",
                                style = MaterialTheme.typography.labelLarge,
                                color = TechFixTextSecondary
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { focusManager.clearFocus() })
                }
                .verticalScroll(rememberScrollState())
                .padding(horizontal = TechFixDimens.Space16, vertical = TechFixDimens.Space8),
            verticalArrangement = Arrangement.spacedBy(TechFixDimens.Space16)
        ) {
            // ---------- Foto ----------
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box {
                    ProfileAvatar(
                        model = initialProfile.avatarUrl.ifBlank { R.drawable.placeholder_perfil }
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(TechFixDimens.CameraBadge)
                            .border(TechFixDimens.AvatarRing, TechFixBackground, CircleShape)
                            .clip(CircleShape)
                            .background(TechFixAccentBrush)
                            .clickable(role = Role.Button, onClick = onChangePhotoClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.CameraAlt,
                            contentDescription = "Alterar foto",
                            tint = TechFixTextPrimary,
                            modifier = Modifier.size(TechFixDimens.IconMedium)
                        )
                    }
                }
                TextButton(onClick = onChangePhotoClick) {
                    Text(
                        "Alterar Foto",
                        style = MaterialTheme.typography.labelLarge,
                        color = TechFixAccentSolid
                    )
                }
            }

            // ---------- Dados básicos ----------
            TechFixCard {
                Text("Dados Básicos", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(TechFixDimens.Space12))
                EditField(
                    value = name,
                    onValueChange = {
                        if (it.length <= ProfileOptions.MAX_NAME) name = it
                        nameTouched = true
                    },
                    label = "Nome completo *",
                    leadingIcon = Icons.Filled.Person,
                    error = if (nameTouched) nameError else null,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    )
                )
                Spacer(Modifier.height(TechFixDimens.Space8))
                EditField(
                    value = location,
                    onValueChange = {
                        if (it.length <= ProfileOptions.MAX_LOCATION) location = it
                    },
                    label = "Localização (cidade, UF)",
                    leadingIcon = Icons.Filled.LocationOn,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    )
                )
            }

            // ---------- Sobre mim ----------
            TechFixCard {
                Text("Sobre Mim", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(TechFixDimens.Space12))
                EditField(
                    value = bio,
                    onValueChange = {
                        if (it.length <= ProfileOptions.MAX_BIO) bio = it
                    },
                    label = "Conte um pouco sobre você",
                    supportingText = "${bio.length}/${ProfileOptions.MAX_BIO}",
                    singleLine = false,
                    minLines = 4,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Default
                    )
                )
            }

            // ---------- Categorias ----------
            TechFixCard {
                Text("Categorias de Interesse", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(TechFixDimens.Space4))
                Text(
                    "Toque para selecionar ou remover.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(TechFixDimens.Space12))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(TechFixDimens.Space8),
                    verticalArrangement = Arrangement.spacedBy(TechFixDimens.Space8)
                ) {
                    allInterestOptions.forEach { option ->
                        SelectableChip(
                            text = option,
                            selected = option in interests,
                            onClick = {
                                interests = if (option in interests) interests - option else interests + option
                            }
                        )
                    }
                }
            }

            // ---------- Preferências ----------
            TechFixCard {
                Text("Preferências de Atendimento", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(TechFixDimens.Space4))
                ProfileOptions.preferences.forEachIndexed { index, option ->
                    if (index > 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(TechFixDimens.BorderWidth)
                                .background(TechFixDivider)
                        )
                    }
                    val checked = option in preferences
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .toggleable(
                                value = checked,
                                role = Role.Switch,
                                onValueChange = { isOn ->
                                    preferences = if (isOn) preferences + option else preferences - option
                                }
                            )
                            .padding(vertical = TechFixDimens.Space12),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            option,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = TechFixDimens.Space12)
                        )
                        Switch(
                            checked = checked,
                            onCheckedChange = null,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = TechFixTextPrimary,
                                checkedTrackColor = TechFixAccentSolid,
                                uncheckedThumbColor = TechFixTextSecondary,
                                uncheckedTrackColor = TechFixSurfaceVariant,
                                uncheckedBorderColor = TechFixBorder
                            )
                        )
                    }
                }
            }

            Spacer(Modifier.height(TechFixDimens.Space8))
        }
    }

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            containerColor = TechFixSurface,
            titleContentColor = TechFixTextPrimary,
            textContentColor = TechFixTextSecondary,
            title = { Text("Descartar alterações?") },
            text = { Text("Você fez alterações que ainda não foram salvas.") },
            confirmButton = {
                TextButton(onClick = {
                    showDiscardDialog = false
                    onCancel()
                }) {
                    Text("Descartar", color = TechFixRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) {
                    Text("Continuar editando", color = TechFixTextPrimary)
                }
            }
        )
    }
}

@Composable
private fun EditField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    error: String? = null,
    supportingText: String? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    val leading: (@Composable () -> Unit)? = if (leadingIcon != null) {
        { Icon(leadingIcon, contentDescription = null) }
    } else null

    val message = error ?: supportingText
    val supporting: (@Composable () -> Unit)? = if (message != null) {
        { Text(message, style = MaterialTheme.typography.bodySmall) }
    } else null

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(label) },
        leadingIcon = leading,
        supportingText = supporting,
        isError = error != null,
        singleLine = singleLine,
        minLines = minLines,
        textStyle = MaterialTheme.typography.bodyLarge,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        shape = MaterialTheme.shapes.medium,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TechFixTextPrimary,
            unfocusedTextColor = TechFixTextPrimary,
            errorTextColor = TechFixTextPrimary,
            focusedBorderColor = TechFixAccentSolid,
            unfocusedBorderColor = TechFixBorder,
            errorBorderColor = TechFixRed,
            focusedLabelColor = TechFixAccentSolid,
            unfocusedLabelColor = TechFixTextTertiary,
            errorLabelColor = TechFixRed,
            focusedLeadingIconColor = TechFixAccentSolid,
            unfocusedLeadingIconColor = TechFixTextTertiary,
            errorLeadingIconColor = TechFixRed,
            focusedSupportingTextColor = TechFixTextTertiary,
            unfocusedSupportingTextColor = TechFixTextTertiary,
            errorSupportingTextColor = TechFixRed,
            cursorColor = TechFixAccentSolid
        )
    )
}

@Composable
private fun SelectableChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = TechFixPillShape,
        color = if (selected) TechFixAccentSolid else TechFixChipBackground,
        border = BorderStroke(
            TechFixDimens.BorderWidth,
            if (selected) TechFixAccentSolid else TechFixBorder
        )
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = TechFixDimens.Space12,
                vertical = TechFixDimens.Space8
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selected) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = null,
                    tint = TechFixTextPrimary,
                    modifier = Modifier.size(TechFixDimens.IconSmall)
                )
                Spacer(Modifier.width(TechFixDimens.Space6))
            }
            Text(
                text,
                style = MaterialTheme.typography.bodyMedium,
                color = TechFixTextPrimary
            )
        }
    }
}
