package com.example.storecomponents.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storecomponents.data.model.Usuarios
import com.example.storecomponents.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UsuariosViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _usuarios = MutableStateFlow<List<Usuarios>>(emptyList())
    val usuarios: StateFlow<List<Usuarios>> = _usuarios.asStateFlow()

    private val _estado = MutableStateFlow<UsuarioEstado>(UsuarioEstado.Inicial)
    val estado: StateFlow<UsuarioEstado> = _estado.asStateFlow()

    init {
        cargarUsuarios()
    }

    private fun cargarUsuarios() {
        viewModelScope.launch {
            _usuarios.value = authRepository.obtenerTodosLosUsuarios()
        }
    }

    fun agregarUsuario(usuario: Usuarios) {
        viewModelScope.launch {
            _estado.value = UsuarioEstado.Cargando
            val res = authRepository.agregarUsuario(usuario)
            _estado.value = if (res.isSuccess) {
                cargarUsuarios()
                UsuarioEstado.Exito("Usuario agregado")
            } else {
                UsuarioEstado.Error(res.exceptionOrNull()?.message ?: "Error")
            }
        }
    }

    fun actualizarUsuario(usuario: Usuarios) {
        viewModelScope.launch {
            _estado.value = UsuarioEstado.Cargando
            val res = authRepository.actualizarUsuario(usuario)
            _estado.value = if (res.isSuccess) {
                cargarUsuarios()
                UsuarioEstado.Exito("Usuario actualizado")
            } else {
                UsuarioEstado.Error(res.exceptionOrNull()?.message ?: "Error")
            }
        }
    }

    fun eliminarUsuario(id: Int) {
        viewModelScope.launch {
            _estado.value = UsuarioEstado.Cargando
            val res = authRepository.eliminarUsuarioPorId(id)
            _estado.value = if (res.isSuccess) {
                cargarUsuarios()
                UsuarioEstado.Exito("Usuario eliminado")
            } else {
                UsuarioEstado.Error(res.exceptionOrNull()?.message ?: "Error")
            }
        }
    }
}

sealed class UsuarioEstado {
    object Inicial : UsuarioEstado()
    object Cargando : UsuarioEstado()
    data class Exito(val mensaje: String) : UsuarioEstado()
    data class Error(val mensaje: String) : UsuarioEstado()
}

