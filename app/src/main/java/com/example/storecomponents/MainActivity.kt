package com.example.storecomponents

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.storecomponents.viewmodel.AuthViewModel
import com.example.storecomponents.ui.theme.StorecomponentsTheme
import com.example.storecomponents.view.AdminMenuScreen
import com.example.storecomponents.view.cliente.ClienteMenuScreen
import com.example.storecomponents.view.cliente.ProductoScreen // Importa la pantalla de productos
import com.example.storecomponents.view.ProductListScreen
import com.example.storecomponents.view.GestionVentasScreen
import com.example.storecomponents.view.cliente.ClienteOrdersScreen
import com.example.storecomponents.view.RegisterScreen

import com.example.storecomponents.view.GestionUsuarioScreen
import com.example.storecomponents.view.LoginScreen
import com.example.storecomponents.view.AppShell
import com.example.storecomponents.navigation.Screen
import androidx.fragment.app.FragmentActivity

class MainActivity : FragmentActivity() {
    private val authViewModel: AuthViewModel by viewModels()

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

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppShell(currentRoute = currentRoute, onNavigate = { route -> navController.navigate(route) }) { padding ->
                        NavHost(
                            navController = navController,
                            startDestination = Screen.login.route,
                            modifier = Modifier.padding(padding)
                        ) {
                            composable(Screen.login.route) {
                                LoginScreen(
                                    onLogin = { emailOrUser, password ->
                                        // intentar login por username o email (método flexible)
                                        authViewModel.loginByUsername(emailOrUser, password)
                                    },
                                    onRegister = { navController.navigate(Screen.register.route) },
                                    onNavigateToProducts = { navController.navigate("productos") } // Navega a la pantalla de productos
                                )
                            }
                            composable(Screen.register.route) {
                                RegisterScreen(onRegistered = {
                                    // después de registrar volvemos al login (el registro puede auto-logear)
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
                            composable("productos") { // Añade esta ruta
                                ProductoScreen()
                            }
                            composable(Screen.usuarios.route) {
                                GestionUsuarioScreen(onNavigate = { route -> navController.navigate(route) })
                            }
                            composable(Screen.Pedidos.route) {
                                // Mostrar la pantalla adecuada según el role actual
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
}
