package com.example.storecomponents.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope //
import com.example.storecomponents.data.model.Producto
import com.example.storecomponents.data.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
// ViewModel para gestionar productos
class ProductoViewModel(
    private val productoRepository: ProductoRepository = ProductoRepository()
) : ViewModel() {

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

    private val _estadoProducto = MutableStateFlow<EstadoProducto>(EstadoProducto.Inicial)
    val estadoProducto: StateFlow<EstadoProducto> = _estadoProducto.asStateFlow()

    init {
        cargarProductos()
    }

    private fun cargarProductos() {
        viewModelScope.launch {
            // cargar desde el repositorio (en este proyecto las funciones son síncronas en memoria)
            _productos.value = productoRepository.obtenerTodosLosProductos()
        }
    }

    // Función para agregar un nuevo producto
    fun agregarProducto(producto: Producto) {
        viewModelScope.launch {
            _estadoProducto.value = EstadoProducto.Cargando
            val resultado = productoRepository.agregarProducto(producto)
            _estadoProducto.value = if (resultado.isSuccess) {
                cargarProductos()
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
}

// Definición del estado de las operaciones de producto

sealed class EstadoProducto {
    object Inicial : EstadoProducto()
    object Cargando : EstadoProducto()
    data class Exito(val mensaje: String) : EstadoProducto()
    data class Error(val mensaje: String) : EstadoProducto()
}
