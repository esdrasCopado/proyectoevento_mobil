package com.itson.proyectoevento.ui.usuarios

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class Usuario(
    val id: String = "",
    val nombre: String = "",
    val correo: String = "",
    val rol: String = "Usuario"
)

sealed class UsuariosState {
    data object Idle : UsuariosState()
    data object Loading : UsuariosState()
    data class Success(val lista: List<Usuario>) : UsuariosState()
    data class Error(val mensaje: String) : UsuariosState()
}

class UsuariosViewModel : ViewModel() {
    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val _uiState = MutableStateFlow<UsuariosState>(UsuariosState.Idle)
    val uiState: StateFlow<UsuariosState> = _uiState

    private val _eventos = MutableSharedFlow<String>()
    val eventos: SharedFlow<String> = _eventos

    init {
        observarUsuarios()
    }

    private fun observarUsuarios() {
        db.collection("usuarios").addSnapshotListener { snapshot, e ->
            if (e != null) {
                _uiState.value = UsuariosState.Error(e.message ?: "Error desconocido")
                return@addSnapshotListener
            }
            val usuarios = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject<Usuario>()?.copy(id = doc.id)
            } ?: emptyList()
            _uiState.value = UsuariosState.Success(usuarios)
        }
    }

    fun crearUsuario(nombre: String, correo: String, contrasena: String) {
        viewModelScope.launch {
            _uiState.value = UsuariosState.Loading
            try {
                val result = auth.createUserWithEmailAndPassword(correo, contrasena).await()
                val uid = result.user?.uid ?: return@launch
                val nuevoUsuario = Usuario(id = uid, nombre = nombre, correo = correo)
                db.collection("usuarios").document(uid).set(nuevoUsuario).await()
                
                // Emitir éxito
                _eventos.emit("REGISTRO_EXITOSO")
                _uiState.value = UsuariosState.Idle 
            } catch (e: FirebaseAuthUserCollisionException) {
                _uiState.value = UsuariosState.Error("Este correo electrónico ya está en uso por otra cuenta")
            } catch (e: FirebaseAuthWeakPasswordException) {
                _uiState.value = UsuariosState.Error("La contraseña es muy débil (mínimo 6 caracteres)")
            } catch (e: Exception) {
                _uiState.value = UsuariosState.Error("Error: ${e.localizedMessage}")
            }
        }
    }

    fun resetRegistroStatus() {
        _uiState.value = UsuariosState.Idle
    }

    fun actualizarUsuario(usuario: Usuario) {
        viewModelScope.launch {
            try {
                db.collection("usuarios").document(usuario.id).set(usuario).await()
            } catch (e: Exception) {
                _uiState.value = UsuariosState.Error("Error al actualizar")
            }
        }
    }

    fun eliminarUsuario(id: String) {
        viewModelScope.launch {
            try {
                db.collection("usuarios").document(id).delete().await()
            } catch (e: Exception) {
                _uiState.value = UsuariosState.Error("Error al eliminar")
            }
        }
    }
}
