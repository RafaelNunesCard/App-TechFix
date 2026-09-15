package com.example.techfix

import com.example.techfix.ui.splash.SplashScreen
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.produtos.ui.explore.ProdutoNavItem
import androidx.core.view.WindowCompat
import kotlinx.coroutines.launch

import com.example.techfix.data.OnboardingRepository
import com.example.techfix.data.UserSession
import com.example.techfix.data.local.TechFixDatabase
import com.example.techfix.model.UserProfile
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.techfix.ui.auth.AuthUiState
import com.example.techfix.ui.auth.AuthViewModel
import com.example.techfix.ui.auth.LoginScreen
import com.example.techfix.ui.auth.SignUpBasicInfoScreen
import com.example.techfix.ui.home.HomeNavItem
import com.example.techfix.ui.home.HomeScreen
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
import com.example.techfix.ui.orders.OrdersScreen
import com.example.produtos.ui.explore.ProdutoScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        enableEdgeToEdge()
        setContent {
            TechFixTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF121212)) {
                    TechFixNavHost()
                }
            }
        }
    }
}

@Composable
fun TechFixTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            background = Color(0xFF121212),
            surface = Color(0xFF121212)
        ),
        content = content
    )
}

private object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val SIGN_UP = "sign_up"
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

@Composable
fun TechFixNavHost(navController: NavHostController = rememberNavController()) {
    val context = LocalContext.current

    // Held here for now (not persisted). Replace with a real session/profile
    // store once you build one.
    var loggedInFullName by remember { mutableStateOf("") }
    // NOVO: o email de quem logou — as tabelas de onboarding no Room usam o
    // email como chave, então precisamos dele aqui pra poder salvar de verdade.
    var loggedInEmail by remember { mutableStateOf("") }

    // NOVO: ponte entre as telas e as tabelas novas do Room
    // (client_onboarding / professional_profile).
    val onboardingRepository = remember { OnboardingRepository(TechFixDatabase.getInstance(context)) }
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
                        }

                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
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
                onForgotPassword = { /* TODO */ },
                onLogin = { email, password -> authViewModel.login(email, password) },
                onSocialLogin = { /* TODO */ },
                onNavigateToSignUp = { navController.navigate(Routes.SIGN_UP) }
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
                    ?: "Highest Rated Professionals first",
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
            ProCreateProfileScreen(
                onBack = { navController.popBackStack() },
                onContinue = { profileInfo ->
                    proProfileInfo = profileInfo
                    navController.navigate(Routes.PRO_PROFILE_READY)
                },
                onUploadPhoto = { /* TODO: launch Photo Picker */ },
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
                            // Ainda não existe upload de foto de verdade
                            // (é só um TODO na tela) — por isso null aqui.
                            photoUri = null
                        )
                    }
                    navController.navigate(Routes.HOME) {
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

            HomeScreen(
                userFirstName = firstName,
                onAboutClick = { navController.navigate(Routes.ABOUT) },
                onNavItemSelected = { item ->
                    when (item) {
                        HomeNavItem.HOME -> Unit
                        HomeNavItem.EXPLORE -> navController.navigate(Routes.EXPLORE)
                        HomeNavItem.ORDERS -> navController.navigate(Routes.ORDERS)
                        HomeNavItem.PROFILE -> navController.navigate(Routes.PROFILE)
                    }
                }
            )
        }

        composable(Routes.ABOUT) {
            AboutScreen(onBack = { navController.popBackStack() })
        }

        // ---------------- Barra inferior da Home (nova adição) ----------------
           composable(Routes.EXPLORE) {
               ProdutoScreen(
                   onNavItemSelected = { item ->
                       when (item) {
                           ProdutoNavItem.HOME -> {
                               navController.navigate(Routes.HOME) {
                                   launchSingleTop = true
                               }
                           }
                           ProdutoNavItem.EXPLORE -> {
                               // Já está na tela de explorar/serviços, não precisa navegar
                           }
                           ProdutoNavItem.ORDERS -> {
                               navController.navigate(Routes.ORDERS) { // Ajuste para a rota de pedidos do seu app
                                   launchSingleTop = true
                               }
                           }
                           ProdutoNavItem.PROFILE -> {
                               navController.navigate(Routes.PROFILE) { // Ajuste para a rota de perfil do seu app
                                   launchSingleTop = true
                               }
                           }
                       }
                   }
               )
           }

        composable(Routes.ORDERS) {
            OrdersScreen(
                onOrderClick = { orderId ->
                    // Futuramente:
                    // navController.navigate("order_detail/$orderId")
                },
                onNavItemSelected = { item ->
                    when (item) {
                        HomeNavItem.HOME -> {
                            navController.navigate(Routes.HOME) {
                                popUpTo(Routes.HOME)
                            }
                        }

                        HomeNavItem.EXPLORE -> {
                            navController.navigate(Routes.EXPLORE)
                        }

                        HomeNavItem.ORDERS -> {
                            // Já estamos em Pedidos
                        }

                        HomeNavItem.PROFILE -> {
                            navController.navigate(Routes.PROFILE)
                        }
                    }
                }
            )
        }

        // ---------------- Perfil (nova adição) ----------------
        composable(Routes.PROFILE) {
            ProfileScreen(
                profile = userProfile,
                onMenuClick = { /* TODO: abrir drawer/menu, se existir */ },
                onEditProfileClick = { navController.navigate(Routes.EDIT_PROFILE) },
                onRequestServiceClick = { /* TODO: navegar para solicitação de serviço */ },
                onSeeAllHistoryClick = { /* TODO: navegar para histórico completo */ }
            )
        }

        composable(Routes.EDIT_PROFILE) {
            EditProfileScreen(
                initialProfile = userProfile,
                onBackClick = { navController.popBackStack() },
                onCancel = { navController.popBackStack() },
                onSave = { updatedProfile ->
                    userProfile = updatedProfile
                    navController.popBackStack()
                },
                onChangePhotoClick = { /* TODO: abrir seletor de imagem */ }
            )
        }
    }
}
