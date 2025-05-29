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

    LaunchedEffect(true) {
        obtenerPerrosDesdeFirestore(
            onSuccess = { perros -> listaPerros = perros },
            onFailure = { /* mostrar error */ }
        )
    }

    if (listaPerros.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay perros disponibles para adopción.")
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(listaPerros) { perro ->
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


