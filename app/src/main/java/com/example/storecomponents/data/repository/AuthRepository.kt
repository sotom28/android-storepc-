package com.example.storecomponents.data.repository

import com.example.storecomponents.data.model.Usuarios
import com.example.storecomponents.data.model.Userole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthRepository {
    // Estado del usuario actual (null si no hay sesión)
    private val _usuarioActual = MutableStateFlow<Usuarios?>(null)
    val usuarioActual: StateFlow<Usuarios?> = _usuarioActual

    // usuarios de ejemplo para autenticación
    private val usuariosList = mutableListOf(
        Usuarios(1, "admin", "admin@store.com", Userole.ADMIN, "admin1232"),
        Usuarios(2, "cliente", "cliente@store.com", Userole.CLIENT, "cliente1234")
    )

    // Función de login
    fun login(email: String, password: String): Result<Usuarios> {
        return try {
            val user = usuariosList.find { it.correo == email && it.password == password }
            if (user != null) {
                _usuarioActual.value = user
                Result.success(user)
            } else {
                Result.failure(Exception("Credenciales inválidas"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun registro(nuevo: Usuarios): Result<Usuarios> {
        return try {
            usuariosList.add(nuevo)
            Result.success(nuevo)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun cerrarSesion() {
        _usuarioActual.value = null
    }

    fun isLoggedIn(): Boolean = _usuarioActual.value != null
}