package com.example.storecomponents.view.cliente

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import com.example.storecomponents.data.model.Producto
import com.example.storecomponents.viewmodel.ProductoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClienteProductoDetailScreen(
    productoId: String?,
    productoViewModel: com.example.storecomponents.viewmodel.ProductoViewModel,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val productos by productoViewModel.productos.collectAsState()
    val producto = productos.firstOrNull { it.id == productoId }

    var imagenUrl by remember { mutableStateOf(producto?.imagenUrl ?: "") }
    var descripcion by remember { mutableStateOf(producto?.descripcion ?: "") }
    var nombre by remember { mutableStateOf(producto?.nombre ?: "") }
    var precioText by remember { mutableStateOf(producto?.precio?.toString() ?: "") }
    var stockText by remember { mutableStateOf(producto?.stock?.toString() ?: "") }

    // Selector de imagen desde dispositivo
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            // Guardar uri como string para previsualizar; si quieres subir la imagen a servidor, implementa upload
            imagenUrl = it.toString()
        }
    }

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Detalles", "Imagen")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = "Detalle de producto", style = MaterialTheme.typography.titleLarge)

        if (producto == null) {
            Text(text = "Producto no encontrado", style = MaterialTheme.typography.bodyMedium)
            Button(onClick = onBack) { Text("Volver") }
            return@Column
        }

        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(selected = selectedTab == index, onClick = { selectedTab = index }, text = { Text(title) })
            }
        }

        when (selectedTab) {
            0 -> {
                // Detalles: nombre, descripción, precio, stock
                OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = precioText, onValueChange = { precioText = it }, label = { Text("Precio") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = stockText, onValueChange = { stockText = it }, label = { Text("Stock") }, modifier = Modifier.fillMaxWidth())

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    Button(onClick = {
                        val precio = precioText.toDoubleOrNull() ?: producto.precio
                        val stock = stockText.toIntOrNull() ?: producto.stock
                        val updated = producto.copy(nombre = nombre, descripcion = descripcion, precio = precio, stock = stock, imagenUrl = imagenUrl)
                        productoViewModel.actualizarProducto(updated)
                        Toast.makeText(context, "Producto actualizado", Toast.LENGTH_SHORT).show()
                    }) {
                        Text("Guardar detalles")
                    }

                    Button(onClick = onBack) {
                        Text("Volver")
                    }
                }
            }
            1 -> {
                // Imagen: preview + seleccionar o pegar URL
                if (imagenUrl.isNotBlank()) {
                    AsyncImage(model = imagenUrl, contentDescription = nombre, modifier = Modifier.fillMaxWidth().height(220.dp), contentScale = ContentScale.Crop)
                } else {
                    Box(modifier = Modifier.fillMaxWidth().height(220.dp), contentAlignment = Alignment.Center) {
                        Text(text = "No image", style = MaterialTheme.typography.bodyMedium)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = imagenUrl, onValueChange = { imagenUrl = it }, label = { Text("URL Imagen") }, modifier = Modifier.fillMaxWidth())

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    Button(onClick = { launcher.launch("image/*") }) {
                        Text("Seleccionar imagen")
                    }
                    Button(onClick = {
                        // Guardar solo la URL/uri de la imagen
                        val precio = precioText.toDoubleOrNull() ?: producto.precio
                        val stock = stockText.toIntOrNull() ?: producto.stock
                        val updated = producto.copy(nombre = nombre, descripcion = descripcion, precio = precio, stock = stock, imagenUrl = imagenUrl)
                        productoViewModel.actualizarProducto(updated)
                        Toast.makeText(context, "Imagen actualizada", Toast.LENGTH_SHORT).show()
                    }) {
                        Text("Guardar imagen")
                    }
                }
            }

        }
    }
}
