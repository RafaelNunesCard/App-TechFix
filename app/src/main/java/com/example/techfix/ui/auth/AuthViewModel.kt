package com.example.techfix.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.techfix.data.AuthRepository
import com.example.techfix.data.AuthResult
import com.example.techfix.data.SessionManager
import com.example.techfix.data.UserSession
import com.example.techfix.data.local.TechFixDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Error(val message: String) : AuthUiState()
    data class LoggedIn(val fullName: String, val email: String) : AuthUiState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository(
        TechFixDatabase.getInstance(application).userDao()
    )

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        // Assim que o app abre e esse ViewModel é criado, tentamos
        // restaurar uma sessão salva anteriormente (ver SessionManager).
        // Se der certo, o "uiState" já nasce como LoggedIn, e quem
        // estiver observando ele (o MainActivity) pode pular a tela
        // de login direto pra Home.
        restoreSession()
    }

    private fun restoreSession() {
        val savedEmail = SessionManager.getLoggedInEmail(getApplication())
        if (savedEmail == null) return

        viewModelScope.launch {
            val user = repository.getUserByEmail(savedEmail)
            if (user != null) {
                UserSession.currentUser = user
                _uiState.value = AuthUiState.LoggedIn(user.fullName, user.email)
            }
            // Se "user" vier null (ex: conta foi removida do banco por
            // algum motivo), simplesmente não faz nada — o uiState
            // continua Idle e a pessoa vê a tela de login normalmente.
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Preencha e-mail e senha.")
            return
        }

        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            when (val result = repository.login(email, password)) {
                is AuthResult.Success -> onAuthSuccess(result.user)
                is AuthResult.Failure -> _uiState.value = AuthUiState.Error(result.message)
            }
        }
    }

    fun signUp(fullName: String, email: String, password: String) {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            when (val result = repository.signUp(fullName, email, password)) {
                is AuthResult.Success -> onAuthSuccess(result.user)
                is AuthResult.Failure -> _uiState.value = AuthUiState.Error(result.message)
            }
        }
    }

    /** Chamado depois de um login OU cadastro bem-sucedido. */
    private fun onAuthSuccess(user: com.example.techfix.data.local.UserEntity) {
        // 1) Guarda o email de forma PERSISTENTE (sobrevive a fechar o app).
        SessionManager.saveLoggedInEmail(getApplication(), user.email)
        // 2) Guarda o usuário completo na memória, pra outras telas
        //    (como a de Perfil) lerem sem precisar buscar no banco de novo.
        UserSession.currentUser = user
        _uiState.value = AuthUiState.LoggedIn(user.fullName, user.email)
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}
