package com.example.storecomponents.data.model

import com.example.storecomponents.data.model.dto.ProductoDTO

data class Producto(
    val id: String,  // Convertiremos Long a String localmente
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val stock: Int,
    val categoria: String,
    val imagenUrl: String = "",  // Esto no viene del backend
    val clienteId: String? = null
) {
    fun estaDisponible(): Boolean = stock > 0
}

// Función de extensión para convertir DTO a modelo de dominio
fun ProductoDTO.toDomain(imagenUrl: String = ""): Producto {
    return Producto(
        id = this.idProducto?.toString() ?: "0",
        nombre = this.nombre,
        descripcion = this.descripcion ?: "",
        precio = this.precio,
        stock = this.stock,
        categoria = this.categoria,
        imagenUrl = imagenUrl
    )
}

// Función de extensión para convertir modelo de dominio a DTO
fun Producto.toDTO(): ProductoDTO {
    return ProductoDTO(
        idProducto = this.id.toLongOrNull(),
        nombre = this.nombre,
        precio = this.precio,
        stock = this.stock,
        categoria = this.categoria,
        descripcion = this.descripcion
    )
}
