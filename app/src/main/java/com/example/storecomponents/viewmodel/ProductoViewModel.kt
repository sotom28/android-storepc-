package com.example.storecomponents.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storecomponents.data.model.Producto
import com.example.storecomponents.data.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

// ViewModel para gestionar productos
class ProductoViewModel(
    private val productoRepository: ProductoRepository = ProductoRepository()
) : ViewModel() {

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

    private val _estadoProducto = MutableStateFlow<EstadoProducto>(EstadoProducto.Inicial)
    val estadoProducto: StateFlow<EstadoProducto> = _estadoProducto.asStateFlow()

    // Estados para búsqueda y filtrado
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    // Lista de categorías únicas
    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    // Productos filtrados según búsqueda y categoría
    private val _productosFiltrados = MutableStateFlow<List<Producto>>(emptyList())
    val productosFiltrados: StateFlow<List<Producto>> = _productosFiltrados.asStateFlow()

    init {
        cargarProductos()
        setupFiltros()
    }

    private fun cargarProductos() {
        viewModelScope.launch {
            _productos.value = productoRepository.obtenerTodosLosProductos()
            actualizarCategorias()
        }
    }

    private fun actualizarCategorias() {
        _categories.value = _productos.value.map { it.categoria }.distinct().sorted()
    }

    private fun setupFiltros() {
        viewModelScope.launch {
            combine(
                _productos,
                _searchQuery,
                _selectedCategory
            ) { productos, query, category ->
                filtrarProductos(productos, query, category)
            }.collect { productosFiltrados ->
                _productosFiltrados.value = productosFiltrados
            }
        }
    }

    private fun filtrarProductos(
        productos: List<Producto>,
        query: String,
        category: String?
    ): List<Producto> {
        return productos.filter { producto ->
            val matchesQuery = query.isBlank() ||
                    producto.nombre.contains(query, ignoreCase = true) ||
                    producto.descripcion.contains(query, ignoreCase = true)
            val matchesCategory = category == null || producto.categoria == category
            matchesQuery && matchesCategory
        }
    }

    // Funciones de búsqueda y filtrado
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelected(category: String?) {
        _selectedCategory.value = category
    }

    // Función para agregar un nuevo producto
    fun agregarProducto(producto: Producto) {
        viewModelScope.launch {
            _estadoProducto.value = EstadoProducto.Cargando
            val resultado = productoRepository.agregarProducto(producto)
            _estadoProducto.value = if (resultado.isSuccess) {
                cargarProductos() // Recarga productos y actualiza categorías
                EstadoProducto.Exito("Producto agregado")
            } else {
                EstadoProducto.Error(resultado.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }

    // Función para actualizar un producto existente
    fun actualizarProducto(producto: Producto) {
        viewModelScope.launch {
            _estadoProducto.value = EstadoProducto.Cargando
            val resultado = productoRepository.actualizarProducto(producto)
            _estadoProducto.value = if (resultado.isSuccess) {
                cargarProductos()
                EstadoProducto.Exito("Producto actualizado")
            } else {
                EstadoProducto.Error(resultado.exceptionOrNull()?.message ?: "Error desconocido")
            }
        }
    }

    // Función para eliminar un producto por su ID
    fun eliminarProducto(productoId: String) {
        viewModelScope.launch {
            _estadoProducto.value = EstadoProducto.Cargando
            val resultado = productoRepository.eliminarProducto(productoId)
            _estadoProducto.value = if (resultado.isSuccess) {
                cargarProductos()
                EstadoProducto.Exito("Producto eliminado")
            } else {
                EstadoProducto.Error(resultado.exceptionOrNull()?.message ?: "Error")
            }
        }
    }

    // Función para limpiar el estado después de mostrar un mensaje
    fun limpiarEstado() {
        _estadoProducto.value = EstadoProducto.Inicial
    }
}


sealed class EstadoProducto {
    object Inicial : EstadoProducto()
    object Cargando : EstadoProducto()
    data class Exito(val mensaje: String) : EstadoProducto()
    data class Error(val mensaje: String) : EstadoProducto()
}