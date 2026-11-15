package com.example.techfix

import com.example.techfix.ui.splash.SplashScreen
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.produtos.ui.explore.ProdutoNavItem
import androidx.core.view.WindowCompat
import kotlinx.coroutines.launch

import com.example.techfix.data.AuthRepository
import com.example.techfix.data.DemoData
import com.example.techfix.data.PaymentPrefs
import com.example.techfix.data.OnboardingRepository
import com.example.techfix.data.SessionManager
import com.example.techfix.data.UserSession
import com.example.techfix.data.local.ChatMessageEntity
import com.example.techfix.data.local.IncomingMessage
import com.example.techfix.data.local.ProfessionalEntity
import com.example.techfix.data.local.ReceivedOrder
import com.example.techfix.data.local.ServiceOrderEntity
import com.example.techfix.data.local.TechFixDatabase
import com.example.techfix.model.ServiceHistoryItem
import com.example.techfix.model.ServiceStatus
import com.example.techfix.model.UserProfile
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.techfix.ui.auth.AuthUiState
import com.example.techfix.ui.auth.AuthViewModel
import com.example.techfix.ui.auth.ForgotPasswordScreen
import com.example.techfix.ui.auth.LoginScreen
import com.example.techfix.ui.auth.SignUpBasicInfoScreen
import com.example.techfix.ui.home.HomeNavItem
import com.example.techfix.ui.chat.ChatBubble
import com.example.techfix.ui.chat.ChatScreen
import com.example.techfix.ui.home.HomeScreen
import com.example.techfix.ui.home.Professional
import com.example.techfix.ui.onboarding.CategorySelectionScreen
import com.example.techfix.ui.onboarding.MatchPriorityScreen
import com.example.techfix.ui.onboarding.OnboardingSummaryScreen
import com.example.techfix.ui.onboarding.PathwaySelectionScreen
import com.example.techfix.ui.onboarding.UserPathway
import com.example.techfix.ui.onboarding.professional.ProCreateProfileScreen
import com.example.techfix.ui.onboarding.professional.ProProfileInfo
import com.example.techfix.ui.onboarding.professional.ProProfileReadyScreen
import com.example.techfix.ui.onboarding.professional.ProServiceCategoriesScreen
import com.example.techfix.ui.onboarding.professional.ProSpecialtiesScreen
import com.example.techfix.ui.onboarding.professional.ProWorkDetails
import com.example.techfix.ui.onboarding.professional.ProWorkDetailsScreen
import com.example.techfix.ui.about.AboutScreen
import com.example.techfix.ui.profile.EditProfileScreen
import com.example.techfix.ui.profile.ProfileScreen
import com.example.techfix.ui.notifications.AppNotification
import com.example.techfix.ui.notifications.NotificationKind
import com.example.techfix.ui.notifications.NotificationsScreen
import com.example.techfix.ui.orders.OrderDetail
import com.example.techfix.ui.orders.OrderDetailScreen
import com.example.techfix.ui.orders.OrderStatus
import com.example.techfix.ui.orders.OrdersScreen
import com.example.techfix.ui.orders.ServiceOrder
import com.example.techfix.ui.payment.PaymentMethodsScreen
import com.example.techfix.ui.pro.ProDashboardScreen
import com.example.techfix.ui.pro.ProRequestDetailScreen
import com.example.techfix.ui.request.RequestServiceScreen
import com.example.techfix.ui.theme.TechFixTheme
import com.example.produtos.ui.explore.ProdutoScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        enableEdgeToEdge()
        setContent {
            TechFixTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    TechFixNavHost()
                }
            }
        }
    }
}

private object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val SIGN_UP = "sign_up"
    const val FORGOT_PASSWORD = "forgot_password"
    const val PATHWAY = "pathway"
    const val ABOUT = "about"

    // Client onboarding
    const val CATEGORIES = "categories"
    const val PRIORITY = "priority"
    const val SUMMARY = "summary"

    // Professional onboarding
    const val PRO_SERVICES = "pro_services"
    const val PRO_SPECIALTIES = "pro_specialties"
    const val PRO_WORK_DETAILS = "pro_work_details"
    const val PRO_PROFILE = "pro_profile"
    const val PRO_PROFILE_READY = "pro_profile_ready"

    const val HOME = "home"
    const val PRO_HOME = "pro_home" // início do PROFISSIONAL

    // Telas de pedido (os {…} são preenchidos na hora de navegar)
    const val REQUEST_SERVICE = "request_service/{proId}"
    const val ORDER_DETAIL = "order_detail/{orderId}"
    const val PRO_REQUEST = "pro_request/{orderId}"
    const val CHAT = "chat/{orderId}"
    const val NOTIFICATIONS = "notifications"
    const val PAYMENT = "payment"

    // Barra inferior da Home (nova adição)
    const val EXPLORE = "explore"
    const val ORDERS = "orders"

    // Perfil do usuário (nova adição)
    const val PROFILE = "profile"
    const val EDIT_PROFILE = "edit_profile"
}

