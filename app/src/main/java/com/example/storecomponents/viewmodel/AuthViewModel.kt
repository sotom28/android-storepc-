package com.example.storecomponents.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storecomponents.data.model.Usuarios
import com.example.storecomponents.data.model.Userole
import com.example.storecomponents.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Enum para roles (compatible con tu MainActivity)
enum class UserRole {
    ADMIN,
    CLIENT,
    NONE
}

// Estados de autenticación (compatible con tu MainActivity)
sealed class EstadoAuth {
    object Inicial : EstadoAuth()
    object Cargando : EstadoAuth()
    data class Exito(val usuario: Usuarios) : EstadoAuth()
    data class Error(val mensaje: String) : EstadoAuth()
}

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    val usuarioActual = repository.usuarioActual

    // Estado del rol actual (compatible con MainActivity)
    private val _role = MutableStateFlow<UserRole>(UserRole.NONE)
    val role: StateFlow<UserRole> = _role.asStateFlow()

    // Estado de autenticación (compatible con MainActivity)
    private val _estadoAuth = MutableStateFlow<EstadoAuth>(EstadoAuth.Inicial)
    val estadoAuth: StateFlow<EstadoAuth> = _estadoAuth.asStateFlow()

    // Estado de registro
    private val _registerState = MutableStateFlow<EstadoAuth>(EstadoAuth.Inicial)
    val registerState: StateFlow<EstadoAuth> = _registerState.asStateFlow()

    /**
     * Login por username (compatible con tu MainActivity)
     */
    fun loginByUsername(username: String, password: String) {
        viewModelScope.launch {
            _estadoAuth.value = EstadoAuth.Cargando

            val result = repository.login(username, password)

            if (result.isSuccess) {
                val usuario = result.getOrNull()!!

                // Actualizar el rol según el usuario
                _role.value = when (usuario.role) {
                    Userole.ADMIN -> UserRole.ADMIN
                    Userole.CLIENT -> UserRole.CLIENT
                }

                _estadoAuth.value = EstadoAuth.Exito(usuario)
            } else {
                _estadoAuth.value = EstadoAuth.Error(
                    result.exceptionOrNull()?.message ?: "Error desconocido"
                )
            }
        }
    }

    /**
     * Login estándar (alternativa)
     */
    fun login(username: String, password: String) {
        loginByUsername(username, password)
    }

    /**
     * Registro con backend - Firma compatible con RegisterScreen
     * Parámetros: nombreReal, email, usernameOrRole, password
     */
    fun register(
        nombreReal: String,
        email: String,
        usernameOrRole: String,
        password: String
    ) {
        viewModelScope.launch {
            _registerState.value = EstadoAuth.Cargando

            // usernameOrRole es el username del usuario
            val username = usernameOrRole.trim()

            val result = repository.registro(
                username = username,
                nombreReal = nombreReal,
                email = email,
                password = password
            )

            if (result.isSuccess) {
                val usuario = result.getOrNull()!!
                _registerState.value = EstadoAuth.Exito(usuario)
            } else {
                _registerState.value = EstadoAuth.Error(
                    result.exceptionOrNull()?.message ?: "Error desconocido"
                )
            }
        }
    }

    fun cerrarSesion() {
        repository.cerrarSesion()
        _role.value = UserRole.NONE
        _estadoAuth.value = EstadoAuth.Inicial
        _registerState.value = EstadoAuth.Inicial
    }

    fun resetLoginState() {
        _estadoAuth.value = EstadoAuth.Inicial
    }

    fun resetRegisterState() {
        _registerState.value = EstadoAuth.Inicial
    }

    fun isLoggedIn(): Boolean = repository.isLoggedIn()
}