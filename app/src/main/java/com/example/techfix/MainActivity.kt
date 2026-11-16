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
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.example.techfix.data.ProfilePrefs
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
import com.example.techfix.ui.debug.DbViewerScreen
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
        hideSystemNavigationBar()
        setContent {
            TechFixTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    TechFixNavHost()
                }
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemNavigationBar()
    }

    /**
     * Esconde a barra de 3 botões do Android. Se a pessoa deslizar a
     * borda de baixo, ela aparece por um instante e some sozinha.
     */
    private fun hideSystemNavigationBar() {
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.navigationBars())
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
    const val DB_VIEWER = "db_viewer"

    // Barra inferior da Home (nova adição)
    const val EXPLORE = "explore"
    const val ORDERS = "orders"

    // Perfil do usuário (nova adição)
    const val PROFILE = "profile"
    const val EDIT_PROFILE = "edit_profile"
}

private val categoryLabels = mapOf(
    "computers" to "Computadores",
    "phones" to "Celulares",
    "wifi" to "Wi-Fi e Redes",
    "electrical" to "Elétrica",
    "plumbing" to "Hidráulica",
    "maintenance" to "Manutenção",
    "painting" to "Pintura",
    "photography" to "Fotografia"
)

private val priorityLabels = mapOf(
    "fastest_response" to "Resposta mais rápida primeiro",
    "best_price" to "Melhor preço primeiro",
    "highest_rated" to "Mais bem avaliados primeiro",
    "nearest_distance" to "Mais perto primeiro"
)

private val weekDayShortLabels = linkedMapOf(
    "mon" to "Seg", "tue" to "Ter", "wed" to "Qua", "thu" to "Qui",
    "fri" to "Sex", "sat" to "Sáb", "sun" to "Dom"
)

