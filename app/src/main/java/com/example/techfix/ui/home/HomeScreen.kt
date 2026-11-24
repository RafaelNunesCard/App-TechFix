package com.example.techfix.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.techfix.ui.components.HomeNavItem
import com.example.techfix.ui.components.TechFixBottomNav
import com.example.techfix.ui.onboarding.professional.proServiceCategories
import com.example.techfix.ui.orders.OrderStatus
import com.example.techfix.ui.orders.ServiceOrder
import com.example.techfix.ui.theme.*
import java.text.Normalizer
import java.util.Locale

private data class HomeCategory(val id: String, val label: String, val icon: ImageVector)

data class Professional(
    val id: String,
    val name: String,
    val specialty: String,
    val rating: Double,
    val reviewCount: Int,
    val distanceKm: Double,
    val available: Boolean,
    val serviceCategoryIds: Set<String> = emptySet(),
    val specialties: List<String> = emptyList()
)

private val homeCategories = listOf(
    HomeCategory("computers", "Computadores", Icons.Outlined.Computer),
    HomeCategory("phones", "Celulares", Icons.Outlined.Smartphone),
    HomeCategory("wifi", "Wi-Fi e Redes", Icons.Outlined.Wifi)
) + proServiceCategories.map { HomeCategory(it.id, it.label, it.icon) }

private val technologySubcategories = setOf("computers", "phones", "wifi")

private val categoryAliases = mapOf(
    "computers" to listOf("computador", "notebook", "informatica", "tecnico em ti"),
    "phones" to listOf("celular", "smartphone", "iphone"),
    "wifi" to listOf("wi-fi", "wifi", "rede"),
    "electrical" to listOf("eletric"),
    "plumbing" to listOf("encanador", "hidraulic"),
    "maintenance" to listOf("manutenc"),
    "painting" to listOf("pintor", "pintura")
)

private val diacritics = "\\p{M}+".toRegex()
private val searchSeparators = "[^a-z0-9]+".toRegex()
private val wifiSpelling = "\\bwi fi\\b".toRegex()

private fun String.searchTerms(): String =
    Normalizer.normalize(lowercase(Locale.ROOT), Normalizer.Form.NFD)
        .replace(diacritics, "")
        .replace(searchSeparators, " ")
        .replace(wifiSpelling, "wifi")
        .trim()

private fun Professional.matchesCategory(category: HomeCategory): Boolean {
    if (category.id in serviceCategoryIds) return true
    if (serviceCategoryIds.isNotEmpty() &&
        (category.id !in technologySubcategories || "technology" !in serviceCategoryIds)
    ) return false
    val serviceText = (listOf(specialty) + specialties).joinToString(" ").searchTerms()
    val aliases = categoryAliases[category.id].orEmpty() + category.label
    return aliases.any { serviceText.contains(it.searchTerms()) } ||
        (category.id == "technology" && technologySubcategories.any { subcategory ->
            categoryAliases.getValue(subcategory).any { serviceText.contains(it.searchTerms()) }
        })
}

private fun List<Professional>.matching(query: String, selectedCategory: HomeCategory?): List<Professional> {
    val terms = if (query.isEmpty()) emptyList() else query.split(" ")
    return filter { professional ->
        val categories = homeCategories.filter { professional.matchesCategory(it) }
        val searchable = (listOf(professional.name, professional.specialty) +
            professional.specialties + categories.map { it.label }).joinToString(" ").searchTerms()
        (selectedCategory == null || selectedCategory in categories) &&
            terms.all { searchable.contains(it) }
    }
}

@Composable
fun HomeScreen(
    userFirstName: String,
    professionals: List<Professional> = emptyList(),
    orders: List<ServiceOrder> = emptyList(),
    onOrderClick: (String) -> Unit = {},
    onProfessionalClick: (String) -> Unit = {},
    onNavItemSelected: (HomeNavItem) -> Unit = {},
    onNotificationsClick: () -> Unit = {}
) {
    var search by rememberSaveable { mutableStateOf("") }
    var selectedCategoryId by rememberSaveable { mutableStateOf<String?>(null) }
    val query = remember(search) { search.searchTerms() }
    val selectedCategory = homeCategories.firstOrNull { it.id == selectedCategoryId }
    val isSearching = query.isNotEmpty() || selectedCategory != null
    val matches = remember(professionals, query, selectedCategory) {
        if (isSearching) professionals.matching(query, selectedCategory) else emptyList()
    }
    val activeOrders = orders.filter { it.status != OrderStatus.COMPLETED && it.status != OrderStatus.CANCELED }

    Surface(modifier = Modifier.fillMaxSize(), color = TechFixBackground) {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.weight(1f).statusBarsPadding(),
                contentPadding = PaddingValues(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { HomeHeader(userFirstName, onNotificationsClick) }
                item { SearchBar(search, onSearchChange = { search = it }) }

                item {
                    Text("Categorias", color = TechFixTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(8.dp))
                    CategoriesRow(
                        selectedId = selectedCategoryId,
                        onSelected = { id -> selectedCategoryId = if (selectedCategoryId == id) null else id }
                    )
                }

                if (isSearching) {
                    item {
                        Text(
                            text = if (matches.size == 1) "1 profissional encontrado" else "${matches.size} profissionais encontrados",
                            color = TechFixTextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    if (matches.isEmpty()) {
                        item {
                            Text("Tente outro termo ou categoria.", color = TechFixTextSecondary)
                        }
                    }
                    items(items = matches, key = { it.id }) { professional ->
                        ProfessionalCard(professional, onClick = { onProfessionalClick(professional.id) })
                    }
                } else {
                    item {
                        HomeOrdersCard(
                            activeOrders = activeOrders,
                            onOrderClick = onOrderClick,
                            onViewOrders = { onNavItemSelected(HomeNavItem.ORDERS) }
                        )
                    }
                }

                item {
                    OutlinedButton(
                        onClick = { onNavItemSelected(HomeNavItem.EXPLORE) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TechFixTextPrimary),
                        border = BorderStroke(1.dp, TechFixBorder)
                    ) {
                        Icon(Icons.Outlined.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Explorar todos os profissionais")
                    }
                }
            }

            TechFixBottomNav(selected = HomeNavItem.HOME, onItemSelected = onNavItemSelected)
        }
    }
}

@Composable
private fun HomeHeader(userFirstName: String, onNotificationsClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("Olá, $userFirstName 👋", color = TechFixTextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text("Que serviço você precisa hoje?", color = TechFixTextSecondary, fontSize = 13.sp)
        }
        FilledTonalIconButton(
            onClick = onNotificationsClick,
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = TechFixSurfaceVariant,
                contentColor = TechFixTextPrimary
            )
        ) {
            Icon(Icons.Outlined.Notifications, contentDescription = "Notificações")
        }
    }
}

