package com.example.storecomponents.data.network

import com.example.storecomponents.data.model.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    // ==================== USUARIOS ====================
    
    @POST("api/usuarios/register")
    suspend fun register(@Body request: RegisterRequest): Response<UsuarioDTO>
    
    @POST("api/usuarios/login")
    suspend fun login(@Body request: LoginRequest): Response<UsuarioDTO>
    
    @GET("api/usuarios/allUsers")
    suspend fun getAllUsers(): Response<List<UsuarioDTO>>
    
    @GET("api/usuarios/{id}")
    suspend fun getUserById(@Path("id") id: Long): Response<UsuarioDTO>
    
    @GET("api/usuarios/username/{username}")
    suspend fun getUserByUsername(@Path("username") username: String): Response<UsuarioDTO>
    
    // ==================== PRODUCTOS ====================
    
    @POST("api/crearProducto")
    suspend fun crearProducto(@Body producto: ProductoDTO): Response<ProductoDTO>
    
    @GET("api/productos")
    suspend fun getAllProductos(): Response<List<ProductoDTO>>
    
    @GET("api/producto/{id}")
    suspend fun getProductoById(@Path("id") id: Long): Response<ProductoDTO>
    
    @GET("api/categoria/{categoria}")
    suspend fun getProductosByCategoria(@Path("categoria") categoria: String): Response<List<ProductoDTO>>
    
    // ==================== STATUS ====================
    
    @GET("api/status")
    suspend fun checkStatus(): Response<Map<String, Any>>
}
