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
    // usuario ejemplo cliente: username = "cliente 1", password = "cliente123"
    // usuario ejemplo admin: username contiene "admin" y password == "admin123"
    fun loginByUsername(username: String, password: String) {
        viewModelScope.launch {
            _estadoAuth.value = EstadoAuth.Cargando

            // caso cliente de ejemplo
            if (username == "cliente 1" && password == "cliente123") {
                val user = Usuarios(
                    id = 2,
                    nombre = username,
                    correo = "cliente@store.com",
                    role = Userole.CLIENT,
                    password = password
                )
                _role.value = UserRole.CLIENT
                _estadoAuth.value = EstadoAuth.Exito(user)
                return@launch
            }

            // caso admin de ejemplo
            if (username.contains("admin", ignoreCase = true) && password == "admin123") {
                val user = Usuarios(
                    id = 1,
                    nombre = username,
                    correo = "admin@store.com",
                    role = Userole.ADMIN,
                    password = password
                )
                _role.value = UserRole.ADMIN
                _estadoAuth.value = EstadoAuth.Exito(user)
                return@launch
            }

            // fallback: intentar con repositorio usando el campo correo
            val resultado = authRepository.login(username, password)
            if (resultado.isSuccess) {
                val user = resultado.getOrNull()!!
                _role.value = if (user.role == Userole.ADMIN) UserRole.ADMIN else UserRole.CLIENT
                _estadoAuth.value = EstadoAuth.Exito(user)
            } else {
                _role.value = UserRole.NOME
                _estadoAuth.value = EstadoAuth.Error("Credenciales inválidas")
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