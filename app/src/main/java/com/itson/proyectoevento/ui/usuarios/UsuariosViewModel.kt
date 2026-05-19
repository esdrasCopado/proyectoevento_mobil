package com.itson.proyectoevento.ui.usuarios

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseApp
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import com.itson.proyectoevento.data.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class UsuariosState {
    data object Loading : UsuariosState()
    data class Success(val lista: List<Usuario>) : UsuariosState()
    data class Error(val mensaje: String) : UsuariosState()
}

class UsuariosViewModel(application: Application) : AndroidViewModel(application) {
    private val db = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow<UsuariosState>(UsuariosState.Loading)
    val uiState: StateFlow<UsuariosState> = _uiState

    private var ultimaLista = emptyList<Usuario>()
    private var listenerRegistration: ListenerRegistration? = null

    // Llamar desde la pantalla (post-login) para evitar PERMISSION_DENIED al inicio
    fun iniciar() {
        if (listenerRegistration != null) return
        listenerRegistration = db.collection("usuarios").addSnapshotListener { snapshot, e ->
            if (e != null) {
                // PERMISSION_DENIED justo al iniciar sesión es un error transitorio:
                // el token de auth aún no se propagó a Firestore. Se ignora y Firestore
                // reintenta automáticamente en cuanto el token está disponible.
                val esTransitorio = e.message?.contains("PERMISSION_DENIED") == true
                        && FirebaseAuth.getInstance().currentUser != null
                if (!esTransitorio) {
                    _uiState.value = UsuariosState.Error(e.message ?: "Error desconocido")
                }
                return@addSnapshotListener
            }
            ultimaLista = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject<Usuario>()
            } ?: emptyList()
            _uiState.value = UsuariosState.Success(ultimaLista)
        }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }

    fun crearUsuario(nombre: String, correo: String, contrasena: String) {
        viewModelScope.launch {
            _uiState.value = UsuariosState.Loading
            try {
                // FirebaseApp secundario para no cerrar la sesión del admin actual
                val context = getApplication<Application>()
                val options = FirebaseApp.getInstance().options
                val secondaryApp = try {
                    FirebaseApp.getInstance("secondary_auth")
                } catch (e: IllegalStateException) {
                    FirebaseApp.initializeApp(context, options, "secondary_auth")!!
                }
                val secondaryAuth = FirebaseAuth.getInstance(secondaryApp)

                val result = secondaryAuth.createUserWithEmailAndPassword(correo, contrasena).await()
                val uid = result.user?.uid ?: return@launch
                secondaryAuth.signOut()

                val nuevoUsuario = Usuario(
                    uid = uid,
                    email = correo,
                    nombre = nombre,
                    rol = "cliente",
                    fechaRegistro = Timestamp.now()
                )
                db.collection("usuarios").document(uid).set(nuevoUsuario).await()
                // El snapshot listener actualizará el estado a Success automáticamente
            } catch (e: FirebaseAuthUserCollisionException) {
                _uiState.value = UsuariosState.Error("Este correo electrónico ya está en uso por otra cuenta")
            } catch (e: FirebaseAuthWeakPasswordException) {
                _uiState.value = UsuariosState.Error("La contraseña es muy débil (mínimo 6 caracteres)")
            } catch (e: Exception) {
                _uiState.value = UsuariosState.Error("Error: ${e.localizedMessage}")
            }
        }
    }

    fun actualizarUsuario(uid: String, nombre: String, rol: String) {
        viewModelScope.launch {
            try {
                db.collection("usuarios").document(uid).update(
                    mapOf("nombre" to nombre, "rol" to rol)
                ).await()
            } catch (e: Exception) {
                _uiState.value = UsuariosState.Error("Error al actualizar: ${e.message}")
            }
        }
    }

    fun eliminarUsuario(uid: String) {
        viewModelScope.launch {
            try {
                db.collection("usuarios").document(uid).delete().await()
            } catch (e: Exception) {
                _uiState.value = UsuariosState.Error("Error al eliminar")
            }
        }
    }

    fun limpiarError() {
        _uiState.value = UsuariosState.Success(ultimaLista)
    }
}
