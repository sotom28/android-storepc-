package com.example.storecomponents.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.storecomponents.data.model.Userole
import com.example.storecomponents.data.model.Usuarios
import com.example.storecomponents.data.repository.AuthRepository
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
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: AuthRepository

    private lateinit var viewModel: AuthViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = AuthViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loginByUsername con credenciales correctas debe cambiar estado a Exito`() = runTest {
        // Given
        val username = "testuser"
        val password = "password123"
        val usuarioMock = Usuarios(
            id = 1L,
            nombre = "Test User",
            username = username,
            correo = "test@example.com",
            role = Userole.CLIENT,
            password = password
        )
        whenever(repository.login(username, password)).thenReturn(Result.success(usuarioMock))

        // When
        viewModel.loginByUsername(username, password)
        advanceUntilIdle()

        // Then
        val estado = viewModel.estadoAuth.first()
        assertTrue(estado is EstadoAuth.Exito)
        assertEquals(Userole.CLIENT, viewModel.role.first())
    }

    @Test
    fun `loginByUsername con username admin debe asignar role ADMIN`() = runTest {
        // Given
        val username = "admin"
        val password = "admin123"
        val usuarioMock = Usuarios(
            id = 1L,
            nombre = "Admin User",
            username = username,
            correo = "admin@example.com",
            role = Userole.ADMIN,
            password = password
        )
        whenever(repository.login(username, password)).thenReturn(Result.success(usuarioMock))

        // When
        viewModel.loginByUsername(username, password)
        advanceUntilIdle()

        // Then
        assertEquals(Userole.ADMIN, viewModel.role.first())
    }

    @Test
    fun `loginByUsername con credenciales incorrectas debe cambiar estado a Error`() = runTest {
        // Given
        val username = "testuser"
        val password = "wrongpassword"
        whenever(repository.login(username, password))
            .thenReturn(Result.failure(Exception("Credenciales inválidas")))

        // When
        viewModel.loginByUsername(username, password)
        advanceUntilIdle()

        // Then
        val estado = viewModel.estadoAuth.first()
        assertTrue(estado is EstadoAuth.Error)
        assertEquals("Credenciales inválidas", (estado as EstadoAuth.Error).mensaje)
    }

    @Test
    fun `register con datos válidos debe cambiar registerState a Exito`() = runTest {
        // Given
        val nombreReal = "Juan Pérez"
        val email = "juan@example.com"
        val username = "juanperez"
        val password = "password123"
        val usuarioMock = Usuarios(
            id = 2L,
            nombre = nombreReal,
            username = username,
            correo = email,
            role = Userole.CLIENT,
            password = password
        )
        whenever(repository.registro(username, nombreReal, email, password))
            .thenReturn(Result.success(usuarioMock))

        // When
        viewModel.register(nombreReal, email, username, password)
        advanceUntilIdle()

        // Then
        val estado = viewModel.registerState.first()
        assertTrue(estado is EstadoAuth.Exito)
    }

    @Test
    fun `register con username existente debe cambiar registerState a Error`() = runTest {
        // Given
        val nombreReal = "Juan Pérez"
        val email = "juan@example.com"
        val username = "existinguser"
        val password = "password123"
        whenever(repository.registro(username, nombreReal, email, password))
            .thenReturn(Result.failure(Exception("El usuario ya existe")))

        // When
        viewModel.register(nombreReal, email, username, password)
        advanceUntilIdle()

        // Then
        val estado = viewModel.registerState.first()
        assertTrue(estado is EstadoAuth.Error)
        assertEquals("El usuario ya existe", (estado as EstadoAuth.Error).mensaje)
    }

    @Test
    fun `cerrarSesion debe resetear role a NONE y estado a Inicial`() = runTest {
        // When
        viewModel.cerrarSesion()
        advanceUntilIdle()

        // Then
        assertEquals(UserRole.NONE, viewModel.role.first())
        val estado = viewModel.estadoAuth.first()
        assertTrue(estado is EstadoAuth.Inicial)
    }

    @Test
    fun `resetLoginState debe cambiar estadoAuth a Inicial`() = runTest {
        // When
        viewModel.resetLoginState()
        advanceUntilIdle()

        // Then
        val estado = viewModel.estadoAuth.first()
        assertTrue(estado is EstadoAuth.Inicial)
    }

    @Test
    fun `resetRegisterState debe cambiar registerState a Inicial`() = runTest {
        // When
        viewModel.resetRegisterState()
        advanceUntilIdle()

        // Then
        val estado = viewModel.registerState.first()
        assertTrue(estado is EstadoAuth.Inicial)
    }
}