package com.example.storecomponents.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

// Datos del carrito: cantidad mutable para poder actualizarla
data class CartItem(val productoId: String, val nombre: String, val precio: Double, var cantidad: Int)

class CartViewModel : ViewModel() {
    // usar el CartItem local y no un paquete diferente
    private val _items = mutableStateListOf<CartItem>()
    val items: List<CartItem> = _items

    // agregar producto al carrito; si existe, aumenta la cantidad
    fun add(productId: String, name: String, price: Double, qty: Int) {
        if (qty <= 0) return
        val existing = _items.find { it.productoId == productId }
        if (existing != null) {
            existing.cantidad += qty
        } else {
            _items.add(CartItem(productId, name, price, qty))
        }
    }

    // actualizar la cantidad de un item; si qty <= 0 lo elimina
    fun updateQuantity(productId: String, qty: Int) {
        val existing = _items.find { it.productoId == productId } ?: return
        if (qty <= 0) {
            _items.remove(existing)
        } else {
            existing.cantidad = qty
        }
    }

    // eliminar un producto del carrito
    fun remove(productId: String) {
        val existing = _items.find { it.productoId == productId } ?: return
        _items.remove(existing)
    }

    fun clear() = _items.clear()

    fun total(): Double = _items.sumOf { it.precio * it.cantidad }

    fun itemCount(): Int = _items.sumOf { it.cantidad }

    fun isEmpty(): Boolean = _items.isEmpty()
}
