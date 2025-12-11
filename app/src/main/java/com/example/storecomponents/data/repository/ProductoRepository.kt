package com.example.storecomponents.data.repository

import com.example.storecomponents.data.model.Producto
import com.example.storecomponents.data.model.toDomain
import com.example.storecomponents.data.model.toDTO
import com.example.storecomponents.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProductoRepository {

    private val apiService = RetrofitClient.apiService

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

    // URLs de imágenes por defecto para categorías
    private val imagenesPorCategoria = mapOf(
        "Electrónicos" to "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400",
        "Accesorios" to "https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=400",
        "Monitores" to "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=400",
        "Computadoras" to "https://images.unsplash.com/photo-1587202372775-e229f172b9d7?w=400"
    )

    /**
     * Cargar todos los productos del backend
     */
    suspend fun cargarProductos(): Result<List<Producto>> {
        return try {
            val response = apiService.getAllProductos()

            if (response.isSuccessful && response.body() != null) {
                val productos = response.body()!!.map { dto ->
                    val imagenUrl = imagenesPorCategoria[dto.categoria]
                        ?: "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400"
                    dto.toDomain(imagenUrl)
                }
                _productos.value = productos
                Result.success(productos)
            } else {
                Result.failure(Exception("Error al cargar productos: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    /**
     * Obtener un producto por ID
     */
    suspend fun obtenerProductoPorId(id: Long): Result<Producto> {
        return try {
            val response = apiService.getProductoById(id)

            if (response.isSuccessful && response.body() != null) {
                val dto = response.body()!!
                val imagenUrl = imagenesPorCategoria[dto.categoria]
                    ?: "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400"
                val producto = dto.toDomain(imagenUrl)
                Result.success(producto)
            } else {
                Result.failure(Exception("Producto no encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    /**
     * Obtener productos por categoría
     */
    suspend fun obtenerProductosPorCategoria(categoria: String): Result<List<Producto>> {
        return try {
            val response = apiService.getProductosByCategoria(categoria)

            if (response.isSuccessful && response.body() != null) {
                val productos = response.body()!!.map { dto ->
                    val imagenUrl = imagenesPorCategoria[dto.categoria]
                        ?: "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400"
                    dto.toDomain(imagenUrl)
                }
                Result.success(productos)
            } else {
                Result.failure(Exception("No se encontraron productos en esa categoría"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    /**
     * Agregar un producto nuevo
     */
    suspend fun agregarProducto(producto: Producto): Result<Producto> {
        return try {
            val dto = producto.toDTO()
            val response = apiService.crearProducto(dto)

            if (response.isSuccessful && response.body() != null) {
                val nuevoDto = response.body()!!
                val imagenUrl = imagenesPorCategoria[nuevoDto.categoria]
                    ?: "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400"
                val nuevoProducto = nuevoDto.toDomain(imagenUrl)

                // Actualizar lista local
                _productos.value = _productos.value + nuevoProducto
                Result.success(nuevoProducto)
            } else {
                Result.failure(Exception("Error al crear producto: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    /**
     * Obtener todos los productos (local)
     */
    fun obtenerTodosLosProductos(): List<Producto> = _productos.value

    /**
     * Obtener un producto por id (local)
     */
    fun obtenerProductoPorIdLocal(id: String): Producto? {
        return _productos.value.find { it.id == id }
    }
}
