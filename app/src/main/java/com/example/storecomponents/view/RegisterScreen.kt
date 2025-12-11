package com.example.storecomponents.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.storecomponents.viewmodel.AuthViewModel
import com.example.storecomponents.viewmodel.EstadoAuth

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    onRegistered: () -> Unit = {},
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val registerState by authViewModel.registerState.collectAsState()

    var nombreReal by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Observar el estado del registro
    LaunchedEffect(registerState) {
        when (registerState) {
            is EstadoAuth.Cargando -> {
                isLoading = true
                errorMessage = null
            }
            is EstadoAuth.Exito -> {
                isLoading = false
                Toast.makeText(context, "¡Registro exitoso!", Toast.LENGTH_SHORT).show()
                authViewModel.resetRegisterState()
                onRegistered()
            }
            is EstadoAuth.Error -> {
                isLoading = false
                errorMessage = (registerState as EstadoAuth.Error).mensaje
            }
            else -> {
                isLoading = false
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Crear Cuenta",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Completa el formulario para registrarte",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Nombre Real
        OutlinedTextField(
            value = nombreReal,
            onValueChange = { nombreReal = it },
            label = { Text("Nombre Completo") },
            placeholder = { Text("Ej: Juan Pérez") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            singleLine = true
        )

        // Username
        OutlinedTextField(
            value = username,
            onValueChange = { username = it.lowercase().trim() },
            label = { Text("Nombre de Usuario") },
            placeholder = { Text("Ej: juanperez") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            singleLine = true,
            supportingText = { Text("Sin espacios ni caracteres especiales") }
        )

        // Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it.trim() },
            label = { Text("Email") },
            placeholder = { Text("Ej: juan@ejemplo.com") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            singleLine = true
        )

        // Password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            singleLine = true,
            supportingText = { Text("Mínimo 6 caracteres") }
        )

        // Confirmar Password
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirmar Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            singleLine = true
        )

        // Mensaje de error
        if (errorMessage != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botón de Registro
        Button(
            onClick = {
                // Validaciones
                errorMessage = null

                if (nombreReal.isBlank()) {
                    errorMessage = "Ingresa tu nombre completo"
                    return@Button
                }

                if (username.isBlank()) {
                    errorMessage = "Ingresa un nombre de usuario"
                    return@Button
                }

                if (username.length < 3) {
                    errorMessage = "El nombre de usuario debe tener al menos 3 caracteres"
                    return@Button
                }

                if (email.isBlank() || !email.contains("@")) {
                    errorMessage = "Ingresa un email válido"
                    return@Button
                }

                if (password.length < 6) {
                    errorMessage = "La contraseña debe tener al menos 6 caracteres"
                    return@Button
                }

                if (password != confirmPassword) {
                    errorMessage = "Las contraseñas no coinciden"
                    return@Button
                }

                // Llamar al registro
                authViewModel.register(
                    nombreReal = nombreReal.trim(),
                    email = email.trim(),
                    usernameOrRole = username.trim(),
                    password = password.trim()
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Registrando...")
            } else {
                Text("Crear Cuenta")
            }
        }

        // Botón para volver al login
        TextButton(
            onClick = { onRegistered() },
            enabled = !isLoading
        ) {
            Text("¿Ya tienes cuenta? Inicia sesión")
        }
    }
}
@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(onRegistered = {})
}
