package com.example.chuchoprojhect.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.chuchoprojhect.R
import com.example.chuchoprojhect.auth.AuthViewModel
import com.example.chuchoprojhect.auth.LoginScreen
import com.google.firebase.auth.FirebaseUser

@Composable
fun AuthButton(
    authViewModel: AuthViewModel, // ViewModel que maneja la autenticación
    modifier: Modifier = Modifier, // Modifier opcional para aplicar estilos
    onLoginClick: () -> Unit = {} // Callback que se ejecuta si el usuario quiere iniciar sesión
) {

    // Se observa el estado del usuario desde el StateFlow del ViewModel
    val user by authViewModel.userState.collectAsState()

    // Si hay un usuario logueado (no es null), se muestra el botón de "Cerrar sesión"
    if (user != null) {
        Button(
            onClick = { authViewModel.signOut() }, // Al presionar, se cierra sesión
            modifier = modifier,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer // Color del botón
            )
        ) {
            Text("Cerrar sesión")
        }
    } else {
        // Si no hay usuario logueado, se muestra el botón de "Iniciar sesión"
        Button(
            onClick = onLoginClick,
            modifier = modifier
        ) {
            Text("Iniciar sesión")
        }
    }
}