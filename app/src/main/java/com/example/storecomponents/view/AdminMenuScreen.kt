package com.example.storecomponents.view

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.storecomponents.navigation.Screen

// Item de menú reutilizable
data class AdminMenuItem(val title: String, val route: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

@Composable
fun AdminMenuScreen(
    menuItems: List<AdminMenuItem> = listOf(
        AdminMenuItem("Gestionar productos", Screen.productos.route, Icons.Filled.ShoppingCart),
        AdminMenuItem("Gestión de Ventas", Screen.Pedidos.route, Icons.AutoMirrored.Filled.List),
        AdminMenuItem("Usuarios", Screen.usuarios.route, Icons.Filled.Person)
    ),
    bottomNavItems: List<AdminMenuItem> = listOf(
        // Cambiado: usar la ruta existente Screen.adminmenu.route en vez de "home"
        AdminMenuItem("Inicio", Screen.adminmenu.route, Icons.Filled.ShoppingCart),
        AdminMenuItem("Gestión de Ventas", Screen.Pedidos.route, Icons.AutoMirrored.Filled.List),
        AdminMenuItem("Usuarios", Screen.usuarios.route, Icons.Filled.Person)
    ),
    menuTitle: String = "Menú Administrador",
    initialSelectedRoute: String? = null,
    showLogout: Boolean = true,
    onNavigate: (String) -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val items = menuItems

    var selectedRoute by remember { mutableStateOf(initialSelectedRoute) }
    val context = LocalContext.current

    // Gradiente para la TopBar
    val topGradient = Brush.horizontalGradient(
        colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
    )

    // Usamos el AppShell global para mostrar la topBar/bottomBar; insertamos un header visual en el contenido
    Scaffold() { padding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 16.dp, vertical = 12.dp)) {

            // Header visual replicando el estilo anterior (gradiente + título + logout)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .clip(RoundedCornerShape(bottomStart = 14.dp, bottomEnd = 14.dp)),
                color = MaterialTheme.colorScheme.surface
            ) {
                Box(modifier = Modifier
                    .background(topGradient)
                    .fillMaxSize(), contentAlignment = Alignment.CenterStart) {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = menuTitle,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        if (showLogout) {
                            IconButton(onClick = onLogout) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar sesión", tint = MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Acciones rápidas",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(items) { item ->
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // asignar selección y navegar sin bloquear por falta de gestor
                                selectedRoute = item.route
                                onNavigate(item.route)
                                // feedback rápido para confirmar el click y ruta (diagnóstico)
                                Log.d("AdminMenu", "navegar a ruta=${item.route} titulo=${item.title}")
                                Toast.makeText(context, "Navegando a: ${item.route} (${item.title})", Toast.LENGTH_SHORT).show()
                            }
                    ) {
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
                                Icon(item.icon, contentDescription = item.title, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = item.title, style = MaterialTheme.typography.titleMedium)
                                Text(text = when (item.title) {
                                    "Gestionar productos" -> "Crear, editar y eliminar productos"
                                    "Gestión de Ventas" -> "Ver y gestionar ventas"
                                    "Usuarios" -> "Administrar usuarios y roles"
                                    else -> ""
                                }, style = MaterialTheme.typography.bodySmall, fontSize = 12.sp)
                            }

                            TextButton(onClick = {
                                // navegar directamente sin bloquear por falta de gestor
                                selectedRoute = item.route
                                onNavigate(item.route)
                                Log.d("AdminMenu", "navegar a ruta=${item.route} titulo=${item.title}")
                                Toast.makeText(context, "Navegando a: ${item.route} (${item.title})", Toast.LENGTH_SHORT).show()
                            }) { Text("Abrir") }
                         }
                     }
                 }

                 item {
                     Spacer(modifier = Modifier.height(12.dp))
                     if (showLogout) {
                         Button(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
                             Text(text = "Cerrar sesión")
                         }
                     }
                 }
             }
         }
     }
 }
