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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.techfix.ui.components.HomeNavItem
import com.example.techfix.ui.components.TechFixBottomNav

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
    ),
    CANCELED(
        "Cancelado",
        Icons.Outlined.Cancel
    )
}


@Composable
fun OrdersScreen(
    orders: List<ServiceOrder> = emptyList(),
    onOrderClick: (String) -> Unit = {},
    onNavItemSelected: (HomeNavItem) -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundDark
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            LazyColumn(
                modifier = Modifier.weight(1f).statusBarsPadding(),
                contentPadding = PaddingValues(
                    start = 24.dp,
                    top = 24.dp,
                    end = 24.dp,
                    bottom = 20.dp
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

            TechFixBottomNav(selected = HomeNavItem.ORDERS, onItemSelected = onNavItemSelected)
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
        it.status != OrderStatus.COMPLETED && it.status != OrderStatus.CANCELED
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

        OrderStatus.PENDING,
        OrderStatus.CANCELED ->
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

        OrderStatus.PENDING,
        OrderStatus.CANCELED ->
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
            Icons.Outlined.Build
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
