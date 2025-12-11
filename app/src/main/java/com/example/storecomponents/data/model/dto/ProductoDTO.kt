package com.example.storecomponents.data.model.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO para Producto que coincide con el backend
 */
data class ProductoDTO(
    @SerializedName("idProducto")
    val idProducto: Long? = null,
    
    @SerializedName("nombre")
    val nombre: String,
    
    @SerializedName("precio")
    val precio: Double,
    
    @SerializedName("stock")
    val stock: Int,
    
    @SerializedName("categoria")
    val categoria: String,
    
    @SerializedName("descripcion")
    val descripcion: String? = null
)
