package com.example.chuchoprojhect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.chuchoprojhect.auth.AuthViewModel
import com.example.chuchoprojhect.auth.LoginScreen
import com.example.chuchoprojhect.auth.ProfileScreen
import com.example.chuchoprojhect.components.AuthButton
import com.example.chuchoprojhect.screen.AgregarPerroScreen
import com.example.chuchoprojhect.screen.PantallaListaPerros
import com.example.chuchoprojhect.ui.theme.ChuchoprojhectTheme
import com.example.chuchoprojhect.util.guardarPerroEnFirestore
import com.example.chuchoprojhect.util.subirImagenAFirebase
import com.google.firebase.FirebaseApp
import android.util.Log
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inicializar Firebase
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContent {
            MyApp()
        }
    }
}

@Composable
fun MyApp() {
    // Controla si el tema oscuro está activado
    var darkModeEnabled by remember { mutableStateOf(false) }

    ChuchoprojhectTheme(darkTheme = darkModeEnabled) { // Aplica el tema
        Surface(modifier = Modifier.fillMaxSize()) {
            val authViewModel: AuthViewModel = viewModel() // ViewModel para autenticación

            AppNavigation( // Llama a la navegación principal de la app
                authViewModel = authViewModel,
                darkModeEnabled = darkModeEnabled,
                onToggleDarkMode = { enabled -> darkModeEnabled = enabled }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
    darkModeEnabled: Boolean,
    onToggleDarkMode: (Boolean) -> Unit
) {
    val navController = rememberNavController() // Crea el controlador de navegación
    var showLoginDialog by remember { mutableStateOf(false) } // Estado para mostrar login
    val user by authViewModel.userState.collectAsState() // Escucha el usuario autenticado

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Adopta un perrito") },
                actions = {
                    // Botón de login/logout en la barra superior
                    AuthButton(
                        authViewModel = authViewModel,
                        modifier = Modifier.padding(end = 8.dp),
                        onLoginClick = { showLoginDialog = true }
                    )
                }
            )
        },
        // Barra inferior de navegación
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->

        if (showLoginDialog) {
            AlertDialog(
                onDismissRequest = { showLoginDialog = false },
                title = { Text("Autenticación") },
                text = {
                    LoginScreen(
                        authViewModel = authViewModel,
                        navController = navController,
                        onDismiss = { showLoginDialog = false }
                    )
                },
                confirmButton = {},
                dismissButton = {}
            )
        }

        NavHost(
            navController = navController,
            startDestination = Screen.Screen1.route, // Ruta inicial
            modifier = Modifier.padding(paddingValues)
        ) {

            // Pantalla principal: lista de perros
            composable(Screen.Screen1.route) {
                PantallaListaPerros(authViewModel = authViewModel)
            }
            // Pantalla para agregar nuevo perro
            composable(Screen.Screen2.route) {
                AgregarPerroScreen { nombre, descripcion, lugar, contacto, raza, uri, limpiarCampos ->
                    subirImagenAFirebase(
                        uri,
                        onSuccess = { imageUrl ->
                            guardarPerroEnFirestore(
                                user = user,
                                nombre = nombre,
                                descripcion = descripcion,
                                lugar = lugar,
                                contacto = contacto,
                                raza = raza,
                                imageUrl = imageUrl,
                                onSuccess = {
                                    // Limpia el formulario
                                    limpiarCampos()
                                    Log.d("Firebase", "Perro guardado exitosamente.")
                                    navController.popBackStack()
                                },
                                onFailure = {
                                    Log.e("Firebase", "Error al guardar datos en Firestore.")
                                }
                            )
                        },
                        onFailure = {
                            Log.e("Firebase", "Error al subir imagen.")
                        }
                    )
                }
            }
            // Pantalla de perfil
            composable(Screen.Screen3.route) {
                ProfileScreen(
                    authViewModel = authViewModel,
                    darkModeEnabled = darkModeEnabled,
                    onToggleDarkMode = onToggleDarkMode,
                    onSignOut = {
                        authViewModel.signOut()
                        navController.navigate(Screen.Screen1.route) {
                            popUpTo(Screen.Screen1.route) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

//definimos las rutas de navegación
sealed class Screen(val route: String, val title: String) {
    object Screen1 : Screen("screen1", "Inicio")
    object Screen2 : Screen("screen2", "Publicar")
    object Screen3 : Screen("screen3", "Perfil")

    companion object {
        val items = listOf(Screen1, Screen2, Screen3)
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {

    // Obtiene la entrada actual del back stack de navegación (pantalla actual)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

// Componente que dibuja la barra inferior de navegación (Material 3)
    NavigationBar {
        // Itera sobre la lista de pantallas definidas en Screen.items
        Screen.items.forEach { screen ->
            NavigationBarItem(
                // Icono para cada ítem según la pantalla
                icon = {
                    Icon(
                        painter = painterResource(
                            when (screen) {
                                Screen.Screen1 -> android.R.drawable.ic_menu_view // Icono para "Inicio"
                                Screen.Screen2 -> android.R.drawable.ic_menu_add // Icono para "Publicar"
                                Screen.Screen3 -> android.R.drawable.ic_menu_my_calendar // Icono para "Perfil"
                            }
                        ),
                        contentDescription = screen.title
                    )
                },
                label = { Text(screen.title) }, // Texto debajo del icono
                selected = currentRoute == screen.route, // Resalta el ítem si es la ruta actual
                onClick = {

                    // Navega a la ruta correspondiente al hacer clic
                    navController.navigate(screen.route) {
                        // Elimina pantallas anteriores hasta la raíz, pero guarda su estado
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true // Evita duplicar la misma pantalla en el stack
                        restoreState = true  // Restaura el estado guardado si estaba en back stack
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    MyApp()
}
