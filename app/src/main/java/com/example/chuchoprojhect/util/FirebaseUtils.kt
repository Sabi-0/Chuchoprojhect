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
    raza: String,
    imageUrl: String,
    onSuccess: () -> Unit,
    onFailure: () -> Unit
) {
    val db = FirebaseFirestore.getInstance() // Obtiene instancia de Firestore
    val perro = hashMapOf<String, Any>( // Mapa que representa los datos del perro
        "nombre" to nombre,
        "descripcion" to descripcion,
        "lugar" to lugar,
        "contacto" to contacto,
        "raza" to raza,
        "imagenUrl" to imageUrl,
        "timestamp" to System.currentTimeMillis(),
        "userId" to (user?.uid ?: ""),
        "usuarioNombre" to (user?.displayName ?: "Anónimo"),
        "usuarioFoto" to (user?.photoUrl?.toString() ?: "")
    )
    db.collection("perros")  // Se accede a la colección 'perros' en Firestore
        .add(perro)  // Se agrega el mapa creado como nuevo documento
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { onFailure() }
}


fun obtenerPerrosDesdeFirestore(
    onSuccess: (List<Perro>) -> Unit, // Callback que recibe la lista de perros
    onFailure: () -> Unit
) {
    val db = FirebaseFirestore.getInstance() // Obtener instancia de Firestore
    db.collection("perros") // Acceder a la colección
        .orderBy("timestamp") // Ordenar por fecha de creación
        .get()
        .addOnSuccessListener { result ->
            // Convertir cada documento a un objeto Perro
            val perros = result.documents.mapNotNull { doc ->
                try {
                    doc.toObject(Perro::class.java)?.copy(id = doc.id)
                } catch (e: Exception) {
                    null
                }
            }
            onSuccess(perros) // Devolver la lista de perros
        }
        .addOnFailureListener {
            onFailure()
        }
}
fun subirImagenAFirebase(
    uri: Uri?,  // URI de la imagen local seleccionada
    onSuccess: (String) -> Unit, // Callback con la URL pública de la imagen
    onFailure: () -> Unit  // Callback si falla la subida
) {
    if (uri == null) {
        onFailure() // No se seleccionó imagen
        return
    }

    val storageRef = FirebaseStorage.getInstance().reference // Referencia raíz de Firebase Storage
    val imageRef = storageRef.child("imagenesPerros/${UUID.randomUUID()}.jpg") // Nombre único

    imageRef.putFile(uri) // Subir la imagen
        .addOnSuccessListener {
            // Obtener la URL pública una vez subida
            imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                onSuccess(downloadUrl.toString()) // Devolver la URL
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
    // Construir el objeto con los cambios a aplicar
    val profileUpdates = UserProfileChangeRequest.Builder()
        .apply {
            if (!nombre.isNullOrEmpty()) {
                setDisplayName(nombre) // Cambiar nombre si se proporciona
            }
            if (!photoUrl.isNullOrEmpty()) {
                setPhotoUri(Uri.parse(photoUrl)) // Cambiar foto si se proporciona
            }
        }
        .build()
// Aplicar los cambios al perfil del usuario
    user.updateProfile(profileUpdates)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { e -> onFailure(e) }
}
