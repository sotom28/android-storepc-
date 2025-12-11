package com.example.storecomponents.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.storecomponents.data.model.Userole
import com.example.storecomponents.data.model.Usuarios
import com.example.storecomponents.viewmodel.UsuarioEstado
import com.example.storecomponents.viewmodel.UsuariosViewModel

@Composable
fun GestionUsuariosScreen(
    usuariosViewModel: UsuariosViewModel = viewModel(),
    onBack: () -> Unit = {}
) {
    val usuarios by usuariosViewModel.usuarios.collectAsState()
    val estado by usuariosViewModel.estado.collectAsState()

    var editingId by remember { mutableStateOf<Long?>(null) }
    var nombre by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var role by remember { mutableStateOf(Userole.CLIENT) }

    LaunchedEffect(estado) {
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "Gestión de Usuarios",
            style = MaterialTheme.typography.titleLarge
        )

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre Completo") },
            placeholder = { Text("Ej: Juan Pérez") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = username,
            onValueChange = { username = it.lowercase().trim() },
            label = { Text("Nombre de Usuario") },
            placeholder = { Text("Ej: juanperez") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Email") },
            placeholder = { Text("usuario@ejemplo.com") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = direccion,
            onValueChange = { direccion = it },
            label = { Text("Dirección") },
            placeholder = { Text("Opcional") },
            modifier = Modifier.fillMaxWidth()
        )

        // Role selector
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Rol:", modifier = Modifier.padding(end = 8.dp))
            RadioButton(
                selected = role == Userole.CLIENT,
                onClick = { role = Userole.CLIENT }
            )
            Text(text = "Cliente", modifier = Modifier.padding(end = 16.dp))
            RadioButton(
                selected = role == Userole.ADMIN,
                onClick = { role = Userole.ADMIN }
            )
            Text(text = "Admin")
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                if (nombre.isBlank() || username.isBlank() || correo.isBlank() || password.isBlank()) {
                    return@Button
                }

                if (editingId == null) {
                    // Generar id simple (en producción esto lo maneja el backend)
                    val nextId = (usuarios.maxOfOrNull { it.id } ?: 0L) + 1
                    val nuevo = Usuarios(
                        id = nextId,
                        nombre = nombre.trim(),
                        username = username.trim(),
                        correo = correo.trim(),
                        role = role,
                        password = password,
                        confirmarPassword = password,
                        direccion = direccion.trim()
                    )
                    usuariosViewModel.agregarUsuario(nuevo)
                } else {
                    val actualizado = Usuarios(
                        id = editingId!!,
                        nombre = nombre.trim(),
                        username = username.trim(),
                        correo = correo.trim(),
                        role = role,
                        password = password,
                        confirmarPassword = password,
                        direccion = direccion.trim()
                    )
                    usuariosViewModel.actualizarUsuario(actualizado)
                    editingId = null
                }

                // Limpiar formulario
                nombre = ""
                username = ""
                correo = ""
                password = ""
                direccion = ""
                role = Userole.CLIENT

            }) {
                Text(text = if (editingId == null) "Crear" else "Guardar")
            }

            Button(onClick = {
                editingId = null
                nombre = ""
                username = ""
                correo = ""
                password = ""
                direccion = ""
                role = Userole.CLIENT
            }) {
                Text(text = "Cancelar")
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(onClick = onBack) {
                Text(text = "Volver")
            }
        }

        HorizontalDivider()

        Text(
            text = "Lista de Usuarios",
            style = MaterialTheme.typography.titleMedium
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(usuarios) { u ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ListItem(
                        headlineContent = { Text(u.nombre) },
                        supportingContent = {
                            Column {
                                Text("@${u.username}")
                                Text(u.correo)
                                Text("Rol: ${u.role.name}", style = MaterialTheme.typography.bodySmall)
                            }
                        },
                        trailingContent = {
                            Row {
                                IconButton(onClick = {
                                    // Editar
                                    editingId = u.id
                                    nombre = u.nombre
                                    username = u.username
                                    correo = u.correo
                                    password = u.password
                                    direccion = u.direccion
                                    role = u.role
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Editar"
                                    )
                                }
                                IconButton(onClick = {
                                    usuariosViewModel.eliminarUsuario(u.id)
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Eliminar"
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}