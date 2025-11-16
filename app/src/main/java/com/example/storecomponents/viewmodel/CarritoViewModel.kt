package com.example.storecomponents.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storecomponents.data.model.Carrito
import com.example.storecomponents.data.model.Producto
import com.example.storecomponents.data.repository.CarritoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CarritoViewModel : ViewModel() {

    private val carritoRepository = CarritoRepository.getInstance()

    private val _carrito = MutableStateFlow(Carrito())
    val carrito: StateFlow<Carrito> = _carrito.asStateFlow()

    private val _estadoCarrito = MutableStateFlow<EstadoCarrito>(EstadoCarrito.Inicial)
    val estadoCarrito: StateFlow<EstadoCarrito> = _estadoCarrito.asStateFlow()

    init {
        observarCarrito()
    }

    private fun observarCarrito() {
        viewModelScope.launch {
            carritoRepository.carrito.collect { carritoActualizado ->
                _carrito.value = carritoActualizado
            }
        }
    }

    // Agregar producto al carrito
    fun agregarProducto(producto: Producto, cantidad: Int = 1) {
        viewModelScope.launch {
            _estadoCarrito.value = EstadoCarrito.Cargando
            val resultado = carritoRepository.agregarProducto(producto, cantidad)
            _estadoCarrito.value = if (resultado.isSuccess) {
                EstadoCarrito.Exito("Producto agregado al carrito")
            } else {
                EstadoCarrito.Error(resultado.exceptionOrNull()?.message ?: "Error al agregar")
            }
        }
    }

    // Actualizar cantidad de un item
    fun actualizarCantidad(itemId: String, nuevaCantidad: Int) {
        viewModelScope.launch {
            val resultado = carritoRepository.actualizarCantidad(itemId, nuevaCantidad)
            if (resultado.isFailure) {
                _estadoCarrito.value = EstadoCarrito.Error(
                    resultado.exceptionOrNull()?.message ?: "Error al actualizar"
                )
            }
        }
    }

    // Eliminar item del carrito
    fun eliminarItem(itemId: String) {
        viewModelScope.launch {
            val resultado = carritoRepository.eliminarItem(itemId)
            if (resultado.isSuccess) {
                _estadoCarrito.value = EstadoCarrito.Exito("Producto eliminado")
            } else {
                _estadoCarrito.value = EstadoCarrito.Error("Error al eliminar")
            }
        }
    }

    // Limpiar carrito
    fun limpiarCarrito() {
        viewModelScope.launch {
            val resultado = carritoRepository.limpiarCarrito()
            if (resultado.isSuccess) {
                _estadoCarrito.value = EstadoCarrito.Exito("Carrito vaciado")
            }
        }
    }

    // Procesar compra (simulado)
    fun procesarCompra() {
        viewModelScope.launch {
            if (_carrito.value.estaVacio) {
                _estadoCarrito.value = EstadoCarrito.Error("El carrito está vacío")
                return@launch
            }

            _estadoCarrito.value = EstadoCarrito.Cargando
            // Aquí iría la lógica real de procesamiento de pago
            // Por ahora solo simulamos
            kotlinx.coroutines.delay(1000)

            limpiarCarrito()
            _estadoCarrito.value = EstadoCarrito.Exito("Compra realizada con éxito")
        }
    }

    // Limpiar estado
    fun limpiarEstado() {
        _estadoCarrito.value = EstadoCarrito.Inicial
    }
}

// Estados del carrito
sealed class EstadoCarrito {
    object Inicial : EstadoCarrito()
    object Cargando : EstadoCarrito()
    data class Exito(val mensaje: String) : EstadoCarrito()
    data class Error(val mensaje: String) : EstadoCarrito()
}