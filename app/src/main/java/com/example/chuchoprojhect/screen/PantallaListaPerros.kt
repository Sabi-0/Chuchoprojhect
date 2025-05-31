package com.example.chuchoprojhect.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.chuchoprojhect.model.Perro
import com.example.chuchoprojhect.util.obtenerPerrosDesdeFirestore
import androidx.compose.ui.Alignment

@Composable
fun PantallaListaPerros() {
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
                        }
                    }
                }
            }
        }
    }
}


