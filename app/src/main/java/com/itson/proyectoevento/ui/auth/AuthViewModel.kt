package com.itson.proyectoevento.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.itson.proyectoevento.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val repo = AuthRepository()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    val usuarioActual get() = repo.usuarioActual

    fun login(correo: String, contrasena: String, onExito: () -> Unit) {
        if (!validarCampos(correo, contrasena)) return
        _uiState.value = _uiState.value.copy(cargando = true, error = null)
        viewModelScope.launch {
            repo.login(correo, contrasena)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(cargando = false)
                    onExito()
                }
                .onFailure { e ->
                    val mensaje = when (e) {
                        is FirebaseAuthInvalidCredentialsException -> "Correo o contraseña incorrectos"
                        else -> "Error al iniciar sesión. Intenta de nuevo."
                    }
                    _uiState.value = _uiState.value.copy(cargando = false, error = mensaje)
                }
        }
    }

    fun registrar(
        correo: String,
        contrasena: String,
        confirmar: String,
        nombre: String,
        onExito: () -> Unit
    ) {
        if (!validarCampos(correo, contrasena)) return
        if (contrasena != confirmar) {
            _uiState.value = _uiState.value.copy(error = "Las contraseñas no coinciden")
            return
        }
        _uiState.value = _uiState.value.copy(cargando = true, error = null)
        viewModelScope.launch {
            repo.registrar(correo, contrasena, nombre)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(cargando = false)
                    onExito()
                }
                .onFailure { e ->
                    val mensaje = when (e) {
                        is FirebaseAuthWeakPasswordException -> "La contraseña debe tener al menos 6 caracteres"
                        is FirebaseAuthUserCollisionException -> "Ya existe una cuenta con ese correo"
                        else -> "Error al registrar. Intenta de nuevo."
                    }
                    _uiState.value = _uiState.value.copy(cargando = false, error = mensaje)
                }
        }
    }

    fun cerrarSesion(onExito: () -> Unit) {
        repo.logout()
        _uiState.value = AuthUiState()
        onExito()
    }

    fun limpiarError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    private fun validarCampos(correo: String, contrasena: String): Boolean {
        return when {
            correo.isBlank() || contrasena.isBlank() -> {
                _uiState.value = _uiState.value.copy(error = "Completa todos los campos")
                false
            }
            !correo.contains("@") -> {
                _uiState.value = _uiState.value.copy(error = "Correo inválido")
                false
            }
            else -> true
        }
    }
}

data class AuthUiState(
    val cargando: Boolean = false,
    val error: String? = null
)
