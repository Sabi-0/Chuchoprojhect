package com.example.chuchoprojhect.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter


@Composable
fun AgregarPerroScreen(
    onSubmit: (String, String, String, String, Uri?) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var lugar by remember { mutableStateOf("") }
    var contacto by remember { mutableStateOf("") } // nuevo
    var imagenUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imagenUri = uri
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .width(320.dp)
                .wrapContentHeight()
                .background(Color(0xFFFFA64D), RoundedCornerShape(24.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Agregar perro", fontSize = 24.sp, fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(16.dp))
            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre del perrito") })
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") })
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = lugar, onValueChange = { lugar = it }, label = { Text("¿De dónde nos saludas?") })
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = contacto,
                onValueChange = { contacto = it.filter { char -> char.isDigit() } }, // solo números
                label = { Text("Número de contacto (+502)") },
                placeholder = { Text("Ejemplo: 12345678") },
                singleLine = true
            )
            Spacer(Modifier.height(12.dp))

            Button(onClick = { launcher.launch("image/*") }) {
                Text("Sube tu imagen")
            }

            imagenUri?.let {
                Spacer(Modifier.height(12.dp))
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = "Imagen seleccionada",
                    modifier = Modifier.size(150.dp).padding(8.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    // Añadimos +502 delante del contacto automáticamente
                    val contactoCompleto = if (contacto.startsWith("+502")) contacto else "+502$contacto"
                    onSubmit(nombre, descripcion, lugar, contactoCompleto, imagenUri)
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF8B5E3C))
            ) {
                Text("Subir", color = Color.White)
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun AgregarPerroScreenPreview() {
    AgregarPerroScreen(onSubmit = { _, _, _, _, _ -> })
}