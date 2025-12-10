package com.example.storecomponents.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.storecomponents.data.model.Producto
import com.example.storecomponents.data.repository.ProductoRepository
import com.example.storecomponents.navigation.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class ProductoViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var productoViewModel: ProductoViewModel
    private val mockProductoRepository: ProductoRepository = mock()

    @Before
    fun setUp() {
        // no-op: cada test configura su propio dispatcher ligado al testScheduler
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onProductSelected should emit navigation event`() = runTest {
        // Given: crear dispatcher ligado al testScheduler y usarlo como Main
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        whenever(mockProductoRepository.getALL()).thenReturn(mutableListOf())
        productoViewModel = ProductoViewModel(mockProductoRepository, dispatcher = dispatcher)
        val productId = "123"

        // When
        // Preparar una suscripción que espere la primera emisión
        val deferred = async { productoViewModel.navigateTo.first() }
        // Ejecutar la acción que debe emitir
        productoViewModel.onProductSelected(productId)
        // Asegurar que las corutinas lanzadas por viewModelScope se ejecuten en este TestScope
        advanceUntilIdle()

        // Then: obtener la emisión que esperaba la suscripción previa
        val emittedRoute = deferred.await()
        val expected = Screen.detalle.route.replace("{productoId}", productId)
        assertEquals(expected, emittedRoute)
    }

    @Test
    fun `productos should be loaded on init`() = runTest {
        // Given
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        val mockProducts = mutableListOf(
            Producto(
                id = "1",
                nombre = "Test Product",
                descripcion = "Description",
                precio = 10.0,
                stock = 5,
                categoria = "General",
                imagenUrl = ""
            )
        )
        whenever(mockProductoRepository.getALL()).thenReturn(mockProducts)

        // When
        productoViewModel = ProductoViewModel(mockProductoRepository, dispatcher = dispatcher)

        // Asegurar que la corrutina de inicialización (cargarProductos) se ejecutó
        advanceUntilIdle()

        // Then
        assertEquals(mockProducts, productoViewModel.productos.value)
    }
}