package com.example.chuchoprojhect.util

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

fun subirImagenAFirebase(uri: Uri?, onSuccess: (String) -> Unit, onFailure: () -> Unit) {
    if (uri == null) {
        onFailure()
        return
    }

    val storageRef = FirebaseStorage.getInstance().reference
    val imageRef = storageRef.child("imagenes_perros/${UUID.randomUUID()}.jpg")

    imageRef.putFile(uri)
        .addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                onSuccess(downloadUrl.toString())
            }
        }
        .addOnFailureListener {
            onFailure()
        }
}

fun guardarPerroEnFirestore(
    nombre: String,
    descripcion: String,
    lugar: String,
    imageUrl: String,
    onSuccess: () -> Unit,
    onFailure: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val perro = hashMapOf(
        "nombre" to nombre,
        "descripcion" to descripcion,
        "lugar" to lugar,
        "imagenUrl" to imageUrl,
        "timestamp" to System.currentTimeMillis()
    )
    db.collection("perros")
        .add(perro)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { onFailure() }
}
