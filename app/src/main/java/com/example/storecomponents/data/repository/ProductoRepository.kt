package com.example.storecomponents.data.repository

import com.example.storecomponents.data.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProductoRepository {

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

    init {
        // Datos de productos de ejemplo (ASCII sencillo)
        val productosIniciales = listOf(
            Producto("1", "Producto 1", "Descripcion 1", 10.0, 5, "Categoria1", ""),
            Producto("2", "Producto 2", "Descripcion 2", 20.0, 10, "Categoria2", ""),
            Producto("3", "Producto 3", "Descripcion 3", 30.0, 3, "Categoria3", "")
        )
        _productos.value = productosIniciales
    }

    // Obtener todos los productos
    fun obtenerTodosLosProductos(): List<Producto> = _productos.value

    // Obtener un producto por id
    fun obtenerProductoPorId(id: String): Producto? {
        return _productos.value.find { it.id == id }
    }

    // Agregar un producto
    fun agregarProducto(producto: Producto): Result<Unit> {
        return try {
            _productos.value = _productos.value + producto
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Actualizar un producto (por id)
    fun actualizarProducto(producto: Producto): Result<Unit> {
        return try {
            _productos.value = _productos.value.map { if (it.id == producto.id) producto else it }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Eliminar un producto por id
    fun eliminarProducto(productoId: String): Result<Unit> {
        return try {
            _productos.value = _productos.value.filter { it.id != productoId }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
