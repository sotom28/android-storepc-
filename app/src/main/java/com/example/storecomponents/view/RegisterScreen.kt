package com.example.storecomponents.view

import android.widget.Toast
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.storecomponents.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    onRegistered: () -> Unit = {},
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current

    var name by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    var role by rememberSaveable { mutableStateOf("cliente") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var direccion by rememberSaveable { mutableStateOf("") }

    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Registrar usuario", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value= username,
            onValueChange = { username = it },
            label = { Text("Nombre de usuario") },
            modifier = Modifier.fillMaxWidth()


        )

        OutlinedTextField(
            value = role,
            onValueChange = { role = it },
            label = { Text("Rol (ej: cliente, admin, vendedor)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirmar contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = direccion,
            onValueChange = { direccion = it },
            label = { Text("Dirección") },
            modifier = Modifier.fillMaxWidth()
        )

        if (errorMessage != null) {
            Text(text = errorMessage!!, color = androidx.compose.ui.graphics.Color.Red)
        }

        Button(onClick = {
            // Validaciones básicas
            errorMessage = null
            if (name.isBlank() || role.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                errorMessage = "Complete todos los campos"
                return@Button
            }
            if (!email.contains("@")) {
                errorMessage = "Email inválido"
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

            // Usar AuthViewModel para registrar y auto-logear
            // CORRECCIÓN: pasar los parámetros en el orden correcto: name, email, role, password, confirmPassword, direccion
            // Si se proporcionó "username" lo usamos como nombre real (para login por username)
            val nombreParaRegistro = if (username.isNotBlank()) username.trim() else name.trim()
            authViewModel.register(
                nombreParaRegistro,
                email.trim(),
                role.trim(),
                password.trim(),
                confirmPassword.trim(),
                direccion.trim()
            )

            Toast.makeText(context, "Usuario registrado", Toast.LENGTH_SHORT).show()
            // limpiar formulario opcional
            name = ""
            role = "cliente"
            username = ""
            email = ""
            password = ""
            confirmPassword = ""
            direccion = ""

            onRegistered()
        }, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Registrar")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(onRegistered = {})
}
