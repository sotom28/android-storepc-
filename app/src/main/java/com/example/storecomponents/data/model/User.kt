package com.example.storecomponents.data.model

import android.graphics.Bitmap
import java.util.UUID

// Definición de la clase de datos para usuarios unificada
data class User(
    val id: String = UUID.randomUUID().toString(),
    var name: String = "",
    var correo: String = "",
    val role: UserRole = UserRole.CLIENT,
    var password: String = "",
    var direccion: String = "",
    var phone: String = "",
    val photo: Bitmap? = null,
    val purchases: List<Purchase> = emptyList()
)

// Definición de roles de usuario
enum class UserRole {
    ADMIN,
    CLIENT
}
