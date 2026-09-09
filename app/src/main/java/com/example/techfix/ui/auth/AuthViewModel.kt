package com.example.techfix.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.techfix.data.AuthRepository
import com.example.techfix.data.AuthResult
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

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Preencha e-mail e senha.")
            return
        }

        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            when (val result = repository.login(email, password)) {
                is AuthResult.Success -> _uiState.value =
                    AuthUiState.LoggedIn(result.user.fullName, result.user.email)
                is AuthResult.Failure -> _uiState.value =
                    AuthUiState.Error(result.message)
            }
        }
    }

    fun signUp(fullName: String, email: String, password: String) {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            when (val result = repository.signUp(fullName, email, password)) {
                is AuthResult.Success -> _uiState.value =
                    AuthUiState.LoggedIn(result.user.fullName, result.user.email)
                is AuthResult.Failure -> _uiState.value =
                    AuthUiState.Error(result.message)
            }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}
