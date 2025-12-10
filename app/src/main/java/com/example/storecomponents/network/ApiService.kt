package com.example.storecomponents.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("/users")
    suspend fun registerUser(@Body user: UserDto): Response<UserDto>

    @GET("/users/{id}")
    suspend fun getUser(@Path("id") id: Long): Response<UserDto>
}

