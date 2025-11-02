package com.example.storecomponents.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storecomponents.data.model.Usuarios
import com.example.storecomponents.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    // Inyección de dependencia del repositorio de autenticación
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _estadoAuth = MutableStateFlow<EstadoAuth>(EstadoAuth.Inicial)
    val estadoAuth: StateFlow<EstadoAuth> = _estadoAuth

    val usuarioActual = authRepository.usuarioActual

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _estadoAuth.value = EstadoAuth.Cargando
            val resultado = authRepository.login(email, password)
            _estadoAuth.value = if (resultado.isSuccess) {
                EstadoAuth.Exito(resultado.getOrNull()!!)
            } else {
                EstadoAuth.Error(resultado.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }

    fun cerrarSesion() {
        authRepository.cerrarSesion()
        _estadoAuth.value = EstadoAuth.Inicial
    }
}

sealed class EstadoAuth{
    object Inicial : EstadoAuth()
    object Cargando : EstadoAuth()
    data class Exito(val usuario: Usuarios) : EstadoAuth()
    data class Error (val mensaje: String) : EstadoAuth()

}