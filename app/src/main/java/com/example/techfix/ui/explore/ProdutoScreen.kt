package com.example.produtos.ui.explore

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.techfix.ui.home.Professional
import com.example.techfix.ui.theme.BackColor
import com.example.techfix.ui.theme.SoraFamily
import java.util.Locale

val LaranjaTechFix = Color(0xFFFF6B35)

// ---- Design tokens padronizados com a HomeScreen ----
private val CardDark = Color(0xFF1C1C1E)
private val CardBorderIdle = Color(0xFF2C2C2E)
private val SurfaceField = Color(0xFF18181B)
private val TextSecondary = Color(0xFFA0A0A5)
private val TextMuted = Color(0xFF71717A)
private val GreenOk = Color(0xFF34C759)
private val AccentGradient = Brush.linearGradient(listOf(Color(0xFFFF8A3D), Color(0xFFFF3D68)))

enum class ProdutoNavItem { HOME, EXPLORE, ORDERS, PROFILE }

private enum class SortOption(val label: String) {
    RATING("Melhor avaliação"),
    DISTANCE("Mais próximo"),
    REVIEWS("Mais avaliações")
}

/** Filtros do botão "ajustar" da busca. */
private data class ExploreFilters(
    val sort: SortOption = SortOption.RATING,
    val onlyAvailable: Boolean = false,
    /** Em km. 10 = sem limite. */
    val maxKm: Float = 10f,
    val minRating: Double = 0.0
) {
    val isActive: Boolean
        get() = sort != SortOption.RATING || onlyAvailable || maxKm < 10f || minRating > 0.0
}

private fun Double.oneDecimal(): String = String.format(Locale("pt", "BR"), "%.1f", this)

private fun initialsOf(name: String): String =
    name.trim().split(" ").filter { it.isNotBlank() }.take(2)
        .joinToString("") { it.first().uppercase() }.ifBlank { "T" }

