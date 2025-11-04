package com.example.storecomponents.data.repository

import com.example.storecomponents.data.model.Usuarios
import com.example.storecomponents.data.model.Userole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepository {
    // Estado del usuario actual (null si no hay sesión)
    private val _usuarioActual = MutableStateFlow<Usuarios?>(null)
    val usuarioActual: StateFlow<Usuarios?> = _usuarioActual

    // usuarios de ejemplo para autenticación
    private val usuariosList = mutableListOf(
        Usuarios(1, "admin", "admin@store.com", Userole.ADMIN, "admin1232"),
        Usuarios(2, "cliente", "cliente@store.com", Userole.CLIENT, "cliente1234")
    )

    // Exponer la lista de usuarios como StateFlow para UI de gestión
    private val _usuariosFlow = MutableStateFlow<List<Usuarios>>(usuariosList.toList())
    val usuariosFlow: StateFlow<List<Usuarios>> = _usuariosFlow.asStateFlow()

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
            _usuariosFlow.value = usuariosList.toList()
            Result.success(nuevo)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun cerrarSesion() {
        _usuarioActual.value = null
    }

    fun isLoggedIn(): Boolean = _usuarioActual.value != null

    // Nuevo: Obtener todos los usuarios
    fun obtenerTodosLosUsuarios(): List<Usuarios> = usuariosList.toList()

    // Nuevo: Agregar usuario
    fun agregarUsuario(usuario: Usuarios): Result<Unit> {
        return try {
            usuariosList.add(usuario)
            _usuariosFlow.value = usuariosList.toList()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Nuevo: Actualizar usuario por id
    fun actualizarUsuario(usuario: Usuarios): Result<Unit> {
        return try {
            val idx = usuariosList.indexOfFirst { it.id == usuario.id }
            if (idx >= 0) {
                usuariosList[idx] = usuario
                _usuariosFlow.value = usuariosList.toList()
                Result.success(Unit)
            } else {
                Result.failure(Exception("Usuario no encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Nuevo: Eliminar usuario por id
    fun eliminarUsuarioPorId(id: Int): Result<Unit> {
        return try {
            val removed = usuariosList.removeAll { it.id == id }
            if (removed) {
                _usuariosFlow.value = usuariosList.toList()
                Result.success(Unit)
            } else {
                Result.failure(Exception("Usuario no encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}