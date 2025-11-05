package com.example.storecomponents.features.biometrica

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

class BiometricManagerHelper(private val context: Context) {
    fun isBiometricAvailable(): Boolean {
        val biometricManager = BiometricManager.from(context)
        return biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS
    }
    fun createPrompt(activity: FragmentActivity, callback: BiometricPrompt.AuthenticationCallback): BiometricPrompt {
        val executor = ContextCompat.getMainExecutor(context)
        return BiometricPrompt(activity, executor, callback)
    }
}

