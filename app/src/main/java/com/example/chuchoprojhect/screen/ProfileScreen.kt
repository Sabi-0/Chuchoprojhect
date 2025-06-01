package com.example.chuchoprojhect.auth

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.chuchoprojhect.util.actualizarPerfilUsuario
import com.example.chuchoprojhect.util.subirImagenAFirebase

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    darkModeEnabled: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
    onSignOut: () -> Unit,
    onImageSelected: (Uri) -> Unit = {},
    onNameChanged: (String) -> Unit = {}

) {
    val user = authViewModel.getCurrentUser()
    val context = LocalContext.current

    var name by remember { mutableStateOf(user?.displayName ?: "") }
    var photoUri by remember { mutableStateOf(user?.photoUrl) }



    // Launcher para abrir galería
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            photoUri = it
            onImageSelected(it)

            // Subir imagen y actualizar perfil
            subirImagenAFirebase(it,
                onSuccess = { imageUrl ->
                    user?.let {
                        actualizarPerfilUsuario(
                            user = it,
                            nombre = name,
                            photoUrl = imageUrl,
                            onSuccess = {},
                            onFailure = {}
                        )
                    }
                },
                onFailure = {}
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Imagen de perfil
        if (photoUri != null) {
            Image(
                painter = rememberAsyncImagePainter(photoUri),
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .clickable { launcher.launch("image/*") },
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Sin foto",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .clickable { launcher.launch("image/*") },
                tint = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Nombre editable
        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                onNameChanged(it)
            },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botón de Cerrar Sesión
        Button(
            onClick = {
                authViewModel.signOut()
                onSignOut()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar sesión")
        }

        // Botón de Modo Oscuro
        Button(
            onClick = { onToggleDarkMode(!darkModeEnabled) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (darkModeEnabled) "Desactivar Modo Oscuro" else "Activar Modo Oscuro")
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}


