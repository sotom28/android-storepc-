package com.example.storecomponents.features.biometrica

import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity

@Composable
fun BiometricScreen(onSuccess: () -> Unit = {}) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    val biometricHelper = BiometricManagerHelper(context)

    Box(modifier = Modifier.fillMaxSize()) {
        Button(onClick = {
            if (!biometricHelper.isBiometricAvailable()) {
                Toast.makeText(context, "Biometría no disponible", Toast.LENGTH_SHORT).show()
                return@Button
            }
            if (activity == null) {
                Toast.makeText(context, "No se puede obtener Activity", Toast.LENGTH_SHORT).show()
                return@Button
            }
            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Autenticación biométrica")
                .setSubtitle("Usa tu huella o rostro para continuar")
                .setNegativeButtonText("Cancelar")
                .build()
            val biometricPrompt = biometricHelper.createPrompt(activity,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        Toast.makeText(context, "Autenticación exitosa", Toast.LENGTH_SHORT).show()
                        onSuccess()
                    }
                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        Toast.makeText(context, "Error: $errString", Toast.LENGTH_SHORT).show()
                    }
                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        Toast.makeText(context, "Autenticación fallida", Toast.LENGTH_SHORT).show()
                    }
                })
            biometricPrompt.authenticate(promptInfo)
        }) {
            Text("Iniciar sesión con huella/biometría")
        }
    }
}

