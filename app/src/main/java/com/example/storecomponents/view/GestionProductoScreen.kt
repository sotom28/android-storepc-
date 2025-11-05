package com.example.storecomponents.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.storecomponents.data.model.Producto
import com.example.storecomponents.viewmodel.ProductoViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add

@Composable
fun GestionProductoScreen(
    productoViewModel: ProductoViewModel = viewModel(),
    productoId: String?,
    onNavigateBack: () -> Unit,
    currentRoute: String? = "productos",
    onNavigate: (String) -> Unit = {},
) {
    val productos by productoViewModel.productos.collectAsState()
    val producto = productos.find { it.id == productoId }

    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var imagenUrl by remember { mutableStateOf("") }

    LaunchedEffect(producto) {
        producto?.let {
            nombre = it.nombre
            descripcion = it.descripcion
            precio = it.precio.toString()
            stock = it.stock.toString()
            categoria = it.categoria
            imagenUrl = it.imagenUrl
        }
    }

    val isEditing = producto != null
    val title = if (isEditing) "Editar Producto" else "Agregar Producto"

    Scaffold(
        topBar = {
            AppTopBar(title = title, showLogo = false)
        },
        bottomBar = {
            AppBottomBar(currentRoute = currentRoute, onNavigate = onNavigate)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // Limpiar formulario para crear nuevo producto
                nombre = ""
                descripcion = ""
                precio = ""
                stock = ""
                categoria = ""
                imagenUrl = ""
            }) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo Producto")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(title, style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre del Producto") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = precio,
                        onValueChange = { precio = it },
                        label = { Text("Precio") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = stock,
                        onValueChange = { stock = it },
                        label = { Text("Stock") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = categoria,
                        onValueChange = { categoria = it },
                        label = { Text("Categoría") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = imagenUrl,
                        onValueChange = { imagenUrl = it },
                        label = { Text("URL de la Imagen") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            val productoActualizado = Producto(
                                id = productoId ?: "",
                                nombre = nombre,
                                descripcion = descripcion,
                                precio = precio.toDoubleOrNull() ?: 0.0,
                                stock = stock.toIntOrNull() ?: 0,
                                categoria = categoria,
                                imagenUrl = imagenUrl
                            )
                            if (isEditing) {
                                productoViewModel.actualizarProducto(productoActualizado)
                            } else {
                                productoViewModel.agregarProducto(productoActualizado)
                            }
                            onNavigateBack()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (isEditing) "Guardar Cambios" else "Agregar Producto")
                    }
                }
            }
        }
    }
}
