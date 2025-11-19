package com.example.storecomponents.view

import android.annotation.SuppressLint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue



import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionProductoScreen(
    productoViewModel: ProductoViewModel = viewModel(),
    productoId: String?,
    onNavigateBack: () -> Unit,
    currentRoute: String? = "productos", // Mantenemos por compatibilidad con BottomBar
    onNavigate: (String) -> Unit = {}
) {
    // Determina si estamos en modo edición o creación
    val isEditing = productoId != null
    val title = if (isEditing) "Editar Producto" else "Agregar Producto"

    // 2. Usamos la clase de estado para el formulario
    var formState by remember { mutableStateOf(FormState()) }

    // Obtenemos el producto solo una vez si estamos en modo edición
    LaunchedEffect(productoId) {
        if (isEditing) {
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
        topBar = {
            // 4. TopBar con botón de navegación para volver atrás
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver atrás")
                    }
                }
            )
        },
        bottomBar = {
            // La BottomBar puede ser opcional aquí si interfiere con la tarea de edición
            AppBottomBar(currentRoute = currentRoute, onNavigate = onNavigate)
        },
        floatingActionButton = {
            // El FAB ahora solo aparece si estás editando, para permitir crear uno nuevo
            if (isEditing) {
                FloatingActionButton(onClick = {
                    // Limpiamos el ID y el estado para pasar a modo "Crear Producto"
                    // Esto requeriría que el ID en la navegación sea nulable.
                    // Una mejor opción sería navegar a la misma pantalla sin ID.
                    // Por ahora, lo dejamos como una limpieza simple del formulario actual:
                    formState = FormState()
                    // Idealmente, se debería navegar a la ruta de creación.
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Crear Nuevo Producto")
                }
            }
        }
    ) { padding ->
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
                        val idToUse = if (isEditing) (productoId ?: UUID.randomUUID().toString()) else UUID.randomUUID().toString()
                        val productoParaGuardar = formState.toProducto(id = idToUse)
                        if (isEditing) {
                            productoViewModel.actualizarProducto(productoParaGuardar)
                        } else {
                            productoViewModel.agregarProducto(productoParaGuardar)
                        }
                        onNavigateBack() // Volvemos a la pantalla anterior tras guardar
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
        }
    }
}
