package com.example.storecomponents.view.cliente

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.storecomponents.viewmodel.OrdersViewModel
import com.example.storecomponents.viewmodel.StoreViewModel

@Composable
fun MisPedidos(
    ordersViewModel: OrdersViewModel = viewModel(),
    storeViewModel: StoreViewModel = viewModel()
) {
    val currentBuyer = storeViewModel.users.firstOrNull { it.role == "cliente" }
    val buyerId = currentBuyer?.id

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Mis Pedidos", style = MaterialTheme.typography.titleLarge)

        val myOrders = ordersViewModel.orders.filter { it.buyerId == buyerId }

        if (myOrders.isEmpty()) {
            Text(text = "No tienes pedidos", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 12.dp))
        } else {
            LazyColumn(modifier = Modifier.padding(top = 12.dp)) {
                items(myOrders) { o ->
                    Card(modifier = Modifier
                        .padding(vertical = 6.dp)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = "#${o.id} - ${o.productName}", style = MaterialTheme.typography.titleMedium)
                            Text(text = "Cantidad: ${o.quantity}", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "Precio unitario: $${"%.2f".format(o.price)}", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "Estado: ${o.status}", style = MaterialTheme.typography.bodyMedium)

                        }
                    }
                }
            }
        }
    }
}