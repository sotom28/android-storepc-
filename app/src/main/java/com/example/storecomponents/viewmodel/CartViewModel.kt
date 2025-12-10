package com.example.storecomponents.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

// Datos del carrito: cantidad inmutable para facilitar la recomposición mediante reemplazo del elemento
data class CartItem(val productoId: String, val nombre: String, val precio: Double, val cantidad: Int, val imagenUrl: String = "")

class CartViewModel : ViewModel() {
    // usar el CartItem local y no un paquete diferente
    private val _items = mutableStateListOf<CartItem>()
    val items: List<CartItem> = _items

    // agregar producto al carrito; si existe, reemplaza el elemento con nueva cantidad para disparar recomposición
    fun add(productId: String, name: String, price: Double, qty: Int, imagenUrl: String = "") {
        Log.d("CartViewModel", "add called: productId=$productId, name=$name, price=$price, qty=$qty, imagenUrl=$imagenUrl")
        if (qty <= 0) return
        val idx = _items.indexOfFirst { it.productoId == productId }
        if (idx >= 0) {
            val existing = _items[idx]
            val updated = existing.copy(cantidad = existing.cantidad + qty)
            _items[idx] = updated
        } else {
            _items.add(CartItem(productId, name, price, qty, imagenUrl))
        }
        Log.d("CartViewModel", "items now=${_items.map { it.productoId + ":" + it.cantidad }}")
    }

    // actualizar la cantidad de un item; si qty <= 0 lo elimina
    fun updateQuantity(productId: String, qty: Int) {
        val idx = _items.indexOfFirst { it.productoId == productId }
        if (idx < 0) return
        if (qty <= 0) {
            _items.removeAt(idx)
        } else {
            val existing = _items[idx]
            _items[idx] = existing.copy(cantidad = qty)
        }
    }

    // eliminar un producto del carrito
    fun remove(productId: String) {
        val idx = _items.indexOfFirst { it.productoId == productId }
        if (idx >= 0) _items.removeAt(idx)
    }

    fun clear() = _items.clear()

    fun total(): Double = _items.sumOf { it.precio * it.cantidad }

    fun itemCount(): Int = _items.sumOf { it.cantidad }

    fun isEmpty(): Boolean = _items.isEmpty()
}