@Composable
fun ProdutoScreen(
    professionals: List<Professional> = emptyList(),
    onProfessionalClick: (String) -> Unit = {},
    onNavItemSelected: (ProdutoNavItem) -> Unit = {}
) {
    var searchText by remember { mutableStateOf("") }
    var selectedSpecialty by remember { mutableStateOf<String?>(null) }
    var filters by remember { mutableStateOf(ExploreFilters()) }
    var showFilters by remember { mutableStateOf(false) }
    var showAll by remember { mutableStateOf(false) }
    var selectedNavItem by remember { mutableStateOf(ProdutoNavItem.EXPLORE) }

    val specialties = remember(professionals) { professionals.map { it.specialty }.distinct().sorted() }
    val isSearching = searchText.isNotBlank()

    val results = remember(professionals, searchText, selectedSpecialty, filters) {
        val query = searchText.trim()
        professionals
            .filter { query.isNotEmpty() || selectedSpecialty == null || it.specialty == selectedSpecialty }
            .filter {
                query.isEmpty() ||
                    it.name.contains(query, ignoreCase = true) ||
                    it.specialty.contains(query, ignoreCase = true)
            }
            .filter { !filters.onlyAvailable || it.available }
            .filter { filters.maxKm >= 10f || it.distanceKm <= filters.maxKm }
            .filter { it.rating >= filters.minRating }
            .let { list ->
                when (filters.sort) {
                    SortOption.RATING -> list.sortedWith(compareByDescending<Professional> { it.rating }.thenByDescending { it.reviewCount })
                    SortOption.DISTANCE -> list.sortedBy { it.distanceKm }
                    SortOption.REVIEWS -> list.sortedByDescending { it.reviewCount }
                }
            }
    }

    if (showFilters) {
        FiltersDialog(
            filters = filters,
            onChange = { filters = it },
            onClear = { filters = ExploreFilters() },
            onDismiss = { showFilters = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackColor)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            // ---------- HEADER ----------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .height(72.dp)
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Techfix",
                    fontFamily = SoraFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color.White
                )
            }

            // ---------- SAUDAÇÃO + BUSCA ----------
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                Text(
                    text = "Olá, bem-vindo de volta 👋",
                    color = Color(0xFFA1A1AA),
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "O que precisa de conserto hoje?",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceField)
                        .padding(start = 16.dp, top = 6.dp, bottom = 6.dp, end = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    BasicTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        singleLine = true,
                        textStyle = TextStyle(color = Color.White, fontSize = 14.sp),
                        cursorBrush = SolidColor(LaranjaTechFix),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { }),
                        modifier = Modifier.weight(1f),
                        decorationBox = { innerTextField ->
                            if (searchText.isEmpty()) {
                                Text(
                                    text = "Buscar nome ou especialidade...",
                                    color = TextMuted,
                                    fontSize = 14.sp
                                )
                            }
                            innerTextField()
                        }
                    )
                    if (searchText.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .clickable { searchText = "" },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Limpar busca", tint = TextMuted, modifier = Modifier.size(16.dp))
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable { showFilters = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Ajustar filtros",
                            tint = if (filters.isActive) LaranjaTechFix else TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                        if (filters.isActive) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(top = 8.dp, end = 8.dp)
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(LaranjaTechFix)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (isSearching) {
                // ---------- RESULTADOS DA BUSCA ----------
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                    Text(
                        text = "Resultados para “${searchText.trim()}”",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (results.size == 1) "1 profissional encontrado" else "${results.size} profissionais encontrados",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (results.isEmpty()) {
                        EmptyResults(
                            hasFilters = filters.isActive,
                            onClear = { filters = ExploreFilters() }
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            results.forEach { pro ->
                                ProfessionalRow(pro, onClick = { onProfessionalClick(pro.id) })
                            }
                        }
                    }
                }
            } else {
                // ---------- CATEGORIAS (carrossel) ----------
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Categorias", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(
                            "Ver tudo",
                            color = LaranjaTechFix,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.clickable {
                                selectedSpecialty = null
                                showAll = true
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            CategoryChip("Todas", selectedSpecialty == null) { selectedSpecialty = null }
                        }
                        items(specialties) { specialty ->
                            CategoryChip(specialty, specialty == selectedSpecialty) { selectedSpecialty = specialty }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ---------- PROFISSIONAIS ----------
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (selectedSpecialty == null) "Profissionais em destaque" else selectedSpecialty!!,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        if (results.size > 1) {
                            Text(
                                text = if (showAll) "Ver menos" else "Ver tudo",
                                color = LaranjaTechFix,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.clickable { showAll = !showAll }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    if (results.isEmpty()) {
                        Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                            EmptyResults(
                                hasFilters = filters.isActive || selectedSpecialty != null,
                                onClear = {
                                    filters = ExploreFilters()
                                    selectedSpecialty = null
                                }
                            )
                        }
                    } else if (showAll) {
                        Column(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            results.forEach { pro ->
                                ProfessionalRow(pro, onClick = { onProfessionalClick(pro.id) })
                            }
                        }
                    } else {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(results) { pro ->
                                ProfessionalCard(pro, onClick = { onProfessionalClick(pro.id) })
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ---------- ESTATÍSTICAS (dados reais da lista) ----------
                val average = if (professionals.isEmpty()) null else professionals.map { it.rating }.average()
                Row(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceField)
                        .height(IntrinsicSize.Min)
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatItem("${professionals.size}", "PROFISSIONAIS", Modifier.weight(1f))
                    Box(Modifier.width(1.dp).fillMaxHeight().background(Color.White.copy(alpha = 0.1f)))
                    StatItem("${specialties.size}", "ESPECIALIDADES", Modifier.weight(1f))
                    Box(Modifier.width(1.dp).fillMaxHeight().background(Color.White.copy(alpha = 0.1f)))
                    StatItem(average?.oneDecimal() ?: "—", "NOTA MÉDIA", Modifier.weight(1f))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // ---------- NAVEGAÇÃO INFERIOR PADRONIZADA ----------
        ProdutoBottomNav(
            selected = selectedNavItem,
            onItemSelected = {
                selectedNavItem = it
                onNavItemSelected(it)
            }
        )
    }
}

// =========================================================================================
// COMPONENTES
// =========================================================================================

@Composable
private fun CategoryChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (selected) LaranjaTechFix else SurfaceField)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = label,
            color = if (selected) Color.White else Color(0xFFA1A1AA),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )
    }
}

@Composable
private fun Avatar(name: String, size: Int) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(AccentGradient),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initialsOf(name),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = (size / 2.8f).sp
        )
    }
}

@Composable
private fun AvailabilityLabel(available: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(if (available) GreenOk else TextMuted)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = if (available) "Disponível" else "Ocupado",
            color = if (available) GreenOk else TextMuted,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun RatingLabel(pro: Professional) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = Color(0xFFFACC15),
            modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = "${pro.rating.oneDecimal()} (${pro.reviewCount})",
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/** Card do carrossel. */
@Composable
private fun ProfessionalCard(pro: Professional, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(170.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceField)
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Avatar(pro.name, 52)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = pro.specialty.uppercase(),
            color = LaranjaTechFix,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = pro.name,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(8.dp))
        RatingLabel(pro)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "${pro.distanceKm.oneDecimal()} km de você", color = TextSecondary, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(8.dp))
        AvailabilityLabel(pro.available)
    }
}

