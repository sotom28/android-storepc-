package com.example.storecomponents.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.storecomponents.navigation.Screen

data class MenuItem(val title: String, val route: String)


@Composable
fun ClienteMenuScreen(onNavigate: (String) -> Unit = {}, onLogout: () -> Unit = {}) {
    val items = listOf(
        MenuItem("Productos", Screen.ProductoList.route),
        MenuItem("Mi Carrito", Screen.carrito.route),
        MenuItem("Mis Pedidos", Screen.Pedidos.route),
        MenuItem("Mi Perfil", Screen.Datos.route),
        MenuItem("Gestión de Productos", "gestion_productos")
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Menú Cliente",
            modifier = Modifier.padding(bottom = 12.dp),
            style = MaterialTheme.typography.titleLarge
        )

        LazyColumn(modifier = Modifier.fillMaxHeight()) {
            items(items) { item ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            println("ClienteMenu: clic en ${item.title} -> ${item.route}")
                            onNavigate(item.route)
                        }
                        .padding(vertical = 12.dp)
                ) {
                    Text(text = item.title)
                }
            }
        }

        Button(onClick = { onLogout() }, modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
            Text(text = "Cerrar sesión")
        }
    }
}