package com.example.chuchoprojhect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.BottomAppBar
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.chuchoprojhect.ui.theme.ChuchoprojhectTheme
import kotlin.collections.listOf
import com.example.chuchoprojhect.screen.AgregarPerroScreen
import com.google.firebase.FirebaseApp
import com.example.chuchoprojhect.util.subirImagenAFirebase
import com.example.chuchoprojhect.util.guardarPerroEnFirestore
import android.util.Log
import com.example.chuchoprojhect.screen.PantallaListaPerros





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
        Surface(modifier = Modifier.fillMaxSize()) {
            AppNavigation()
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
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

            composable(Screen.Screen3.route) { Screen3() }
        }
    }
}

sealed class Screen(val route: String, val title: String) {
    object Screen1 : Screen("screen1", "Inicio")
    object Screen2 : Screen("screen2", "Poner en adopción")
    object Screen3 : Screen("screen3", "Iniciar sesión")

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
        text = "Pantalla 2",
        modifier = Modifier.fillMaxSize(),
        style = MaterialTheme.typography.headlineLarge
    )
}

@Composable
fun Screen3() {
    Text(
        text = "Pantalla 3",
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