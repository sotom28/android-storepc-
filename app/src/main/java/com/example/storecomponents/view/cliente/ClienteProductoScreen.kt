package com.example.storecomponents.view.cliente

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.storecomponents.data.repository.ProductoRepository
import com.example.storecomponents.viewmodel.CartViewModel
import com.example.storecomponents.viewmodel.ProductoViewModel
import com.example.storecomponents.data.model.Producto
import com.example.storecomponents.navigation.Screen
import com.example.storecomponents.viewmodel.ProductoViewModelFactory
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ClienteProductosScreen(
    onNavigate: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val productoRepository = remember { ProductoRepository(context) }
    val productoViewModel: ProductoViewModel = viewModel(
        factory = ProductoViewModelFactory(productoRepository)
    )

    // Obtener CartViewModel scoped al Activity para compartir la misma instancia entre pantallas
    val activity = LocalContext.current as FragmentActivity
    val cartViewModel: CartViewModel = viewModel(activity)
    //
    var query by remember { mutableStateOf("") }

    // Obtener lista reactiva desde el ViewModel (StateFlow -> collectAsState)
    val productosState by productoViewModel.productos.collectAsState(initial = emptyList())
    val products: List<Producto> = productosState

    LaunchedEffect(key1 = productoViewModel) {
        productoViewModel.navigateTo.collectLatest { route ->
            onNavigate(route)
        }
    }

    val filtered = remember(products, query) {
        if (query.isBlank()) products
        else products.filter { p ->
            listOfNotNull(p.nombre, p.descripcion).joinToString(" ").contains(query, ignoreCase = true)
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

        // Título con botón al carrito
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Productos", style = MaterialTheme.typography.titleLarge)
            // Mostrar contador del carrito para depuración
            OutlinedButton(onClick = { onNavigate(Screen.carrito.route) }) {
                Text(text = "Carrito (${cartViewModel.itemCount()})")
            }
        }

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Buscar productos") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (filtered.isEmpty()) {
            Text(text = "No hay productos disponibles", style = MaterialTheme.typography.bodySmall)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filtered, key = { it.id }) { product ->
                    Card(modifier = Modifier
                        .fillMaxWidth()
                        .clickable { productoViewModel.onProductSelected(product.id) }
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (product.imagenUrl.isNotBlank()) {
                                    AsyncImage(
                                        model = product.imagenUrl,
                                        contentDescription = product.nombre,
                                        modifier = Modifier
                                            .size(72.dp)
                                            .clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    // Placeholder box
                                    Box(modifier = Modifier.size(72.dp).clip(RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                                        Text(text = "No img", style = MaterialTheme.typography.bodySmall)
                                    }
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = product.nombre, style = MaterialTheme.typography.titleMedium)
                                    Text(text = product.descripcion, style = MaterialTheme.typography.bodySmall)
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(text = "Precio: ${product.precio}", style = MaterialTheme.typography.bodyMedium)
                                    Text(text = "Stock: ${product.stock}", style = MaterialTheme.typography.bodyMedium)
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                val inStock = product.stock > 0
                                Button(
                                    onClick = {
                                        if (!inStock) {
                                            Toast.makeText(context, "Producto sin stock", Toast.LENGTH_SHORT).show()
                                            return@Button
                                        }
                                        // Añadir al carrito usando CartViewModel (activity-scoped)
                                        cartViewModel.add(productId = product.id, name = product.nombre, price = product.precio, qty = 1, imagenUrl = product.imagenUrl)
                                        Toast.makeText(context, "Producto añadido al carrito", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.weight(1f),
                                    enabled = inStock
                                ) {
                                    Text("Comprar")
                                }

                                OutlinedButton(
                                    onClick = {
                                        val id = product.id
                                        if (id.isBlank()) {
                                            Toast.makeText(context, "ID de producto inválido", Toast.LENGTH_SHORT).show()
                                            return@OutlinedButton
                                        }
                                        runCatching { productoViewModel.onProductSelected(id) }
                                            .onFailure { ex ->
                                                Toast.makeText(context, "Error al abrir detalle: ${'$'}{ex.localizedMessage}", Toast.LENGTH_SHORT).show()
                                            }
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Detalle")
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(onClick = { onNavigate(Screen.clienteMenu.route) }, modifier = Modifier.fillMaxWidth()) {
            Text("Volver")
        }
    }
}
