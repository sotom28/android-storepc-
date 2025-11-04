package com.example.storecomponents.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.storecomponents.data.model.Producto

@Composable
fun ProductListScreen(onOpenProduct: (Producto) -> Unit = {}) {
    val sample = listOf(
        Producto(id = "1", nombre = "PLACA MADRE ASUS TUF ", descripcion = "placa madre x570 am4 ", precio = 359.99, stock = 5),
        Producto(id = "2", nombre = "Procesador", descripcion = "Ryzen 7", precio = 399.99, stock = 2),
        Producto(id = "3", nombre = "memoria ramm 32gb ", descripcion = "ram 32gb x2 16gb 4000mhz", precio = 89.99, stock = 10)
    )

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(sample) { product ->
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

