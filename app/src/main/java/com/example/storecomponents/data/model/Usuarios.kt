package com.example.storecomponents.data.model


// Definición de la clase de datos para usuarios
data class Usuarios(
    val id : Int,
    val nombre : String = "",
    val correo : String = "",
    val role: Userole = Userole.CLIENTE,
    val password : String

)
// Definición de roles de usuario
enum class Userole {
    ADMIN,
    CLIENTE
}
/// Definición de los posibles estados relacionados con el usuario
sealed class usuarioEstado {
    object Inicial : usuarioEstado()
    object Cargando : usuarioEstado()
    data class Exito(val usuario: Usuarios) : usuarioEstado()
    data class Error(val mensaje: String) : usuarioEstado()
}