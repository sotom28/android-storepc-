package com.example.storecomponents.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.storecomponents.data.model.Producto
import com.example.storecomponents.viewmodel.ProductoViewModel

@Composable
fun ProductListScreen(onOpenProduct: (Producto) -> Unit = {}, productoViewModel: ProductoViewModel = viewModel()) {
    val productosState = productoViewModel.productos.collectAsState()
    val productos = productosState.value

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(productos) { product ->
            Column(modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpenProduct(product) }
                .padding(vertical = 8.dp)
            ) {
                Text(text = product.nombre)
                Text(text = "Precio: $${"%.2f".format(product.precio)}")
            }
        }
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