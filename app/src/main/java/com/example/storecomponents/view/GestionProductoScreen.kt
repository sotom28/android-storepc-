package com.example.storecomponents.view

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue



import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.storecomponents.data.model.Producto
import com.example.storecomponents.viewmodel.ProductoViewModel
import java.util.UUID

// 1. Clase de datos para gestionar el estado del formulario
private data class FormState(
    val nombre: String = "",
    val descripcion: String = "",
    val precio: String = "",
    val stock: String = "",
    val categoria: String = "",
    val imagenUrl: String = "",
) {
    fun toProducto(id: String): Producto {
        return Producto(
            id = id,
            nombre = nombre,
            descripcion = descripcion,
            precio = precio.toDoubleOrNull() ?: 0.0,
            stock = stock.toIntOrNull() ?: 0,
            categoria = categoria,
            imagenUrl = imagenUrl
        )
    }
}

// Función para convertir un Producto a FormState
private fun Producto.toFormState(): FormState {
    return FormState(
        nombre = this.nombre,
        descripcion = this.descripcion,
        precio = this.precio.toString(),
        stock = this.stock.toString(),
        categoria = this.categoria,
        imagenUrl = this.imagenUrl
    )
}


@SuppressLint("UnrememberedMutableState")
@Suppress("UNUSED_PARAMETER")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionProductoScreen(
    productoViewModel: ProductoViewModel = viewModel(),
    productoId: String?,
    onNavigateBack: () -> Unit,
    currentRoute: String? = "productos", // Mantenemos por compatibilidad con BottomBar
    onNavigate: (String) -> Unit = {}
) {
    // Determina si estamos en modo edición o creación.
    // Usamos un estado local `editingId` para permitir editar productos desde la lista sin depender exclusivamente del parámetro de navegación.
    var editingId by remember { mutableStateOf(productoId) }
    // Hacemos que isEditing sea reactivo para que cambie cuando editingId se actualiza
    val isEditing by derivedStateOf { editingId != null }
    val title = if (isEditing) "Editar Producto" else "Agregar Producto"

    // 2. Usamos la clase de estado para el formulario
    var formState by remember { mutableStateOf(FormState()) }

    // Observamos la lista de productos desde el ViewModel
    val productos by productoViewModel.productos.collectAsState(initial = emptyList<Producto>())

    // Obtenemos el producto solo una vez si estamos en modo edición desde la ruta
    LaunchedEffect(productoId) {
        if (productoId != null) {
            editingId = productoId
            productoViewModel.productos.value.find { it.id == productoId }?.let { productoEncontrado ->
                formState = productoEncontrado.toFormState()
            }
        }
    }

    // 3. Validación de campos (ejemplo simple)
    val isFormValid by derivedStateOf {
        formState.nombre.isNotBlank() &&
                formState.precio.toDoubleOrNull() != null &&
                formState.stock.toIntOrNull() != null
    }

    Scaffold(
        // Top bar global provista por AppShell; aquí insertamos el header visual dentro del contenido
        floatingActionButton = {
            // El FAB ahora sirve para limpiar el formulario y salir del modo edición local
            FloatingActionButton(onClick = {
                formState = FormState()
                editingId = null
            }) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo Producto")
            }
        }
    ) { padding ->
        // Header visual (gradiente con título y botón volver) para mantener apariencia sin duplicar TopBar
        // Obtener colores del tema antes de la lambda de remember (MaterialTheme.* es @Composable)
        val colors = MaterialTheme.colorScheme
        // Definimos el Brush una sola vez y lo recordamos para evitar recomposiciones costosas
        val topGradient = remember {
            Brush.horizontalGradient(listOf(colors.primary, colors.secondary))
        }
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .clip(RoundedCornerShape(bottomStart = 14.dp, bottomEnd = 14.dp)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Box(modifier = Modifier
                .background(topGradient)
                .fillMaxSize(), contentAlignment = Alignment.CenterStart) {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver atrás")
                    }
                    Text(text = title, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 5. Usamos LazyColumn para que el formulario sea desplazable
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            item {
                OutlinedTextField(
                    value = formState.nombre,
                    onValueChange = { formState = formState.copy(nombre = it) },
                    label = { Text("Nombre del Producto *") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = formState.nombre.isBlank() // Validación visual
                )
            }
            item {
                OutlinedTextField(
                    value = formState.descripcion,
                    onValueChange = { formState = formState.copy(descripcion = it) },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
            item {
                OutlinedTextField(
                    value = formState.precio,
                    onValueChange = { formState = formState.copy(precio = it) },
                    label = { Text("Precio *") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = formState.precio.toDoubleOrNull() == null
                )
            }
            item {
                OutlinedTextField(
                    value = formState.stock,
                    onValueChange = { formState = formState.copy(stock = it) },
                    label = { Text("Stock *") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = formState.stock.toIntOrNull() == null
                )
            }
            item {
                OutlinedTextField(
                    value = formState.categoria,
                    onValueChange = { formState = formState.copy(categoria = it) },
                    label = { Text("Categoría") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                OutlinedTextField(
                    value = formState.imagenUrl,
                    onValueChange = { formState = formState.copy(imagenUrl = it) },
                    label = { Text("URL de la Imagen") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                Button(
                    onClick = {
                        // Generar id al crear; si estamos editando usamos el id existente
                        val idToUse = if (isEditing) editingId!! else UUID.randomUUID().toString()
                        val productoParaGuardar = formState.toProducto(id = idToUse)
                        if (isEditing) {
                            productoViewModel.actualizarProducto(productoParaGuardar)
                        } else {
                            productoViewModel.agregarProducto(productoParaGuardar)
                        }
                        // No navegamos atrás para que el usuario pueda seguir añadiendo/editar.
                        // Limpiamos el formulario y salimos del modo edición local.
                        formState = FormState()
                        editingId = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    enabled = isFormValid // 6. El botón se activa solo si el formulario es válido
                ) {
                    Text(if (isEditing) "Guardar Cambios" else "Agregar Producto")
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Lista de productos guardados debajo del formulario
            item {
                Text(text = "Productos guardados", style = MaterialTheme.typography.titleMedium)
            }

            // Mostrar cada producto con acciones editar/eliminar
            items(items = productos) { p ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = p.nombre, style = MaterialTheme.typography.titleSmall)
                            Text(text = p.descripcion, style = MaterialTheme.typography.bodySmall)
                            Text(text = "Precio: ${p.precio} · Stock: ${p.stock}", style = MaterialTheme.typography.bodySmall)
                        }
                        Row {
                            IconButton(onClick = {
                                // Poner el producto en modo edición rellenando el formulario
                                editingId = p.id
                                formState = p.toFormState()
                            }) {
                                Icon(Icons.Filled.Edit, contentDescription = "Editar")
                            }
                            IconButton(onClick = { productoViewModel.eliminarProducto(p.id) }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Eliminar")
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}
