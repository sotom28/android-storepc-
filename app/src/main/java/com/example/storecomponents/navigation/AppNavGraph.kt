package com.example.storecomponents.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.storecomponents.view.ProductListScreen
import com.example.storecomponents.view.ProductoScreen
import com.example.storecomponents.view.LoginScreen
import com.example.storecomponents.view.admin.AdminMenuScreen
import com.example.storecomponents.viewmodel.AuthViewModel
import com.example.storecomponents.viewmodel.EstadoAuth
import com.example.storecomponents.viewmodel.CartViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.storecomponents.data.model.Producto

object Routes {
    // mantener PRODUCTO y CART como constantes internas para detalle y carrito
    const val PRODUCTO = "product/{id}"
    const val CART = "cart"
}


@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val cartViewModel: CartViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()

    // pantalla inicial: login
    val startDestination = Screen.login.route

    NavHost(navController = navController, startDestination = startDestination) {
        // Login
        composable(Screen.login.route) {
            // observar el estado de autenticación
            val estado = authViewModel.estadoAuth.collectAsState().value
            val roleState = authViewModel.role.collectAsState().value

            LoginScreen(onLogin = { usernameOrEmail, password ->
                // usar la variante que acepta username o correo
                authViewModel.loginByUsername(usernameOrEmail, password)
            })

            LaunchedEffect(estado) {
                if (estado is EstadoAuth.Exito) {
                    // navegar según el rol observado
                    if (roleState == com.example.storecomponents.viewmodel.UserRole.ADMIN) {
                        navController.navigate(Screen.adminmenu.route) {
                            popUpTo(Screen.login.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.clienteMenu.route) {
                            popUpTo(Screen.login.route) { inclusive = true }
                        }
                    }
                }
            }
        }

        // Menú cliente
        composable(Screen.clienteMenu.route) {
            com.example.storecomponents.view.ClienteMenuScreen(
                onNavigate = { route: String -> navController.navigate(route) },
                onLogout = {
                    authViewModel.cerrarSesion()
                    navController.navigate(Screen.login.route) { popUpTo(Screen.clienteMenu.route) { inclusive = true } }
                }
            )
        }

        // Menú admin
        composable(Screen.adminmenu.route) {
            AdminMenuScreen(
                onNavigate = { route: String -> navController.navigate(route) },
                onLogout = {
                    authViewModel.cerrarSesion()
                    navController.navigate(Screen.login.route) { popUpTo(Screen.adminmenu.route) { inclusive = true } }
                }
            )
        }

        // Rutas adicionales referenciadas por ClienteMenuScreen
        composable(Screen.carrito.route) {
            // reutilizar el CartScreen existente
            com.example.storecomponents.view.CartScreen(cartViewModel = cartViewModel) {
                cartViewModel.clear()
            }
        }

        composable(Screen.Pedidos.route) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Pedidos (placeholder)")
            }
        }

        composable(Screen.Datos.route) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Mi perfil (placeholder)")
            }
        }

        // Lista de productos (ruta desde Screen)
        composable(Screen.ProductoList.route) {
            ProductListScreen(onOpenProduct = { product: Producto ->
                navController.navigate("product/${product.id}")
            })
        }

        // Detalle de producto (usa la constante ROUTE PRODUCTO)
        composable(
            Routes.PRODUCTO,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { navBackStackEntry ->
            val id = navBackStackEntry.arguments?.getString("id") ?: ""
            val product = com.example.storecomponents.data.model.Producto(
                id = id,
                nombre = "Producto $id",
                descripcion = "Descripción del producto $id",
                precio = 9.99,
                stock = 5
            )
            ProductoScreen(producto = product, cartViewModel = cartViewModel) { _, _ ->
                navController.navigate(Routes.CART)
            }
        }

        // Carrito (ruta alternativa "cart")
        composable(Routes.CART) {
            com.example.storecomponents.view.CartScreen(cartViewModel = cartViewModel) {
                cartViewModel.clear()
            }
        }
    }
}
