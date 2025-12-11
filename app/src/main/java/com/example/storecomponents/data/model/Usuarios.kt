package com.example.storecomponents.data.model

import com.example.storecomponents.data.model.dto.UsuarioDTO

// Definición de la clase de datos para usuarios (modelo de dominio)
data class Usuarios(
    val id: Long,
    var nombre: String = "",
    var username: String = "",
    var correo: String = "",
    val role: Userole = Userole.CLIENT,
    var password: String = "",
    var confirmarPassword: String = "",
    var direccion: String = ""
)

// Función de extensión para convertir DTO a modelo de dominio
fun UsuarioDTO.toDomain(role: Userole = Userole.CLIENT): Usuarios {
    return Usuarios(
        id = this.idUsuario ?: 0L,
        nombre = this.nombreReal,
        username = this.username,
        correo = this.email,
        role = role,
        password = "",  // No exponemos la contraseña
        confirmarPassword = "",
        direccion = ""
    )
}

// Función de extensión para convertir modelo de dominio a DTO
fun Usuarios.toDTO(): UsuarioDTO {
    return UsuarioDTO(
        idUsuario = if (this.id > 0) this.id else null,
        username = this.username,
        nombreReal = this.nombre,
        email = this.correo,
        contrasena = if (this.password.isNotEmpty()) this.password else null
    )
}

// Definición de roles de usuario
enum class Userole {
    ADMIN,
    CLIENT
}

// Definición de los posibles estados relacionados con el usuario
sealed class usuarioEstado {
    object Inicial : usuarioEstado()
    object Cargando : usuarioEstado()
    data class Exito(val usuario: Usuarios) : usuarioEstado()
    data class Error(val mensaje: String) : usuarioEstado()
}
