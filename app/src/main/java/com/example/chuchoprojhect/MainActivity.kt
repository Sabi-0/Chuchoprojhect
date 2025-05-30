package com.example.chuchoprojhect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import com.google.firebase.auth.FirebaseUser
import android.util.Log

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContent {
            MyApp()
        }
    }
}

@Composable
fun MyApp() {
    ChuchoprojhectTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize()) {
            val authViewModel: AuthViewModel = viewModel()
            AppNavigation(authViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    var showLoginDialog by remember { mutableStateOf(false) }
    val user by authViewModel.userState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Adopta un perrito") },
                actions = {
                    AuthButton(
                        authViewModel = authViewModel,
                        modifier = Modifier.padding(end = 8.dp),
                        onLoginClick = { showLoginDialog = true }
                    )
                }
            )
        },
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
            startDestination = Screen.Screen1.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Screen1.route) { Screen1() }
            composable(Screen.Screen2.route) {
                AgregarPerroScreen { nombre, descripcion, lugar, contacto, uri ->
                    subirImagenAFirebase(uri,
                        onSuccess = { imageUrl ->
                            guardarPerroEnFirestore(nombre, descripcion, lugar, contacto, imageUrl,
                                onSuccess = {
                                    Log.d("Firebase", "Perro guardado exitosamente.")
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
            composable(Screen.Screen3.route) {
                if (user != null) {
                    ProfileScreen(authViewModel = authViewModel, user = user)
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Debes iniciar sesión para ver tu perfil")
                            Button(onClick = { showLoginDialog = true }) {
                                Text("Iniciar sesión")
                            }
                        }
                    }
                }
            }
        }
    }
}

sealed class Screen(val route: String, val title: String) {
    object Screen1 : Screen("screen1", "Inicio")
    object Screen2 : Screen("screen2", "Publicar")
    object Screen3 : Screen("screen3", "Perfil")

    companion object {
        val items = listOf(Screen1, Screen2, Screen3)
    }
}

@Composable
fun Screen1() {
    PantallaListaPerros()
}

@Composable
fun Screen2() {
    Text(
        text = "Publicar nuevo perro",
        modifier = Modifier.fillMaxSize(),
        style = MaterialTheme.typography.headlineLarge
    )
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    BottomAppBar {
        Screen.items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(android.R.drawable.ic_dialog_info),
                        contentDescription = screen.title
                    )
                },
                label = { Text(screen.title) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
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