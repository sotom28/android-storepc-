package com.example.storecomponents.view.cliente

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import com.example.storecomponents.viewmodel.CartViewModel
import com.example.storecomponents.viewmodel.OrdersViewModel
import com.example.storecomponents.viewmodel.StoreViewModel

@Composable
fun CartScreen(
    onCheckout: () -> Unit = {}
) {
    val activity = LocalContext.current as FragmentActivity
    val cartViewModel: CartViewModel = viewModel(activity)
    val ordersViewModel: OrdersViewModel = viewModel(activity)
    val storeViewModel: StoreViewModel = viewModel(activity)
    val items = cartViewModel.items

    Log.d("CarritoScreen", "items in cart = ${items.map { it.productoId + ":" + it.cantidad }}")
    // Tasa de IVA (configurable)
    val IVA_RATE = 0.19

    // Cálculos
    val computedSubtotal = items.sumOf { it.precio * it.cantidad }
    val iva = computedSubtotal * IVA_RATE
    val total = computedSubtotal + iva
    val totalCantidad = cartViewModel.itemCount()

    Log.d("CarritoScreen", "computedSubtotal=$computedSubtotal, iva=$iva, total=$total, totalCantidad=$totalCantidad")

    var showPayment by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
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
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (item.imagenUrl.isNotBlank()) {
                            AsyncImage(
                                model = item.imagenUrl,
                                contentDescription = item.nombre,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(6.dp)),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(6.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No img",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = item.nombre, style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = "Precio: $${"%.2f".format(item.precio)}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        IconButton(onClick = {
                            cartViewModel.updateQuantity(
                                item.productoId,
                                item.cantidad - 1
                            )
                        }) {
                            Text(text = "-")
                        }

                        Text(
                            text = item.cantidad.toString(),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        IconButton(onClick = {
                            cartViewModel.updateQuantity(
                                item.productoId,
                                item.cantidad + 1
                            )
                        }) {
                            Text(text = "+")
                        }

                        IconButton(onClick = { cartViewModel.remove(item.productoId) }) {
                            Text(text = "Eliminar")
                        }
                    }
                }
            }
        }

        // Resumen de totales
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Cantidad total: $totalCantidad",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Valor neto: $${"%.2f".format(computedSubtotal)}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "IVA (${(IVA_RATE * 100).toInt()}%): $${"%.2f".format(iva)}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Total: $${"%.2f".format(total)}",
                style = MaterialTheme.typography.titleMedium
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = { cartViewModel.clear() }) {
                Text(text = "Vaciar carrito")
            }
            Button(onClick = { showPayment = true }) {
                Text(text = "Confirmar productos")
            }
        }
    }

    if (showPayment) {
        PaymentDialog(
            onDismiss = { showPayment = false },
            onConfirm = { method, cardInfo ->
                // Crear órdenes por cada item
                val currentBuyer = storeViewModel.users.firstOrNull { it.role == "cliente" }
                val buyerId = currentBuyer?.id
                if (items.isEmpty()) {
                    Toast.makeText(activity, "El carrito está vacío", Toast.LENGTH_SHORT).show()
                    showPayment = false
                    return@PaymentDialog
                }
                items.forEach { item ->
                    val masked = cardInfo?.number?.takeLast(4)?.let { " - Tarjeta ****$it" } ?: ""
                    ordersViewModel.addOrder(
                        productName = item.nombre,
                        quantity = item.cantidad,
                        price = item.precio,
                        descripcion = "Pago: $method$masked",
                        buyerId = buyerId
                    )
                }
                // Opcional: podríamos guardar detalles de la tarjeta en descripción (no recomendado en producción)
                cartViewModel.clear()
                Toast.makeText(activity, "Pago registrado y órdenes creadas", Toast.LENGTH_SHORT).show()
                showPayment = false
                onCheckout()
            }
        )
    }
}

@Composable
private fun PaymentDialog(
    onDismiss: () -> Unit,
    onConfirm: (method: String, cardInfo: CardInfo?) -> Unit
) {
    var selectedMethod by remember { mutableStateOf("Efectivo") }
    var cardNumber by remember { mutableStateOf("") }
    var cardHolder by remember { mutableStateOf("") }
    var expiry by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var cuotas by remember { mutableStateOf("") }


    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Método de pago") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = selectedMethod == "Efectivo", onClick = { selectedMethod = "Efectivo" })
                    Text(text = "Efectivo")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = selectedMethod == "Tarjeta Crédito", onClick = { selectedMethod = "Tarjeta Crédito" })
                    Text(text = "Tarjeta Crédito")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = selectedMethod == "Tarjeta Débito", onClick = { selectedMethod = "Tarjeta Débito" })
                    Text(text = "Tarjeta Débito")
                }

                if (selectedMethod != "Efectivo") {
                    OutlinedTextField(
                        value = cardNumber,
                        onValueChange = { cardNumber = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Número de tarjeta") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = cardHolder,
                        onValueChange = { cardHolder = it },
                        label = { Text("Nombre en tarjeta") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = expiry,
                            onValueChange = { expiry = it },
                            label = { Text("MM/AA") },
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = cvv,
                            onValueChange = { cvv = it.filter { ch -> ch.isDigit() } },
                            label = { Text("CVV") },
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = cuotas,
                            onValueChange = { cuotas = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Cuotas") },
                            modifier = Modifier.weight(1f)

                        )





                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                // Validaciones básicas
                if (selectedMethod != "Efectivo") {
                    if (cardNumber.length < 12) {
                        // mostrar error simple
                        return@Button
                    }
                }
                val cardInfo = if (selectedMethod == "Efectivo") null else CardInfo(cardNumber, cardHolder, expiry, cvv)
                onConfirm(selectedMethod, cardInfo)
            }) { Text("Confirmar pago") }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

private data class CardInfo(val number: String, val holder: String, val expiry: String, val cvv: String)
