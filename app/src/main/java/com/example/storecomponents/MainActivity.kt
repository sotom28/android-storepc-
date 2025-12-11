package com.example.storecomponents

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.storecomponents.viewmodel.AuthViewModel
import com.example.storecomponents.viewmodel.ProductoViewModel
import com.example.storecomponents.viewmodel.CarritoViewModel
import com.example.storecomponents.ui.theme.StorecomponentsTheme
import com.example.storecomponents.view.AdminMenuScreen
import com.example.storecomponents.view.cliente.ClienteMenuScreen
import com.example.storecomponents.view.ProductoScreen
import com.example.storecomponents.view.ProductoDetalleScreen
import com.example.storecomponents.view.CarritoScreen
import com.example.storecomponents.view.GestionVentasScreen
import com.example.storecomponents.view.cliente.ClienteOrdersScreen
import com.example.storecomponents.view.RegisterScreen
import com.example.storecomponents.view.GestionUsuarioScreen
import com.example.storecomponents.view.LoginScreen
import com.example.storecomponents.view.AppShell
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import com.example.storecomponents.navigation.Screen
import androidx.fragment.app.FragmentActivity
import com.example.storecomponents.view.GestionProductoScreen

class MainActivity : FragmentActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    private val productoViewModel: ProductoViewModel by viewModels()
    private val carritoViewModel: CarritoViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StorecomponentsTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val context = LocalContext.current

                // Observar rol para navegar automáticamente cuando cambie (login/registro)
                val role by authViewModel.role.collectAsState()
                LaunchedEffect(role) {
                    when (role) {
                        com.example.storecomponents.viewmodel.UserRole.ADMIN -> {
                            navController.navigate(Screen.adminmenu.route) {
                                popUpTo(Screen.login.route) { inclusive = true }
                            }
                        }
                        com.example.storecomponents.viewmodel.UserRole.CLIENT -> {
                            navController.navigate(Screen.clienteMenu.route) {
                                popUpTo(Screen.login.route) { inclusive = true }
                            }
                        }
                        else -> { /* no-op */ }
                    }
                }

                // Mostrar Toasts según estado de autenticación para ayudar a depurar botones de prueba
                val estadoAuth by authViewModel.estadoAuth.collectAsState()
                LaunchedEffect(estadoAuth) {
                    when (estadoAuth) {
                        is com.example.storecomponents.viewmodel.EstadoAuth.Exito -> {
                            val user = (estadoAuth as com.example.storecomponents.viewmodel.EstadoAuth.Exito).usuario
                            Toast.makeText(context, "Login OK: ${user.nombre}", Toast.LENGTH_SHORT).show()
                        }
                        is com.example.storecomponents.viewmodel.EstadoAuth.Error -> {
                            val msg = (estadoAuth as com.example.storecomponents.viewmodel.EstadoAuth.Error).mensaje
                            Toast.makeText(context, "Login failed: $msg", Toast.LENGTH_SHORT).show()
                        }
                        else -> { /* no-op */ }
                    }
                }

                AppShell(currentRoute = currentRoute, onNavigate = { route -> navController.navigate(route) }) { padding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.login.route,
                        modifier = Modifier.padding(padding)
                    ) {
                        composable(Screen.login.route) {
                            LoginScreen(
                                onLogin = { emailOrUser, password ->
                                    authViewModel.loginByUsername(emailOrUser, password)
                                },
                                onRegister = { navController.navigate(Screen.register.route) },
                                onNavigateToProducts = { navController.navigate("productos") }
                            )
                        }
                        composable(Screen.register.route) {
                            RegisterScreen(onRegistered = {
                                navController.popBackStack()
                            }, authViewModel = authViewModel)
                        }
                        composable(Screen.clienteMenu.route) {
                            ClienteMenuScreen(
                                onNavigate = { route -> navController.navigate(route) },
                                onLogout = {
                                    authViewModel.cerrarSesion()
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
                                    authViewModel.cerrarSesion()
                                    navController.navigate(Screen.login.route) {
                                        popUpTo(Screen.login.route) { inclusive = true }
                                    }
                                }
                            )
                        }

                        // RUTA DE PRODUCTOS - Pasando ViewModel y callbacks
                        composable("productos") {
                            ProductoScreen(
                                viewModel = productoViewModel,
                                onNavigateToCarrito = {
                                    navController.navigate("carrito")
                                },
                                onNavigateToDetalle = { producto ->
                                    navController.navigate("producto_detalle/${producto.id}")
                                }
                            )
                        }

                        composable(
                            route = "producto_detalle/{productoId}",
                            arguments = listOf(navArgument("productoId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val productoId = backStackEntry.arguments?.getString("productoId")
                            // Usar el repository directamente para buscar el producto
                            val producto = productoId?.let {
                                productoViewModel.productos.collectAsState().value.find { it.id == productoId }
                            }

                            if (producto != null) {
                                ProductoDetalleScreen(
                                    producto = producto,
                                    onNavigateBack = {
                                        navController.popBackStack()
                                    },
                                    onNavigateToCarrito = {
                                        navController.navigate("carrito")
                                    }
                                )
                            } else {
                                // Pantalla de error o volver atrás
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text("Producto no encontrado")
                                    Text("ID: $productoId", style = MaterialTheme.typography.bodySmall)
                                    Button(onClick = { navController.popBackStack() }) {
                                        Text("Volver")
                                    }
                                }
                            }
                        }

                        composable(Screen.gestionProductos.route) {
                            GestionProductoScreen(
                                productoViewModel = productoViewModel,
                                productoId = null,
                                onNavigateBack = { navController.popBackStack() },
                                currentRoute = Screen.gestionProductos.route,
                                onNavigate = { route -> navController.navigate(route) }
                            )
                        }

                        composable("carrito") {
                            CarritoScreen(
                                viewModel = carritoViewModel,
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable(Screen.usuarios.route) {
                            GestionUsuarioScreen(onNavigate = { route -> navController.navigate(route) })
                        }
                        composable(Screen.Pedidos.route) {
                            val roleInner by authViewModel.role.collectAsState()
                            if (roleInner == com.example.storecomponents.viewmodel.UserRole.CLIENT) {
                                ClienteOrdersScreen(onNavigate = { route -> navController.navigate(route) })
                            } else {
                                GestionVentasScreen(onNavigate = { route -> navController.navigate(route) })
                            }
                        }
                    }
                }
            }
        }
    }
}