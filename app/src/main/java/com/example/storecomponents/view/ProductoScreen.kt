package com.example.storecomponents.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.storecomponents.data.model.Producto
import com.example.storecomponents.viewmodel.CartViewModel

@Composable
fun ProductoScreen(
    producto: Producto,
    cartViewModel: CartViewModel = viewModel(),
    onAddToCart: (producto: Producto, quantity: Int) -> Unit = { _, _ -> }
) {
    var quantity by rememberSaveable { mutableStateOf(1) }

    // limitar cantidad entre 1 y stock (si stock > 0)
    val maxQuantity = if (producto.stock > 0) producto.stock else 1
    if (quantity > maxQuantity) quantity = maxQuantity

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(text = producto.nombre, style = MaterialTheme.typography.headlineSmall)

        Text(text = producto.descripcion, style = MaterialTheme.typography.bodyMedium)
        Text(text = "Precio: $${"%.2f".format(producto.precio)}", style = MaterialTheme.typography.bodyMedium)

        if (producto.stock > 0) {
            Text(text = "Stock disponible: ${producto.stock}")
        } else {
            Text(text = "Agotado", color = Color.Red)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { if (quantity > 1) quantity-- }
            ) {
                Text(text = "-")
            }

            Text(text = quantity.toString(), modifier = Modifier.padding(horizontal = 8.dp))

            IconButton(
                onClick = { if (quantity < maxQuantity) quantity++ }
            ) {
                Text(text = "+")
            }
        }

        Button(
            onClick = {
                // agregar al carrito usando el ViewModel y callback opcional
                if (producto.stock > 0) {
                    cartViewModel.add(producto.id, producto.nombre, producto.precio, quantity)
                    onAddToCart(producto, quantity)
                }
            },
            enabled = producto.stock > 0,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (producto.stock > 0) "Agregar al carrito" else "Agotado")
        }
    }
}

