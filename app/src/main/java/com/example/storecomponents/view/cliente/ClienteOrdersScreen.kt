package com.example.storecomponents.view.cliente

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.storecomponents.viewmodel.OrdersViewModel
import com.example.storecomponents.viewmodel.UsersViewModel

@Composable
fun ClienteOrdersScreen(
    onNavigate: (String) -> Unit = {},
    ordersViewModel: OrdersViewModel = viewModel(),
    usersViewModel: UsersViewModel = viewModel()
) {
    val context = LocalContext.current

    // Simulamos que el primer usuario con role 'cliente' es el actual en este ejemplo.
    // En una app real, deberías obtener el userId del AuthViewModel o similar.
    val currentBuyer = usersViewModel.users.firstOrNull { it.role == "cliente" }
    val buyerId = currentBuyer?.id

    var productName by remember { mutableStateOf("") }
    var quantityText by remember { mutableStateOf("1") }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

        Text(text = "Mis Compras", style = MaterialTheme.typography.titleLarge)

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Crear Compra", style = MaterialTheme.typography.titleSmall)

                OutlinedTextField(
                    value = productName,
                    onValueChange = { productName = it },
                    label = { Text("Nombre del Producto") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = quantityText,
                    onValueChange = { quantityText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Cantidad") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        if (productName.isBlank()) {
                            Toast.makeText(context, "Ingrese el nombre del producto", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val qty = quantityText.toIntOrNull() ?: 1
                        if (qty <= 0) {
                            Toast.makeText(context, "La cantidad debe ser mayor a 0", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        ordersViewModel.addOrder(productName.trim(), qty, null, buyerId)
                        Toast.makeText(context, "Compra registrada", Toast.LENGTH_SHORT).show()
                        productName = ""
                        quantityText = "1"
                    }, modifier = Modifier.weight(1f)) {
                        Text("Comprar")
                    }

                    OutlinedButton(onClick = { onNavigate("cliente") }, modifier = Modifier.weight(1f)) {
                        Text("Volver")
                    }
                }
            }
        }

        Text(text = "Historial de Compras", style = MaterialTheme.typography.titleMedium)

        val myOrders = ordersViewModel.orders.filter { it.buyerId == buyerId }

        if (myOrders.isEmpty()) {
            Text(text = "No tienes compras", style = MaterialTheme.typography.bodySmall)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(myOrders) { o ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = "#${o.id} - ${o.productName}", style = MaterialTheme.typography.titleMedium)
                            Text(text = "Cantidad: ${o.quantity}")
                            Text(text = "Estado: ${o.status}")
                        }
                    }
                }
            }
        }
    }
}

