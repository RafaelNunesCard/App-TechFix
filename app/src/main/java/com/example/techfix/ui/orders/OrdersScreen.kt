package com.example.techfix.ui.orders

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.techfix.ui.home.HomeNavItem

private val BackgroundDark = Color(0xFF121212)
private val CardDark = Color(0xFF1C1C1E)
private val CardBorderIdle = Color(0xFF2C2C2E)

private val AccentStart = Color(0xFFFF8A3D)
private val AccentEnd = Color(0xFFFF3D68)

private val TextPrimary = Color.White
private val TextSecondary = Color(0xFFA0A0A5)
private val TextLabel = Color(0xFF8E8E93)

private val AccentGradient =
    Brush.horizontalGradient(listOf(AccentStart, AccentEnd))


data class ServiceOrder(
    val id: String,
    val deviceName: String,
    val serviceDescription: String,
    val professionalName: String,
    val status: OrderStatus,
    val date: String,
    val price: String
)


enum class OrderStatus(
    val label: String,
    val icon: ImageVector
) {
    PENDING(
        "Aguardando orçamento",
        Icons.Outlined.Schedule
    ),
    APPROVED(
        "Serviço aprovado",
        Icons.Outlined.CheckCircle
    ),
    IN_PROGRESS(
        "Em andamento",
        Icons.Outlined.Build
    ),
    WAITING_PARTS(
        "Aguardando peças",
        Icons.Outlined.Inventory2
    ),
    COMPLETED(
        "Concluído",
        Icons.Outlined.TaskAlt
    )
}


/*
 * Dados temporários para visualização.
 *
 * Depois podemos trocar isso pelo banco/API.
 */
private val mockOrders = listOf(
    ServiceOrder(
        id = "001",
        deviceName = "Notebook Dell Inspiron",
        serviceDescription = "Limpeza interna e troca de pasta térmica",
        professionalName = "Carlos Mendes",
        status = OrderStatus.IN_PROGRESS,
        date = "Hoje",
        price = "R$ 180,00"
    ),
    ServiceOrder(
        id = "002",
        deviceName = "iPhone 14 Pro",
        serviceDescription = "Substituição da tela OLED frontal",
        professionalName = "Ana Ferreira",
        status = OrderStatus.WAITING_PARTS,
        date = "12/09/2026",
        price = "R$ 890,00"
    ),
    ServiceOrder(
        id = "003",
        deviceName = "MacBook Pro 16",
        serviceDescription = "Troca de bateria e limpeza técnica",
        professionalName = "Roberto Lima",
        status = OrderStatus.COMPLETED,
        date = "28/08/2026",
        price = "R$ 420,00"
    )
)


@Composable
fun OrdersScreen(
    orders: List<ServiceOrder> = mockOrders,
    onOrderClick: (String) -> Unit = {},
    onNavItemSelected: (HomeNavItem) -> Unit = {}
) {
    var selectedNavItem by remember {
        mutableStateOf(HomeNavItem.ORDERS)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundDark
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(
                    horizontal = 24.dp,
                    vertical = 20.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                item {
                    OrdersHeader()

                    Spacer(
                        modifier = Modifier.height(22.dp)
                    )
                }

                item {
                    OrdersSummary(
                        orders = orders
                    )

                    Spacer(
                        modifier = Modifier.height(22.dp)
                    )
                }

                item {
                    Text(
                        text = "Meus pedidos",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )
                }

                if (orders.isEmpty()) {

                    item {
                        EmptyOrders()
                    }

                } else {

                    items(
                        items = orders,
                        key = { it.id }
                    ) { order ->

                        OrderCard(
                            order = order,
                            onClick = {
                                onOrderClick(order.id)
                            }
                        )
                    }
                }

                item {
                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )
                }
            }

            OrdersBottomNav(
                selected = selectedNavItem,
                onItemSelected = {
                    selectedNavItem = it
                    onNavItemSelected(it)
                }
            )
        }
    }
}


@Composable
private fun OrdersHeader() {
    Column {

        Text(
            text = "Meus Pedidos",
            color = TextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = "Acompanhe seus serviços e solicitações",
            color = TextSecondary,
            fontSize = 13.sp
        )
    }
}


@Composable
private fun OrdersSummary(
    orders: List<ServiceOrder>
) {
    val activeOrders = orders.count {
        it.status != OrderStatus.COMPLETED
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = CardDark,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            1.dp,
            CardBorderIdle
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        brush = AccentGradient,
                        shape = RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Receipt,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(23.dp)
                )
            }

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "$activeOrders pedidos ativos",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = "${orders.size} pedidos no total",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = TextLabel
            )
        }
    }
}


