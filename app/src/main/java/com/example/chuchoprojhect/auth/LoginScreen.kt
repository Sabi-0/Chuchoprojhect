package com.example.chuchoprojhect.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chuchoprojhect.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.GoogleAuthProvider
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation

//mostramos la pantalla de login
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel, // ViewModel que maneja la autenticación
    navController: NavController, // Controlador de navegación
    onDismiss: () -> Unit // Callback que se llama cuando el login o registro tiene éxito (para cerrar esta pantalla)
) {

    // Estado local para email, contraseña, modo de registro y mensaje de error
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isSignUp by remember { mutableStateOf(false) }// true = registrarse, false = iniciar sesión
    var errorMessage by remember { mutableStateOf<String?>(null) }


    // Layout principal
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título dinámico dependiendo si es registro o login
        Text(
            text = if (isSignUp) "Registrarse" else "Iniciar sesión",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de texto para el email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Campo de texto para la contraseña
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        // Mostrar mensaje de error si existe
        errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón principal (Iniciar sesión o Registrarse según el modo)
        Button(
            onClick = {
                // Validación
                if (email.isBlank() || password.isBlank()) {
                    errorMessage = "Por favor completa todos los campos"
                    return@Button
                }

                // Si está en modo registro
                if (isSignUp) {
                    authViewModel.signUpWithEmail(email, password) { success, message ->
                        if (success) {
                            onDismiss() // Cierra la pantalla si el registro fue exitoso
                        } else {
                            errorMessage = message ?: "Error desconocido"
                        }
                    }
                } else {
                    // Si está en modo login
                    authViewModel.signInWithEmail(email, password) { success, message ->
                        if (success) {
                            onDismiss() // Cierra la pantalla si el login fue exitoso
                        } else {
                            errorMessage = message ?: "Error desconocido"
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isSignUp) "Registrarse" else "Iniciar sesión")
        }

        Spacer(modifier = Modifier.height(8.dp))


        // Botón para alternar entre registro e inicio de sesión
        TextButton(
            onClick = { isSignUp = !isSignUp },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (isSignUp) "¿Ya tienes cuenta? Inicia sesión"
                else "¿No tienes cuenta? Regístrate"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para iniciar sesión como invitado
        Button(
            onClick = {
                authViewModel.signInAnonymously { success, message ->
                    if (success) {
                        onDismiss()
                    } else {
                        errorMessage = message ?: "Error al entrar como invitado"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continuar como invitado")
        }
    }
}