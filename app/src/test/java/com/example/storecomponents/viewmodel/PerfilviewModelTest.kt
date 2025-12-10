package com.example.storecomponents.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.storecomponents.data.model.Purchase
import com.example.storecomponents.data.model.User
import com.example.storecomponents.data.repository.PerfilRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.verify
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class PerfilviewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var perfilViewModel: PerfilviewModel
    private val mockPerfilRepository: PerfilRepository = mock()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        runTest {
            whenever(mockPerfilRepository.loadUser()).thenReturn(User())
        }
        perfilViewModel = PerfilviewModel(mockPerfilRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `updateUserFields should update user and persist`() = runTest {
        // Given
        val name = "John Doe"
        val correo = "john.doe@example.com"
        val phone = "123456789"
        val direccion = "123 Main St"

        // When
        perfilViewModel.updateUserFields(name, correo, phone, direccion)

        // Then
        val updatedUser = perfilViewModel.user.value
        assertEquals(name, updatedUser.name)
        assertEquals(correo, updatedUser.correo)
        assertEquals(phone, updatedUser.phone)
        assertEquals(direccion, updatedUser.direccion)

        verify(mockPerfilRepository).saveUser(updatedUser)
    }

    @Test
    fun `addPurchase should add purchase to user and persist`() = runTest {
        // Given
        val title = "New Purchase"
        val amount = 99.99
        val date = "2024-01-01"

        // When
        perfilViewModel.addPurchase(title, amount, date)

        // Then
        val updatedUser = perfilViewModel.user.value
        assertEquals(1, updatedUser.purchases.size)
        val newPurchase = updatedUser.purchases.first()
        assertEquals(title, newPurchase.title)
        assertEquals(amount, newPurchase.amount, 0.0)
        assertEquals(date, newPurchase.date)

        verify(mockPerfilRepository).addPurchase(newPurchase)
    }

    @Test
    fun `removePurchase should remove purchase and persist`() = runTest {
        // Given
        val purchase = Purchase(title = "A purchase", amount = 10.0, date = "2023-01-01")
        val userWithPurchase = User(purchases = listOf(purchase))
        whenever(mockPerfilRepository.loadUser()).thenReturn(userWithPurchase)
        perfilViewModel = PerfilviewModel(mockPerfilRepository) // Re-init to load user with purchase

        // When
        perfilViewModel.removePurchase(purchase.id)

        // Then
        val updatedUser = perfilViewModel.user.value
        assertTrue(updatedUser.purchases.isEmpty())
        verify(mockPerfilRepository).removePurchase(purchase.id)
    }
}