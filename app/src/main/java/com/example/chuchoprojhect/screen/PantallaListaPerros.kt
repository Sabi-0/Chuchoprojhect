package com.example.chuchoprojhect.screen

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.chuchoprojhect.model.Perro
import com.example.chuchoprojhect.util.obtenerPerrosDesdeFirestore
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share


@Composable
fun PantallaListaPerros() {
    val context = LocalContext.current
    var listaPerros by remember { mutableStateOf<List<Perro>>(emptyList()) }
    var filtro by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

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

    // Función de filtrado simple
    val perrosFiltrados = listaPerros.filter { perro ->
        filtro?.let { filtroTexto ->
            perro.lugar.contains(filtroTexto, ignoreCase = true) ||
                    perro.nombre.contains(filtroTexto, ignoreCase = true) ||
                    perro.descripcion.contains(filtroTexto, ignoreCase = true)
        } ?: true
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Barra de búsqueda simple
        OutlinedTextField(
            value = filtro ?: "",
            onValueChange = { filtro = if (it.isEmpty()) null else it },
            label = { Text("Buscar perros...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (perrosFiltrados.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    if (filtro != null) "No hay perros que coincidan con tu búsqueda"
                    else "No hay perros disponibles para adopción."
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                items(perrosFiltrados) { perro ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = perro.nombre, style = MaterialTheme.typography.titleLarge)
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

                            // Botón para compartir
                            Button(
                                onClick = {
                                    shareDogInfo(
                                        context = context,
                                        nombre = perro.nombre,
                                        descripcion = perro.descripcion,
                                        lugar = perro.lugar,
                                        contacto = perro.contacto,
                                        imagenUrl = perro.imagenUrl
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Compartir",
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text("Compartir")
                            }
                        }
                    }
                }
            }
        }
    }
}

// Función para compartir
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
        Descripción: $descripcion
        Ubicación: $lugar
        Contacto: $contacto
        
        Enlace a la imagen: $imagenUrl
        
        #AdoptaNoCompres
    """.trimIndent()

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareText)
        type = "text/plain"
    }

    context.startActivity(
        Intent.createChooser(sendIntent, "Compartir información del perro")
    )
}


