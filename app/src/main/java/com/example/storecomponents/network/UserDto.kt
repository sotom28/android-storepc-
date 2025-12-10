package com.example.storecomponents.network

import com.google.gson.annotations.SerializedName

// DTO simple que coincide con el JSON esperado por el backend
data class UserDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("name") val name: String,
    @SerializedName("role") val role: String,
    @SerializedName("isSalesManager") val isSalesManager: Boolean = false,
    @SerializedName("email") val email: String? = null,
    @SerializedName("password") val password: String? = null,
    @SerializedName("direccion") val direccion: String? = null
)

