package com.example.storecomponents.data.model

import java.util.UUID

data class Producto(
    val id: String = UUID.randomUUID().toString(),
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val stock: Int,
    val categoria: String,
    val imagenUrl: String,
    val clienteId: String? = null
) {
    fun estaDisponible(): Boolean = stock > 0
}

