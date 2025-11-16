package com.example.storecomponents.data.repository

import com.example.storecomponents.data.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProductoRepository {

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

    init {
        // Productos de muestra con imágenes y descripciones completas
        val productosIniciales = listOf(
            Producto(
                id = "1",
                nombre = "Laptop HP Pavilion",
                descripcion = "Laptop HP Pavilion 15.6\" con procesador Intel Core i5, 8GB RAM, 256GB SSD. Ideal para trabajo y estudio.",
                precio = 699.99,
                stock = 15,
                categoria = "Electrónicos",
                imagenUrl = "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400"
            ),
            Producto(
                id = "2",
                nombre = "Mouse Logitech MX Master 3",
                descripcion = "Mouse inalámbrico ergonómico con sensor de alta precisión y batería de larga duración. Perfecto para productividad.",
                precio = 99.99,
                stock = 50,
                categoria = "Accesorios",
                imagenUrl = "https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=400"
            ),
            Producto(
                id = "3",
                nombre = "Teclado Mecánico Keychron K2",
                descripcion = "Teclado mecánico inalámbrico compacto 75% con switches Gateron. RGB, conexión Bluetooth y cable USB-C.",
                precio = 89.99,
                stock = 30,
                categoria = "Accesorios",
                imagenUrl = "https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=400"
            ),
            Producto(
                id = "4",
                nombre = "Monitor LG UltraWide 29\"",
                descripcion = "Monitor IPS UltraWide 29\" 2560x1080, ideal para multitarea y edición. HDR10, 75Hz, FreeSync.",
                precio = 299.99,
                stock = 20,
                categoria = "Monitores",
                imagenUrl = "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=400"
            )
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