package com.itson.proyectoevento.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val mensaje: String) : LoginState()
}

class LoginViewModel : ViewModel() {
    private val auth = Firebase.auth
    private val _uiState = MutableStateFlow<LoginState>(LoginState.Idle)
    val uiState: StateFlow<LoginState> = _uiState

    fun login(correo: String, contrasena: String) {
        if (correo.isBlank() || contrasena.isBlank()) {
            _uiState.value = LoginState.Error("Por favor, llena todos los campos")
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginState.Loading
            try {
                auth.signInWithEmailAndPassword(correo, contrasena).await()
                _uiState.value = LoginState.Success
            } catch (e: FirebaseAuthInvalidUserException) {
                _uiState.value = LoginState.Error("El correo electrónico no está registrado")
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                _uiState.value = LoginState.Error("La contraseña es incorrecta")
            } catch (e: Exception) {
                _uiState.value = LoginState.Error("Error de conexión o datos inválidos")
            }
        }
    }

    fun logout() {
        auth.signOut()
        _uiState.value = LoginState.Idle
    }

    fun resetState() {
        _uiState.value = LoginState.Idle
    }
}
