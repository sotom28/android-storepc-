package com.example.storecomponents.auth

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AuthState(
    val role: String? = null,
    val error: String? = null
)

class AuthViewModel : ViewModel() {

    private val _authState = MutableStateFlow(AuthState())
    val authState = _authState.asStateFlow()

    fun login(username: String, password: String) {
        // Lógica de autenticación de ejemplo
        if (username.contains("admin", ignoreCase = true) && password == "admin123") {
            _authState.value = AuthState(role = "admin")
        } else if (username == "cliente1" && password == "cliente123") {
            _authState.value = AuthState(role = "cliente")
        } else {
            _authState.value = AuthState(error = "Credenciales inválidas")
        }
    }

    fun logout() {
        _authState.value = AuthState()
    }
}
