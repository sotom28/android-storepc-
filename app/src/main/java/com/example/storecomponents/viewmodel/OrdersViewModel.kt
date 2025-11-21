package com.example.storecomponents.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import org.json.JSONArray
import org.json.JSONObject

data class OrderModel(
    val id: Long,
    val productName: String,
    val quantity: Int,
    val price: Double = 0.0,
    val descripcion: String = "",
    val status: String = "PENDIENTE",
    val assignedToId: Long? = null,
    val buyerId: Long? = null
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("id", id)
        put("productName", productName)
        put("quantity", quantity)
        put("price", price)
        put("descripcion", descripcion)
        put("status", status)
        assignedToId?.let { put("assignedToId", it) }
        buyerId?.let { put("buyerId", it) }
    }

    companion object {
        fun fromJson(obj: JSONObject): OrderModel = OrderModel(
            id = obj.optLong("id"),
            productName = obj.optString("productName", ""),
            quantity = obj.optInt("quantity", 1),
            price = obj.optDouble("price", 0.0),
            descripcion = obj.optString("descripcion", ""),
            status = obj.optString("status", "PENDIENTE"),
            assignedToId = if (obj.has("assignedToId")) obj.optLong("assignedToId") else null,
            buyerId = if (obj.has("buyerId")) obj.optLong("buyerId") else null
        )
    }
}

class OrdersViewModel(application: Application) : AndroidViewModel(application) {
    private val _orders = mutableStateListOf<OrderModel>()
    val orders: List<OrderModel> get() = _orders

    private val prefsName = "store_prefs"
    private val ordersKey = "orders_json"
    private val prefs = application.getSharedPreferences(prefsName, Context.MODE_PRIVATE)

    init {
        loadFromPrefs()
    }

    fun addOrder(productName: String, quantity: Int, assignedToId: Long? = null, price: Double = 0.0, descripcion: String = "", buyerId: Long? = null) {
        val newId = if (_orders.isEmpty()) 1L else (_orders.maxOf { it.id } + 1)
        val o = OrderModel(newId, productName, quantity, price, descripcion, "PENDIENTE", assignedToId, buyerId)
        _orders.add(o)
        saveToPrefs()
    }

    fun updateOrder(id: Long, productName: String, quantity: Int, price: Double = 0.0, descripcion: String = "") {
        val idx = _orders.indexOfFirst { it.id == id }
        if (idx >= 0) {
            val existing = _orders[idx]
            _orders[idx] = existing.copy(productName = productName, quantity = quantity, price = price, descripcion = descripcion)
            saveToPrefs()
        }
    }

    fun removeOrder(id: Long) {
        val removed = _orders.removeAll { it.id == id }
        if (removed) saveToPrefs()
    }

    fun updateStatus(id: Long, status: String) {
        val idx = _orders.indexOfFirst { it.id == id }
        if (idx >= 0) {
            val existing = _orders[idx]
            _orders[idx] = existing.copy(status = status)
            saveToPrefs()
        }
    }

    private fun saveToPrefs() {
        val arr = JSONArray()
        _orders.forEach { arr.put(it.toJson()) }
        prefs.edit().putString(ordersKey, arr.toString()).apply()
    }

    private fun loadFromPrefs() {
        val json = prefs.getString(ordersKey, null)
        if (!json.isNullOrBlank()) {
            try {
                val arr = JSONArray(json)
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    _orders.add(OrderModel.fromJson(obj))
                }
            } catch (e: Exception) {
                prefs.edit().remove(ordersKey).apply()
            }
        }
    }
}
