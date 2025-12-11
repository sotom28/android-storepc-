package com.example.storecomponents.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.storecomponents.data.model.Carrito
import com.example.storecomponents.data.model.ItemCarrito
import com.example.storecomponents.data.model.Producto
import com.example.storecomponents.data.repository.CarritoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CarritoViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: CarritoRepository

    private lateinit var viewModel: CarritoViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val productoMock = Producto(
        id = "1",
        nombre = "Laptop HP",
        descripcion = "Laptop gamer",
        precio = 999.99,
        stock = 10,
        categoria = "Electrónicos",
        imagenUrl = "https://example.com/laptop.jpg"
    )

    private val carritoVacioMock = Carrito(
        items = emptyList(),
        total = 0.0,
        cantidadTotal = 0
    )

    private val itemCarritoMock = ItemCarrito(
        producto = productoMock,
        cantidad = 2
    )

    private val carritoConItemsMock = Carrito(
        items = listOf(itemCarritoMock),
        total = 1999.98,
        cantidadTotal = 2
    )

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = CarritoViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `agregarProducto debe agregar producto al carrito`() = runTest {
        // When
        viewModel.agregarProducto(productoMock)
        advanceUntilIdle()

        // Then
        verify(repository).agregarProducto(productoMock)
    }

    @Test
    fun `agregarProducto debe actualizar el carrito`() = runTest {
        // Given
        whenever(repository.obtenerCarrito()).thenReturn(carritoConItemsMock)

        // When
        viewModel.agregarProducto(productoMock)
        advanceUntilIdle()

        // Then
        val carrito = viewModel.carrito.first()
        assertTrue(carrito.cantidadTotal > 0)
    }

    @Test
    fun `eliminarProducto debe eliminar producto del carrito`() = runTest {
        // When
        viewModel.eliminarProducto("1")
        advanceUntilIdle()

        // Then
        verify(repository).eliminarProducto("1")
    }

    @Test
    fun `actualizarCantidad debe actualizar cantidad del producto`() = runTest {
        // When
        viewModel.actualizarCantidad("1", 5)
        advanceUntilIdle()

        // Then
        verify(repository).actualizarCantidad("1", 5)
    }

    @Test
    fun `vaciarCarrito debe eliminar todos los items`() = runTest {
        // When
        viewModel.vaciarCarrito()
        advanceUntilIdle()

        // Then
        verify(repository).vaciarCarrito()
    }

    @Test
    fun `carrito inicial debe estar vacío`() = runTest {
        // Given
        whenever(repository.obtenerCarrito()).thenReturn(carritoVacioMock)

        // When
        advanceUntilIdle()

        // Then
        val carrito = viewModel.carrito.first()
        assertEquals(0, carrito.cantidadTotal)
        assertEquals(0.0, carrito.total)
        assertTrue(carrito.items.isEmpty())
    }

    @Test
    fun `carrito con items debe calcular total correctamente`() = runTest {
        // Given
        whenever(repository.obtenerCarrito()).thenReturn(carritoConItemsMock)

        // When
        advanceUntilIdle()

        // Then
        val carrito = viewModel.carrito.first()
        assertEquals(2, carrito.cantidadTotal)
        assertEquals(1999.98, carrito.total, 0.01)
        assertEquals(1, carrito.items.size)
    }

    @Test
    fun `obtenerTotalItems debe retornar cantidad total correcta`() = runTest {
        // Given
        whenever(repository.obtenerCarrito()).thenReturn(carritoConItemsMock)

        // When
        advanceUntilIdle()
        val total = viewModel.obtenerTotalItems()

        // Then
        assertEquals(2, total)
    }

    @Test
    fun `obtenerTotal debe retornar precio total correcto`() = runTest {
        // Given
        whenever(repository.obtenerCarrito()).thenReturn(carritoConItemsMock)

        // When
        advanceUntilIdle()
        val total = viewModel.obtenerTotal()

        // Then
        assertEquals(1999.98, total, 0.01)
    }
}