/** Linha da lista (resultados da busca e "Ver tudo"). */
@Composable
private fun ProfessionalRow(pro: Professional, onClick: () -> Unit) {
    Surface(
        color = SurfaceField,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, CardBorderIdle),
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Avatar(pro.name, 48)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pro.name,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = pro.specialty,
                    color = LaranjaTechFix,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RatingLabel(pro)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "${pro.distanceKm.oneDecimal()} km", color = TextSecondary, fontSize = 11.sp)
                }
            }
            AvailabilityLabel(pro.available)
        }
    }
}

@Composable
private fun EmptyResults(hasFilters: Boolean, onClear: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceField)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Nenhum profissional encontrado", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (hasFilters) "Tente remover alguns filtros." else "Tente outro nome ou especialidade.",
            color = TextSecondary,
            fontSize = 12.sp
        )
        if (hasFilters) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Limpar filtros",
                color = LaranjaTechFix,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                modifier = Modifier.clickable(onClick = onClear).padding(8.dp)
            )
        }
    }
}

@Composable
private fun StatItem(valor: String, rotulo: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(valor, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(rotulo, color = TextMuted, fontSize = 10.sp, maxLines = 1)
    }
}

// =========================================================================================
// FILTROS ("ajustar")
// =========================================================================================

@Composable
private fun FiltersDialog(
    filters: ExploreFilters,
    onChange: (ExploreFilters) -> Unit,
    onClear: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardDark,
        title = { Text("Ajustar busca", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterTitle("Ordenar por")
                SortOption.values().forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onChange(filters.copy(sort = option)) }
                            .padding(vertical = 8.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(if (filters.sort == option) LaranjaTechFix else CardBorderIdle),
                            contentAlignment = Alignment.Center
                        ) {
                            if (filters.sort == option) {
                                Box(Modifier.size(7.dp).clip(CircleShape).background(Color.White))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(option.label, color = Color.White, fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                FilterTitle("Distância máxima")
                Text(
                    text = if (filters.maxKm >= 10f) "Qualquer distância" else "Até ${filters.maxKm.toInt()} km",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Slider(
                    value = filters.maxKm,
                    onValueChange = { onChange(filters.copy(maxKm = it.toInt().coerceIn(1, 10).toFloat())) },
                    valueRange = 1f..10f,
                    steps = 8,
                    colors = SliderDefaults.colors(
                        thumbColor = LaranjaTechFix,
                        activeTrackColor = LaranjaTechFix,
                        inactiveTrackColor = CardBorderIdle
                    )
                )

                FilterTitle("Avaliação mínima")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(0.0 to "Todas", 4.5 to "4,5+", 4.8 to "4,8+").forEach { (value, label) ->
                        CategoryChip(label, filters.minRating == value) { onChange(filters.copy(minRating = value)) }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Somente disponíveis agora", color = Color.White, fontSize = 14.sp, modifier = Modifier.weight(1f))
                    Switch(
                        checked = filters.onlyAvailable,
                        onCheckedChange = { onChange(filters.copy(onlyAvailable = it)) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = LaranjaTechFix,
                            uncheckedThumbColor = TextSecondary,
                            uncheckedTrackColor = CardBorderIdle
                        )
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Aplicar", color = LaranjaTechFix, fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(onClick = onClear) { Text("Limpar", color = TextSecondary) }
        }
    )
}

@Composable
private fun FilterTitle(text: String) {
    Text(text, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
}

// =========================================================================================
// BARRA INFERIOR
// =========================================================================================

@Composable
private fun ProdutoBottomNav(
    selected: ProdutoNavItem,
    onItemSelected: (ProdutoNavItem) -> Unit
) {
    Surface(color = CardDark, border = BorderStroke(1.dp, CardBorderIdle)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NavItem(Icons.Default.Home, "Início", selected == ProdutoNavItem.HOME) { onItemSelected(ProdutoNavItem.HOME) }
            NavItem(Icons.Default.Search, "Explorar", selected == ProdutoNavItem.EXPLORE) { onItemSelected(ProdutoNavItem.EXPLORE) }
            NavItem(Icons.Outlined.Receipt, "Pedidos", selected == ProdutoNavItem.ORDERS) { onItemSelected(ProdutoNavItem.ORDERS) }
            NavItem(Icons.Default.Person, "Perfil", selected == ProdutoNavItem.PROFILE) { onItemSelected(ProdutoNavItem.PROFILE) }
        }
    }
}

@Composable
private fun NavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val tint = if (selected) LaranjaTechFix else TextSecondary

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = tint, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = label, color = tint, fontSize = 10.sp)
    }
}
