package com.example.storecomponents.view

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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLogin: (usernameOrEmail: String, password: String) -> Unit = { _, _ -> }
) {
    val focusManager = LocalFocusManager.current

    var userOrEmail by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

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
            onClick = { onLogin("cliente1", "cliente123") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Entrar como cliente (prueba)")
        }

        Button(
            onClick = { onLogin("admin", "admin123") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Entrar como admin (prueba)")
        }

        // Hint para pruebas
        Text(text = "Prueba: cliente -> user: 'cliente1' pass: 'cliente123' | admin -> user contains 'admin' and pass 'admin123'", style = MaterialTheme.typography.bodySmall)
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen { username, password ->
        println("Usuario/Correo: $username")
        println("Contraseña: $password")
    }
}
