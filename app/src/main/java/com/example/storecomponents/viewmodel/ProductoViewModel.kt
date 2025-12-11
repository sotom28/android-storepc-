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

sealed class ProductoEstado {
    object Inicial : ProductoEstado()
    object Cargando : ProductoEstado()
    data class Exito(val productos: List<Producto>) : ProductoEstado()
    data class Error(val mensaje: String) : ProductoEstado()
}

class ProductoViewModel(
    private val repository: ProductoRepository = ProductoRepository()
) : ViewModel() {

    val productos = repository.productos

    private val _productosState = MutableStateFlow<ProductoEstado>(ProductoEstado.Inicial)
    val productosState: StateFlow<ProductoEstado> = _productosState.asStateFlow()

    // Estados para filtrado y búsqueda
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    private val _productosFiltrados = MutableStateFlow<List<Producto>>(emptyList())
    val productosFiltrados: StateFlow<List<Producto>> = _productosFiltrados.asStateFlow()

    // StateFlow interno para mantener compatibilidad
    private val _productosInterno = MutableStateFlow<List<Producto>>(emptyList())

    init {
        cargarProductos()
        observarCambios()
    }

    /**
     * Observar cambios en productos, búsqueda y categoría para filtrar
     */
    private fun observarCambios() {
        viewModelScope.launch {
            // Sincronizar con el repository
            repository.productos.collect { productos ->
                _productosInterno.value = productos
                actualizarCategorias(productos)
            }
        }

        viewModelScope.launch {
            // Combinar productos, búsqueda y categoría para filtrar
            combine(
                _productosInterno,
                _searchQuery,
                _selectedCategory
            ) { productos, query, category ->
                filtrarProductos(productos, query, category)
            }.collect { productosFiltrados ->
                _productosFiltrados.value = productosFiltrados
            }
        }
    }

    /**
     * Actualizar lista de categorías disponibles
     */
    private fun actualizarCategorias(productos: List<Producto>) {
        val categoriasUnicas = productos
            .map { it.categoria }
            .distinct()
            .sorted()
        _categories.value = categoriasUnicas
    }

    /**
     * Filtrar productos según búsqueda y categoría
     */
    private fun filtrarProductos(
        productos: List<Producto>,
        query: String,
        category: String?
    ): List<Producto> {
        var resultado = productos

        // Filtrar por categoría
        if (category != null) {
            resultado = resultado.filter { it.categoria == category }
        }

        // Filtrar por búsqueda
        if (query.isNotBlank()) {
            resultado = resultado.filter { producto ->
                producto.nombre.contains(query, ignoreCase = true) ||
                        producto.descripcion.contains(query, ignoreCase = true) ||
                        producto.categoria.contains(query, ignoreCase = true)
            }
        }

        return resultado
    }

    /**
     * Actualizar consulta de búsqueda
     */
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    /**
     * Seleccionar categoría para filtrar
     */
    fun onCategorySelected(category: String?) {
        _selectedCategory.value = category
    }

    /**
     * Cargar productos desde el backend
     */
    fun cargarProductos() {
        viewModelScope.launch {
            _productosState.value = ProductoEstado.Cargando

            val result = repository.cargarProductos()

            _productosState.value = if (result.isSuccess) {
                ProductoEstado.Exito(result.getOrNull() ?: emptyList())
            } else {
                ProductoEstado.Error(result.exceptionOrNull()?.message ?: "Error al cargar productos")
            }
        }
    }

    /**
     * Obtener producto por ID (desde backend)
     */
    fun obtenerProductoPorId(id: Long, onResult: (Result<Producto>) -> Unit) {
        viewModelScope.launch {
            val result = repository.obtenerProductoPorId(id)
            onResult(result)
        }
    }

    /**
     * Filtrar productos por categoría (desde backend)
     */
    fun filtrarPorCategoria(categoria: String) {
        viewModelScope.launch {
            _productosState.value = ProductoEstado.Cargando

            val result = repository.obtenerProductosPorCategoria(categoria)

            _productosState.value = if (result.isSuccess) {
                ProductoEstado.Exito(result.getOrNull() ?: emptyList())
            } else {
                ProductoEstado.Error(result.exceptionOrNull()?.message ?: "Error al filtrar")
            }
        }
    }

    /**
     * Agregar producto - Compatible con GestionProductoScreen
     */
    fun agregarProducto(producto: Producto) {
        viewModelScope.launch {
            _productosState.value = ProductoEstado.Cargando

            val result = repository.agregarProducto(producto)

            if (result.isSuccess) {
                // Recargar productos después de agregar
                cargarProductos()
            } else {
                _productosState.value = ProductoEstado.Error(
                    result.exceptionOrNull()?.message ?: "Error al agregar producto"
                )
            }
        }
    }

    /**
     * Agregar producto con callback
     */
    fun agregarProducto(producto: Producto, onResult: (Result<Producto>) -> Unit) {
        viewModelScope.launch {
            val result = repository.agregarProducto(producto)
            onResult(result)

            // Recargar productos después de agregar
            if (result.isSuccess) {
                cargarProductos()
            }
        }
    }


    fun actualizarProducto(producto: Producto) {
        viewModelScope.launch {
            // TODO: Cuando el backend tenga el endpoint PUT, usar:
            // val result = repository.actualizarProducto(producto)

            // Por ahora, actualizamos localmente
            val productosActuales = _productosInterno.value.toMutableList()
            val index = productosActuales.indexOfFirst { it.id == producto.id }

            if (index != -1) {
                productosActuales[index] = producto
                _productosInterno.value = productosActuales
                _productosState.value = ProductoEstado.Exito(productosActuales)
            } else {
                _productosState.value = ProductoEstado.Error("Producto no encontrado")
            }
        }
    }

    fun eliminarProducto(productoId: String) {
        viewModelScope.launch {
            // TODO: Cuando el backend tenga el endpoint DELETE, usar:
            // val result = repository.eliminarProducto(productoId)

            // Por ahora, eliminamos localmente
            val productosActuales = _productosInterno.value.filter { it.id != productoId }
            _productosInterno.value = productosActuales
            _productosState.value = ProductoEstado.Exito(productosActuales)
        }
    }

    /**
     * Obtener producto por ID local
     */
    fun obtenerProductoPorIdLocal(id: String): Producto? {
        return repository.obtenerProductoPorIdLocal(id)
    }
}