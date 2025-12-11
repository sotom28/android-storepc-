package com.example.storecomponents.data.repository

import com.example.storecomponents.data.model.Usuarios
import com.example.storecomponents.data.model.Userole
import com.example.storecomponents.data.model.toDomain
import com.example.storecomponents.data.model.dto.LoginRequest
import com.example.storecomponents.data.model.dto.RegisterRequest
import com.example.storecomponents.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepository {

    private val apiService = RetrofitClient.apiService

    // Estado del usuario actual (null si no hay sesión)
    private val _usuarioActual = MutableStateFlow<Usuarios?>(null)
    val usuarioActual: StateFlow<Usuarios?> = _usuarioActual

    // Lista de usuarios (para administración)
    private val _usuariosFlow = MutableStateFlow<List<Usuarios>>(emptyList())
    val usuariosFlow: StateFlow<List<Usuarios>> = _usuariosFlow.asStateFlow()

    // ==================== AUTENTICACIÓN ====================

    /**
     * Login con conexión al backend
     */
    suspend fun login(username: String, password: String): Result<Usuarios> {
        return try {
            val request = LoginRequest(username = username, contrasena = password)
            val response = apiService.login(request)

            if (response.isSuccessful && response.body() != null) {
                val usuarioDTO = response.body()!!
                // Determinar el rol basado en alguna lógica (puedes ajustar esto)
                val role = if (username.lowercase().contains("admin")) {
                    Userole.ADMIN
                } else {
                    Userole.CLIENT
                }
                val usuario = usuarioDTO.toDomain(role)
                _usuarioActual.value = usuario
                Result.success(usuario)
            } else {
                when (response.code()) {
                    401 -> Result.failure(Exception("Credenciales inválidas"))
                    else -> Result.failure(Exception("Error al iniciar sesión: ${response.code()}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    /**
     * Registro con conexión al backend
     */
    suspend fun registro(
        username: String,
        nombreReal: String,
        email: String,
        password: String
    ): Result<Usuarios> {
        return try {
            val request = RegisterRequest(
                username = username,
                nombreReal = nombreReal,
                email = email,
                contrasena = password
            )
            val response = apiService.register(request)

            if (response.isSuccessful && response.body() != null) {
                val usuarioDTO = response.body()!!
                val usuario = usuarioDTO.toDomain(Userole.CLIENT)
                Result.success(usuario)
            } else {
                when (response.code()) {
                    409 -> Result.failure(Exception("El usuario ya existe"))
                    else -> Result.failure(Exception("Error al registrar: ${response.code()}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    fun cerrarSesion() {
        _usuarioActual.value = null
    }

    fun isLoggedIn(): Boolean = _usuarioActual.value != null

    // ==================== GESTIÓN DE USUARIOS (ADMIN) ====================

    /**
     * Obtener todos los usuarios del backend
     */
    suspend fun obtenerTodosLosUsuarios(): Result<List<Usuarios>> {
        return try {
            val response = apiService.getAllUsers()

            if (response.isSuccessful && response.body() != null) {
                val usuarios = response.body()!!.map { it.toDomain() }
                _usuariosFlow.value = usuarios
                Result.success(usuarios)
            } else {
                Result.failure(Exception("Error al obtener usuarios: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    /**
     * Obtener usuario por ID
     */
    suspend fun obtenerUsuarioPorId(id: Long): Result<Usuarios> {
        return try {
            val response = apiService.getUserById(id)

            if (response.isSuccessful && response.body() != null) {
                val usuario = response.body()!!.toDomain()
                Result.success(usuario)
            } else {
                Result.failure(Exception("Usuario no encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    /**
     * Obtener usuario por username
     */
    suspend fun obtenerUsuarioPorUsername(username: String): Result<Usuarios> {
        return try {
            val response = apiService.getUserByUsername(username)

            if (response.isSuccessful && response.body() != null) {
                val usuario = response.body()!!.toDomain()
                Result.success(usuario)
            } else {
                Result.failure(Exception("Usuario no encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }
}
