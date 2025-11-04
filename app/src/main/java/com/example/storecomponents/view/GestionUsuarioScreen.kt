package com.example.storecomponents.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.storecomponents.viewmodel.UsersViewModel

@Composable
fun GestionUsuarioScreen(
    onNavigate: (String) -> Unit = {},
    usersViewModel: UsersViewModel = viewModel()
) {
    val context = LocalContext.current

    // Usar la lista del ViewModel
    val users = usersViewModel.users

    // Form state
    var editingId by remember { mutableStateOf<Long?>(null) }
    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = "Gestión de Usuarios", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = role,
            onValueChange = { role = it },
            label = { Text("Rol (ej: vendedor, admin, cliente)") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                if (name.isBlank() || role.isBlank()) {
                    Toast.makeText(context, "Complete nombre y rol", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (editingId == null) {
                    usersViewModel.addUser(name.trim(), role.trim())
                    Toast.makeText(context, "Usuario creado", Toast.LENGTH_SHORT).show()
                } else {
                    usersViewModel.updateUser(editingId!!, name.trim(), role.trim())
                    Toast.makeText(context, "Usuario actualizado", Toast.LENGTH_SHORT).show()
                    editingId = null
                }
                name = ""
                role = ""
            }) {
                Text(text = if (editingId == null) "Crear" else "Guardar")
            }

            Button(onClick = {
                editingId = null
                name = ""
                role = ""
            }) {
                Text(text = "Cancelar")
            }
        }

        HorizontalDivider()

        Text(text = "Lista de usuarios", style = MaterialTheme.typography.titleMedium)

        if (users.isEmpty()) {
            Text(text = "No hay usuarios. Agrega uno arriba.", style = MaterialTheme.typography.bodySmall)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(users, key = { it.id }) { u ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = u.name, style = MaterialTheme.typography.titleMedium)
                                Text(text = "Rol: ${u.role}", style = MaterialTheme.typography.bodySmall)
                                if (u.isSalesManager) {
                                    Text(text = "Gestor de ventas", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                TextButton(onClick = {
                                    // editar
                                    editingId = u.id
                                    name = u.name
                                    role = u.role
                                }) { Text("Editar") }

                                TextButton(onClick = {
                                    // eliminar
                                    usersViewModel.removeUser(u.id)
                                    Toast.makeText(context, "Usuario eliminado", Toast.LENGTH_SHORT).show()
                                }) { Text("Eliminar") }

                                Button(onClick = {
                                    // asignar como gestor de ventas (único)
                                    usersViewModel.assignSalesManager(u.id)
                                    Toast.makeText(context, "${u.name} asignado como gestor de ventas", Toast.LENGTH_SHORT).show()
                                }) { Text("Asignar gestor") }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Acción rápida: volver al menú admin
        OutlinedButton(onClick = { onNavigate("admin") }, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Volver al Menú Admin")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GestionUsuarioScreenPreview() {
    GestionUsuarioScreen()
}
