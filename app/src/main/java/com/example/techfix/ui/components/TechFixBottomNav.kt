package com.example.techfix.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.techfix.ui.theme.TechFixAccentSolid
import com.example.techfix.ui.theme.TechFixBorder
import com.example.techfix.ui.theme.TechFixSurface
import com.example.techfix.ui.theme.TechFixTextSecondary

enum class HomeNavItem(val label: String, val icon: ImageVector) {
    HOME("Início", Icons.Outlined.Home),
    EXPLORE("Explorar", Icons.Outlined.Search),
    ORDERS("Pedidos", Icons.Outlined.Receipt),
    PROFILE("Perfil", Icons.Outlined.Person)
}

@Composable
fun TechFixBottomNav(selected: HomeNavItem, onItemSelected: (HomeNavItem) -> Unit) {
    Surface(color = TechFixSurface, border = BorderStroke(1.dp, TechFixBorder)) {
        Row(
            modifier = Modifier.fillMaxWidth().navigationBarsPadding().selectableGroup()
        ) {
            HomeNavItem.entries.forEach { item ->
                val isSelected = item == selected
                val tint = if (isSelected) TechFixAccentSolid else TechFixTextSecondary
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 62.dp)
                        .selectable(selected = isSelected, role = Role.Tab) {
                            if (!isSelected) onItemSelected(item)
                        }
                        .padding(horizontal = 2.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(item.icon, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.height(4.dp))
                    Text(item.label, color = tint, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
