package com.example.chuchoprojhect.screen

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.chuchoprojhect.auth.AuthViewModel
import com.example.chuchoprojhect.model.Perro
import com.example.chuchoprojhect.util.obtenerPerrosDesdeFirestore
import com.google.firebase.firestore.FirebaseFirestore
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.draw.clip

@Composable
fun PantallaListaPerros(authViewModel: AuthViewModel = viewModel()) {
    val context = LocalContext.current
    val user by authViewModel.userState.collectAsState()
    var listaPerros by remember { mutableStateOf<List<Perro>>(emptyList()) }
    var filtro by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var perroToDelete by remember { mutableStateOf<Perro?>(null) }

    // Función para eliminar perro
    fun eliminarPerro(perroId: String) {
        FirebaseFirestore.getInstance()
            .collection("perros")
            .document(perroId)
            .delete()
            .addOnSuccessListener {
                listaPerros = listaPerros.filter { it.id != perroId }
            }
            .addOnFailureListener {
                Log.e("Firebase", "Error al eliminar perro", it)
            }
    }

    LaunchedEffect(true) {
        obtenerPerrosDesdeFirestore(
            onSuccess = { perros ->
                listaPerros = perros
                isLoading = false
            },
            onFailure = {
                isLoading = false
            }
        )
    }

    // Filtrado
    val perrosFiltrados = listaPerros.filter { perro ->
        filtro?.let { filtroTexto ->
            perro.lugar.contains(filtroTexto, ignoreCase = true) ||
                    perro.nombre.contains(filtroTexto, ignoreCase = true) ||
                    perro.descripcion.contains(filtroTexto, ignoreCase = true) ||
                    perro.raza.contains(filtroTexto, ignoreCase = true)
        } ?: true
    }

    // Diálogo de confirmación para eliminar
    if (showDeleteDialog && perroToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de eliminar esta publicación?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        eliminarPerro(perroToDelete!!.id)
                        showDeleteDialog = false
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Barra de búsqueda
        OutlinedTextField(
            value = filtro ?: "",
            onValueChange = { filtro = if (it.isEmpty()) null else it },
            label = { Text("Buscar por nombre, raza o ubicación") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            perrosFiltrados.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        if (filtro != null) "No hay perros que coincidan con tu búsqueda"
                        else "No hay perros disponibles para adopción."
                    )
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    items(perrosFiltrados) { perro ->
                        val isOwner = user?.uid == perro.userId ||
                                (user?.isAnonymous == true && perro.userId.isEmpty())

                        DogCard(
                            perro = perro,
                            isOwner = isOwner,
                            onDelete = { perroToDelete = perro; showDeleteDialog = true },
                            onShare = {
                                shareDogInfo(
                                    context = context,
                                    nombre = perro.nombre,
                                    descripcion = perro.descripcion,
                                    lugar = perro.lugar,
                                    contacto = perro.contacto,
                                    imagenUrl = perro.imagenUrl
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DogCard(
    perro: Perro,
    isOwner: Boolean,
    onDelete: () -> Unit,
    onShare: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header con info de usuario
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (perro.usuarioFoto.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(perro.usuarioFoto),
                        contentDescription = "Foto del usuario",
                        modifier = Modifier
                            .size(36.dp)
                            .padding(end = 8.dp)
                            .clip(MaterialTheme.shapes.small)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Sin foto",
                        modifier = Modifier
                            .size(36.dp)
                            .padding(end = 8.dp)
                    )
                }
                Text(
                    text = perro.usuarioNombre,
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Información del perro
            Text(text = perro.nombre, style = MaterialTheme.typography.titleLarge)
            Text(text = "Raza: ${perro.raza}", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(8.dp))

            Image(
                painter = rememberAsyncImagePainter(perro.imagenUrl),
                contentDescription = "Imagen de ${perro.nombre}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = perro.descripcion)
            Text(text = "Ubicación: ${perro.lugar}")
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Contacto: ${perro.contacto}")

            // Botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onShare,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Compartir")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Compartir")
                }

                if (isOwner) {
                    Button(
                        onClick = onDelete,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Eliminar")
                    }
                }
            }
        }
    }
}

fun shareDogInfo(
    context: Context,
    nombre: String,
    descripcion: String,
    lugar: String,
    contacto: String,
    imagenUrl: String
) {
    val shareText = """
        🐶 ¡Mira este perro en adopción!
        
        Nombre: $nombre
        Raza: ${Perro::raza.name}
        Descripción: $descripcion
        Ubicación: $lugar
        Contacto: $contacto
        
        ¡Adóptame! ❤️
    """.trimIndent()

    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareText)
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(shareIntent, "Compartir información del perro"))
}


