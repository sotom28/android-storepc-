package com.example.storecomponents.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.storecomponents.navigation.Screen
import com.example.storecomponents.viewmodel.CartViewModel

@Composable
fun CartScreen(
    cartViewModel: CartViewModel = viewModel(),
    onCheckout: () -> Unit = {}
) {
    val items = cartViewModel.items

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Carrito", style = MaterialTheme.typography.headlineSmall)

        if (items.isEmpty()) {
            Text(text = "El carrito está vacío")
            return@Column
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(items) { item ->
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = item.nombre, style = MaterialTheme.typography.titleMedium)
                        Text(text = "Precio: $${"%.2f".format(item.precio)}", style = MaterialTheme.typography.bodyMedium)
                    }

                    IconButton(onClick = { cartViewModel.updateQuantity(item.productoId, item.cantidad - 1) }) {
                        Text(text = "-")
                    }

                    Text(text = item.cantidad.toString(), modifier = Modifier.padding(horizontal = 8.dp))

                    IconButton(onClick = { cartViewModel.updateQuantity(item.productoId, item.cantidad + 1) }) {
                        Text(text = "+")
                    }

                    IconButton(onClick = { cartViewModel.remove(item.productoId) }) {
                        Text(text = "Eliminar")
                    }
                }
            }
        }

        Text(text = "Total: $${"%.2f".format(cartViewModel.total())}")

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { cartViewModel.clear() }) {
                Text(text = "Vaciar carrito")
            }
            Button(onClick = onCheckout) {
                Text(text = "Comprar")
            }
        }
    }
}
