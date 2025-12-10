package com.example.storecomponents

import android.app.Application
import android.util.Log
import com.example.storecomponents.data.local.LocalAuthStore

class StoreApp : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            // Inicializar el store local para autenticación (SharedPreferences)
            LocalAuthStore.init(this)
            Log.d("StoreApp", "LocalAuthStore initialized")
        } catch (e: Exception) {
            Log.w("StoreApp", "Initialization skipped or failed: ${e.message}")
        }
    }
}
