package com.example.chuchoprojhect.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


// ViewModel para manejar la autenticación con Firebase
class AuthViewModel : ViewModel() {

    // Instancia del servicio de autenticación de Firebase
    private val auth: FirebaseAuth = Firebase.auth

    // Flow para emitir el estado actual del usuario autenticado
    private val _userState = MutableStateFlow<FirebaseUser?>(auth.currentUser)

    // Flow de solo lectura para que otras partes de la app observen el usuario actual
    val userState: StateFlow<FirebaseUser?> = _userState


    // Bloque que se ejecuta al inicializar el ViewModel
    init {

        // Se añade un listener que actualiza el estado del usuario cuando cambia en Firebase
        auth.addAuthStateListener { firebaseAuth ->
            _userState.value = firebaseAuth.currentUser
        }
    }

    // Función para iniciar sesión con email y contraseña
    fun signInWithEmail(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        // Se lanza una corrutina
        viewModelScope.launch {
            try {

                //llamamos a firebase para autenticar
                auth.signInWithEmailAndPassword(email, password).await()

                //llamamos el callback
                onComplete(true, null)
            } catch (e: Exception) {
                // Si hay error, se pasa el mensaje al callback
                onComplete(false, e.message)
            }
        }
    }

    // Función para registrar un nuevo usuario con email y contraseña
    fun signUpWithEmail(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                onComplete(true, null)
            } catch (e: Exception) {
                onComplete(false, e.message)
            }
        }
    }

    // Función para iniciar sesión como usuario anónimo (sin email)
    fun signInAnonymously(onComplete: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                auth.signInAnonymously().await()
                onComplete(true, null)
            } catch (e: Exception) {
                onComplete(false, e.message)
            }
        }
    }
    // Función para cerrar sesión
    fun signOut() {
        auth.signOut()
    }

    // Devuelve el usuario actual (puede ser null si no hay nadie autenticado)
    fun getCurrentUser(): FirebaseUser? = auth.currentUser
}