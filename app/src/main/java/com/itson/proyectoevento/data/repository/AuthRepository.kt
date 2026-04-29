package com.itson.proyectoevento.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.itson.proyectoevento.data.model.Usuario
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    val usuarioActual: FirebaseUser?
        get() = auth.currentUser

    fun estaAutenticado(): Boolean = auth.currentUser != null

    suspend fun registrar(
        email: String,
        password: String,
        nombre: String
    ): Result<Usuario> = runCatching {
        val authResult = auth.createUserWithEmailAndPassword(email, password).await()
        val uid = authResult.user?.uid
            ?: throw IllegalStateException("No se pudo obtener el UID")

        val nuevoUsuario = Usuario(
            uid = uid,
            email = email,
            nombre = nombre,
            rol = "cliente",
            fechaRegistro = Timestamp.now()
        )

        firestore.collection("usuarios")
            .document(uid)
            .set(nuevoUsuario)
            .await()

        nuevoUsuario
    }

    suspend fun login(email: String, password: String): Result<Usuario> = runCatching {
        val authResult = auth.signInWithEmailAndPassword(email, password).await()
        val uid = authResult.user?.uid
            ?: throw IllegalStateException("No se pudo obtener el UID")

        obtenerUsuario(uid).getOrThrow()
    }

    suspend fun obtenerUsuario(uid: String): Result<Usuario> = runCatching {
        val snapshot = firestore.collection("usuarios")
            .document(uid)
            .get()
            .await()

        snapshot.toObject(Usuario::class.java)
            ?: throw IllegalStateException("Usuario no encontrado en Firestore")
    }

    suspend fun obtenerUsuarioActual(): Result<Usuario>? {
        val uid = auth.currentUser?.uid ?: return null
        return obtenerUsuario(uid)
    }

    fun logout() {
        auth.signOut()
    }
}
