package com.example.storecomponents.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

data class UserItemModel(
    val id: Long,
    var name: String,
    var role: String,
    var isSalesManager: Boolean = false
)

class UsersViewModel : ViewModel() {
    // Lista que puede ser observada por Compose
    private val _users = mutableStateListOf<UserItemModel>()
    val users: List<UserItemModel> get() = _users

    fun addUser(name: String, role: String) {
        val newId = if (_users.isEmpty()) 1L else (_users.maxOf { it.id } + 1)
        _users.add(UserItemModel(newId, name, role, false))
    }

    fun updateUser(id: Long, name: String, role: String) {
        val idx = _users.indexOfFirst { it.id == id }
        if (idx >= 0) {
            _users[idx] = _users[idx].copy(name = name, role = role)
        }
    }

    fun removeUser(id: Long) {
        val idx = _users.indexOfFirst { it.id == id }
        if (idx >= 0) _users.removeAt(idx)
    }

    fun assignSalesManager(id: Long) {
        _users.forEachIndexed { index, user ->
            _users[index] = user.copy(isSalesManager = user.id == id)
        }
    }

    fun getSalesManager(): UserItemModel? = _users.firstOrNull { it.isSalesManager }
}

