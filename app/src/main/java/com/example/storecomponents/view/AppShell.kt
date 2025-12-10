package com.example.storecomponents.view

import android.media.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.ImageVector

// Item para bottom bar
data class ShellNavItem(val route: String, val label: String, val icon: ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(title: String = "StorePC", showLogo: Boolean = true) {
    CenterAlignedTopAppBar(
        title = {
            Text(text = title, style = MaterialTheme.typography.titleLarge)
        },
        navigationIcon = {
            if (showLogo) {
                Icon(Icons.Default.Home, contentDescription = "Logo", modifier = Modifier.size(36.dp))
            }
        },
        actions = {}
    )
}

@Composable
fun AppBottomBar(currentRoute: String?, onNavigate: (String) -> Unit) {
    val items = listOf(
        ShellNavItem("adminMenu", "Inicio", Icons.Default.Home),
        ShellNavItem("pedidos", "Ventas", Icons.Filled.List),
        ShellNavItem("usuarios", "Usuarios", Icons.Default.Person)
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}

@Composable
fun AppShell(

    currentRoute: String?,
    onNavigate: (String) -> Unit,
    title: String = "StorePC",
    showTopBar: Boolean = true,
    showBottomBar: Boolean = true,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            if (showTopBar) {
                AppTopBar(title = title, showLogo = true)
            }
        },
        bottomBar = {
            if (showBottomBar) AppBottomBar(currentRoute = currentRoute, onNavigate = onNavigate)
        }
    ) { padding ->
        content(padding)
    }
}
