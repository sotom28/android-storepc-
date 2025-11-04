package com.example.storecomponents.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

data class ProductoModel(
    val id: Long,
    val name: String,
    val price: String,
    val imageUrl: String
)

class ProductsViewModel : ViewModel() {
    private val _products = mutableStateListOf<ProductoModel>()
    val products: List<ProductoModel> get() = _products

    fun addProduct(name: String, price: String, imageUrl: String) {
        val newId = if (_products.isEmpty()) 1L else (_products.maxOf { it.id } + 1)
        _products.add(ProductoModel(newId, name, price, imageUrl))
    }

    fun updateProduct(id: Long, name: String, price: String, imageUrl: String) {
        val idx = _products.indexOfFirst { it.id == id }
        if (idx >= 0) {
            _products[idx] = _products[idx].copy(name = name, price = price, imageUrl = imageUrl)
        }
    }

    fun removeProduct(id: Long) {
        val idx = _products.indexOfFirst { it.id == id }
        if (idx >= 0) _products.removeAt(idx)
    }

    fun getProductById(id: Long): ProductoModel? = _products.firstOrNull { it.id == id }
}

