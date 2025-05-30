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

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth

    private val _userState = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val userState: StateFlow<FirebaseUser?> = _userState

    init {
        auth.addAuthStateListener { firebaseAuth ->
            _userState.value = firebaseAuth.currentUser
        }
    }

    fun signInWithEmail(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                onComplete(true, null)
            } catch (e: Exception) {
                onComplete(false, e.message)
            }
        }
    }

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

    fun signOut() {
        auth.signOut()
    }

    fun getCurrentUser(): FirebaseUser? = auth.currentUser
}