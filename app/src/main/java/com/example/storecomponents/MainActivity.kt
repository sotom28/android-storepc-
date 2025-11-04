package com.example.storecomponents

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.storecomponents.auth.AuthViewModel
import com.example.storecomponents.ui.theme.StorecomponentsTheme
import com.example.storecomponents.view.AdminMenuScreen
import com.example.storecomponents.view.ClienteMenuScreen
import com.example.storecomponents.view.ProductListScreen
import com.example.storecomponents.view.GestionVentasScreen

import com.example.storecomponents.view.GestionUsuarioScreen
import com.example.storecomponents.view.LoginScreen
import com.example.storecomponents.navigation.Screen

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StorecomponentsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    val authState by authViewModel.authState.collectAsState()

                    NavHost(
                        navController = navController,
                        startDestination = Screen.login.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.login.route) {
                            LoginScreen(
                                onLogin = { email, password ->
                                    authViewModel.login(email, password)
                                }
                            )
                        }
                        composable(Screen.clienteMenu.route) {
                            ClienteMenuScreen(
                                onNavigate = { route -> navController.navigate(route) },
                                onLogout = {
                                    authViewModel.logout()
                                    navController.navigate(Screen.login.route) {
                                        popUpTo(Screen.login.route) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable(Screen.adminmenu.route) {
                            AdminMenuScreen(
                                onNavigate = { route -> navController.navigate(route) },
                                onLogout = {
                                    authViewModel.logout()
                                    navController.navigate(Screen.login.route) {
                                        popUpTo(Screen.login.route) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable(Screen.productos.route) {
                            // Mostrar la lista de productos
                            ProductListScreen(onOpenProduct = { product -> /* navegar a detalle si está implementado */ })
                        }
                        composable(Screen.usuarios.route) {
                            GestionUsuarioScreen(onNavigate = { route -> navController.navigate(route) })
                        }
                        composable(Screen.Pedidos.route) {
                            GestionVentasScreen(onNavigate = { route -> navController.navigate(route) })
                        }
                    }

                    // Observe auth state changes to navigate
                    when (authState.role) {
                        "admin" -> navController.navigate(Screen.adminmenu.route) {
                            popUpTo(Screen.login.route) { inclusive = true }
                        }
                        "cliente" -> navController.navigate(Screen.clienteMenu.route) {
                            popUpTo(Screen.login.route) { inclusive = true }
                        }
                        else -> {
                            if (authState.error != null) {
                                // Optionally show a toast or a dialog with authState.error
                                println("Authentication error: ${authState.error}")
                            }
                            if (navController.currentBackStackEntry?.destination?.route != Screen.login.route) {
                                navController.navigate(Screen.login.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
