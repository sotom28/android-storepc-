package com.example.storecomponents.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.storecomponents.data.model.Producto
import com.example.storecomponents.data.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductoViewModel(application: Application) : AndroidViewModel(application) {

    private val productoRepository: ProductoRepository = ProductoRepository(application.applicationContext)

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

    private val _estadoProducto = MutableStateFlow<EstadoProducto>(EstadoProducto.Inicial)
    val estadoProducto: StateFlow<EstadoProducto> = _estadoProducto.asStateFlow()

    init {
        cargarProductos()
    }

    private fun cargarProductos() {
        viewModelScope.launch {
            _productos.value = productoRepository.getALL()
        }
    }

    fun agregarProducto(producto: Producto) {
        viewModelScope.launch {
            _estadoProducto.value = EstadoProducto.Cargando
            try {
                productoRepository.add(producto)
                cargarProductos()
                _estadoProducto.value = EstadoProducto.Exito("Producto agregado")
            } catch (e: Exception) {
                _estadoProducto.value = EstadoProducto.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun actualizarProducto(producto: Producto) {
        viewModelScope.launch {
            _estadoProducto.value = EstadoProducto.Cargando
            try {
                productoRepository.update(producto)
                cargarProductos()
                _estadoProducto.value = EstadoProducto.Exito("Producto actualizado")
            } catch (e: Exception) {
                _estadoProducto.value = EstadoProducto.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun eliminarProducto(productoId: String) {
        viewModelScope.launch {
            _estadoProducto.value = EstadoProducto.Cargando
            try {
                productoRepository.delete(productoId)
                cargarProductos()
                _estadoProducto.value = EstadoProducto.Exito("Producto eliminado")
            } catch (e: Exception) {
                _estadoProducto.value = EstadoProducto.Error(e.message ?: "Error")
            }
        }
    }
}

sealed class EstadoProducto {
    object Inicial : EstadoProducto()
    object Cargando : EstadoProducto()
    data class Exito(val mensaje: String) : EstadoProducto()
    data class Error(val mensaje: String) : EstadoProducto()
}
