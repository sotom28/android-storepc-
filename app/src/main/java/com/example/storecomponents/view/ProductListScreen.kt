package com.example.storecomponents.view

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.storecomponents.data.model.Producto
import com.example.storecomponents.viewmodel.ProductoViewModel
import java.util.UUID

@Composable
fun ProductListScreen(onOpenProduct: (Producto) -> Unit = {}, productoViewModel: ProductoViewModel = viewModel()) {
    val context = LocalContext.current
    val productosState = productoViewModel.productos.collectAsState()
    val productos = productosState.value

    // Estado para mostrar diálogo de creación
    var showCreateDialog by remember { mutableStateOf(false) }

    // Estado para confirmar eliminación
    var productToDelete by remember { mutableStateOf<Producto?>(null) }

    // Campos del formulario (temporal dentro del diálogo)
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precioText by remember { mutableStateOf("") }
    var stockText by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var imagenUrl by remember { mutableStateOf("") }

    Scaffold { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            // Barra simple con título y botón para crear
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Productos", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                Button(onClick = { showCreateDialog = true }) {
                    Text(text = "Agregar producto")
                }
            }

            LazyColumn(modifier = Modifier.padding(top = 12.dp)) {
                items(productos) { product ->
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenProduct(product) }
                        .padding(vertical = 8.dp)
                    ) {
                        Text(text = product.nombre)
                        Text(text = "Precio: $${"%.2f".format(product.precio)}")
                        Text(text = "Stock: ${product.stock}")
                        Text(text = "Categoría: ${product.categoria}")
                        Text(text = "Descripción: ${product.descripcion}")

                        // Botones de acción para cada producto
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Button(onClick = { onOpenProduct(product) }) {
                                Text(text = "Editar")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = { productToDelete = product }) {
                                Text(text = "Eliminar")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text(text = "Crear Producto") },
            text = {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre *") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = precioText, onValueChange = { precioText = it }, label = { Text("Precio *") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = stockText, onValueChange = { stockText = it }, label = { Text("Stock *") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = categoria, onValueChange = { categoria = it }, label = { Text("Categoría") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = imagenUrl, onValueChange = { imagenUrl = it }, label = { Text("URL Imagen") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    // Validaciones básicas
                    val precio = precioText.toDoubleOrNull()
                    val stock = stockText.toIntOrNull()
                    if (nombre.isBlank() || precio == null || stock == null) {
                        Toast.makeText(context, "Complete los campos requeridos (nombre, precio, stock)", Toast.LENGTH_SHORT).show()
                        return@TextButton
                    }
                    val newProducto = Producto(
                        id = UUID.randomUUID().toString(),
                        nombre = nombre,
                        descripcion = descripcion,
                        precio = precio,
                        stock = stock,
                        categoria = categoria,
                        imagenUrl = imagenUrl,
                        clienteId = null
                    )
                    productoViewModel.agregarProducto(newProducto)
                    Toast.makeText(context, "Producto agregado", Toast.LENGTH_SHORT).show()
                    // limpiar y cerrar
                    nombre = ""; descripcion = ""; precioText = ""; stockText = ""; categoria = ""; imagenUrl = ""
                    showCreateDialog = false
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) { Text("Cancelar") }
            }
        )
    }

    // Diálogo de confirmación para eliminar
    if (productToDelete != null) {
        AlertDialog(
            onDismissRequest = { productToDelete = null },
            title = { Text(text = "Confirmar eliminación") },
            text = { Text(text = "¿Seguro que quieres eliminar el producto '${productToDelete?.nombre}'?") },
            confirmButton = {
                TextButton(onClick = {
                    productToDelete?.let {
                        productoViewModel.eliminarProducto(it.id)
                        productToDelete = null
                    }
                }) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { productToDelete = null }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
fun PedidosScreen(onNavigate: (String) -> Unit = {}) {
    Scaffold { padding ->
        Box(
            modifier = Modifier.Companion
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Companion.Center
        ) {
            Text(
                text = "Pantalla: Pedidos",
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PedidosScreenPreview() {
    PedidosScreen()
}