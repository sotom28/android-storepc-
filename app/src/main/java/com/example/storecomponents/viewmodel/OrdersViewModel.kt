package com.example.storecomponents.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

data class OrderModel(
    val id: Long,
    val productName: String,
    val quantity: Int,
    val assignedToId: Long? = null,
    val status: String = "PENDIENTE"
)

class OrdersViewModel : ViewModel() {
    private val _orders = mutableStateListOf<OrderModel>()
    val orders: List<OrderModel> get() = _orders

    fun addOrder(productName: String, quantity: Int, assignedToId: Long?) {
        val newId = if (_orders.isEmpty()) 1L else (_orders.maxOf { it.id } + 1)
        _orders.add(OrderModel(newId, productName, quantity, assignedToId, "PENDIENTE"))
    }

    fun removeOrder(id: Long) {
        val idx = _orders.indexOfFirst { it.id == id }
        if (idx >= 0) _orders.removeAt(idx)
    }

    fun updateStatus(id: Long, newStatus: String) {
        val idx = _orders.indexOfFirst { it.id == id }
        if (idx >= 0) _orders[idx] = _orders[idx].copy(status = newStatus)
    }

    fun updateOrder(id: Long, productName: String, quantity: Int) {
        val idx = _orders.indexOfFirst { it.id == id }
        if (idx >= 0) _orders[idx] = _orders[idx].copy(productName = productName, quantity = quantity)
    }
}

