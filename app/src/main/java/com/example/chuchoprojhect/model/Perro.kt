package com.example.chuchoprojhect.model

//definimos los datos que se necesitan guardar
data class Perro(
    val id: String = "",
    val userId: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val lugar: String = "",
    val contacto: String = "",
    val imagenUrl: String = "",
    val usuarioNombre: String = "",
    val usuarioFoto: String = "",
    val raza: String = "",
    val timestamp: Long = 0L
)
