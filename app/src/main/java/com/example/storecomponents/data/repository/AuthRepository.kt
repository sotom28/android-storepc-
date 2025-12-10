package com.example.storecomponents.data.repository

import com.example.storecomponents.data.model.Usuarios
import com.example.storecomponents.data.model.Userole
import com.example.storecomponents.data.local.LocalAuthStore
import com.example.storecomponents.data.remote.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepository {
    // Estado del usuario actual (null si no hay sesión)
    private val _usuarioActual = MutableStateFlow<Usuarios?>(null)
    val usuarioActual: StateFlow<Usuarios?> = _usuarioActual

    // usuarios de ejemplo para autenticación (usando Usuarios)
    private var usuariosList = mutableListOf(
        Usuarios(id = 1, nombre = "admin", correo = "admin@store.com", role = Userole.ADMIN, password = "admin123", confirmarPassword = ""),
        Usuarios(id = 2, nombre = "cliente", correo = "cliente@store.com", role = Userole.CLIENT, password = "cliente123", confirmarPassword = "")
    )

    init {
        // Si existe LocalAuthStore inicializado, cargar desde ahí
        if (LocalAuthStore.isInitialized()) {
            val loaded = LocalAuthStore.loadUsers()
            if (loaded.isNotEmpty()) {
                // usar directamente la lista cargada (ya son Usuarios)
                usuariosList = loaded.toMutableList()

                // cargar sesión si existe
                val currentId = LocalAuthStore.loadCurrentUserId()
                if (currentId != null) {
                    _usuarioActual.value = usuariosList.firstOrNull { it.id == currentId }
                }
            }
        }
    }

    // Exponer la lista de usuarios como StateFlow para UI de gestión
    private val _usuariosFlow = MutableStateFlow<List<Usuarios>>(usuariosList.toList())
    val usuariosFlow: StateFlow<List<Usuarios>> = _usuariosFlow.asStateFlow()

    // Función de login
    fun login(emailOrName: String, password: String): Result<Usuarios> {
        return try {
            // Permitir iniciar sesión usando correo o nombre de usuario
            val user = usuariosList.find { (it.correo == emailOrName || it.nombre == emailOrName) && it.password == password }
            if (user != null) {
                _usuarioActual.value = user
                if (LocalAuthStore.isInitialized()) LocalAuthStore.saveCurrentUserId(user.id)
                Result.success(user)
            } else {
                Result.failure(Exception("Credenciales inválidas"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Ahora: registro intenta primero al backend; si falla guarda localmente
    suspend fun registro(nuevo: Usuarios): Result<Usuarios> {
        return try {
            val api = ApiClient.retrofit
            try {
                val response = api.registerUser(nuevo)
                if (response.isSuccessful) {
                    val creado = response.body() ?: nuevo
                    usuariosList.add(creado)
                    _usuariosFlow.value = usuariosList.toList()
                    Result.success(creado)
                } else {
                    // backend respondió con error; guardado local como fallback
                    usuariosList.add(nuevo)
                    _usuariosFlow.value = usuariosList.toList()
                    Result.success(nuevo)
                }
            } catch (e: Exception) {
                // comunicación fallida -> fallback local
                usuariosList.add(nuevo)
                _usuariosFlow.value = usuariosList.toList()
                Result.success(nuevo)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun cerrarSesion() {
        _usuarioActual.value = null
        if (LocalAuthStore.isInitialized()) LocalAuthStore.saveCurrentUserId(null)
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