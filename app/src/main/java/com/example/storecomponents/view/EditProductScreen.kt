package com.example.storecomponents.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.storecomponents.data.model.Producto
import com.example.storecomponents.viewmodel.ProductoViewModel


@Composable
fun EditProductScreen(
    navController: NavController,
    productId: String?,
    productoViewModel: ProductoViewModel = viewModel()
) {
    // Obtenemos la lista de productos desde el StateFlow y la convertimos a estado Compose
    val productos by productoViewModel.productos.collectAsState()
    val product = productId?.let { id -> productos.find { it.id == id } }

    if (product == null) {
        Text("Producto no encontrado")
        return
    }

    var nombre by remember { mutableStateOf(product.nombre) }
    var descripcion by remember { mutableStateOf(product.descripcion) }
    var precio by remember { mutableStateOf(product.precio.toString()) }
    var stock by remember { mutableStateOf(product.stock.toString()) }
    var categoria by remember { mutableStateOf(product.categoria) }
    var imagenUrl by remember { mutableStateOf(product.imagenUrl) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Editar Producto", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = precio,
            onValueChange = { precio = it },
            label = { Text("Precio") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        OutlinedTextField(
            value = stock,
            onValueChange = { stock = it },
            label = { Text("Stock") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        OutlinedTextField(
            value = categoria,
            onValueChange = { categoria = it },
            label = { Text("Categoría") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = imagenUrl,
            onValueChange = { imagenUrl = it },
            label = { Text("URL de la Imagen") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val precioDouble = precio.toDoubleOrNull()
                val stockInt = stock.toIntOrNull()

                if (nombre.isNotBlank() && precioDouble != null && stockInt != null) {
                    // Crear objeto Producto con el mismo id y llamar al ViewModel
                    val actualizado = Producto(
                        id = product.id,
                        nombre = nombre,
                        descripcion = descripcion,
                        precio = precioDouble,
                        stock = stockInt,
                        categoria = categoria,
                        imagenUrl = imagenUrl
                    )
                    productoViewModel.actualizarProducto(actualizado)
                    navController.popBackStack()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Cambios")
        }
    }
}
