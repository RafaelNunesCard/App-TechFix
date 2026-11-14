package com.example.techfix.ui.pro

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.techfix.ui.home.HomeNavItem
import com.example.techfix.ui.orders.OrderStatus
import com.example.techfix.ui.orders.ServiceOrder
import com.example.techfix.ui.theme.TechFixAccentBrush
import com.example.techfix.ui.theme.TechFixAccentSolid
import com.example.techfix.ui.theme.TechFixBackground
import com.example.techfix.ui.theme.TechFixBorder
import com.example.techfix.ui.theme.TechFixChipBackground
import com.example.techfix.ui.theme.TechFixGreen
import com.example.techfix.ui.theme.TechFixGreenBg
import com.example.techfix.ui.theme.TechFixSurface
import com.example.techfix.ui.theme.TechFixTextPrimary
import com.example.techfix.ui.theme.TechFixTextSecondary
import com.example.techfix.ui.theme.TechFixTextTertiary
import com.example.techfix.ui.theme.TechFixWarning
import com.example.techfix.ui.theme.TechFixWarningBg

/**
 * Início do PROFISSIONAL (mobile-dashboard-pro no Figma).
 *
 * Nos pedidos recebidos, [ServiceOrder.professionalName] carrega o nome do CLIENTE
 * (é quem importa para o profissional).
 */
@Composable
fun ProDashboardScreen(
    proName: String,
    specialty: String,
    available: Boolean,
    onAvailabilityChange: (Boolean) -> Unit,
    earningsLabel: String,
    orders: List<ServiceOrder>,
    onAdvanceOrder: (orderId: String) -> Unit,
    onViewRequests: () -> Unit,
    onNotificationsClick: () -> Unit,
    onNavItemSelected: (HomeNavItem) -> Unit
) {
    val pending = orders.count { it.status == OrderStatus.PENDING }
    val inProgress = orders.count {
        it.status == OrderStatus.APPROVED ||
            it.status == OrderStatus.IN_PROGRESS ||
            it.status == OrderStatus.WAITING_PARTS
    }
    val completed = orders.count { it.status == OrderStatus.COMPLETED }
    val active = orders.filter {
        it.status != OrderStatus.COMPLETED && it.status != OrderStatus.CANCELED
    }
    val firstName = proName.trim().split(" ").firstOrNull().orEmpty().ifBlank { "profissional" }

    Surface(modifier = Modifier.fillMaxSize(), color = TechFixBackground) {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Olá, $firstName 👋",
                                color = TechFixTextPrimary,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = "Veja como está o seu dia",
                                color = TechFixTextSecondary,
                                fontSize = 13.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(TechFixSurface)
                                .clickable(onClick = onNotificationsClick),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Notificações",
                                tint = TechFixTextPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                item {
                    ProCard {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Avatar(name = proName, size = 52)
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(proName, color = TechFixTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Text(specialty, color = TechFixTextSecondary, fontSize = 12.sp)
                            }
                        }
                        Spacer(Modifier.height(14.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (available) "Disponível agora" else "Indisponível",
                                color = if (available) TechFixGreen else TechFixTextTertiary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f)
                            )
                            Switch(
                                checked = available,
                                onCheckedChange = onAvailabilityChange,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = TechFixTextPrimary,
                                    checkedTrackColor = TechFixGreen
                                )
                            )
                        }
                    }
                }

                if (pending > 0) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(TechFixAccentSolid.copy(alpha = 0.12f))
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.NotificationsActive,
                                contentDescription = null,
                                tint = TechFixAccentSolid,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = if (pending == 1) "1 nova solicitação aguardando resposta"
                                else "$pending novas solicitações aguardando resposta",
                                color = TechFixTextPrimary,
                                fontSize = 13.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(Modifier.width(8.dp))
                            GradientPill(text = "Ver", onClick = onViewRequests)
                        }
                    }
                }

                item {
                    ProCard {
                        Text("Ganhos (serviços concluídos)", color = TechFixTextSecondary, fontSize = 12.sp)
                        Spacer(Modifier.height(6.dp))
                        Text(earningsLabel, color = TechFixTextPrimary, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    }
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        StatBlock("Pendentes", pending, Modifier.weight(1f))
                        StatBlock("Em andamento", inProgress, Modifier.weight(1f))
                        StatBlock("Concluídos", completed, Modifier.weight(1f))
                    }
                }

                item {
                    Text(
                        text = "Próximos atendimentos",
                        color = TechFixTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (active.isEmpty()) {
                    item {
                        ProCard {
                            Text(
                                text = "Nenhum atendimento em aberto no momento.",
                                color = TechFixTextSecondary,
                                fontSize = 13.sp
                            )
                        }
                    }
                } else {
                    items(active, key = { it.id }) { order ->
                        ReceivedOrderCard(order = order, onAdvance = { onAdvanceOrder(order.id) })
                    }
                }
            }

            ProBottomNav(onItemSelected = onNavItemSelected)
        }
    }
}

