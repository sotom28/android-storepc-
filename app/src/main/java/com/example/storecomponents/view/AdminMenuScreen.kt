package com.example.storecomponents.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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

data class AdminMenuItem(val title: String, val route: String)

@Composable
fun AdminMenuScreen(onNavigate: (String) -> Unit, onLogout: () -> Unit = {}) {
    val items = listOf(
        AdminMenuItem("Gestionar productos", Screen.productos.route),
        AdminMenuItem("Pedidos", Screen.Pedidos.route),
        AdminMenuItem("Usuarios", Screen.usuarios.route)
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Menú Administrador", modifier = Modifier.padding(bottom = 12.dp), style = MaterialTheme.typography.titleLarge)

        LazyColumn {
            items(items) { item ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigate(item.route) }
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
