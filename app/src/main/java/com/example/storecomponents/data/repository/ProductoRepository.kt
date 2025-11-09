package com.example.storecomponents.data.repository

import android.content.Context
import androidx.core.content.edit
import com.example.storecomponents.data.model.Producto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ProductoRepository(private val context: Context) {
    private val prefs by lazy { context.getSharedPreferences("productos", Context.MODE_PRIVATE) }
    private val gson = Gson()
    private val key = "productos"

    private fun locallist(): MutableList<Producto> {
        val json = prefs.getString(key, null) ?: return mutableListOf()
        val type = object : TypeToken<List<Producto>>() {}.type
        return gson.fromJson<List<Producto>>(json, type).toMutableList()
    }

    private fun savelist(list: MutableList<Producto>) {
        prefs.edit {
            putString(key, gson.toJson(list))
        }
    }

    fun getALL(): MutableList<Producto> = locallist()

    fun add(producto: Producto) {
        val list = locallist()
        list.add(producto)
        savelist(list)
    }

    fun update(producto: Producto) {
        val list = locallist()
        val index = list.indexOfFirst { it.id == producto.id }
        if (index != -1) {
            list[index] = producto
            savelist(list)
        }
    }

    fun delete(productoId: String) {
        val list = locallist()
        val updatedList = list.filter { it.id != productoId }.toMutableList()
        savelist(updatedList)
    }
}
