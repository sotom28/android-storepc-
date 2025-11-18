package com.example.storecomponents.data.repository

import com.example.storecomponents.data.model.Carrito
import com.example.storecomponents.data.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CarritoRepository private constructor() {

    companion object {
        @Volatile
        private var INSTANCE: CarritoRepository? = null

        fun getInstance(): CarritoRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: CarritoRepository().also { INSTANCE = it }
            }
        }
    }

    // Estado del carrito en memoria
    private val _carrito = MutableStateFlow(Carrito())
    val carrito: StateFlow<Carrito> = _carrito.asStateFlow()

    // Agregar producto al carrito
    fun agregarProducto(producto: Producto, cantidad: Int = 1): Result<Carrito> {
        return try {
            // Validar stock disponible
            val itemExistente = _carrito.value.items.find { it.producto.id == producto.id }
            val cantidadActual = itemExistente?.cantidad ?: 0
            val cantidadTotal = cantidadActual + cantidad

            if (cantidadTotal > producto.stock) {
                return Result.failure(Exception("Stock insuficiente. Disponible: ${producto.stock}"))
            }

            _carrito.value = _carrito.value.agregarProducto(producto, cantidad)
            Result.success(_carrito.value)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Actualizar cantidad de un item
    fun actualizarCantidad(itemId: String, nuevaCantidad: Int): Result<Carrito> {
        return try {
            val item = _carrito.value.items.find { it.id == itemId }
                ?: return Result.failure(Exception("Item no encontrado"))

            // Validar stock
            if (nuevaCantidad > item.producto.stock) {
                return Result.failure(Exception("Stock insuficiente. Disponible: ${item.producto.stock}"))
            }

            _carrito.value = _carrito.value.actualizarCantidad(itemId, nuevaCantidad)
            Result.success(_carrito.value)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Eliminar item del carrito
    fun eliminarItem(itemId: String): Result<Carrito> {
        return try {
            _carrito.value = _carrito.value.eliminarItem(itemId)
            Result.success(_carrito.value)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Limpiar carrito
    fun limpiarCarrito(): Result<Carrito> {
        return try {
            _carrito.value = _carrito.value.limpiar()
            Result.success(_carrito.value)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Obtener carrito actual
    fun obtenerCarrito(): Carrito {
        return _carrito.value
    }
}