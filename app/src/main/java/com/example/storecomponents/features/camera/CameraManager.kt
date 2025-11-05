package com.example.storecomponents.features.camera

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview

class CameraManager(private val context: Context) {
    fun getPreview(): Preview {
        return Preview.Builder().build()
    }
    fun getCameraSelector(): CameraSelector {
        return CameraSelector.DEFAULT_BACK_CAMERA
    }
}

