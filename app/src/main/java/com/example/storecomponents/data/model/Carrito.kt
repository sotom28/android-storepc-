package com.example.storecomponents.data.model

import java.util.UUID

// Item individual en el carrito
data class ItemCarrito(
    val id: String = UUID.randomUUID().toString(),
    val producto: Producto,
    val cantidad: Int = 1
) {
    val subtotal: Double get() = producto.precio * cantidad
}

// Carrito de compras
data class Carrito(
    val id: String = UUID.randomUUID().toString(),
    val items: List<ItemCarrito> = emptyList(),
    val clienteId: String? = null
) {
    val total: Double get() = items.sumOf { it.subtotal }
    val cantidadTotal: Int get() = items.sumOf { it.cantidad }
    val estaVacio: Boolean get() = items.isEmpty()

    fun agregarProducto(producto: Producto, cantidad: Int = 1): Carrito {
        val itemExistente = items.find { it.producto.id == producto.id }

        return if (itemExistente != null) {
            val nuevosItems = items.map {
                if (it.producto.id == producto.id) {
                    it.copy(cantidad = it.cantidad + cantidad)
                } else {
                    it
                }
            }
            copy(items = nuevosItems)
        } else {
            // Si no existe, agregar nuevo item
            copy(items = items + ItemCarrito(producto = producto, cantidad = cantidad))
        }
    }

    fun actualizarCantidad(itemId: String, nuevaCantidad: Int): Carrito {
        if (nuevaCantidad <= 0) {
            return eliminarItem(itemId)
        }

        val nuevosItems = items.map {
            if (it.id == itemId) {
                it.copy(cantidad = nuevaCantidad)
            } else {
                it
            }
        }
        return copy(items = nuevosItems)
    }

    fun eliminarItem(itemId: String): Carrito {
        return copy(items = items.filter { it.id != itemId })
    }

    fun limpiar(): Carrito {
        return copy(items = emptyList())
    }
}