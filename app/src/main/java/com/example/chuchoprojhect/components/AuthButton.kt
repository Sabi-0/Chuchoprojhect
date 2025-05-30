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
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit = {}
) {
    val user by authViewModel.userState.collectAsState()

    if (user != null) {
        Button(
            onClick = { authViewModel.signOut() },
            modifier = modifier,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Text("Cerrar sesión")
        }
    } else {
        Button(
            onClick = onLoginClick,
            modifier = modifier
        ) {
            Text("Iniciar sesión")
        }
    }
}