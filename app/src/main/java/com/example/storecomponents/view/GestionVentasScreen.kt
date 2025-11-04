package com.example.storecomponents.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.storecomponents.viewmodel.OrdersViewModel
import com.example.storecomponents.viewmodel.UsersViewModel

@Composable
fun GestionVentasScreen(
    onNavigate: (String) -> Unit = {},
    ordersViewModel: OrdersViewModel = viewModel(),
    usersViewModel: UsersViewModel = viewModel()
) {
    val context = LocalContext.current

    // Datos
    val orders = ordersViewModel.orders
    val manager = usersViewModel.getSalesManager()

    // Form state
    var productName by remember { mutableStateOf("") }
    var quantityText by remember { mutableStateOf("1") }
    var editingOrderId by remember { mutableStateOf<Long?>(null) }
    var editingProductName by remember { mutableStateOf("") }
    var editingQuantity by remember { mutableStateOf("") }

    // Estados disponibles
    val estadosPedido = listOf("PENDIENTE", "ENVIADO", "ENTREGADO", "CANCELADO")

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Cambiado: título de la pantalla a gestión de ventas
        Text(text = "Gestión de Ventas", style = MaterialTheme.typography.titleLarge)

        if (manager == null) {
            Text(text = "No hay gestor de ventas asignado. Asigna uno en Usuarios para procesar ventas.", style = MaterialTheme.typography.bodyMedium)
        } else {
            Text(text = "Gestor activo: ${manager.name}", style = MaterialTheme.typography.bodyMedium)
        }

        // Form para crear venta (antes pedido)
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Crear Nueva Venta", style = MaterialTheme.typography.titleSmall)

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
                        val assignedId = manager?.id
                        ordersViewModel.addOrder(productName.trim(), qty, assignedId)
                        // Toast actualizado
                        Toast.makeText(context, "Venta creada exitosamente", Toast.LENGTH_SHORT).show()
                        productName = ""
                        quantityText = "1"
                    }, modifier = Modifier.weight(1f)) {
                        Text("Crear Venta")
                    }

                    OutlinedButton(onClick = { onNavigate("admin") }, modifier = Modifier.weight(1f)) {
                        Text("Volver")
                    }
                }
            }
        }

        // Usar HorizontalDivider en lugar de Divider (deprecated -> HorizontalDivider)
        HorizontalDivider()

        // Lista de ventas
        Text(text = "Lista de Ventas (${orders.size})", style = MaterialTheme.typography.titleMedium)

        if (orders.isEmpty()) {
            Text(text = "No hay ventas registradas", style = MaterialTheme.typography.bodySmall)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(orders, key = { it.id }) { o ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Información de la venta (antes pedido)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "#${o.id} - ${o.productName}", style = MaterialTheme.typography.titleMedium)
                                Surface(
                                    color = when (o.status) {
                                        "PENDIENTE" -> MaterialTheme.colorScheme.secondary
                                        "ENVIADO" -> MaterialTheme.colorScheme.tertiary
                                        "ENTREGADO" -> MaterialTheme.colorScheme.primaryContainer
                                        "CANCELADO" -> MaterialTheme.colorScheme.errorContainer
                                        else -> MaterialTheme.colorScheme.surface
                                    },
                                    shape = MaterialTheme.shapes.small,
                                    modifier = Modifier.padding(4.dp)
                                ) {
                                    Text(
                                        text = o.status,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }

                            Text(text = "Cantidad: ${o.quantity}")
                            val assignedName = o.assignedToId?.let { usersViewModel.users.firstOrNull { u -> u.id == it }?.name } ?: "Sin asignar"
                            Text(text = "Asignado a: $assignedName")

                            // Selector de estado
                            var expandedStatus by remember { mutableStateOf(false) }
                            Column {
                                OutlinedButton(
                                    onClick = { expandedStatus = true },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Cambiar estado: ${o.status}")
                                }
                                DropdownMenu(
                                    expanded = expandedStatus,
                                    onDismissRequest = { expandedStatus = false },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    estadosPedido.forEach { estado ->
                                        DropdownMenuItem(
                                            text = { Text(estado) },
                                            onClick = {
                                                ordersViewModel.updateStatus(o.id, estado)
                                                expandedStatus = false
                                                Toast.makeText(context, "Estado actualizado a $estado", Toast.LENGTH_SHORT).show()
                                            }
                                        )
                                    }
                                }
                            }

                            // Acciones (Editar/Eliminar)
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        editingOrderId = o.id
                                        editingProductName = o.productName
                                        editingQuantity = o.quantity.toString()
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .heightIn(min = 40.dp)
                                ) {
                                    Text("Editar")
                                }

                                Button(
                                    onClick = {
                                        ordersViewModel.removeOrder(o.id)
                                        // Toast actualizado
                                        Toast.makeText(context, "Venta eliminada", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .heightIn(min = 40.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Text("Eliminar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Diálogo para editar venta (antes pedido)
    if (editingOrderId != null) {
        AlertDialog(
            onDismissRequest = { editingOrderId = null },
            title = { Text("Editar Venta #${editingOrderId}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editingProductName,
                        onValueChange = { editingProductName = it },
                        label = { Text("Producto") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editingQuantity,
                        onValueChange = { editingQuantity = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Cantidad") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val newQty = editingQuantity.toIntOrNull()
                    if (editingProductName.isNotBlank() && newQty != null && newQty > 0) {
                        ordersViewModel.updateOrder(editingOrderId!!, editingProductName, newQty)
                        // Toast actualizado
                        Toast.makeText(context, "Venta actualizada", Toast.LENGTH_SHORT).show()
                        editingOrderId = null
                    } else {
                        Toast.makeText(context, "Datos inválidos", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { editingOrderId = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
