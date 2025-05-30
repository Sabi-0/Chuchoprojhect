package com.example.chuchoprojhect.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.chuchoprojhect.components.AuthButton
import com.google.firebase.auth.FirebaseUser

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    user: FirebaseUser?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (user != null) {
            if (user.isAnonymous) {
                Text("Modo invitado", style = MaterialTheme.typography.headlineMedium)
                Text("Funcionalidad limitada", style = MaterialTheme.typography.bodyMedium)
            } else {
                Text("Perfil de ${user.email}", style = MaterialTheme.typography.headlineMedium)
                // Aquí puedes añadir más información del usuario
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { authViewModel.signOut() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar sesión")
            }
        } else {
            Text("Por favor inicia sesión para ver tu perfil")
        }
    }
}