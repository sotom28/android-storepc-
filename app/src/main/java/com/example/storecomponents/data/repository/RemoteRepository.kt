package com.example.storecomponents.data.repository

import com.example.storecomponents.data.model.orden
import com.example.storecomponents.data.remote.ApiClient
import retrofit2.HttpException

class RemoteRepository {
    private val api = ApiClient.retrofit

    // Obtiene las órdenes del backend; lanza excepción si la respuesta no es exitosa
    suspend fun getOrders(): List<orden> {
        val resp = api.getOrders()
        if (resp.isSuccessful) return resp.body() ?: emptyList()
        throw HttpException(resp)
    }

    // Envía una orden al backend; devuelve la orden creada o lanza excepción
    suspend fun postOrder(o: orden): orden {
        val resp = api.createOrder(o)
        if (resp.isSuccessful) return resp.body() ?: o
        throw HttpException(resp)
    }
}
