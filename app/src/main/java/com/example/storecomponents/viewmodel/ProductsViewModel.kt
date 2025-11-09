package com.example.storecomponents.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import org.json.JSONArray
import org.json.JSONObject

data class ProductoModel(
    val id: Long,
    val name: String,
    val price: String,
    val imageUrl: String
)

data class UserItemModel(
    val id: Long,
    val name: String,
    val role: String,
    val isSalesManager: Boolean = false,
    val email: String? = null,
    val password: String? = null,
    val direccion: String? = null
)

class ProductsViewModel(application: Application) : AndroidViewModel(application) {
    private val prefsName = "products_prefs"
    private val prefsKey = "products_json"
    private val prefs = application.getSharedPreferences(prefsName, Context.MODE_PRIVATE)

    private val _products = mutableStateListOf<ProductoModel>()
    val products: List<ProductoModel> get() = _products

    init {
        loadProducts()
    }

    fun addProduct(name: String, price: String, imageUrl: String) {
        val newId = if (_products.isEmpty()) 1L else (_products.maxOf { it.id } + 1)
        _products.add(ProductoModel(newId, name, price, imageUrl))
        saveProducts()
    }

    fun updateProduct(id: Long, name: String, price: String, imageUrl: String) {
        val idx = _products.indexOfFirst { it.id == id }
        if (idx >= 0) {
            _products[idx] = _products[idx].copy(name = name, price = price, imageUrl = imageUrl)
            saveProducts()
        }
    }

    fun removeProduct(id: Long) {
        val idx = _products.indexOfFirst { it.id == id }
        if (idx >= 0) {
            _products.removeAt(idx)
            saveProducts()
        }
    }

    fun getProductById(id: Long): ProductoModel? = _products.firstOrNull { it.id == id }

    private fun saveProducts() {
        val ja = JSONArray()
        for (p in _products) {
            val jo = JSONObject()
            jo.put("id", p.id)
            jo.put("name", p.name)
            jo.put("price", p.price)
            jo.put("imageUrl", p.imageUrl)
            ja.put(jo)
        }
        prefs.edit().putString(prefsKey, ja.toString()).apply()
    }

    private fun loadProducts() {
        val raw = prefs.getString(prefsKey, null) ?: return
        try {
            val ja = JSONArray(raw)
            for (i in 0 until ja.length()) {
                val jo = ja.getJSONObject(i)
                val id = jo.optLong("id", -1L)
                val name = jo.optString("name", "")
                val price = jo.optString("price", "")
                val imageUrl = jo.optString("imageUrl", "")
                if (id >= 0) _products.add(ProductoModel(id, name, price, imageUrl))
            }
        } catch (e: Exception) {
            // si el JSON está corrupto, ignorar y resetear
            prefs.edit().remove(prefsKey).apply()
        }
    }
}

class UsersViewModel : androidx.lifecycle.ViewModel() {
    private val _users = mutableStateListOf<UserItemModel>()
    val users: List<UserItemModel> get() = _users

    fun addUser(name: String, role: String, email: String? = null, password: String? = null, direccion: String? = null) {
        val newId = if (_users.isEmpty()) 1L else (_users.maxOf { it.id } + 1)
        _users.add(UserItemModel(newId, name, role, false, email, password, direccion))
    }

    fun updateUser(id: Long, name: String, role: String) {
        val idx = _users.indexOfFirst { it.id == id }
        if (idx >= 0) {
            val u = _users[idx]
            _users[idx] = u.copy(name = name, role = role)
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
