package com.example.chuchoprojhect.util
import com.example.chuchoprojhect.model.Perro

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*


fun guardarPerroEnFirestore(
    nombre: String,
    descripcion: String,
    lugar: String,
    contacto: String, // nuevo parámetro
    imageUrl: String,
    onSuccess: () -> Unit,
    onFailure: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val perro = hashMapOf(
        "nombre" to nombre,
        "descripcion" to descripcion,
        "lugar" to lugar,
        "contacto" to contacto,  // guardamos contacto
        "imagenUrl" to imageUrl,
        "timestamp" to System.currentTimeMillis()
    )
    db.collection("perros")
        .add(perro)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { onFailure() }
}

fun obtenerPerrosDesdeFirestore(
    onSuccess: (List<Perro>) -> Unit,
    onFailure: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    db.collection("perros")
        .orderBy("timestamp")
        .get()
        .addOnSuccessListener { result ->
            val perros = result.mapNotNull { doc ->
                doc.toObject(Perro::class.java)
            }
            onSuccess(perros)
        }
        .addOnFailureListener {
            onFailure()
        }
}

fun subirImagenAFirebase(
    uri: Uri?,
    onSuccess: (String) -> Unit,
    onFailure: () -> Unit
) {
    if (uri == null) {
        onFailure()
        return
    }

    val storageRef = FirebaseStorage.getInstance().reference
    val imageRef = storageRef.child("imagenesPerros/${UUID.randomUUID()}.jpg")

    imageRef.putFile(uri)
        .addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                onSuccess(downloadUrl.toString())
            }.addOnFailureListener {
                onFailure()
            }
        }
        .addOnFailureListener {
            onFailure()
        }
}