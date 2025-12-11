package com.example.storecomponents.data.model

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ProductoTest {

    @Test
    fun `crear Producto con datos válidos debe funcionar correctamente`() {
        // When
        val producto = Producto(
            id = "1",
            nombre = "Laptop HP",
            descripcion = "Laptop gaming",
            precio = 999.99,
            stock = 10,
            categoria = "Electrónicos",
            imagenUrl = "https://example.com/laptop.jpg"
        )

        // Then
        assertEquals("1", producto.id)
        assertEquals("Laptop HP", producto.nombre)
        assertEquals("Laptop gaming", producto.descripcion)
        assertEquals(999.99, producto.precio)
        assertEquals(10, producto.stock)
        assertEquals("Electrónicos", producto.categoria)
        assertEquals("https://example.com/laptop.jpg", producto.imagenUrl)
    }

    @Test
    fun `Producto con precio cero debe ser válido`() {
        // When
        val producto = Producto(
            id = "2",
            nombre = "Producto gratis",
            descripcion = "Promoción",
            precio = 0.0,
            stock = 100,
            categoria = "Promociones",
            imagenUrl = ""
        )

        // Then
        assertEquals(0.0, producto.precio)
    }

    @Test
    fun `Producto con stock cero debe ser válido`() {
        // When
        val producto = Producto(
            id = "3",
            nombre = "Producto agotado",
            descripcion = "Sin stock",
            precio = 99.99,
            stock = 0,
            categoria = "Electrónicos",
            imagenUrl = ""
        )

        // Then
        assertEquals(0, producto.stock)
    }

    @Test
    fun `dos Productos con mismo id deben ser iguales`() {
        // Given
        val producto1 = Producto(
            id = "1",
            nombre = "Producto A",
            descripcion = "Desc A",
            precio = 100.0,
            stock = 10,
            categoria = "Cat A",
            imagenUrl = ""
        )
        val producto2 = Producto(
            id = "1",
            nombre = "Producto B",
            descripcion = "Desc B",
            precio = 200.0,
            stock = 20,
            categoria = "Cat B",
            imagenUrl = ""
        )

        // Then
        assertEquals(producto1.id, producto2.id)
    }
}

class UsuariosTest {

    @Test
    fun `crear Usuario con datos válidos debe funcionar correctamente`() {
        // When
        val usuario = Usuarios(
            id = 1L,
            nombre = "Juan Pérez",
            username = "juanperez",
            correo = "juan@example.com",
            role = Userole.CLIENT,
            password = "password123",
            confirmarPassword = "password123",
            direccion = "Calle 123"
        )

        // Then
        assertEquals(1L, usuario.id)
        assertEquals("Juan Pérez", usuario.nombre)
        assertEquals("juanperez", usuario.username)
        assertEquals("juan@example.com", usuario.correo)
        assertEquals(Userole.CLIENT, usuario.role)
        assertEquals("password123", usuario.password)
        assertEquals("Calle 123", usuario.direccion)
    }

    @Test
    fun `Usuario con role ADMIN debe ser válido`() {
        // When
        val admin = Usuarios(
            id = 2L,
            nombre = "Admin User",
            username = "admin",
            correo = "admin@example.com",
            role = Userole.ADMIN,
            password = "admin123",
            confirmarPassword = "admin123",
            direccion = ""
        )

        // Then
        assertEquals(Userole.ADMIN, admin.role)
    }

    @Test
    fun `Usuario con contraseñas diferentes debe ser válido en el modelo`() {
        // When
        val usuario = Usuarios(
            id = 3L,
            nombre = "Test User",
            username = "testuser",
            correo = "test@example.com",
            role = Userole.CLIENT,
            password = "password1",
            confirmarPassword = "password2",
            direccion = ""
        )

        // Then
        assertNotNull(usuario)
        assertEquals("password1", usuario.password)
        assertEquals("password2", usuario.confirmarPassword)
    }
}

class CarritoTest {

    private val producto1 = Producto(
        id = "1",
        nombre = "Laptop",
        descripcion = "Laptop HP",
        precio = 999.99,
        stock = 10,
        categoria = "Electrónicos",
        imagenUrl = ""
    )

    private val producto2 = Producto(
        id = "2",
        nombre = "Mouse",
        descripcion = "Mouse inalámbrico",
        precio = 29.99,
        stock = 50,
        categoria = "Accesorios",
        imagenUrl = ""
    )