@Composable
private fun OrderCard(
    order: ServiceOrder,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = CardDark,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            1.dp,
            CardBorderIdle
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                verticalAlignment = Alignment.Top
            ) {

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(
                            brush = AccentGradient,
                            shape = RoundedCornerShape(13.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = deviceIcon(order.deviceName),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = order.deviceName,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(
                        modifier = Modifier.height(3.dp)
                    )

                    Text(
                        text = order.serviceDescription,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = "Abrir pedido",
                    tint = TextLabel,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            HorizontalDivider(
                color = CardBorderIdle
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                StatusBadge(
                    status = order.status
                )

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = order.price,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null,
                    tint = TextLabel,
                    modifier = Modifier.size(14.dp)
                )

                Spacer(
                    modifier = Modifier.width(5.dp)
                )

                Text(
                    text = order.professionalName,
                    color = TextSecondary,
                    fontSize = 11.sp
                )

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                Icon(
                    imageVector = Icons.Outlined.CalendarToday,
                    contentDescription = null,
                    tint = TextLabel,
                    modifier = Modifier.size(13.dp)
                )

                Spacer(
                    modifier = Modifier.width(5.dp)
                )

                Text(
                    text = order.date,
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }
}


@Composable
private fun StatusBadge(
    status: OrderStatus
) {
    val background = when (status) {
        OrderStatus.COMPLETED ->
            Color(0xFF1E3A2A)

        OrderStatus.IN_PROGRESS,
        OrderStatus.APPROVED ->
            Color(0xFF302A3A)

        OrderStatus.WAITING_PARTS ->
            Color(0xFF3A3425)

        OrderStatus.PENDING ->
            Color(0xFF2A2A2C)
    }

    val foreground = when (status) {
        OrderStatus.COMPLETED ->
            Color(0xFF4CD97B)

        OrderStatus.IN_PROGRESS,
        OrderStatus.APPROVED ->
            AccentEnd

        OrderStatus.WAITING_PARTS ->
            Color(0xFFE0C441)

        OrderStatus.PENDING ->
            TextSecondary
    }

    Row(
        modifier = Modifier
            .background(
                background,
                RoundedCornerShape(8.dp)
            )
            .padding(
                horizontal = 8.dp,
                vertical = 5.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = status.icon,
            contentDescription = null,
            tint = foreground,
            modifier = Modifier.size(13.dp)
        )

        Spacer(
            modifier = Modifier.width(5.dp)
        )

        Text(
            text = status.label,
            color = foreground,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}


private fun deviceIcon(
    deviceName: String
): ImageVector {
    return when {
        deviceName.contains(
            "iphone",
            ignoreCase = true
        ) ->
            Icons.Outlined.Smartphone

        deviceName.contains(
            "macbook",
            ignoreCase = true
        ) ||
                deviceName.contains(
                    "notebook",
                    ignoreCase = true
                ) ||
                deviceName.contains(
                    "laptop",
                    ignoreCase = true
                ) ->
            Icons.Outlined.Computer

        else ->
            Icons.Outlined.Devices
    }
}


@Composable
private fun EmptyOrders() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            imageVector = Icons.Outlined.ReceiptLong,
            contentDescription = null,
            tint = TextLabel,
            modifier = Modifier.size(48.dp)
        )

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        Text(
            text = "Nenhum pedido ainda",
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(
            modifier = Modifier.height(5.dp)
        )

        Text(
            text = "Seus pedidos de serviço aparecerão aqui.",
            color = TextSecondary,
            fontSize = 12.sp
        )
    }
}


@Composable
private fun OrdersBottomNav(
    selected: HomeNavItem,
    onItemSelected: (HomeNavItem) -> Unit
) {
    Surface(
        color = CardDark,
        border = BorderStroke(
            1.dp,
            CardBorderIdle
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            OrdersNavItem(
                icon = Icons.Outlined.Home,
                label = "Início",
                selected = selected == HomeNavItem.HOME,
                onClick = {
                    onItemSelected(HomeNavItem.HOME)
                }
            )

            OrdersNavItem(
                icon = Icons.Outlined.Search,
                label = "Explorar",
                selected = selected == HomeNavItem.EXPLORE,
                onClick = {
                    onItemSelected(HomeNavItem.EXPLORE)
                }
            )

            OrdersNavItem(
                icon = Icons.Outlined.Receipt,
                label = "Pedidos",
                selected = selected == HomeNavItem.ORDERS,
                onClick = {
                    onItemSelected(HomeNavItem.ORDERS)
                }
            )

            OrdersNavItem(
                icon = Icons.Outlined.Person,
                label = "Perfil",
                selected = selected == HomeNavItem.PROFILE,
                onClick = {
                    onItemSelected(HomeNavItem.PROFILE)
                }
            )
        }
    }
}


@Composable
private fun OrdersNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val tint = if (selected) {
        AccentEnd
    } else {
        TextSecondary
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(
            onClick = onClick
        )
    ) {

        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(20.dp)
        )

        Spacer(
            modifier = Modifier.height(2.dp)
        )

        Text(
            text = label,
            color = tint,
            fontSize = 10.sp
        )
    }
}
