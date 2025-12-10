package com.example.storecomponents.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.storecomponents.navigation.Screen
import com.example.storecomponents.viewmodel.OrdersViewModel
import com.example.storecomponents.viewmodel.StoreViewModel
import kotlinx.coroutines.launch
import java.util.*

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.geometry.Offset
import androidx.compose.runtime.snapshotFlow

@Composable
fun GestionVentasScreen(
    onNavigate: (String) -> Unit = {},
    ordersViewModel: OrdersViewModel = viewModel<OrdersViewModel>(),
    storeViewModel: StoreViewModel = viewModel<StoreViewModel>()
) {
    val context = LocalContext.current

    // Datos
    val orders = ordersViewModel.orders
    val manager = storeViewModel.getSalesManager()

    // Form state
    var productName by remember { mutableStateOf("") }
    var quantityText by remember { mutableStateOf("1") }
    var priceText by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    var editingOrderId by remember { mutableStateOf<Long?>(null) }
    var editingProductName by remember { mutableStateOf("") }
    var editingQuantity by remember { mutableStateOf("") }
    var editingPriceText by remember { mutableStateOf("") }
    var editingDescripcion by remember { mutableStateOf("") }

    // Estados disponibles
    val estadosPedido = listOf("PENDIENTE", "ENVIADO", "ENTREGADO", "CANCELADO")

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Título de la pantalla
        Text(text = "Gestión de Ventas", style = MaterialTheme.typography.titleLarge)

        if (manager == null) {
            Text(text = "No hay gestor de ventas asignado. Asigna uno en Usuarios para procesar ventas.", style = MaterialTheme.typography.bodyMedium)
        } else {
            Text(text = "Gestor activo: ${manager.name}", style = MaterialTheme.typography.bodyMedium)
        }

        // Form para crear venta
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
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text("Precio") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
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
                        val price = priceText.toDoubleOrNull() ?: 0.0
                        if (price < 0.0) {
                            Toast.makeText(context, "Precio inválido", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val assignedId = manager?.id
                        ordersViewModel.addOrder(productName.trim(), qty, assignedId, price, descripcion)
                        Toast.makeText(context, "Venta creada exitosamente", Toast.LENGTH_SHORT).show()
                        productName = ""
                        quantityText = "1"
                        priceText = ""
                        descripcion = ""
                    }, modifier = Modifier.weight(1f)) {
                        Text("Crear Venta")
                    }

                    OutlinedButton(onClick = { onNavigate(Screen.adminmenu.route) }, modifier = Modifier.weight(1f)) {
                        Text("Volver")
                    }
                }
            }
        }

        // Separador
        HorizontalDivider()

        // Lista de ventas
        Text(text = "Lista de Ventas (${orders.size})", style = MaterialTheme.typography.titleMedium)

        if (orders.isEmpty()) {
            Text(text = "No hay ventas registradas", style = MaterialTheme.typography.bodySmall)
        } else {
            // Calcular total
            val total = orders.sumOf { it.quantity * it.price }

            // Añadimos un estado de lista para controlar el scroll y construir la barra
            val listState = rememberLazyListState()
            val scope = rememberCoroutineScope()
            var overlayHeightPx by remember { mutableStateOf(0) }

            // Estados para el pulgar (thumb) — calculados fuera de la composición
            val thumbHeightPxState = remember { mutableStateOf(0) }
            val thumbYState = remember { mutableStateOf(0) }

            // Actualizar thumb cuando cambie el layoutInfo
            LaunchedEffect(listState, overlayHeightPx) {
                snapshotFlow { listState.layoutInfo }
                    .collect { layoutInfo ->
                        val totalItems = layoutInfo.totalItemsCount
                        val visibleItems = layoutInfo.visibleItemsInfo.size
                        val firstIndex = layoutInfo.visibleItemsInfo.firstOrNull()?.index ?: 0

                        if (totalItems > 0 && overlayHeightPx > 0) {
                            val thumbFraction = (visibleItems.toFloat() / totalItems).coerceIn(0.05f, 1f)
                            val positionFraction = if (totalItems > visibleItems) firstIndex.toFloat() / (totalItems - visibleItems) else 0f
                            val thPx = (overlayHeightPx * thumbFraction).toInt().coerceAtLeast(20)
                            val ty = (positionFraction * (overlayHeightPx - thPx)).toInt().coerceIn(0, overlayHeightPx - thPx)
                            thumbHeightPxState.value = thPx
                            thumbYState.value = ty
                        } else {
                            thumbHeightPxState.value = 0
                            thumbYState.value = 0
                        }
                    }
            }

            // Contenedor que reserva espacio y permite overlay de la barra
            Box(modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
            ) {
                LazyColumn(state = listState, verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxSize().padding(end = 28.dp)) {
                    items(orders, key = { it.id }) { o ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                // Información de la venta
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "#${o.id} - ${o.productName}", style = MaterialTheme.typography.titleMedium)
                                    Surface(
                                        color = when (o.status) {
                                            "PENDIENTE" -> MaterialTheme.colorScheme.secondary
                                            "PROCESANDO" -> MaterialTheme.colorScheme.tertiaryContainer
                                            "EN_CAMINO" -> MaterialTheme.colorScheme.primary
                                            "PAGADO" -> MaterialTheme.colorScheme.secondaryContainer
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
                                Text(text = "Precio unitario: ${String.format(Locale.getDefault(), "%.2f", o.price)}")
                                Text(text = "Subtotal: ${String.format(Locale.getDefault(), "%.2f", o.quantity * o.price)}")
                                if (o.descripcion.isNotBlank()) {
                                    Text(text = "Descripción: ${o.descripcion}")
                                }

                                val assignedName = o.assignedToId?.let { storeViewModel.users.firstOrNull { u -> u.id == it }?.name } ?: "Sin asignar"
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
                                            editingPriceText = o.price.toString()
                                            editingDescripcion = o.descripcion
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

                    // Mostrar total al final
                    item {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        Text(text = "Total: ${String.format(Locale.getDefault(), "%.2f", total)}", style = MaterialTheme.typography.titleMedium)
                    }
                }

                // Overlay: barra visible a la derecha
                Box(modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.CenterEnd)
                    .width(28.dp)
                    .onGloballyPositioned { overlayHeightPx = it.size.height }
                    // Drag para desplazar arrastrando el pulgar
                    .pointerInput(Unit) {
                        detectVerticalDragGestures { _, dragAmount ->
                            // No consumimos explícitamente el cambio aquí (evita APIs no resueltas); dejamos que el sistema maneje el consumo.
                            // change.consumePositionChange()
                            val layoutInfo = listState.layoutInfo
                            val totalItems = layoutInfo.totalItemsCount
                            val visibleItems = layoutInfo.visibleItemsInfo.size
                            if (totalItems > 0 && overlayHeightPx > 0) {
                                val maxFirstIndex = (totalItems - visibleItems).coerceAtLeast(0)
                                // convertir delta px a fracción relativa
                                val fraction = (dragAmount / overlayHeightPx).coerceIn(-1f, 1f)
                                val deltaFirst = (fraction * maxFirstIndex).toInt()
                                val currentFirst = layoutInfo.visibleItemsInfo.firstOrNull()?.index ?: 0
                                val target = (currentFirst + deltaFirst).coerceIn(0, maxFirstIndex)
                                scope.launch { listState.scrollToItem(target) }
                              }
                          }
                    }
                    .pointerInput(Unit) {
                        detectTapGestures { offset: Offset ->
                            val layoutInfo = listState.layoutInfo
                            val totalItems = layoutInfo.totalItemsCount
                            val visibleItems = layoutInfo.visibleItemsInfo.size
                            val firstIndex = layoutInfo.visibleItemsInfo.firstOrNull()?.index ?: 0
                            if (totalItems > 0 && overlayHeightPx > 0) {
                                val maxFirstIndex = (totalItems - visibleItems).coerceAtLeast(0)
                                val positionFraction = (offset.y / overlayHeightPx).coerceIn(0f, 1f)
                                val targetFirstIndex = (positionFraction * maxFirstIndex).toInt()
                                val targetIndex = (firstIndex + targetFirstIndex).coerceIn(0, totalItems - 1)
                                scope.launch { listState.animateScrollToItem(targetIndex) }
                            }
                        }
                    }
                    .padding(end = 4.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    // Dibujar pulgar (thumb) si hay elementos
                    val thumbHeightPx = thumbHeightPxState.value
                    val thumbY = thumbYState.value

                    if (thumbHeightPx > 0 && overlayHeightPx > 0) {
                        val density = LocalDensity.current
                        val thumbHeightDp = (thumbHeightPx / density.density).dp
                        Box(modifier = Modifier
                            .offset { IntOffset(0, thumbY) }
                            .width(6.dp)
                            .height(thumbHeightDp)
                            .background(Color.Gray.copy(alpha = 0.7f), shape = RoundedCornerShape(6.dp))
                        )
                    }
                }
            }
        }
    }

    // Diálogo para editar venta
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
                    OutlinedTextField(
                        value = editingPriceText,
                        onValueChange = { editingPriceText = it },
                        label = { Text("Precio") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editingDescripcion,
                        onValueChange = { editingDescripcion = it },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val newQty = editingQuantity.toIntOrNull()
                    val newPrice = editingPriceText.toDoubleOrNull() ?: 0.0
                    if (editingProductName.isNotBlank() && newQty != null && newQty > 0 && newPrice >= 0.0) {
                        ordersViewModel.updateOrder(editingOrderId!!, editingProductName, newQty, newPrice, editingDescripcion)
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
