package com.example.storecomponents.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.storecomponents.data.model.Producto
import com.example.storecomponents.data.repository.ProductoRepository
import com.example.storecomponents.navigation.Screen
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductoViewModel(private val productoRepository: ProductoRepository, private val dispatcher: CoroutineDispatcher = kotlinx.coroutines.Dispatchers.Main) : ViewModel() {

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

    private val _estadoProducto = MutableStateFlow<EstadoProducto>(EstadoProducto.Inicial)
    val estadoProducto: StateFlow<EstadoProducto> = _estadoProducto.asStateFlow()

    private val _navigateTo = MutableSharedFlow<String>()
    val navigateTo: SharedFlow<String> = _navigateTo.asSharedFlow()

    init {
        cargarProductos()
    }

    fun onProductSelected(productId: String) {
        viewModelScope.launch(dispatcher) {
            // Emitir la ruta que está definida en Screen.detalle reemplazando el placeholder
            val route = Screen.detalle.route.replace("{productoId}", productId)
            try {
                Log.d("ProductoViewModel", "navegar a ruta: $route")
            } catch (t: Throwable) {
                // en entornos de test sin Android la llamada a Log puede fallar; ignoramos
            }
            _navigateTo.emit(route)
        }
    }

    private fun cargarProductos() {
        viewModelScope.launch(dispatcher) {
            _productos.value = productoRepository.getALL()
        }
    }

    fun agregarProducto(producto: Producto) {
        viewModelScope.launch(dispatcher) {
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
        viewModelScope.launch(dispatcher) {
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
        viewModelScope.launch(dispatcher) {
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

class ProductoViewModelFactory(private val productoRepository: ProductoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductoViewModel(productoRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

sealed class EstadoProducto {
    object Inicial : EstadoProducto()
    object Cargando : EstadoProducto()
    data class Exito(val mensaje: String) : EstadoProducto()
    data class Error(val mensaje: String) : EstadoProducto()
}
