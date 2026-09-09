package com.example.techfix

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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.example.techfix.ui.auth.AuthUiState
import com.example.techfix.ui.auth.AuthViewModel
import com.example.techfix.ui.auth.LoginScreen
import com.example.techfix.ui.auth.SignUpBasicInfoScreen
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    const val LOGIN = "login"
    const val SIGN_UP = "sign_up"
    const val PATHWAY = "pathway"

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

@Composable
fun TechFixNavHost(navController: NavHostController = rememberNavController()) {
    val context = LocalContext.current

    // Held here for now (not persisted). Replace with a real session/profile
    // store once you build one.
    var loggedInFullName by remember { mutableStateOf("") }

    // Client onboarding state
    var selectedCategoryIds by remember { mutableStateOf(setOf<String>()) }
    var selectedPriorityId by remember { mutableStateOf("") }

    // Professional onboarding state
    var proSelectedCategoryIds by remember { mutableStateOf(setOf("technology", "electrical")) }
    var proSelectedSpecialties by remember { mutableStateOf(setOf<String>()) }
    var proWorkDetails by remember { mutableStateOf<ProWorkDetails?>(null) }
    var proProfileInfo by remember { mutableStateOf<ProProfileInfo?>(null) }

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        // ---------------- Auth ----------------
        composable(Routes.LOGIN) {
            val authViewModel: AuthViewModel = viewModel()
            val uiState by authViewModel.uiState.collectAsState()

            LaunchedEffect(uiState) {
                when (val state = uiState) {
                    is AuthUiState.LoggedIn -> {
                        loggedInFullName = state.fullName
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
                    // TODO: persist the full professional profile (categories,
                    // specialties, work details, profile info) to your backend.
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

            HomeScreen(userFirstName = firstName)
        }
    }
}