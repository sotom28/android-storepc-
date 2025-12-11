package com.example.storecomponents.data.model.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO para Usuario que coincide con el backend
 */
data class UsuarioDTO(
    @SerializedName("idUsuario")
    val idUsuario: Long? = null,
    
    @SerializedName("username")
    val username: String,
    
    @SerializedName("nombreReal")
    val nombreReal: String,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("contrasena")
    val contrasena: String? = null  // Null en respuestas (WRITE_ONLY)
)

/**
 * Request para Login
 */
data class LoginRequest(
    val username: String,
    val contrasena: String
)

/**
 * Request para Registro
 */
data class RegisterRequest(
    val username: String,
    val nombreReal: String,
    val email: String,
    val contrasena: String
)