/** Texto do botão que faz o pedido avançar para a próxima etapa (null = nada a fazer). */
internal fun advanceLabel(status: OrderStatus): String? = when (status) {
    OrderStatus.PENDING -> "Aceitar solicitação"
    OrderStatus.APPROVED -> "Iniciar serviço"
    OrderStatus.IN_PROGRESS -> "Concluir serviço"
    OrderStatus.WAITING_PARTS -> "Retomar serviço"
    OrderStatus.COMPLETED, OrderStatus.CANCELED -> null
}

@Composable
private fun ReceivedOrderCard(order: ServiceOrder, onAdvance: () -> Unit) {
    ProCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Avatar(name = order.professionalName, size = 44)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(order.professionalName, color = TechFixTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text(order.deviceName, color = TechFixTextSecondary, fontSize = 12.sp)
            }
            StatusChip(order.status)
        }
        Spacer(Modifier.height(10.dp))
        Text(order.serviceDescription, color = TechFixTextSecondary, fontSize = 12.sp)
        Spacer(Modifier.height(6.dp))
        Text("${order.date}  •  ${order.price}", color = TechFixTextTertiary, fontSize = 11.sp)

        val label = advanceLabel(order.status)
        if (label != null) {
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(percent = 50))
                    .background(TechFixAccentBrush)
                    .clickable(onClick = onAdvance),
                contentAlignment = Alignment.Center
            ) {
                Text(label, color = TechFixTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun StatusChip(status: OrderStatus) {
    val (bg, fg) = when (status) {
        OrderStatus.COMPLETED -> TechFixGreenBg to TechFixGreen
        OrderStatus.CANCELED -> TechFixChipBackground to TechFixTextTertiary
        OrderStatus.WAITING_PARTS, OrderStatus.PENDING -> TechFixWarningBg to TechFixWarning
        OrderStatus.APPROVED, OrderStatus.IN_PROGRESS -> TechFixAccentSolid.copy(alpha = 0.12f) to TechFixAccentSolid
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(status.label, color = fg, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun StatBlock(label: String, value: Int, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = TechFixSurface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, TechFixBorder)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value.toString(), color = TechFixTextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(2.dp))
            Text(label, color = TechFixTextSecondary, fontSize = 11.sp)
        }
    }
}

@Composable
private fun ProCard(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = TechFixSurface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, TechFixBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) { content() }
    }
}

@Composable
private fun Avatar(name: String, size: Int) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(TechFixAccentBrush),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.trim().firstOrNull()?.uppercase() ?: "?",
            color = TechFixTextPrimary,
            fontSize = (size / 2.6f).sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun GradientPill(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .height(36.dp)
            .clip(RoundedCornerShape(percent = 50))
            .background(TechFixAccentBrush)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = TechFixTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ProBottomNav(onItemSelected: (HomeNavItem) -> Unit) {
    Surface(color = TechFixSurface, border = BorderStroke(1.dp, TechFixBorder)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ProNavItem(Icons.Outlined.Home, "Início", selected = true) { onItemSelected(HomeNavItem.HOME) }
            ProNavItem(Icons.Outlined.Search, "Explorar", selected = false) { onItemSelected(HomeNavItem.EXPLORE) }
            ProNavItem(Icons.Outlined.Receipt, "Pedidos", selected = false) { onItemSelected(HomeNavItem.ORDERS) }
            ProNavItem(Icons.Outlined.Person, "Perfil", selected = false) { onItemSelected(HomeNavItem.PROFILE) }
        }
    }
}

