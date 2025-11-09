package com.example.storecomponents.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import org.json.JSONArray
import org.json.JSONObject

// Archivo consolidado: un solo ViewModel para usuarios y productos
// Se usa SharedPreferences para persistencia simple (JSON) sin dependencias externas

// Modelos de datos (ligeros y convertibles a/from JSON)
data class StoreUser(
    val id: Long,
    val name: String,
    val role: String,
    val isSalesManager: Boolean = false,
    val email: String? = null,
    val password: String? = null,
    val direccion: String? = null
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("id", id)
        put("name", name)
        put("role", role)
        put("isSalesManager", isSalesManager)
        put("email", email)
        put("password", password)
        put("direccion", direccion)
    }

    companion object {
        fun fromJson(obj: JSONObject): StoreUser = StoreUser(
            id = obj.optLong("id"),
            name = obj.optString("name"),
            role = obj.optString("role"),
            isSalesManager = obj.optBoolean("isSalesManager", false),
            email = obj.optString("email", null),
            password = obj.optString("password", null),
            direccion = obj.optString("direccion", null)
        )
    }
}

data class StoreProduct(
    val id: Long,
    val name: String,
    val description: String? = null,
    val price: Double = 0.0,
    val imageUrl: String? = null
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("id", id)
        put("name", name)
        put("description", description)
        put("price", price)
        put("imageUrl", imageUrl)
    }

    companion object {
        fun fromJson(obj: JSONObject): StoreProduct = StoreProduct(
            id = obj.optLong("id"),
            name = obj.optString("name"),
            description = obj.optString("description", null),
            price = obj.optDouble("price", 0.0),
            imageUrl = obj.optString("imageUrl", null)
        )
    }
}

class StoreViewModel(application: Application) : AndroidViewModel(application) {
    // listas observables para Compose
    private val _users = mutableStateListOf<StoreUser>()
    val users: List<StoreUser> get() = _users

    private val _products = mutableStateListOf<StoreProduct>()
    val products: List<StoreProduct> get() = _products

    // SharedPreferences
    private val prefsName = "store_prefs"
    private val usersKey = "users_json"
    private val productsKey = "products_json"
    private val prefs = application.getSharedPreferences(prefsName, Context.MODE_PRIVATE)

    init {
        loadFromPrefs()
    }

    // --- Usuarios CRUD ---
    fun addUser(name: String, role: String, email: String? = null, password: String? = null, direccion: String? = null) {
        val newId = if (_users.isEmpty()) 1L else (_users.maxOf { it.id } + 1)
        val u = StoreUser(newId, name, role, false, email, password, direccion)
        _users.add(u)
        saveUsersToPrefs()
    }

    fun updateUser(id: Long, name: String, role: String, email: String? = null, password: String? = null, direccion: String? = null) {
        val idx = _users.indexOfFirst { it.id == id }
        if (idx >= 0) {
            val u = _users[idx]
            _users[idx] = u.copy(name = name, role = role, email = email, password = password, direccion = direccion)
            saveUsersToPrefs()
        }
    }

    fun removeUser(id: Long) {
        val removed = _users.removeAll { it.id == id }
        if (removed) saveUsersToPrefs()
    }

    fun assignSalesManager(id: Long) {
        _users.forEachIndexed { index, user ->
            _users[index] = user.copy(isSalesManager = user.id == id)
        }
        saveUsersToPrefs()
    }

    fun getSalesManager(): StoreUser? = _users.firstOrNull { it.isSalesManager }

    // --- Productos CRUD ---
    fun addProduct(name: String, description: String? = null, price: Double = 0.0, imageUrl: String? = null) {
        val newId = if (_products.isEmpty()) 1L else (_products.maxOf { it.id } + 1)
        val p = StoreProduct(newId, name, description, price, imageUrl)
        _products.add(p)
        saveProductsToPrefs()
    }

    fun updateProduct(id: Long, name: String, description: String? = null, price: Double = 0.0, imageUrl: String? = null) {
        val idx = _products.indexOfFirst { it.id == id }
        if (idx >= 0) {
            val p = _products[idx]
            _products[idx] = p.copy(name = name, description = description, price = price, imageUrl = imageUrl)
            saveProductsToPrefs()
        }
    }

    fun removeProduct(id: Long) {
        val removed = _products.removeAll { it.id == id }
        if (removed) saveProductsToPrefs()
    }

    fun getProductById(id: Long): StoreProduct? = _products.firstOrNull { it.id == id }

    // --- Persistencia simple en SharedPreferences ---
    private fun saveUsersToPrefs() {
        val arr = JSONArray()
        _users.forEach { arr.put(it.toJson()) }
        prefs.edit().putString(usersKey, arr.toString()).apply()
    }

    private fun saveProductsToPrefs() {
        val arr = JSONArray()
        _products.forEach { arr.put(it.toJson()) }
        prefs.edit().putString(productsKey, arr.toString()).apply()
    }

    private fun loadFromPrefs() {
        // usuarios
        val usersJson = prefs.getString(usersKey, null)
        if (!usersJson.isNullOrBlank()) {
            try {
                val arr = JSONArray(usersJson)
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    _users.add(StoreUser.fromJson(obj))
                }
            } catch (e: Exception) {
                // si falla, limpiar clave
                prefs.edit().remove(usersKey).apply()
            }
        }

        // productos
        val productsJson = prefs.getString(productsKey, null)
        if (!productsJson.isNullOrBlank()) {
            try {
                val arr = JSONArray(productsJson)
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    _products.add(StoreProduct.fromJson(obj))
                }
            } catch (e: Exception) {
                prefs.edit().remove(productsKey).apply()
            }
        }
    }
}
