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


@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    navController: NavController,
    onDismiss: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isSignUp by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isSignUp) "Registrarse" else "Iniciar sesión",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    errorMessage = "Por favor completa todos los campos"
                    return@Button
                }

                if (isSignUp) {
                    authViewModel.signUpWithEmail(email, password) { success, message ->
                        if (success) {
                            onDismiss()
                        } else {
                            errorMessage = message ?: "Error desconocido"
                        }
                    }
                } else {
                    authViewModel.signInWithEmail(email, password) { success, message ->
                        if (success) {
                            onDismiss()
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