package com.example.storecomponents

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.example.storecomponents.view.GestionVentasScreen
import com.example.storecomponents.view.cliente.ClienteOrdersScreen
import com.example.storecomponents.view.RegisterScreen

import com.example.storecomponents.view.GestionUsuarioScreen
import com.example.storecomponents.view.LoginScreen
import com.example.storecomponents.view.AppShell
import com.example.storecomponents.navigation.Screen
import androidx.fragment.app.FragmentActivity
import com.example.storecomponents.view.GestionProductoScreen
import com.example.storecomponents.view.cliente.ClienteProductosScreen
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.storecomponents.view.cliente.ClienteProductoDetailScreen
import com.example.storecomponents.view.cliente.PerfilScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.storecomponents.data.repository.PerfilRepository
import com.example.storecomponents.viewmodel.PerfilviewModel
import com.example.storecomponents.view.cliente.CartScreen

class MainActivity : FragmentActivity() {
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Inicializar el store local de autenticación para persistencia
        com.example.storecomponents.data.local.LocalAuthStore.init(applicationContext)
        setContent {
            StorecomponentsTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val context = LocalContext.current
                // Crear repositorio y ViewModel de productos compartido para evitar crear ViewModel sin factory en pantallas hijas
                val productoRepository = com.example.storecomponents.data.repository.ProductoRepository(context)
                val productoViewModel: com.example.storecomponents.viewmodel.ProductoViewModel = viewModel(factory = com.example.storecomponents.viewmodel.ProductoViewModelFactory(productoRepository))

                // Helper seguro para navegar desde callbacks externos (evita que la app se caiga si la ruta es inválida)
                val safeNavigate: (String) -> Unit = { route ->
                    try {
                        Log.d("Nav", "intentando navegar a: $route")
                        navController.navigate(route)
                        Log.d("Nav", "navegación exitosa a: $route")
                    } catch (e: Exception) {
                        Log.e("Nav", "navegación fallida a: $route", e)
                        // Mostrar feedback visible para facilitar depuración en dispositivo
                        Toast.makeText(context, "Navegación fallida a: $route: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }

                // Crear repo + viewmodel para perfil y compartirlo con la pantalla de perfil
                val perfilRepo = PerfilRepository(context)
                val perfilViewModel: PerfilviewModel = viewModel(factory = PerfilviewModel.Factory(perfilRepo))

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
                /// Usar AppShell como el Scaffold principal (evita doble topBar)
                // Ocultar la topBar global en pantallas que ya insertan un header dentro del contenido
                val showTopBar = when (currentRoute) {
                    Screen.adminmenu.route, Screen.agregarProducto.route, Screen.editarProducto.route -> false
                    else -> true
                }
                // Usar el helper seguro para navegación
                AppShell(currentRoute = currentRoute, onNavigate = safeNavigate, showTopBar = showTopBar) { appPadding ->
                     // Usar el padding que provee AppShell para el NavHost
                     NavHost(
                         navController = navController,
                         startDestination = Screen.login.route,
                         modifier = Modifier.fillMaxSize().padding(appPadding)
                     ) {
                        composable(Screen.login.route) {
                            LoginScreen(
                                onLogin = { emailOrUser, password ->
                                    // intentar login por username o email (método flexible)
                                    authViewModel.loginByUsername(emailOrUser, password)
                                },
                                onRegister = { navController.navigate(Screen.register.route) }
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
                                onNavigate = safeNavigate,
                                onLogout = {
                                    authViewModel.cerrarSesion()
                                    // logout usa ruta estática, mantener comportamiento
                                    navController.navigate(Screen.login.route) {
                                        popUpTo(Screen.login.route) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable(Screen.adminmenu.route) {
                            AdminMenuScreen(
                                onNavigate = safeNavigate,
                                onLogout = {
                                    authViewModel.cerrarSesion()
                                    navController.navigate(Screen.login.route) {
                                        popUpTo(Screen.login.route) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable(Screen.productos.route) {
                            // Mostrar la lista de productos: cliente ve ClienteProductosScreen, admin ve GestionProductoScreen
                            val roleInner by authViewModel.role.collectAsState()
                            if (roleInner == com.example.storecomponents.viewmodel.UserRole.CLIENT) {
                                ClienteProductosScreen(onNavigate = safeNavigate, productoViewModel = productoViewModel)
                            } else {
                                GestionProductoScreen(productoId = null, productoViewModel = productoViewModel, onNavigateBack = { navController.popBackStack() })
                            }
                        }
                        // Rutas para crear/editar producto desde este NavHost
                        composable(Screen.agregarProducto.route) {
                            GestionProductoScreen(productoId = null, onNavigateBack = { navController.popBackStack() })
                        }
                        composable(
                            Screen.editarProducto.route,
                            arguments = listOf(navArgument("productoId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val productoId = backStackEntry.arguments?.getString("productoId")
                            GestionProductoScreen(productoId = productoId, onNavigateBack = { navController.popBackStack() })
                        }
                        composable(Screen.usuarios.route) {
                            GestionUsuarioScreen(onNavigate = safeNavigate)
                        }
                        composable(Screen.Pedidos.route) {
                            // Mostrar la pantalla adecuada según el role actual
                            val roleInner by authViewModel.role.collectAsState()
                            if (roleInner == com.example.storecomponents.viewmodel.UserRole.CLIENT) {
                                ClienteOrdersScreen(onNavigate = safeNavigate)
                            } else {
                                GestionVentasScreen(onNavigate = safeNavigate)
                            }
                        }
                        composable(Screen.perfil.route) {
                            PerfilScreen(viewModel = perfilViewModel)
                        }
                        // Ruta para el carrito del cliente
                        composable(Screen.carrito.route) {
                            CartScreen(onCheckout = {
                                // después de confirmar, navegar a Pedidos (ruta estática)
                                safeNavigate(Screen.Pedidos.route)
                            })
                        }
                         composable(
                             Screen.detalle.route,
                             arguments = listOf(navArgument("productoId") { type = NavType.StringType })
                         ) { backStackEntry ->
                             val productoId = backStackEntry.arguments?.getString("productoId")
                             ClienteProductoDetailScreen(productoId = productoId, productoViewModel = productoViewModel, onBack = { navController.popBackStack() })
                         }

                     } // fin NavHost
                  } // fin AppShell
                 } // fin StorecomponentsTheme
             } // fin setContent
     } // fin onCreate
 } // fin MainActivity
