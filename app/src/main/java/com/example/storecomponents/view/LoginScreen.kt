package com.example.storecomponents.view

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.storecomponents.features.camera.CameraCaptureScreen
import kotlinx.coroutines.launch
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLogin: (usernameOrEmail: String, password: String) -> Unit = { _, _ -> },
    onRegister: () -> Unit = {},
    onCameraLogin: (() -> Unit)? = null,
    capturedPhoto: ImageBitmap? = null,
    onNavigateToProducts: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val activity = LocalContext.current as? FragmentActivity

    var userOrEmail by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var showCamera by remember { mutableStateOf(false) }
    var photoBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // ahora permitimos login con usuario o correo; validamos solo la longitud de password
    val isPasswordValid = password.length >= 8
    val canSubmit = userOrEmail.isNotBlank() && isPasswordValid

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Iniciar sesión", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = userOrEmail,
            onValueChange = {
                userOrEmail = it
                showError = false
            },
            label = { Text("Usuario o correo") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                showError = false
            },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        if (showError) {
            Text(text = "Usuario o contraseña incorrectos", color = Color.Red)
        }

        Button(
            onClick = {
                focusManager.clearFocus()
                if (canSubmit) {
                    onLogin(userOrEmail.trim(), password)
                } else {
                    showError = true
                }
            },
            enabled = canSubmit,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Iniciar sesión")
        }

        // Botones de prueba para debugging: entradas rápidas
        Button(
            onClick = {
                Toast.makeText(context, "Intentando login como cliente...", Toast.LENGTH_SHORT).show()
                onLogin("cliente@store.com", "cliente123")
                //Toast.makeText(context, "Navegando a la pantalla de productos...", Toast.LENGTH_SHORT).show()
                //onNavigateToProducts()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Entrar como cliente (prueba)")
        }

        Button(
            onClick = {
                Toast.makeText(context, "Intentando login como admin...", Toast.LENGTH_SHORT).show()
                onLogin("admin@store.com", "admin123")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Entrar como admin (prueba)")
        }

        // Nuevo: botón para ir a la pantalla de registro
        Button(
            onClick = { onRegister() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Registrar")
        }

        // Botón para iniciar sesión con cámara
        Button(
            onClick = { showCamera = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Iniciar sesión con cámara")
        }

        // Botón para iniciar sesión con huella/biometría
        Button(
            onClick = {
                if (activity != null) {
                    val executor = ContextCompat.getMainExecutor(activity)
                    val promptInfo = BiometricPrompt.PromptInfo.Builder()
                        .setTitle("Autenticación biométrica")
                        .setSubtitle("Usa tu huella o rostro para iniciar sesión")
                        .setNegativeButtonText("Cancelar")
                        .build()
                    val biometricPrompt = BiometricPrompt(activity, executor,
                        object : BiometricPrompt.AuthenticationCallback() {
                            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                                Toast.makeText(activity, "Autenticación biométrica exitosa", Toast.LENGTH_SHORT).show()
                                // Usar credenciales válidas para login automático
                                onLogin("cliente@store.com", "cliente123")
                            }
                            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                                Toast.makeText(activity, "Error biométrico: $errString", Toast.LENGTH_SHORT).show()
                            }
                            override fun onAuthenticationFailed() {
                                Toast.makeText(activity, "Autenticación biométrica fallida", Toast.LENGTH_SHORT).show()
                            }
                        })
                    biometricPrompt.authenticate(promptInfo)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Iniciar sesión con huella/biometría")
        }

        // Mostrar la foto capturada si existe
        photoBitmap?.let {
            Text(text = "Foto capturada:")
            Image(
                bitmap = it,
                contentDescription = "Foto de usuario",
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .padding(8.dp)
            )
            Button(
                onClick = {
                    Toast.makeText(context, "Foto confirmada para login", Toast.LENGTH_SHORT).show()
                    onCameraLogin?.invoke()
                },
                modifier = Modifier.fillMaxWidth(0.5f)
            ) {
                Text(text = "Confirmar foto e iniciar sesión")
            }
        }

        // Hint para pruebas
        Text(text = "Prueba: cliente -> correo 'cliente@store.com' pass: 'cliente123' | admin -> correo 'admin@store.com' pass 'admin123'", style = MaterialTheme.typography.bodySmall)
    }
    // Pantalla de cámara modal
    if (showCamera) {
        CameraCaptureScreen(onPhotoCaptured = { bmp ->
            photoBitmap = bmp.asImageBitmap()
            showCamera = false
        })
    }
}


@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        onLogin = { username: String, password: String ->
            println("Usuario/Correo: $username")
            println("Contraseña: $password")
        },
        onRegister = {}
    )
}
