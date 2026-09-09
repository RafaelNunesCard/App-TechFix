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
    const val CATEGORIES = "categories"
    const val PRIORITY = "priority"
    const val SUMMARY = "summary"
    const val HOME = "home"
}

// id -> label shown on the summary card. Keep these in sync with the ids
// used in CategorySelectionScreen.kt / MatchPriorityScreen.kt.
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

@Composable
fun TechFixNavHost(navController: NavHostController = rememberNavController()) {
    val context = LocalContext.current

    // Held here for now (not persisted). Replace with a real session/profile
    // store once you build one.
    var loggedInFullName by remember { mutableStateOf("") }
    var selectedCategoryIds by remember { mutableStateOf(setOf<String>()) }
    var selectedPriorityId by remember { mutableStateOf("") }

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
                onForgotPassword = {
                    // TODO: navigate to a "forgot password" flow
                },
                onLogin = { email, password ->
                    authViewModel.login(email, password)
                },
                onSocialLogin = { provider ->
                    // TODO: trigger the corresponding social login SDK
                },
                onNavigateToSignUp = {
                    navController.navigate(Routes.SIGN_UP)
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
                    authViewModel.signUp(
                        fullName = basicInfo.fullName,
                        email = basicInfo.email,
                        password = basicInfo.password
                    )
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // ---------------- First-access onboarding ----------------
        composable(Routes.PATHWAY) {
            PathwaySelectionScreen(
                onContinue = { pathway ->
                    // TODO: persist the chosen pathway (client vs professional)
                    navController.navigate(Routes.CATEGORIES)
                }
            )
        }

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
                    // TODO: persist onboarding-complete flag so this flow
                    // doesn't run again on the next login.
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onBackToPreferences = {
                    navController.popBackStack()
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