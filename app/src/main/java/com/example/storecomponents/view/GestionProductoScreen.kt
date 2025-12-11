package com.example.storecomponents.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.storecomponents.data.model.Producto
import com.example.storecomponents.viewmodel.ProductoEstado
import com.example.storecomponents.viewmodel.ProductoViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun GestionProductoScreen(
    productoViewModel: ProductoViewModel = viewModel(),
    productoId: String?,
    onNavigateBack: () -> Unit,
    currentRoute: String? = "productos",
    onNavigate: (String) -> Unit = {},
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope() // IMPORTANTE: Para usar launch
    val productos by productoViewModel.productos.collectAsState()
    val productosState by productoViewModel.productosState.collectAsState()

    val producto = productos.find { it.id == productoId }

    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var imagenUrl by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Cargar datos del producto si estamos editando
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

    // Observar el estado de productos
    LaunchedEffect(productosState) {
        when (productosState) {
            is ProductoEstado.Cargando -> {
                isLoading = true
                errorMessage = null
            }
            is ProductoEstado.Exito -> {
                isLoading = false
                errorMessage = null
            }
            is ProductoEstado.Error -> {
                isLoading = false
                errorMessage = (productosState as ProductoEstado.Error).mensaje
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
            else -> {
                isLoading = false
            }
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
            FloatingActionButton(
                onClick = {
                    // Limpiar formulario para crear nuevo producto
                    nombre = ""
                    descripcion = ""
                    precio = ""
                    stock = ""
                    categoria = ""
                    imagenUrl = ""
                    errorMessage = null
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo Producto")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isEditing) "Modifica los campos que desees actualizar"
                        else "Completa los datos del nuevo producto",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre del Producto *") },
                        placeholder = { Text("Ej: Laptop HP Pavilion") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        label = { Text("Descripción *") },
                        placeholder = { Text("Descripción detallada del producto") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = precio,
                        onValueChange = { precio = it },
                        label = { Text("Precio *") },
                        placeholder = { Text("Ej: 699.99") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        singleLine = true,
                        supportingText = { Text("Solo números y punto decimal") }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = stock,
                        onValueChange = { stock = it },
                        label = { Text("Stock *") },
                        placeholder = { Text("Ej: 15") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        singleLine = true,
                        supportingText = { Text("Cantidad disponible") }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = categoria,
                        onValueChange = { categoria = it },
                        label = { Text("Categoría *") },
                        placeholder = { Text("Ej: Electrónicos, Accesorios") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = imagenUrl,
                        onValueChange = { imagenUrl = it },
                        label = { Text("URL de la Imagen") },
                        placeholder = { Text("https://ejemplo.com/imagen.jpg") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        singleLine = true,
                        supportingText = { Text("Opcional - URL de la imagen del producto") }
                    )

                    // Mensaje de error
                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
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

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            // Validaciones
                            errorMessage = null

                            if (nombre.isBlank()) {
                                errorMessage = "El nombre es obligatorio"
                                return@Button
                            }

                            if (descripcion.isBlank()) {
                                errorMessage = "La descripción es obligatoria"
                                return@Button
                            }

                            val precioDouble = precio.toDoubleOrNull()
                            if (precioDouble == null || precioDouble <= 0) {
                                errorMessage = "Ingresa un precio válido mayor a 0"
                                return@Button
                            }

                            val stockInt = stock.toIntOrNull()
                            if (stockInt == null || stockInt < 0) {
                                errorMessage = "Ingresa un stock válido (mayor o igual a 0)"
                                return@Button
                            }

                            if (categoria.isBlank()) {
                                errorMessage = "La categoría es obligatoria"
                                return@Button
                            }

                            // Crear objeto producto
                            val productoActualizado = Producto(
                                id = productoId ?: "",
                                nombre = nombre.trim(),
                                descripcion = descripcion.trim(),
                                precio = precioDouble,
                                stock = stockInt,
                                categoria = categoria.trim(),
                                imagenUrl = imagenUrl.trim()
                            )

                            // Guardar o actualizar
                            if (isEditing) {
                                productoViewModel.actualizarProducto(productoActualizado)
                                Toast.makeText(
                                    context,
                                    "Producto actualizado (solo local por ahora)",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                productoViewModel.agregarProducto(productoActualizado)
                                Toast.makeText(
                                    context,
                                    "Producto agregado exitosamente",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            // Volver atrás después de un breve delay usando el scope correcto
                            scope.launch {
                                delay(500)
                                onNavigateBack()
                            }
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
                            Text("Guardando...")
                        } else {
                            Text(if (isEditing) "Guardar Cambios" else "Agregar Producto")
                        }
                    }

                    // Botón de cancelar
                    TextButton(
                        onClick = { onNavigateBack() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    ) {
                        Text("Cancelar")
                    }

                    // Nota informativa si estamos editando
                    if (isEditing) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "ℹ️ Nota",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "La actualización de productos solo afecta la copia local. " +
                                            "El backend aún no tiene endpoint para actualizar productos.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}