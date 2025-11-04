package com.example.storecomponents.viewmodel

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

data class ProductModel(
    val id: Long,
    var name: String,
    var description: String,
    var price: Double,
    var imageUri: Uri? = null
)

class ProductViewModel : ViewModel() {
    private val _products = mutableStateListOf<ProductModel>()
    val products: List<ProductModel> get() = _products

    private var nextId = 1L

    fun addProduct(name: String, description: String, price: Double, imageUri: Uri?) {
        _products.add(
            ProductModel(
                id = nextId++,
                name = name,
                description = description,
                price = price,
                imageUri = imageUri
            )
        )
    }

    fun updateProduct(id: Long, name: String, description: String, price: Double, imageUri: Uri?) {
        val index = _products.indexOfFirst { it.id == id }
        if (index != -1) {
            _products[index] = _products[index].copy(
                name = name,
                description = description,
                price = price,
                imageUri = imageUri
            )
        }
    }

    fun deleteProduct(id: Long) {
        _products.removeAll { it.id == id }
    }

    fun getProduct(id: Long): ProductModel? {
        return _products.find { it.id == id }
    }
}