@Composable
private fun HomeOrdersCard(
    activeOrders: List<ServiceOrder>,
    onOrderClick: (String) -> Unit,
    onViewOrders: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = TechFixSurface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, TechFixBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Seus pedidos", color = TechFixTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(6.dp))
            Text(
                text = when (activeOrders.size) {
                    0 -> "Você não tem pedidos ativos."
                    1 -> "1 pedido ativo"
                    else -> "${activeOrders.size} pedidos ativos"
                },
                color = TechFixTextSecondary,
                fontSize = 13.sp
            )
            activeOrders.firstOrNull()?.let { latest ->
                TextButton(onClick = { onOrderClick(latest.id) }, contentPadding = PaddingValues(0.dp)) {
                    Column {
                        Text(latest.serviceDescription, color = TechFixTextPrimary, fontWeight = FontWeight.SemiBold)
                        Text(latest.status.label, color = TechFixTextSecondary, fontSize = 12.sp)
                    }
                }
            }
            TextButton(onClick = onViewOrders, contentPadding = PaddingValues(0.dp)) {
                Text("Ver todos os pedidos", color = TechFixAccentSolid)
                Spacer(Modifier.width(4.dp))
                Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = TechFixAccentSolid)
            }
        }
    }
}

@Composable
private fun SearchBar(search: String, onSearchChange: (String) -> Unit) {
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        value = search,
        onValueChange = onSearchChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
        label = { Text("Buscar profissionais ou serviços") },
        leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
        trailingIcon = {
            if (search.isNotEmpty()) {
                IconButton(onClick = { onSearchChange("") }) {
                    Icon(Icons.Outlined.Close, contentDescription = "Limpar busca")
                }
            }
        },
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = TechFixSurface,
            unfocusedContainerColor = TechFixSurface,
            focusedBorderColor = TechFixAccentSolid,
            unfocusedBorderColor = TechFixBorder,
            cursorColor = TechFixAccentSolid
        )
    )
}

@Composable
private fun CategoriesRow(selectedId: String?, onSelected: (String?) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            FilterChip(selected = selectedId == null, onClick = { onSelected(null) }, label = { Text("Todas") })
        }
        items(items = homeCategories, key = { it.id }) { category ->
            FilterChip(
                selected = category.id == selectedId,
                onClick = { onSelected(category.id) },
                label = { Text(category.label) },
                leadingIcon = { Icon(category.icon, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
        }
    }
}

@Composable
private fun ProfessionalCard(
    professional: Professional,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        color = TechFixSurface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, TechFixBorder)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(TechFixAccentBrush),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = professional.name.firstOrNull()?.uppercase() ?: "?",
                    color = TechFixTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = professional.name,
                    color = TechFixTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = professional.specialty,
                    color = TechFixTextSecondary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Star,
                        contentDescription = null,
                        tint = TechFixAccentSolid,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${professional.rating} (${professional.reviewCount})",
                        color = TechFixTextTertiary,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = TechFixTextTertiary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${professional.distanceKm} km",
                        color = TechFixTextTertiary,
                        fontSize = 11.sp
                    )
                }
            }

            AvailabilityBadge(available = professional.available)
        }
    }
}

@Composable
private fun AvailabilityBadge(available: Boolean) {
    val backgroundColor = if (available) TechFixGreenBg else TechFixRedBg
    val textColor = if (available) TechFixGreen else TechFixRed
    val label = if (available) "Disponível" else "Ocupado"

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = label, color = textColor, fontSize = 10.sp, fontWeight = FontWeight.Medium)
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    TechFixTheme { HomeScreen(userFirstName = "Rafael") }
}
