package com.example.storecomponents.data.repository

import org.junit.Assert.*
import org.junit.Test

class AuthRepositoryTest {
    @Test
    fun login_success() {
        val repo = AuthRepository()
        val result = repo.login("admin@store.com", "admin123")
        assertTrue(result.isSuccess)
        val user = result.getOrNull()
        assertNotNull(user)
        assertEquals("admin@store.com", user?.correo)
        assertEquals("admin", user?.nombre)
    }

    @Test
    fun login_failure() {
        val repo = AuthRepository()
        val result = repo.login("no@existe.com", "123")
        assertTrue(result.isFailure)
    }
}
