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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProductoRepository {

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

    init {
        // Productos de muestra con imágenes y descripciones completas
        val productosIniciales = listOf(
            Producto(
                id = "1",
                nombre = "Laptop HP Pavilion",
                descripcion = "Laptop HP Pavilion 15.6\" con procesador Intel Core i5, 8GB RAM, 256GB SSD. Ideal para trabajo y estudio.",
                precio = 699.99,
                stock = 15,
                categoria = "Electrónicos",
                imagenUrl = "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400"
            ),
            Producto(
                id = "2",
                nombre = "Mouse Logitech MX Master 3",
                descripcion = "Mouse inalámbrico ergonómico con sensor de alta precisión y batería de larga duración. Perfecto para productividad.",
                precio = 99.99,
                stock = 50,
                categoria = "Accesorios",
                imagenUrl = "https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=400"
            ),
            Producto(
                id = "3",
                nombre = "Teclado Mecánico Keychron K2",
                descripcion = "Teclado mecánico inalámbrico compacto 75% con switches Gateron. RGB, conexión Bluetooth y cable USB-C.",
                precio = 89.99,
                stock = 30,
                categoria = "Accesorios",
                imagenUrl = "https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=400"
            ),
            Producto(
                id = "4",
                nombre = "Monitor LG UltraWide 29\"",
                descripcion = "Monitor IPS UltraWide 29\" 2560x1080, ideal para multitarea y edición. HDR10, 75Hz, FreeSync.",
                precio = 299.99,
                stock = 20,
                categoria = "Monitores",
                imagenUrl = "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=400"
            )
        )
        _productos.value = productosIniciales
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