private val categoryLabels = mapOf(
    "computers" to "Computers",
    "phones" to "Phones",
    "wifi" to "Wi-Fi & Networks",
    "electrical" to "Electrical",
    "plumbing" to "Plumbing",
    "maintenance" to "Maintenance",
    "painting" to "Painting",
    "photography" to "Photography"
)

private val priorityLabels = mapOf(
    "fastest_response" to "Fastest Response Professionals first",
    "best_price" to "Best Price Match first",
    "highest_rated" to "Highest Rated Professionals first",
    "nearest_distance" to "Nearest Distance first"
)

private val weekDayShortLabels = linkedMapOf(
    "mon" to "Mon", "tue" to "Tue", "wed" to "Wed", "thu" to "Thu",
    "fri" to "Fri", "sat" to "Sat", "sun" to "Sun"
)

/** Turns a set of day ids into something like "Mon-Fri Availability". */
private fun availabilityLabel(dayIds: Set<String>): String {
    if (dayIds.isEmpty()) return "No availability set"
    val orderedDays = weekDayShortLabels.keys.filter { it in dayIds }
    return if (orderedDays.size >= 2 &&
        weekDayShortLabels.keys.toList().indexOf(orderedDays.first()) + orderedDays.size - 1 ==
        weekDayShortLabels.keys.toList().indexOf(orderedDays.last())
    ) {
        "${weekDayShortLabels[orderedDays.first()]}-${weekDayShortLabels[orderedDays.last()]} Availability"
    } else {
        orderedDays.joinToString(", ") { weekDayShortLabels[it] ?: it } + " Availability"
    }
}


/**
 * Monta um UserProfile de verdade a partir do usuário logado, com
 * valores de "conta nova" nos campos que ainda não têm dado real
 * (0 serviços concluídos, sem nota, sem foto) — em vez de usar o
 * perfil fictício do Carlos Eduardo (SampleData.carlosProfile).
 */
private fun buildUserProfile(
    fullName: String,
    createdAtMillis: Long,
    interestCategories: List<String>
): UserProfile {
    val memberSinceLabel = try {
        val formatted = SimpleDateFormat("MMMM 'de' yyyy", Locale("pt", "BR"))
            .format(Date(createdAtMillis))
            .replaceFirstChar { it.uppercase() }
        "Membro desde $formatted"
    } catch (e: Exception) {
        "Membro recente"
    }

    return UserProfile(
        name = fullName,
        avatarUrl = "",
        isVerified = false,
        location = "",
        memberSince = memberSinceLabel,
        completedServices = 0,
        rating = 0.0,
        confidencePercent = 0,
        bio = "",
        interestCategories = interestCategories,
        preferences = emptyList(),
        loyaltyLabel = "Novo por aqui",
        loyaltyPercent = 0,
        serviceHistory = emptyList()
    )
}

// ---------- Converte o que vem do Room no que as telas esperam ----------

private fun formatOrderDate(millis: Long): String {
    val format = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
    val day = format.format(Date(millis))
    return if (day == format.format(Date())) "Hoje" else day
}

private fun formatPrice(cents: Int?): String =
    if (cents == null) "A combinar"
    else NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(cents / 100.0)

private fun ServiceOrderEntity.toUi() = ServiceOrder(
    id = id.toString(),
    deviceName = deviceName,
    serviceDescription = serviceDescription,
    professionalName = professionalName,
    status = runCatching { OrderStatus.valueOf(status) }.getOrDefault(OrderStatus.PENDING),
    date = formatOrderDate(createdAt),
    price = formatPrice(priceCents)
)

private fun ProfessionalEntity.toUi() = Professional(
    id = id,
    name = name,
    specialty = specialty,
    rating = rating,
    reviewCount = reviewCount,
    distanceKm = distanceKm,
    available = available
)

/** No lado do profissional, o campo "professionalName" mostra o nome do CLIENTE. */
private fun ReceivedOrder.toProUi() = order.toUi().copy(professionalName = clientName)

