package com.example.chuchoprojhect.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.rememberAsyncImagePainter

@Composable
fun AgregarPerroScreen(
    onSubmit: (String, String, String, String, String, Uri?, () -> Unit) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var lugar by remember { mutableStateOf("") }
    var contacto by remember { mutableStateOf("") }
    var raza by remember { mutableStateOf("") }
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
            Text(
                text = "Agregar perro",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del perrito") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = lugar,
                onValueChange = { lugar = it },
                label = { Text("Ubicación") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = contacto,
                onValueChange = { contacto = it.filter { char -> char.isDigit() } },
                label = { Text("Teléfono de contacto") },
                placeholder = { Text("Ejemplo: 12345678") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = raza,
                onValueChange = { raza = it },
                label = { Text("Raza del perro") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { launcher.launch("image/*") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFFFFA64D)
                )
            ) {
                Text("Seleccionar imagen")
            }

            imagenUri?.let {
                Spacer(Modifier.height(12.dp))
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = "Imagen seleccionada",
                    modifier = Modifier
                        .size(150.dp)
                        .padding(8.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    if (nombre.isBlank() || descripcion.isBlank() || lugar.isBlank() || contacto.isBlank() || raza.isBlank()) {
                        return@Button
                    }

                    val contactoCompleto = if (contacto.startsWith("+502")) contacto else "+502$contacto"
                    onSubmit(nombre, descripcion, lugar, contactoCompleto, raza, imagenUri) {
                        nombre = ""
                        descripcion = ""
                        lugar = ""
                        contacto = ""
                        raza = ""
                        imagenUri = null
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8B5E3C),
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Publicar perro")
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun AgregarPerroScreenPreview() {
    AgregarPerroScreen(onSubmit = { _, _, _, _, _, _, _ -> })
}