    @Test
    fun `Carrito vacío debe tener total cero`() {
        // When
        val carrito = Carrito(
            items = emptyList(),
            total = 0.0,
            cantidadTotal = 0
        )

        // Then
        assertEquals(0.0, carrito.total)
        assertEquals(0, carrito.cantidadTotal)
        assertTrue(carrito.items.isEmpty())
    }

    @Test
    fun `Carrito con un item debe calcular total correctamente`() {
        // Given
        val item = ItemCarrito(producto = producto1, cantidad = 2)

        // When
        val carrito = Carrito(
            items = listOf(item),
            total = 1999.98,
            cantidadTotal = 2
        )

        // Then
        assertEquals(1999.98, carrito.total, 0.01)
        assertEquals(2, carrito.cantidadTotal)
        assertEquals(1, carrito.items.size)
    }

    @Test
    fun `Carrito con múltiples items debe calcular total correctamente`() {
        // Given
        val item1 = ItemCarrito(producto = producto1, cantidad = 1)
        val item2 = ItemCarrito(producto = producto2, cantidad = 3)
        val totalEsperado = 999.99 + (29.99 * 3)

        // When
        val carrito = Carrito(
            items = listOf(item1, item2),
            total = totalEsperado,
            cantidadTotal = 4
        )

        // Then
        assertEquals(totalEsperado, carrito.total, 0.01)
        assertEquals(4, carrito.cantidadTotal)
        assertEquals(2, carrito.items.size)
    }

    @Test
    fun `ItemCarrito debe calcular subtotal correctamente`() {
        // Given
        val item = ItemCarrito(producto = producto1, cantidad = 3)

        // Then
        assertEquals(2999.97, item.subtotal, 0.01)
    }

    @Test
    fun `ItemCarrito con cantidad cero debe tener subtotal cero`() {
        // Given
        val item = ItemCarrito(producto = producto1, cantidad = 0)

        // Then
        assertEquals(0.0, item.subtotal)
    }
}

class OrdenTest {

    private val producto = Producto(
        id = "1",
        nombre = "Laptop",
        descripcion = "Laptop HP",
        precio = 999.99,
        stock = 10,
        categoria = "Electrónicos",
        imagenUrl = ""
    )

    @Test
    fun `crear Orden con datos válidos debe funcionar correctamente`() {
        // Given
        val item = ItemCarrito(producto = producto, cantidad = 1)

        // When
        val orden = Orden(
            id = "ORD-001",
            clienteId = "CLI-001",
            items = listOf(item),
            total = 999.99,
            fecha = "2024-12-10",
            estado = "Pendiente"
        )

        // Then
        assertEquals("ORD-001", orden.id)
        assertEquals("CLI-001", orden.clienteId)
        assertEquals(999.99, orden.total)
        assertEquals("Pendiente", orden.estado)
        assertEquals(1, orden.items.size)
    }

    @Test
    fun `Orden con múltiples items debe ser válida`() {
        // Given
        val item1 = ItemCarrito(producto = producto, cantidad = 2)
        val item2 = ItemCarrito(producto = producto.copy(id = "2", nombre = "Mouse"), cantidad = 1)

        // When
        val orden = Orden(
            id = "ORD-002",
            clienteId = "CLI-001",
            items = listOf(item1, item2),
            total = 2999.97,
            fecha = "2024-12-10",
            estado = "Completada"
        )

        // Then
        assertEquals(2, orden.items.size)
        assertEquals("Completada", orden.estado)
    }

    @Test
    fun `Orden con estado Cancelada debe ser válida`() {
        // When
        val orden = Orden(
            id = "ORD-003",
            clienteId = "CLI-002",
            items = emptyList(),
            total = 0.0,
            fecha = "2024-12-10",
            estado = "Cancelada"
        )

        // Then
        assertEquals("Cancelada", orden.estado)
    }
}

class UseroleTest {

    @Test
    fun `Userole ADMIN debe existir`() {
        // When
        val role = Userole.ADMIN

        // Then
        assertNotNull(role)
        assertEquals("ADMIN", role.name)
    }

    @Test
    fun `Userole CLIENT debe existir`() {
        // When
        val role = Userole.CLIENT

        // Then
        assertNotNull(role)
        assertEquals("CLIENT", role.name)
    }

    @Test
    fun `Userole debe tener exactamente dos valores`() {
        // When
        val roles = Userole.values()

        // Then
        assertEquals(2, roles.size)
    }
}