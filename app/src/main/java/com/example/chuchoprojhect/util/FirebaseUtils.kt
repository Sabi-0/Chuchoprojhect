package com.example.chuchoprojhect.util
import com.example.chuchoprojhect.model.Perro
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*


fun guardarPerroEnFirestore(
    user: FirebaseUser?,
    nombre: String,
    descripcion: String,
    lugar: String,
    contacto: String,
    imageUrl: String,
    onSuccess: () -> Unit,
    onFailure: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val perro = hashMapOf(
        "nombre" to nombre,
        "descripcion" to descripcion,
        "lugar" to lugar,
        "contacto" to contacto,
        "imagenUrl" to imageUrl,
        "timestamp" to System.currentTimeMillis(),
        "usuarioNombre" to (user?.displayName ?: "Anónimo"),
        "usuarioFoto" to (user?.photoUrl?.toString() ?: "")
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

fun actualizarPerfilUsuario(
    user: FirebaseUser,
    nombre: String?,
    photoUrl: String?,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit
) {
    val profileUpdates = UserProfileChangeRequest.Builder()
        .apply {
            if (!nombre.isNullOrEmpty()) {
                setDisplayName(nombre)
            }
            if (!photoUrl.isNullOrEmpty()) {
                setPhotoUri(Uri.parse(photoUrl))
            }
        }
        .build()

    user.updateProfile(profileUpdates)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { e -> onFailure(e) }
}
