package com.example.storecomponents.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.storecomponents.data.model.Producto
import com.example.storecomponents.data.repository.ProductoRepository
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
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ProductoViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: ProductoRepository

    private lateinit var viewModel: ProductoViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val productosMock = listOf(
        Producto(
            id = "1",
            nombre = "Laptop HP",
            descripcion = "Laptop gamer",
            precio = 999.99,
            stock = 10,
            categoria = "Electrónicos",
            imagenUrl = "https://example.com/laptop.jpg"
        ),
        Producto(
            id = "2",
            nombre = "Mouse Logitech",
            descripcion = "Mouse inalámbrico",
            precio = 29.99,
            stock = 50,
            categoria = "Accesorios",
            imagenUrl = "https://example.com/mouse.jpg"
        ),
        Producto(
            id = "3",
            nombre = "Monitor Dell",
            descripcion = "Monitor 24 pulgadas",
            precio = 199.99,
            stock = 15,
            categoria = "Monitores",
            imagenUrl = "https://example.com/monitor.jpg"
        )
    )

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `cargarProductos exitoso debe actualizar productosState a Exito`() = runTest {
        // Given
        whenever(repository.cargarProductos()).thenReturn(Result.success(productosMock))
        viewModel = ProductoViewModel(repository)

        // When
        viewModel.cargarProductos()
        advanceUntilIdle()

        // Then
        val estado = viewModel.productosState.first()
        assertTrue(estado is ProductoEstado.Exito)
        assertEquals(3, (estado as ProductoEstado.Exito).productos.size)
    }

    @Test
    fun `cargarProductos con error debe actualizar productosState a Error`() = runTest {
        // Given
        whenever(repository.cargarProductos())
            .thenReturn(Result.failure(Exception("Error de conexión")))
        viewModel = ProductoViewModel(repository)

        // When
        viewModel.cargarProductos()
        advanceUntilIdle()

        // Then
        val estado = viewModel.productosState.first()
        assertTrue(estado is ProductoEstado.Error)
        assertEquals("Error de conexión", (estado as ProductoEstado.Error).mensaje)
    }

    @Test
    fun `onSearchQueryChange debe actualizar searchQuery`() = runTest {
        // Given
        whenever(repository.cargarProductos()).thenReturn(Result.success(productosMock))
        viewModel = ProductoViewModel(repository)
        advanceUntilIdle()

        // When
        viewModel.onSearchQueryChange("laptop")
        advanceUntilIdle()

        // Then
        assertEquals("laptop", viewModel.searchQuery.first())
    }

    @Test
    fun `onCategorySelected debe actualizar selectedCategory`() = runTest {
        // Given
        whenever(repository.cargarProductos()).thenReturn(Result.success(productosMock))
        viewModel = ProductoViewModel(repository)
        advanceUntilIdle()

        // When
        viewModel.onCategorySelected("Electrónicos")
        advanceUntilIdle()

        // Then
        assertEquals("Electrónicos", viewModel.selectedCategory.first())
    }

    @Test
    fun `productosFiltrados debe filtrar por búsqueda correctamente`() = runTest {
        // Given
        whenever(repository.cargarProductos()).thenReturn(Result.success(productosMock))
        viewModel = ProductoViewModel(repository)
        advanceUntilIdle()

        // When
        viewModel.onSearchQueryChange("mouse")
        advanceUntilIdle()

        // Then
        val filtrados = viewModel.productosFiltrados.first()
        assertEquals(1, filtrados.size)
        assertEquals("Mouse Logitech", filtrados[0].nombre)
    }

    @Test
    fun `productosFiltrados debe filtrar por categoría correctamente`() = runTest {
        // Given
        whenever(repository.cargarProductos()).thenReturn(Result.success(productosMock))
        viewModel = ProductoViewModel(repository)
        advanceUntilIdle()

        // When
        viewModel.onCategorySelected("Accesorios")
        advanceUntilIdle()

        // Then
        val filtrados = viewModel.productosFiltrados.first()
        assertEquals(1, filtrados.size)
        assertEquals("Mouse Logitech", filtrados[0].nombre)
    }

    @Test
    fun `productosFiltrados debe filtrar por búsqueda y categoría combinadas`() = runTest {
        // Given
        val productosExtendidos = productosMock + Producto(
            id = "4",
            nombre = "Laptop Dell",
            descripcion = "Laptop empresarial",
            precio = 1299.99,
            stock = 5,
            categoria = "Electrónicos",
            imagenUrl = "https://example.com/laptop-dell.jpg"
        )
        whenever(repository.cargarProductos()).thenReturn(Result.success(productosExtendidos))
        viewModel = ProductoViewModel(repository)
        advanceUntilIdle()

        // When
        viewModel.onSearchQueryChange("laptop")
        viewModel.onCategorySelected("Electrónicos")
        advanceUntilIdle()

        // Then
        val filtrados = viewModel.productosFiltrados.first()
        assertEquals(2, filtrados.size)
        assertTrue(filtrados.all { it.categoria == "Electrónicos" })
        assertTrue(filtrados.all { it.nombre.contains("Laptop", ignoreCase = true) })
    }

    @Test
    fun `categories debe contener todas las categorías únicas`() = runTest {
        // Given
        whenever(repository.cargarProductos()).thenReturn(Result.success(productosMock))
        viewModel = ProductoViewModel(repository)
        advanceUntilIdle()

        // Then
        val categorias = viewModel.categories.first()
        assertEquals(3, categorias.size)
        assertTrue(categorias.contains("Electrónicos"))
        assertTrue(categorias.contains("Accesorios"))
        assertTrue(categorias.contains("Monitores"))
    }

    @Test
    fun `agregarProducto exitoso debe recargar productos`() = runTest {
        // Given
        val nuevoProducto = Producto(
            id = "4",
            nombre = "Teclado Mecánico",
            descripcion = "Teclado RGB",
            precio = 89.99,
            stock = 20,
            categoria = "Accesorios",
            imagenUrl = "https://example.com/teclado.jpg"
        )
        whenever(repository.cargarProductos()).thenReturn(Result.success(productosMock))
        whenever(repository.agregarProducto(nuevoProducto))
            .thenReturn(Result.success(nuevoProducto))
        viewModel = ProductoViewModel(repository)
        advanceUntilIdle()

        // When
        viewModel.agregarProducto(nuevoProducto)
        advanceUntilIdle()

        // Then
        val estado = viewModel.productosState.first()
        assertTrue(estado is ProductoEstado.Exito)
    }

    @Test
    fun `obtenerProductoPorIdLocal debe retornar producto correcto`() = runTest {
        // Given
        whenever(repository.cargarProductos()).thenReturn(Result.success(productosMock))
        whenever(repository.obtenerProductoPorIdLocal("1")).thenReturn(productosMock[0])
        viewModel = ProductoViewModel(repository)
        advanceUntilIdle()

        // When
        val producto = viewModel.obtenerProductoPorIdLocal("1")

        // Then
        assertNotNull(producto)
        assertEquals("Laptop HP", producto?.nombre)
    }
}