/** Próxima etapa de um pedido quando o profissional toca no botão (null = nenhuma). */
private fun nextStatus(status: String): String? = when (status) {
    "PENDING" -> "APPROVED"
    "APPROVED" -> "IN_PROGRESS"
    "IN_PROGRESS" -> "COMPLETED"
    "WAITING_PARTS" -> "IN_PROGRESS"
    else -> null
}

private fun ServiceOrderEntity.toDetail(personName: String) = OrderDetail(
    id = id.toString(),
    deviceName = deviceName,
    service = serviceDescription,
    problem = problemText,
    personName = personName,
    status = runCatching { OrderStatus.valueOf(status) }.getOrDefault(OrderStatus.PENDING),
    schedule = scheduledLabel,
    location = locationLabel,
    priceCents = priceCents
)

private fun relativeTime(millis: Long): String {
    val minutes = (System.currentTimeMillis() - millis) / 60_000
    return when {
        minutes < 1 -> "agora"
        minutes < 60 -> "há $minutes min"
        minutes < 60 * 24 -> "há ${minutes / 60} h"
        else -> "há ${minutes / (60 * 24)} d"
    }
}

/** Texto da notificação de um pedido; [other] é o nome da outra pessoa (cliente ou profissional). */
private fun orderNotificationText(isPro: Boolean, order: ServiceOrderEntity, other: String): Pair<String, String> {
    val service = order.serviceDescription
    val device = order.deviceName
    return if (isPro) when (order.status) {
        "PENDING" -> "Nova solicitação" to "$other pediu: $service ($device)."
        "APPROVED" -> "Solicitação aceita" to "Você aceitou o pedido de $other."
        "IN_PROGRESS" -> "Serviço em andamento" to "$service ($device) para $other."
        "WAITING_PARTS" -> "Aguardando peças" to "O serviço de $other depende da chegada de peças."
        "COMPLETED" -> "Serviço concluído" to "Você concluiu o serviço de $other."
        else -> "Pedido cancelado" to "O pedido de $other foi cancelado."
    } else when (order.status) {
        "PENDING" -> "Solicitação enviada" to "$other ainda não respondeu seu pedido de $service."
        "APPROVED" -> "Pedido aceito" to "$other aceitou sua solicitação."
        "IN_PROGRESS" -> "Serviço em andamento" to "$service ($device) com $other."
        "WAITING_PARTS" -> "Aguardando peças" to "O serviço do seu $device depende da chegada de peças."
        "COMPLETED" -> "Serviço concluído" to "O serviço do seu $device foi concluído."
        else -> "Pedido cancelado" to "O pedido de $service foi cancelado."
    }
}

private fun buildNotifications(
    isPro: Boolean,
    clientOrders: List<ServiceOrderEntity>,
    receivedOrders: List<ReceivedOrder>,
    incoming: List<IncomingMessage>
): List<AppNotification> {
    val fromOrders = if (isPro) {
        receivedOrders.map { r ->
            val (title, body) = orderNotificationText(true, r.order, r.clientName)
            r.order.createdAt to AppNotification("o${r.order.id}", NotificationKind.ORDER, title, body, relativeTime(r.order.createdAt), r.order.id)
        }
    } else {
        clientOrders.map { o ->
            val (title, body) = orderNotificationText(false, o, o.professionalName)
            o.createdAt to AppNotification("o${o.id}", NotificationKind.ORDER, title, body, relativeTime(o.createdAt), o.id)
        }
    }
    val fromMessages = incoming.map { m ->
        m.message.createdAt to AppNotification(
            "m${m.message.id}", NotificationKind.MESSAGE, "Nova mensagem de ${m.senderName}",
            m.message.text, relativeTime(m.message.createdAt), m.message.orderId
        )
    }
    return (fromOrders + fromMessages).sortedByDescending { it.first }.map { it.second }
}

private fun ServiceOrder.toHistoryItem(): ServiceHistoryItem {
    val (historyStatus, progress) = when (status) {
        OrderStatus.COMPLETED -> ServiceStatus.CONCLUIDO to 100
        OrderStatus.WAITING_PARTS -> ServiceStatus.AGUARDANDO_PECAS to 60
        OrderStatus.IN_PROGRESS -> ServiceStatus.EM_ANDAMENTO to 50
        OrderStatus.APPROVED -> ServiceStatus.EM_ANDAMENTO to 25
        OrderStatus.PENDING -> ServiceStatus.EM_ANDAMENTO to 10
        OrderStatus.CANCELED -> ServiceStatus.CONCLUIDO to 0
    }
    val completed = status == OrderStatus.COMPLETED
    return ServiceHistoryItem(
        deviceName = deviceName,
        description = serviceDescription,
        status = historyStatus,
        progress = progress,
        dateLabel = if (completed) "FINALIZADO EM" else "ABERTO EM",
        date = date,
        warranty = if (completed) "Válida por 90 dias" else null
    )
}

