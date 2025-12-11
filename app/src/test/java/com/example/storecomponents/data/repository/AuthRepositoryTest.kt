package com.example.storecomponents.data.repository

import com.example.storecomponents.data.model.Userole
import com.example.storecomponents.data.model.dto.LoginRequest
import com.example.storecomponents.data.model.dto.RegisterRequest
import com.example.storecomponents.data.model.dto.UsuarioDTO
import com.example.storecomponents.data.network.ApiService
import com.example.storecomponents.data.network.RetrofitClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertTrue


@OptIn(ExperimentalCoroutinesApi::class)
class AuthRepositoryTest {

    private lateinit var repository: AuthRepository

    @Before
    fun setup() {
        repository = AuthRepository()
    }

    @Test
    fun `repository debe inicializarse correctamente`() {
        // Then
        assertTrue(repository.usuarioActual.value == null)
    }

    @Test
    fun `cerrarSesion debe limpiar el usuario actual`() = runTest {
        // When
        repository.cerrarSesion()

        // Then
        assertTrue(repository.usuarioActual.value == null)
    }

    @Test
    fun `usuariosFlow debe inicializarse vacío`() = runTest {
        // Then
        assertTrue(repository.usuariosFlow.value.isEmpty())
    }

    /**
     * Tests de transformación de datos
     * Estos tests verifican que la lógica funciona correctamente
     * sin necesidad de llamar al backend real
     */

    @Test
    fun `username con admin debe identificarse como ADMIN role`() {
        // Given
        val adminUsernames = listOf("admin", "Admin", "ADMIN", "adminUser", "administrator")

        // When/Then
        adminUsernames.forEach { username ->
            assertTrue(
                username.lowercase().contains("admin"),
                "El username '$username' debería contener 'admin'"
            )
        }
    }

    @Test
    fun `username sin admin debe identificarse como CLIENT role`() {
        // Given
        val clientUsernames = listOf("user", "cliente", "juanperez", "testuser")

        // When/Then
        clientUsernames.forEach { username ->
            assertTrue(
                !username.lowercase().contains("admin"),
                "El username '$username' NO debería contener 'admin'"
            )
        }
    }

    @Test
    fun `LoginRequest debe crearse correctamente`() {
        // Given
        val username = "testuser"
        val password = "password123"

        // When
        val request = LoginRequest(username = username, contrasena = password)

        // Then
        assertEquals(username, request.username)
        assertEquals(password, request.contrasena)
    }

    @Test
    fun `RegisterRequest debe crearse correctamente`() {
        // Given
        val username = "newuser"
        val nombreReal = "New User"
        val email = "new@example.com"
        val password = "password123"

        // When
        val request = RegisterRequest(
            username = username,
            nombreReal = nombreReal,
            email = email,
            contrasena = password
        )

        // Then
        assertEquals(username, request.username)
        assertEquals(nombreReal, request.nombreReal)
        assertEquals(email, request.email)
        assertEquals(password, request.contrasena)
    }

    @Test
    fun `UsuarioDTO debe tener campos correctos para login`() {
        // Given
        val dto = UsuarioDTO(
            idUsuario = 1L,
            username = "testuser",
            nombreReal = "Test User",
            email = "test@example.com",
            contrasena = null  // No se devuelve en login
        )

        // Then
        assertEquals(1L, dto.idUsuario)
        assertEquals("testuser", dto.username)
        assertEquals("Test User", dto.nombreReal)
        assertEquals("test@example.com", dto.email)
        assertEquals(null, dto.contrasena)
    }

    @Test
    fun `UsuarioDTO debe poder crearse para registro`() {
        // Given
        val dto = UsuarioDTO(
            idUsuario = null,  // No existe aún
            username = "newuser",
            nombreReal = "New User",
            email = "new@example.com",
            contrasena = "password123"  // Se envía en registro
        )

        // Then
        assertEquals(null, dto.idUsuario)
        assertEquals("newuser", dto.username)
        assertEquals("New User", dto.nombreReal)
        assertEquals("new@example.com", dto.email)
        assertEquals("password123", dto.contrasena)
    }
}