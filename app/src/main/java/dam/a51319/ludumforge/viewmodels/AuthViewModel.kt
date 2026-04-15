package dam.a51319.ludumforge.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam.a51319.ludumforge.data.repositories.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.content.Context

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    object Success : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Fields cannot be empty")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            repository.signIn(email, password)
                .onSuccess { _uiState.value = AuthUiState.Success }
                .onFailure { _uiState.value = AuthUiState.Error(it.message ?: "Login failed") }
        }
    }

    fun register(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Fields cannot be empty")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            repository.signUp(email, password)
                .onSuccess { _uiState.value = AuthUiState.Success }
                .onFailure {
                    _uiState.value = AuthUiState.Error(it.message ?: "Registration failed")
                }
        }
    }

    fun loginWithGoogle(context: Context, webClientId: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            repository.signInWithGoogle(context, webClientId)
                .onSuccess { _uiState.value = AuthUiState.Success }
                .onFailure {
                    _uiState.value = AuthUiState.Error(it.message ?: "Google sign-in failed")
                }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}