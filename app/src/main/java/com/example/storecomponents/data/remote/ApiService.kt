package com.example.storecomponents.data.remote

import com.example.storecomponents.data.model.Producto
import com.example.storecomponents.data.model.orden
import com.example.storecomponents.data.model.Usuarios
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("api/productos")
    suspend fun getProductos(): Response<List<Producto>>

    @GET("api/productos/{id}")
    suspend fun getProducto(@Path("id") id: String): Response<Producto>

    @POST("api/productos")
    suspend fun createProducto(@Body producto: Producto): Response<Producto>

    // Endpoint para registrar usuario (fallback a LocalAuthStore si falla)
    @POST("/users")
    suspend fun registerUser(@Body user: Usuarios): Response<Usuarios>

    // Endpoints para órdenes
    @GET("api/orders")
    suspend fun getOrders(): Response<List<orden>>

    @POST("api/orders")
    suspend fun createOrder(@Body o: orden): Response<orden>
}