@Composable
private fun ProNavItem(icon: ImageVector, label: String, selected: Boolean, onClick: () -> Unit) {
    val tint: Color = if (selected) TechFixAccentSolid else TechFixTextSecondary
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = tint, modifier = Modifier.size(20.dp))
        Spacer(Modifier.height(2.dp))
        Text(label, color = tint, fontSize = 10.sp)
    }
}
WAITING_PARTS -> "Retomar serviço"
    OrderStatus.COMPLETED, OrderStatus.CANCELED -> null
}

@Composable
private fun ReceivedOrderCard(order: ServiceOrder, onAdvance: () -> Unit) {
    ProCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Avatar(name = order.professionalName, size = 44)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(order.professionalName, color = TechFixTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text(order.deviceName, color = TechFixTextSecondary, fontSize = 12.sp)
            }
            StatusChip(order.status)
        }
        Spacer(Modifier.height(10.dp))
        Text(order.serviceDescription, color = TechFixTextSecondary, fontSize = 12.sp)
        Spacer(Modifier.height(6.dp))
        Text("${order.date}  •  ${order.price}", color = TechFixTextTertiary, fontSize = 11.sp)

        val label = advanceLabel(order.status)
        if (label != null) {
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(percent = 50))
                    .background(TechFixAccentBrush)
                    .clickable(onClick = onAdvance),
                contentAlignment = Alignment.Center
            ) {
                Text(label, color = TechFixTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun StatusChip(status: OrderStatus) {
    val (bg, fg) = when (status) {
        OrderStatus.COMPLETED -> TechFixGreenBg to TechFixGreen
        OrderStatus.CANCELED -> TechFixChipBackground to TechFixTextTertiary
        OrderStatus.WAITING_PARTS, OrderStatus.PENDING -> TechFixWarningBg to TechFixWarning
        OrderStatus.APPROVED, OrderStatus.IN_PROGRESS -> TechFixAccentSolid.copy(alpha = 0.12f) to TechFixAccentSolid
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(status.label, color = fg, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun StatBlock(label: String, value: Int, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = TechFixSurface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, TechFixBorder)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value.toString(), color = TechFixTextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(2.dp))
            Text(label, color = TechFixTextSecondary, fontSize = 11.sp)
        }
    }
}

@Composable
private fun ProCard(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = TechFixSurface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, TechFixBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) { content() }
    }
}

@Composable
private fun Avatar(name: String, size: Int) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(TechFixAccentBrush),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.trim().firstOrNull()?.uppercase() ?: "?",
            color = TechFixTextPrimary,
            fontSize = (size / 2.6f).sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun GradientPill(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .height(36.dp)
            .clip(RoundedCornerShape(percent = 50))
            .background(TechFixAccentBrush)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = TechFixTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ProBottomNav(onItemSelected: (HomeNavItem) -> Unit) {
    Surface(color = TechFixSurface, border = BorderStroke(1.dp, TechFixBorder)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ProNavItem(Icons.Outlined.Home, "Início", selected = true) { onItemSelected(HomeNavItem.HOME) }
            ProNavItem(Icons.Outlined.Search, "Explorar", selected = false) { onItemSelected(HomeNavItem.EXPLORE) }
            ProNavItem(Icons.Outlined.Receipt, "Pedidos", selected = false) { onItemSelected(HomeNavItem.ORDERS) }
            ProNavItem(Icons.Outlined.Person, "Perfil", selected = false) { onItemSelected(HomeNavItem.PROFILE) }
        }
    }
}

@Composable
private fun ProNavItem(icon: ImageVector, label: String, selected: Boolean, onClick: () -> Unit) {
    val tint: Color = if (selected) TechFixAccentSolid else TechFixTextSecondary
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = tint, modifier = Modifier.size(20.dp))
        Spacer(Modifier.height(2.dp))
        Text(label, color = tint, fontSize = 10.sp)
    }
}
