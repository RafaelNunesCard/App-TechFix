package com.example.techfix.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ---- Design tokens (same palette used across the whole app) ----
private val BackgroundDark = Color(0xFF121212)
private val CardDark = Color(0xFF1C1C1E)
private val CardBorderIdle = Color(0xFF2C2C2E)
private val ChipBackground = Color(0xFF2A2A2C)
private val AccentStart = Color(0xFFFF8A3D) // orange
private val AccentEnd = Color(0xFFFF3D68)   // pink/red
private val TextPrimary = Color(0xFFFFFFFF)
private val TextSecondary = Color(0xFFA0A0A5)
private val TextLabel = Color(0xFF8E8E93)

private val AccentGradient = Brush.horizontalGradient(listOf(AccentStart, AccentEnd))

// =========================================================================================
// DATA
// =========================================================================================

data class HomeCategory(val id: String, val label: String, val icon: ImageVector)

data class Professional(
    val id: String,
    val name: String,
    val specialty: String,
    val rating: Double,
    val reviewCount: Int,
    val distanceKm: Double,
    val available: Boolean
)

private val homeCategories = listOf(
    HomeCategory("computers", "Computers", Icons.Outlined.Computer),
    HomeCategory("phones", "Phones", Icons.Outlined.Smartphone),
    HomeCategory("electrical", "Electrical", Icons.Outlined.Bolt),
    HomeCategory("plumbing", "Plumbing", Icons.Outlined.Plumbing),
    HomeCategory("painting", "Painting", Icons.Outlined.FormatPaint)
)

// NOTE: mock data — swap for a real repository/API call when the backend is ready.
private val mockProfessionals = listOf(
    Professional("1", "Carlos Mendes", "Eletricista", 4.9, 128, 1.2, true),
    Professional("2", "Ana Ferreira", "Encanadora", 4.8, 94, 2.4, true),
    Professional("3", "Roberto Lima", "Técnico em TI", 4.7, 210, 0.8, false),
    Professional("4", "Juliana Costa", "Pintora", 4.9, 76, 3.1, true)
)

// =========================================================================================
// HOME SCREEN
// =========================================================================================

@Composable
fun HomeScreen(
    userFirstName: String,
    onSearchClick: () -> Unit = {},
    onCategoryClick: (String) -> Unit = {},
    onProfessionalClick: (String) -> Unit = {},
    onNavItemSelected: (HomeNavItem) -> Unit = {}
) {
    var selectedNavItem by remember { mutableStateOf(HomeNavItem.HOME) }

    Surface(modifier = Modifier.fillMaxSize(), color = BackgroundDark) {
        Column(modifier = Modifier.fillMaxSize()) {

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 20.dp)
            ) {
                item {
                    HomeHeader(userFirstName = userFirstName)
                    Spacer(modifier = Modifier.height(20.dp))
                    SearchBar(onClick = onSearchClick)
                    Spacer(modifier = Modifier.height(24.dp))
                }

                item {
                    Text(
                        text = "Categorias",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    CategoriesRow(
                        categories = homeCategories,
                        onCategoryClick = onCategoryClick
                    )
                    Spacer(modifier = Modifier.height(28.dp))
                }

                item {
                    Text(
                        text = "Profissionais em destaque",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                items(mockProfessionals) { professional ->
                    ProfessionalCard(
                        professional = professional,
                        onClick = { onProfessionalClick(professional.id) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }
            }

            HomeBottomNav(
                selected = selectedNavItem,
                onItemSelected = {
                    selectedNavItem = it
                    onNavItemSelected(it)
                }
            )
        }
    }
}

enum class HomeNavItem { HOME, EXPLORE, ORDERS, PROFILE }

// =========================================================================================
// COMPONENTS
// =========================================================================================

@Composable
private fun HomeHeader(userFirstName: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Olá, $userFirstName 👋",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Que serviço você precisa hoje?",
                color = TextSecondary,
                fontSize = 13.sp
            )
        }

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(CardDark)
                .border(BorderStroke(1.dp, CardBorderIdle), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "Notificações",
                tint = TextPrimary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun SearchBar(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(CardDark)
            .border(BorderStroke(1.dp, CardBorderIdle), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "Buscar especialistas ou serviços",
            color = TextSecondary,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun CategoriesRow(
    categories: List<HomeCategory>,
    onCategoryClick: (String) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        items(categories) { category ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(72.dp)
                    .clickable { onCategoryClick(category.id) }
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(CardDark)
                        .border(BorderStroke(1.dp, CardBorderIdle), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = category.icon,
                        contentDescription = null,
                        tint = AccentEnd,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = category.label,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }
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
        color = CardDark,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, CardBorderIdle)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(AccentGradient),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = professional.name.first().toString(),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = professional.name,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = professional.specialty,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Star,
                        contentDescription = null,
                        tint = AccentEnd,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${professional.rating} (${professional.reviewCount})",
                        color = TextLabel,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = TextLabel,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${professional.distanceKm} km",
                        color = TextLabel,
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
    val backgroundColor = if (available) Color(0xFF1E3A2A) else Color(0xFF3A2A2A)
    val textColor = if (available) Color(0xFF4CD97B) else Color(0xFFD97B7B)
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

@Composable
private fun HomeBottomNav(
    selected: HomeNavItem,
    onItemSelected: (HomeNavItem) -> Unit
) {
    Surface(color = CardDark, border = BorderStroke(1.dp, CardBorderIdle)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NavItem(
                icon = Icons.Default.Home,
                label = "Início",
                selected = selected == HomeNavItem.HOME,
                onClick = { onItemSelected(HomeNavItem.HOME) }
            )
            NavItem(
                icon = Icons.Default.Search,
                label = "Explorar",
                selected = selected == HomeNavItem.EXPLORE,
                onClick = { onItemSelected(HomeNavItem.EXPLORE) }
            )
            NavItem(
                icon = Icons.Outlined.Receipt,
                label = "Pedidos",
                selected = selected == HomeNavItem.ORDERS,
                onClick = { onItemSelected(HomeNavItem.ORDERS) }
            )
            NavItem(
                icon = Icons.Default.Person,
                label = "Perfil",
                selected = selected == HomeNavItem.PROFILE,
                onClick = { onItemSelected(HomeNavItem.PROFILE) }
            )
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
    val tint = if (selected) AccentEnd else TextSecondary

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = tint, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = label, color = tint, fontSize = 10.sp)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen(userFirstName = "Rafael")
    }
}
