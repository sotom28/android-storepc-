package com.example.storecomponents.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

enum class UserRole { NONE, CLIENT, ADMIN }

class AuthViewModel : ViewModel() {
    var isLoggedIn by mutableStateOf(false)
        private set
    var role by mutableStateOf(UserRole.NONE)
        private set
    var userEmail by mutableStateOf("")
        private set

    fun login(email: String, password: String) {
        userEmail = email
        isLoggedIn = true
        role = if (email.contains("admin")) UserRole.ADMIN else UserRole.CLIENT
    }

    fun logout() {
        isLoggedIn = false
        role = UserRole.NONE
        userEmail = ""
    }
}

