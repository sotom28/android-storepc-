package com.example.storecomponents.navigation
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

sealed class Screen(val route: String){
    object clienteMenu : Screen(route = "clienteMenu")
    object adminmenu  : Screen(route = "adminMenu")
    object ProductoList : Screen(route = "productoList")
    object cart : Screen(route = "cart")
    object login : Screen(route = "login")
    object register : Screen(route = "register")
    object usuarios : Screen(route = "usuarios")
    object productos : Screen(route = "productos")
    object carrito : Screen(route = "carrito")
    object detalle : Screen(route = "detalle")

    // Rutas para el CRUD de productos
    object agregarProducto : Screen(route = "agregarProducto")
    object editarProducto : Screen(route = "editarProducto/{productoId}")

    // Agregadas rutas para menú de cliente
    object Pedidos : Screen(route = "pedidos")
    object Datos : Screen(route = "datos")




}