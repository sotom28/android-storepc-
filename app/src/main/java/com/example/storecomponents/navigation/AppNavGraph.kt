package com.example.storecomponents.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.storecomponents.view.* // Importa todas las vistas
import com.example.storecomponents.viewmodel.AuthViewModel
import com.example.storecomponents.viewmodel.CartViewModel

object Routes {
    const val PRODUCTO = "product/{id}"
    const val CART = "cart"
}

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val cartViewModel: CartViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()

    val startDestination = Screen.login.route

    NavHost(navController = navController, startDestination = startDestination) {
        // ... (código de login y menús)

        // Admin: listado de productos (usar la pantalla existente ProductListScreen)
        composable(Screen.productos.route) {
            ProductListScreen(onOpenProduct = { product ->
                // navegar al detalle usando la ruta definida (product/{id})
                navController.navigate("product/${product.id}")
            })
        }

        composable(Screen.agregarProducto.route) {
            GestionProductoScreen(
                productoId = null, // modo "agregar"
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            Screen.editarProducto.route,
            arguments = listOf(navArgument("productoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productoId = backStackEntry.arguments?.getString("productoId")
            GestionProductoScreen(
                productoId = productoId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ... (resto de las rutas)
    }
}
