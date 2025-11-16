package com.example.storecomponents.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.storecomponents.data.model.Producto
import com.example.storecomponents.viewmodel.CarritoViewModel
import com.example.storecomponents.viewmodel.EstadoCarrito
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoDetalleScreen(
    producto: Producto,
    carritoViewModel: CarritoViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToCarrito: () -> Unit
) {
    var cantidad by remember { mutableStateOf(1) }
    val estadoCarrito by carritoViewModel.estadoCarrito.collectAsState()
    val carrito by carritoViewModel.carrito.collectAsState()

    // Mostrar Snackbar cuando se agrega al carrito
    LaunchedEffect(estadoCarrito) {
        if (estadoCarrito is EstadoCarrito.Exito) {
            kotlinx.coroutines.delay(2000)
            carritoViewModel.limpiarEstado()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles del Producto") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    // Botón de carrito en el TopBar
                    BadgedBox(
                        badge = {
                            if (carrito.cantidadTotal > 0) {
                                Badge {
                                    Text("${carrito.cantidadTotal}")
                                }
                            }
                        }
                    ) {
                        IconButton(onClick = onNavigateToCarrito) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito")
                        }
                    }
                }
            )
        },
        bottomBar = {
            BottomAddToCartBar(
                precio = producto.precio,
                cantidad = cantidad,
                onAgregarAlCarrito = {
                    carritoViewModel.agregarProducto(producto, cantidad)
                },
                isLoading = estadoCarrito is EstadoCarrito.Cargando,
                stockDisponible = producto.stock
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Imagen del producto
                AsyncImage(
                    model = producto.imagenUrl.ifEmpty { "https://via.placeholder.com/400" },
                    contentDescription = producto.nombre,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Categoría
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = producto.categoria,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Nombre del producto
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Precio
                Text(
                    text = formatearPrecio(producto.precio),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Stock disponible
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Disponibilidad:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = if (producto.estaDisponible()) {
                            "${producto.stock} unidades disponibles"
                        } else {
                            "Agotado"
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (producto.estaDisponible()) {
                            Color(0xFF4CAF50)
                        } else {
                            MaterialTheme.colorScheme.error
                        },
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                HorizontalDivider()

                Spacer(modifier = Modifier.height(16.dp))

                // Selector de cantidad
                Text(
                    text = "Cantidad",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(12.dp))

                CantidadSelector(
                    cantidad = cantidad,
                    onCantidadChange = { cantidad = it },
                    maxStock = producto.stock
                )

                Spacer(modifier = Modifier.height(24.dp))

                HorizontalDivider()

                Spacer(modifier = Modifier.height(16.dp))

                // Descripción
                Text(
                    text = "Descripción",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = producto.descripcion,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Espacio para el bottom bar
                Spacer(modifier = Modifier.height(100.dp))
            }

            // Snackbar para mensajes
            if (estadoCarrito is EstadoCarrito.Exito) {
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    action = {
                        TextButton(onClick = onNavigateToCarrito) {
                            Text("VER CARRITO")
                        }
                    }
                ) {
                    Text((estadoCarrito as EstadoCarrito.Exito).mensaje)
                }
            }

            if (estadoCarrito is EstadoCarrito.Error) {
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    action = {
                        TextButton(onClick = { carritoViewModel.limpiarEstado() }) {
                            Text("OK")
                        }
                    }
                ) {
                    Text((estadoCarrito as EstadoCarrito.Error).mensaje)
                }
            }
        }
    }
}

@Composable
fun CantidadSelector(
    cantidad: Int,
    onCantidadChange: (Int) -> Unit,
    maxStock: Int
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FilledIconButton(
            onClick = { if (cantidad > 1) onCantidadChange(cantidad - 1) },
            enabled = cantidad > 1
        ) {
            Text("-", style = MaterialTheme.typography.titleLarge)
        }

        Surface(
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "$cantidad",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 12.dp)
            )
        }

        FilledIconButton(
            onClick = { if (cantidad < maxStock) onCantidadChange(cantidad + 1) },
            enabled = cantidad < maxStock
        ) {
            Icon(Icons.Default.Add, contentDescription = "Aumentar")
        }
    }
}

@Composable
fun BottomAddToCartBar(
    precio: Double,
    cantidad: Int,
    onAgregarAlCarrito: () -> Unit,
    isLoading: Boolean,
    stockDisponible: Int
) {
    Surface(
        tonalElevation = 8.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatearPrecio(precio * cantidad),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Button(
                onClick = onAgregarAlCarrito,
                enabled = !isLoading && stockDisponible > 0,
                modifier = Modifier.height(56.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null)
                        Text("Agregar al carrito")
                    }
                }
            }
        }
    }
}

private fun formatearPrecio(precio: Double): String {
    val formato = NumberFormat.getCurrencyInstance(Locale.US)
    return formato.format(precio)
}

@Preview(showBackground = true)
@Composable
fun ProductoDetalleScreenPreview() {
    val productoDeEjemplo = Producto(
        id = "1",
        nombre = "Laptop Gamer de Última Generación",
        descripcion = "Una laptop potente para todo tipo de tareas, desde gaming hasta desarrollo. Equipada con lo último en tecnología.",
        precio = 1499.99,
        stock = 10,
        categoria = "Portátiles",
        imagenUrl = ""
    )
    ProductoDetalleScreen(
        producto = productoDeEjemplo,
        onNavigateBack = {},
        onNavigateToCarrito = {}
    )
}