/** Turns a set of day ids into something like "Seg-Sex disponível". */
private fun availabilityLabel(dayIds: Set<String>): String {
    if (dayIds.isEmpty()) return "Sem disponibilidade definida"
    val orderedDays = weekDayShortLabels.keys.filter { it in dayIds }
    return if (orderedDays.size >= 2 &&
        weekDayShortLabels.keys.toList().indexOf(orderedDays.first()) + orderedDays.size - 1 ==
        weekDayShortLabels.keys.toList().indexOf(orderedDays.last())
    ) {
        "${weekDayShortLabels[orderedDays.first()]}-${weekDayShortLabels[orderedDays.last()]} disponível"
    } else {
        orderedDays.joinToString(", ") { weekDayShortLabels[it] ?: it } + " disponível"
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
    // Sobe 1 sempre que a foto de perfil muda, para as telas recarregarem.
    var photoVersion by remember { mutableStateOf(0) }

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
                            if (proProfile != null) {
                                userProfile = userProfile.copy(bio = proProfile.bio)
                            }

                            navController.navigate(if (proProfile != null) Routes.PRO_HOME else Routes.HOME) {
                                popUpTo(Routes.LOGIN) { inclusive = true }
                            }
                        }
                        authViewModel.resetState()
                    }
                    is AuthUiState.Error -> {
                        Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                        authViewModel.resetState()
                    }
                    else -> Unit
                }
            }

            LoginScreen(
                onForgotPassword = { navController.navigate(Routes.FORGOT_PASSWORD) },
                onLogin = { email, password -> authViewModel.login(email, password) },
                onSocialLogin = {
                    Toast.makeText(context, "Login social em breve. Use e-mail e senha.", Toast.LENGTH_SHORT).show()
                },
                onNavigateToSignUp = { navController.navigate(Routes.SIGN_UP) }
            )
        }

        composable(Routes.FORGOT_PASSWORD) {
            val authRepository = remember { AuthRepository(TechFixDatabase.getInstance(context).userDao()) }
            ForgotPasswordScreen(
                onBack = { navController.popBackStack() },
                onResetPassword = { email, newPassword ->
                    authRepository.resetPassword(email, newPassword)
                },
                onDone = {
                    Toast.makeText(context, "Senha alterada! Entre com a nova senha.", Toast.LENGTH_LONG).show()
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.SIGN_UP) {
            val authViewModel: AuthViewModel = viewModel()
            val uiState by authViewModel.uiState.collectAsState()

            LaunchedEffect(uiState) {
                when (val state = uiState) {
                    is AuthUiState.LoggedIn -> {
                        loggedInFullName = state.fullName
                        loggedInEmail = state.email

                        // Conta acabou de ser criada — ainda não existe
                        // onboarding salvo, então o perfil nasce "zerado".
                        isProfessional = false
                        val user = UserSession.currentUser
                        userProfile = buildUserProfile(
                            fullName = state.fullName,
                            createdAtMillis = user?.createdAt ?: System.currentTimeMillis(),
                            interestCategories = emptyList()
                        )

                        navController.navigate(Routes.PATHWAY)
                        authViewModel.resetState()
                    }
                    is AuthUiState.Error -> {
                        Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                        authViewModel.resetState()
                    }
                    else -> Unit
                }
            }

            SignUpBasicInfoScreen(
                onContinue = { basicInfo ->
                    authViewModel.signUp(basicInfo.fullName, basicInfo.email, basicInfo.password)
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        // ---------------- Pathway: branches into client vs. professional ----------------
        composable(Routes.PATHWAY) {
            PathwaySelectionScreen(
                onContinue = { pathway ->
                    when (pathway) {
                        UserPathway.NEEDS_SERVICE -> navController.navigate(Routes.CATEGORIES)
                        UserPathway.OFFERS_SERVICE -> navController.navigate(Routes.PRO_SERVICES)
                    }
                }
            )
        }

        // ================= CLIENT ONBOARDING =================
        composable(Routes.CATEGORIES) {
            CategorySelectionScreen(
                onBack = { navController.popBackStack() },
                onContinue = { selectedIds ->
                    selectedCategoryIds = selectedIds
                    navController.navigate(Routes.PRIORITY)
                }
            )
        }

        composable(Routes.PRIORITY) {
            MatchPriorityScreen(
                onBack = { navController.popBackStack() },
                onContinue = { priorityId ->
                    selectedPriorityId = priorityId
                    navController.navigate(Routes.SUMMARY)
                }
            )
        }

        composable(Routes.SUMMARY) {
            OnboardingSummaryScreen(
                selectedInterests = selectedCategoryIds.mapNotNull { categoryLabels[it] },
                matchingPriorityLabel = priorityLabels[selectedPriorityId]
                    ?: "Mais bem avaliados primeiro",
                onStartExploring = {
                    // NOVO: salva de verdade no Room antes de ir pra Home.
                    coroutineScope.launch {
                        onboardingRepository.saveClientOnboarding(
                            userEmail = loggedInEmail,
                            selectedCategoryIds = selectedCategoryIds,
                            priorityId = selectedPriorityId
                        )
                    }
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onBackToPreferences = { navController.popBackStack() }
            )
        }

        // ================= PROFESSIONAL ONBOARDING =================
        composable(Routes.PRO_SERVICES) {
            ProServiceCategoriesScreen(
                onBack = { navController.popBackStack() },
                onContinue = { selectedIds ->
                    proSelectedCategoryIds = selectedIds
                    navController.navigate(Routes.PRO_SPECIALTIES)
                }
            )
        }

        composable(Routes.PRO_SPECIALTIES) {
            ProSpecialtiesScreen(
                selectedCategoryIds = proSelectedCategoryIds,
                onBack = { navController.popBackStack() },
                onContinue = { specialties ->
                    proSelectedSpecialties = specialties
                    navController.navigate(Routes.PRO_WORK_DETAILS)
                }
            )
        }

        composable(Routes.PRO_WORK_DETAILS) {
            ProWorkDetailsScreen(
                onBack = { navController.popBackStack() },
                onContinue = { details ->
                    proWorkDetails = details
                    navController.navigate(Routes.PRO_PROFILE)
                }
            )
        }

        composable(Routes.PRO_PROFILE) {
            val proPhotoPicker = rememberLauncherForActivityResult(
                ActivityResultContracts.PickVisualMedia()
            ) { uri ->
                if (uri != null && ProfilePrefs.savePhoto(context, loggedInEmail, uri) != null) {
                    photoVersion++
                }
            }
            val proPhoto = remember(photoVersion, loggedInEmail) {
                ProfilePrefs.photoUri(context, loggedInEmail)
            }
            ProCreateProfileScreen(
                photoUri = proPhoto,
                onBack = { navController.popBackStack() },
                onContinue = { profileInfo ->
                    proProfileInfo = profileInfo
                    navController.navigate(Routes.PRO_PROFILE_READY)
                },
                onUploadPhoto = {
                    proPhotoPicker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                onAddPortfolioItem = { /* TODO: launch Photo Picker for portfolio */ }
            )
        }

        composable(Routes.PRO_PROFILE_READY) {
            val profile = proProfileInfo
            val details = proWorkDetails

            ProProfileReadyScreen(
                fullName = profile?.fullName ?: loggedInFullName,
                bio = profile?.shortBio.orEmpty(),
                specialties = proSelectedSpecialties.toList(),
                serviceRadiusKm = details?.serviceRadiusKm ?: 15,
                availabilityLabel = availabilityLabel(details?.availableDayIds ?: emptySet()),
                onEditInfo = { navController.popBackStack() },
                onPublish = {
                    // NOVO: salva o perfil profissional completo no Room.
                    coroutineScope.launch {
                        onboardingRepository.saveProfessionalProfile(
                            userEmail = loggedInEmail,
                            serviceCategoryIds = proSelectedCategoryIds,
                            specialtyIds = proSelectedSpecialties,
                            experienceLevel = details?.experienceLevel.orEmpty(),
                            serviceRadiusKm = details?.serviceRadiusKm ?: 15,
                            availableDayIds = details?.availableDayIds ?: emptySet(),
                            bio = profile?.shortBio.orEmpty(),
                            photoUri = ProfilePrefs.photoUri(context, loggedInEmail)
                        )

                        // Aparece em "Profissionais em destaque" na Home dos clientes.
                        database.professionalDao().insertAll(
                            listOf(
                                ProfessionalEntity(
                                    id = loggedInEmail,
                                    email = loggedInEmail,
                                    name = profile?.fullName?.ifBlank { null } ?: loggedInFullName,
                                    specialty = proSelectedCategoryIds
                                        .mapNotNull { categoryLabels[it] }
                                        .firstOrNull() ?: "Profissional TechFix",
                                    rating = 0.0,
                                    reviewCount = 0,
                                    distanceKm = 0.0,
                                    available = true
                                )
                            )
                        )
                    }
                    isProfessional = true
                    navController.navigate(Routes.PRO_HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // ---------------- App ----------------
        composable(Routes.HOME) {
            val firstName = loggedInFullName.trim()
                .split(" ")
                .firstOrNull()
                .takeUnless { it.isNullOrBlank() } ?: "Bem-vindo(a)"

            val professionals by remember { database.professionalDao().observeAll() }
                .collectAsState(initial = emptyList())

            HomeScreen(
                userFirstName = firstName,
                professionals = professionals.map { it.toUi() },
                onProfessionalClick = { id -> navController.navigate("request_service/$id") },
                onNotificationsClick = { navController.navigate(Routes.NOTIFICATIONS) },
                onAboutClick = { navController.navigate(Routes.ABOUT) },
                onNavItemSelected = { item ->
                    when (item) {
                        HomeNavItem.HOME -> Unit
                        HomeNavItem.EXPLORE -> navController.navigateToTab(Routes.EXPLORE, homeRoute())
                        HomeNavItem.ORDERS -> navController.navigateToTab(Routes.ORDERS, homeRoute())
                        HomeNavItem.PROFILE -> navController.navigateToTab(Routes.PROFILE, homeRoute())
                    }
                }
            )
        }

        composable(Routes.PRO_HOME) {
            val received by remember(loggedInEmail) {
                database.serviceOrderDao().observeReceivedByProfessional(loggedInEmail)
            }.collectAsState(initial = emptyList())
            val availability by remember(loggedInEmail) {
                database.professionalDao().observeAvailability(loggedInEmail)
            }.collectAsState(initial = true)

            val earningsCents = received
                .filter { it.order.status == "COMPLETED" }
                .sumOf { it.order.priceCents ?: 0 }

            ProDashboardScreen(
                proName = loggedInFullName,
                specialty = "Profissional TechFix",
                available = availability ?: true,
                onAvailabilityChange = { newValue ->
                    coroutineScope.launch {
                        database.professionalDao().setAvailability(loggedInEmail, newValue)
                    }
                },
                earningsLabel = formatPrice(earningsCents),
                orders = received.map { it.toProUi() },
                onAdvanceOrder = { orderId ->
                    val target = received.firstOrNull { it.order.id.toString() == orderId }?.order
                    val next = target?.let { nextStatus(it.status) }
                    if (target != null && next != null) {
                        coroutineScope.launch {
                            database.serviceOrderDao().updateStatus(target.id, next)
                        }
                    }
                },
                onViewRequests = { navController.navigateToTab(Routes.ORDERS, homeRoute()) },
                onNotificationsClick = { navController.navigate(Routes.NOTIFICATIONS) },
                onNavItemSelected = { item ->
                    when (item) {
                        HomeNavItem.HOME -> Unit
                        HomeNavItem.EXPLORE -> navController.navigateToTab(Routes.EXPLORE, homeRoute())
                        HomeNavItem.ORDERS -> navController.navigateToTab(Routes.ORDERS, homeRoute())
                        HomeNavItem.PROFILE -> navController.navigateToTab(Routes.PROFILE, homeRoute())
                    }
                }
            )
        }

        // ---------------- Pedidos: solicitar, detalhe do cliente, detalhe do profissional ----------------
        composable(
            Routes.REQUEST_SERVICE,
            arguments = listOf(navArgument("proId") { type = NavType.StringType })
        ) { entry ->
            val proId = entry.arguments?.getString("proId").orEmpty()
            val pros by remember { database.professionalDao().observeAll() }
                .collectAsState(initial = emptyList())
            // "0" (botão do Perfil) = sem profissional escolhido → pega um disponível.
            val target = pros.firstOrNull { it.id == proId }
                ?: pros.firstOrNull { it.available }
                ?: pros.firstOrNull()

            RequestServiceScreen(
                professionalName = target?.name ?: "Profissional TechFix",
                professionalSpecialty = target?.specialty.orEmpty(),
                onBack = { navController.popBackStack() },
                onSubmit = { form ->
                    coroutineScope.launch {
                        database.serviceOrderDao().insert(
                            ServiceOrderEntity(
                                clientEmail = loggedInEmail,
                                professionalEmail = target?.email,
                                professionalName = target?.name ?: "Profissional TechFix",
                                deviceName = form.device,
                                serviceDescription = form.serviceName,
                                status = "PENDING",
                                priceCents = form.servicePriceCents,
                                createdAt = System.currentTimeMillis(),
                                problemText = form.problem,
                                scheduledLabel = form.scheduleLabel,
                                locationLabel = form.locationLabel
                            )
                        )
                    }
                },
                onViewOrders = {
                    navController.navigate(Routes.ORDERS) {
                        popUpTo(Routes.REQUEST_SERVICE) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onGoHome = { navController.popBackStack(homeRoute(), false) }
            )
        }

        composable(
            Routes.ORDER_DETAIL,
            arguments = listOf(navArgument("orderId") { type = NavType.LongType })
        ) { entry ->
            val orderId = entry.arguments?.getLong("orderId") ?: 0L
            val received by remember(orderId) {
                database.serviceOrderDao().observeReceivedById(orderId)
            }.collectAsState(initial = null)

            OrderDetailScreen(
                detail = received?.let { it.order.toDetail(it.order.professionalName) },
                onBack = { navController.popBackStack() },
                onCancelOrder = {
                    coroutineScope.launch {
                        database.serviceOrderDao().updateStatus(orderId, "CANCELED")
                    }
                },
                onOpenChat = { navController.navigate("chat/$orderId") }
            )
        }

        composable(
            Routes.PRO_REQUEST,
            arguments = listOf(navArgument("orderId") { type = NavType.LongType })
        ) { entry ->
            val orderId = entry.arguments?.getLong("orderId") ?: 0L
            val received by remember(orderId) {
                database.serviceOrderDao().observeReceivedById(orderId)
            }.collectAsState(initial = null)

            ProRequestDetailScreen(
                detail = received?.let { it.order.toDetail(it.clientName) },
                onBack = { navController.popBackStack() },
                onAdvance = {
                    val order = received?.order
                    val next = order?.let { nextStatus(it.status) }
                    if (order != null && next != null) {
                        coroutineScope.launch {
                            database.serviceOrderDao().updateStatus(order.id, next)
                        }
                    }
                },
                onRefuse = {
                    coroutineScope.launch {
                        database.serviceOrderDao().updateStatus(orderId, "CANCELED")
                    }
                    navController.popBackStack()
                },
                onOpenChat = { navController.navigate("chat/$orderId") }
            )
        }

        composable(Routes.CHAT, arguments = listOf(navArgument("orderId") { type = NavType.LongType })) { entry ->
            val orderId = entry.arguments?.getLong("orderId") ?: 0L
            val received by remember(orderId) {
                database.serviceOrderDao().observeReceivedById(orderId)
            }.collectAsState(initial = null)
            val messages by remember(orderId) {
                database.chatMessageDao().observeByOrder(orderId)
            }.collectAsState(initial = emptyList())
            val order = received?.order
            val timeFormat = remember { SimpleDateFormat("HH:mm", Locale("pt", "BR")) }

            ChatScreen(
                title = if (isProfessional) received?.clientName ?: "Cliente"
                else order?.professionalName ?: "Profissional",
                contextTitle = order?.let { "${it.serviceDescription} - ${it.deviceName}" } ?: "Pedido #$orderId",
                contextStatus = order?.let {
                    runCatching { OrderStatus.valueOf(it.status) }.getOrDefault(OrderStatus.PENDING)
                } ?: OrderStatus.PENDING,
                messages = messages.map {
                    ChatBubble(it.id.toString(), it.text, it.senderEmail == loggedInEmail, timeFormat.format(Date(it.createdAt)))
                },
                quickReplies = if (isProfessional) listOf("Estou a caminho", "Pode confirmar o horário?", "Obrigado!")
                else listOf("Olá! Tenho uma dúvida", "Qual o melhor horário?", "Obrigado!"),
                onBack = { navController.popBackStack() },
                onSend = { text ->
                    coroutineScope.launch {
                        database.chatMessageDao().insert(
                            ChatMessageEntity(
                                orderId = orderId,
                                senderEmail = loggedInEmail,
                                text = text,
                                createdAt = System.currentTimeMillis()
                            )
                        )
                    }
                }
            )
        }

        composable(Routes.NOTIFICATIONS) {
            val clientOrders by remember(loggedInEmail) {
                database.serviceOrderDao().observeByClient(loggedInEmail)
            }.collectAsState(initial = emptyList())
            val receivedOrders by remember(loggedInEmail) {
                database.serviceOrderDao().observeReceivedByProfessional(loggedInEmail)
            }.collectAsState(initial = emptyList())
            val incoming by remember(loggedInEmail) {
                database.chatMessageDao().observeIncoming(loggedInEmail)
            }.collectAsState(initial = emptyList())

            NotificationsScreen(
                notifications = buildNotifications(isProfessional, clientOrders, receivedOrders, incoming),
                onBack = { navController.popBackStack() },
                onClick = { n ->
                    navController.navigate(
                        when {
                            n.kind == NotificationKind.MESSAGE -> "chat/${n.orderId}"
                            isProfessional -> "pro_request/${n.orderId}"
                            else -> "order_detail/${n.orderId}"
                        }
                    )
                }
            )
        }

        composable(Routes.PAYMENT) {
            PaymentMethodsScreen(
                initialMethod = PaymentPrefs.get(context),
                onBack = { navController.popBackStack() },
                onSave = { method ->
                    PaymentPrefs.set(context, method)
                    Toast.makeText(context, "Forma de pagamento salva.", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.DB_VIEWER) {
            DbViewerScreen(database = database, onBack = { navController.popBackStack() })
        }

        composable(Routes.ABOUT) {
            AboutScreen(onBack = { navController.popBackStack() })
        }

        // ---------------- Barra inferior da Home ----------------
        composable(Routes.EXPLORE) {
            val explorePros by remember { database.professionalDao().observeAll() }
                .collectAsState(initial = emptyList())

            ProdutoScreen(
                professionals = explorePros.map { it.toUi() },
                onProfessionalClick = { id -> navController.navigate("request_service/$id") },
                onNavItemSelected = { item ->
                    when (item) {
                        ProdutoNavItem.HOME -> navController.navigateToTab(homeRoute(), homeRoute())
                        ProdutoNavItem.EXPLORE -> Unit
                        ProdutoNavItem.ORDERS -> navController.navigateToTab(Routes.ORDERS, homeRoute())
                        ProdutoNavItem.PROFILE -> navController.navigateToTab(Routes.PROFILE, homeRoute())
                    }
                }
            )
        }

        composable(Routes.ORDERS) {
            val clientOrders by remember(loggedInEmail) {
                database.serviceOrderDao().observeByClient(loggedInEmail)
            }.collectAsState(initial = emptyList())
            val receivedOrders by remember(loggedInEmail) {
                database.serviceOrderDao().observeReceivedByProfessional(loggedInEmail)
            }.collectAsState(initial = emptyList())

            OrdersScreen(
                orders = if (isProfessional) receivedOrders.map { it.toProUi() }
                else clientOrders.map { it.toUi() },
                onOrderClick = { orderId ->
                    navController.navigate(
                        if (isProfessional) "pro_request/$orderId" else "order_detail/$orderId"
                    )
                },
                onNavItemSelected = { item ->
                    when (item) {
                        HomeNavItem.HOME -> navController.navigateToTab(homeRoute(), homeRoute())
                        HomeNavItem.EXPLORE -> navController.navigateToTab(Routes.EXPLORE, homeRoute())
                        HomeNavItem.ORDERS -> Unit
                        HomeNavItem.PROFILE -> navController.navigateToTab(Routes.PROFILE, homeRoute())
                    }
                }
            )
        }

        // ---------------- Perfil ----------------
        composable(Routes.PROFILE) {
            var showMenuDialog by remember { mutableStateOf(false) }

            // O histórico e o contador de serviços vêm dos pedidos reais do Room.
            val profileOrders by remember(loggedInEmail) {
                database.serviceOrderDao().observeByClient(loggedInEmail)
            }.collectAsState(initial = emptyList())
            val profileReceived by remember(loggedInEmail) {
                database.serviceOrderDao().observeReceivedByProfessional(loggedInEmail)
            }.collectAsState(initial = emptyList())
            val uiOrders = if (isProfessional) profileReceived.map { it.toProUi() }
            else profileOrders.map { it.toUi() }

            // Esquece a sessão salva e limpa tudo que era da conta anterior.
            val performLogout: () -> Unit = {
                SessionManager.clear(context)
                UserSession.currentUser = null
                loggedInFullName = ""
                loggedInEmail = ""
                isProfessional = false
                selectedCategoryIds = emptySet()
                selectedPriorityId = ""
                proSelectedSpecialties = emptySet()
                proWorkDetails = null
                proProfileInfo = null
                userProfile = buildUserProfile(
                    fullName = "",
                    createdAtMillis = System.currentTimeMillis(),
                    interestCategories = emptyList()
                )
                navController.navigate(Routes.LOGIN) {
                    popUpTo(navController.graph.id) { inclusive = true }
                    launchSingleTop = true
                }
            }

            if (showMenuDialog) {
                AlertDialog(
                    onDismissRequest = { showMenuDialog = false },
                    title = { Text("Menu") },
                    text = {
                        Column {
                            TextButton(onClick = {
                                showMenuDialog = false
                                navController.navigate(Routes.NOTIFICATIONS)
                            }) { Text("Notificações") }
                            TextButton(onClick = {
                                showMenuDialog = false
                                navController.navigate(Routes.PAYMENT)
                            }) { Text("Formas de pagamento") }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            showMenuDialog = false
                            performLogout()
                        }) { Text("Sair da conta") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showMenuDialog = false }) { Text("Cancelar") }
                    }
                )
            }

            val profilePhoto = remember(photoVersion, loggedInEmail) {
                ProfilePrefs.photoUri(context, loggedInEmail)
            }
            ProfileScreen(
                profile = userProfile.copy(
                    avatarUrl = profilePhoto ?: "",
                    completedServices = uiOrders.count { it.status == OrderStatus.COMPLETED },
                    serviceHistory = uiOrders
                        .filter { it.status != OrderStatus.CANCELED }
                        .map { it.toHistoryItem() }
                ),
                onMenuClick = { showMenuDialog = true },
                onEditProfileClick = { navController.navigate(Routes.EDIT_PROFILE) },
                onRequestServiceClick = { navController.navigate("request_service/0") },
                onSeeAllHistoryClick = { navController.navigateToTab(Routes.ORDERS, homeRoute()) },
                onLogoutClick = { performLogout() },
                onOpenDbViewer = { navController.navigate(Routes.DB_VIEWER) }
            )
        }

        composable(Routes.EDIT_PROFILE) {
            val photoPicker = rememberLauncherForActivityResult(
                ActivityResultContracts.PickVisualMedia()
            ) { uri ->
                if (uri != null && ProfilePrefs.savePhoto(context, loggedInEmail, uri) != null) {
                    photoVersion++
                }
            }
            val editPhoto = remember(photoVersion, loggedInEmail) {
                ProfilePrefs.photoUri(context, loggedInEmail)
            }
            EditProfileScreen(
                initialProfile = userProfile.copy(avatarUrl = editPhoto ?: ""),
                onBackClick = { navController.popBackStack() },
                onCancel = { navController.popBackStack() },
                onSave = { updatedProfile ->
                    userProfile = updatedProfile
                    navController.popBackStack()
                },
                onChangePhotoClick = {
                    photoPicker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            )
        }
    }
}