/**
 * Navegação da barra inferior (Início / Explorar / Pedidos / Perfil) igual para
 * TODAS as telas: não empilha a mesma tela várias vezes e o botão "voltar"
 * sempre leva para o Início (e do Início, sai do app).
 */
private fun NavHostController.navigateToTab(route: String, homeRoute: String) {
    navigate(route) {
        popUpTo(homeRoute) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun TechFixNavHost(navController: NavHostController = rememberNavController()) {
    val context = LocalContext.current

    // Held here for now (not persisted). Replace with a real session/profile
    // store once you build one.
    var loggedInFullName by remember { mutableStateOf("") }
    // NOVO: o email de quem logou — as tabelas de onboarding no Room usam o
    // email como chave, então precisamos dele aqui pra poder salvar de verdade.
    var loggedInEmail by remember { mutableStateOf("") }
    // NOVO: true quando a conta tem perfil profissional salvo — decide qual
    // "Início" a pessoa vê (cliente: HOME, profissional: PRO_HOME).
    var isProfessional by remember { mutableStateOf(false) }
    val homeRoute: () -> String = { if (isProfessional) Routes.PRO_HOME else Routes.HOME }

    // NOVO: ponte entre as telas e as tabelas novas do Room
    // (client_onboarding / professional_profile).
    val database = remember { TechFixDatabase.getInstance(context) }
    val onboardingRepository = remember { OnboardingRepository(database) }

    // Cria as contas e os dados de demonstração se ainda não existirem
    // (ver DemoData.kt). Roda em segundo plano enquanto a Splash aparece.
    LaunchedEffect(Unit) { DemoData.seedIfNeeded(database) }
    // NOVO: "saveClientOnboarding"/"saveProfessionalProfile" são funções
    // "suspend" — só podem ser chamadas de dentro de uma corrotina, e é
    // esse escopo que nos deixa abrir uma a partir de um clique.
    val coroutineScope = rememberCoroutineScope()

    // Client onboarding state
    var selectedCategoryIds by remember { mutableStateOf(setOf<String>()) }
    var selectedPriorityId by remember { mutableStateOf("") }

    // Professional onboarding state
    var proSelectedCategoryIds by remember { mutableStateOf(setOf("technology", "electrical")) }
    var proSelectedSpecialties by remember { mutableStateOf(setOf<String>()) }
    var proWorkDetails by remember { mutableStateOf<ProWorkDetails?>(null) }
    var proProfileInfo by remember { mutableStateOf<ProProfileInfo?>(null) }

    // Perfil do usuário exibido/editado nas telas de Perfil.
    // Começa com um placeholder vazio; assim que o login/cadastro conclui
    // (ver os blocos LaunchedEffect abaixo), é substituído pelos dados
    // reais de quem está logado.
    var userProfile by remember {
        mutableStateOf(buildUserProfile(fullName = "", createdAtMillis = System.currentTimeMillis(), interestCategories = emptyList()))
    }

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onFinished = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        // ---------------- Auth ----------------
        composable(Routes.LOGIN) {
            val authViewModel: AuthViewModel = viewModel()
            val uiState by authViewModel.uiState.collectAsState()

            LaunchedEffect(uiState) {
                when (val state = uiState) {
                    is AuthUiState.LoggedIn -> {
                        loggedInFullName = state.fullName
                        loggedInEmail = state.email

                        // Monta o perfil real (nome + data de criação da conta
                        // vêm do UserSession, preenchido pelo AuthViewModel).
                        val user = UserSession.currentUser
                        userProfile = buildUserProfile(
                            fullName = state.fullName,
                            createdAtMillis = user?.createdAt ?: System.currentTimeMillis(),
                            interestCategories = emptyList()
                        )
                        // Em paralelo, busca no Room se esse usuário já tem
                        // interesses salvos de um onboarding anterior, e
                        // completa o perfil assim que a busca terminar.
                        coroutineScope.launch {
                            val saved = onboardingRepository.getClientOnboarding(state.email)
                            if (saved != null) {
                                userProfile = userProfile.copy(
                                    interestCategories = saved.selectedCategoryIds.mapNotNull { categoryLabels[it] }
                                )
                            }

                            // Tem perfil profissional salvo? Então entra como profissional.
                            val proProfile = database.professionalProfileDao().getByEmail(state.email)
                            isProfessional = proProfile != null
               
