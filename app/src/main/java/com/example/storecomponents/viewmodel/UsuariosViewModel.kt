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

    /**
     * Cargar usuarios desde el backend
     */
    fun cargarUsuarios() {
        viewModelScope.launch {
            _estado.value = UsuarioEstado.Cargando

            val result = authRepository.obtenerTodosLosUsuarios()

            if (result.isSuccess) {
                _usuarios.value = result.getOrNull() ?: emptyList()
                _estado.value = UsuarioEstado.Inicial
            } else {
                _estado.value = UsuarioEstado.Error(
                    result.exceptionOrNull()?.message ?: "Error al cargar usuarios"
                )
            }
        }
    }

    /**
     * Agregar usuario (solo local por ahora)
     * El backend no tiene endpoint específico para que un admin cree usuarios
     * Se podría usar el endpoint de registro
     */
    fun agregarUsuario(usuario: Usuarios) {
        viewModelScope.launch {
            _estado.value = UsuarioEstado.Cargando

            // Por ahora agregamos localmente
            // TODO: Usar authRepository.registro() cuando se implemente para admin
            val usuariosActuales = _usuarios.value.toMutableList()
            usuariosActuales.add(usuario)
            _usuarios.value = usuariosActuales

            _estado.value = UsuarioEstado.Exito("Usuario agregado (solo local)")
        }
    }

    /**
     * Actualizar usuario (solo local por ahora)
     * El backend no tiene endpoint para actualizar usuarios
     */
    fun actualizarUsuario(usuario: Usuarios) {
        viewModelScope.launch {
            _estado.value = UsuarioEstado.Cargando

            // Por ahora actualizamos localmente
            // TODO: Implementar cuando el backend tenga endpoint PUT
            val usuariosActuales = _usuarios.value.toMutableList()
            val index = usuariosActuales.indexOfFirst { it.id == usuario.id }

            if (index != -1) {
                usuariosActuales[index] = usuario
                _usuarios.value = usuariosActuales
                _estado.value = UsuarioEstado.Exito("Usuario actualizado (solo local)")
            } else {
                _estado.value = UsuarioEstado.Error("Usuario no encontrado")
            }
        }
    }

    /**
     * Eliminar usuario (solo local por ahora)
     * El backend no tiene endpoint para eliminar usuarios
     * Parámetro cambiado a Long para compatibilidad
     */
    fun eliminarUsuario(id: Long) {
        viewModelScope.launch {
            _estado.value = UsuarioEstado.Cargando

            // Por ahora eliminamos localmente
            // TODO: Implementar cuando el backend tenga endpoint DELETE
            val usuariosActuales = _usuarios.value.filter { it.id != id }
            _usuarios.value = usuariosActuales

            _estado.value = UsuarioEstado.Exito("Usuario eliminado (solo local)")
        }
    }
}

sealed class UsuarioEstado {
    object Inicial : UsuarioEstado()
    object Cargando : UsuarioEstado()
    data class Exito(val mensaje: String) : UsuarioEstado()
    data class Error(val mensaje: String) : UsuarioEstado()
}