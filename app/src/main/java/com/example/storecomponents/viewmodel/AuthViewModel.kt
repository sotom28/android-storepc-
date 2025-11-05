package com.example.storecomponents.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storecomponents.data.model.Usuarios
import com.example.storecomponents.data.repository.AuthRepository
import com.example.storecomponents.data.model.Userole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class UserRole {CLIENT , ADMIN,NOME}

class AuthViewModel(
    // Inyección de dependencia del repositorio de autenticación
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _estadoAuth = MutableStateFlow<EstadoAuth>(EstadoAuth.Inicial)
    val estadoAuth: StateFlow<EstadoAuth> = _estadoAuth

    // estado simple de rol para facilitar la comprobación desde la UI
    private val _role = MutableStateFlow(UserRole.NOME)
    val role: StateFlow<UserRole> = _role

    val usuarioActual = authRepository.usuarioActual

    // Método para iniciar sesión con correo electrónico y contraseña
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _estadoAuth.value = EstadoAuth.Cargando
            val resultado = authRepository.login(email, password)
            _estadoAuth.value = if (resultado.isSuccess) {
                // actualizar rol según el usuario devuelto
                val user = resultado.getOrNull()!!
                _role.value = if (user.role == Userole.ADMIN) UserRole.ADMIN else UserRole.CLIENT
                EstadoAuth.Exito(user)
            } else {
                _role.value = UserRole.NOME
                EstadoAuth.Error(resultado.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }

    // Nuevo método: login por username (credenciales de ejemplo)
    // usuario ejemplo cliente: username = "cliente 1" o "cliente1" o correo "cliente@store.com", password = "cliente123"
    // usuario ejemplo admin: username contiene "admin" y password == "admin123"
    fun loginByUsername(username: String, password: String) {
        viewModelScope.launch {
            _estadoAuth.value = EstadoAuth.Cargando

            val normalized = username.trim().lowercase()

            // caso cliente de ejemplo (acepta varias variantes)
            if ((normalized.contains("cliente") || normalized == "cliente@store.com") && password == "cliente123") {
                val user = Usuarios(
                    id = 2,
                    nombre = username,
                    correo = "cliente@store.com",
                    role = Userole.CLIENT,
                    password = password,
                    confirmarPassword = "",
                    direccion = ""
                )
                _role.value = UserRole.CLIENT
                _estadoAuth.value = EstadoAuth.Exito(user)
                println("AuthViewModel: loginByUsername -> SUCCESS CLIENT normalized='$normalized'")
                return@launch
            }

            // caso admin de ejemplo
            if (normalized.contains("admin") && password == "admin123") {
                val user = Usuarios(
                    id = 1,
                    nombre = username,
                    correo = "admin@store.com",
                    role = Userole.ADMIN,
                    password = password,
                    confirmarPassword = "",
                    direccion = ""
                )
                _role.value = UserRole.ADMIN
                _estadoAuth.value = EstadoAuth.Exito(user)
                println("AuthViewModel: loginByUsername -> SUCCESS ADMIN normalized='$normalized'")
                return@launch
            }

            // fallback: intentar con repositorio usando el campo correo
            val resultado = authRepository.login(username, password)
            if (resultado.isSuccess) {
                val user = resultado.getOrNull()!!
                _role.value = if (user.role == Userole.ADMIN) UserRole.ADMIN else UserRole.CLIENT
                _estadoAuth.value = EstadoAuth.Exito(user)
                println("AuthViewModel: loginByUsername -> SUCCESS REPO username='$username'")
            } else {
                _role.value = UserRole.NOME
                _estadoAuth.value = EstadoAuth.Error("Credenciales inválidas")
                println("AuthViewModel: loginByUsername -> FAILED normalized='$normalized'")
            }
        }
    }

    // Nuevo: registrar y auto-logear
    fun register(name: String, email: String, roleStr: String, password: String, confirmarPassword: String = "", direccion: String = "") {
        viewModelScope.launch {
            _estadoAuth.value = EstadoAuth.Cargando
            try {
                // determinar role
                val role = if (roleStr.equals("admin", ignoreCase = true)) Userole.ADMIN else Userole.CLIENT
                // calcular id nuevo
                val existing = authRepository.obtenerTodosLosUsuarios()
                val newId = (existing.maxOfOrNull { it.id } ?: 0) + 1
                val nuevo = Usuarios(id = newId, nombre = name, correo = email, role = role, password = password, confirmarPassword = confirmarPassword, direccion = direccion)

                val reg = authRepository.registro(nuevo)
                if (reg.isSuccess) {
                    // después de registrar, intentar login para activar la sesión
                    val loginRes = authRepository.login(email, password)
                    if (loginRes.isSuccess) {
                        val user = loginRes.getOrNull()!!
                        _role.value = if (user.role == Userole.ADMIN) UserRole.ADMIN else UserRole.CLIENT
                        _estadoAuth.value = EstadoAuth.Exito(user)
                    } else {
                        _role.value = UserRole.NOME
                        _estadoAuth.value = EstadoAuth.Error("Registro OK, pero login falló: ${loginRes.exceptionOrNull()?.message}")
                    }
                } else {
                    _role.value = UserRole.NOME
                    _estadoAuth.value = EstadoAuth.Error(reg.exceptionOrNull()?.message ?: "Error al registrar")
                }

            } catch (e: Exception) {
                _role.value = UserRole.NOME
                _estadoAuth.value = EstadoAuth.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun cerrarSesion() {
        authRepository.cerrarSesion()
        _estadoAuth.value = EstadoAuth.Inicial
        _role.value = UserRole.NOME
    }
}
////
sealed class EstadoAuth{
    object Inicial : EstadoAuth()
    object Cargando : EstadoAuth()
    data class Exito(val usuario: Usuarios) : EstadoAuth()
    data class Error (val mensaje: String) : EstadoAuth()

}