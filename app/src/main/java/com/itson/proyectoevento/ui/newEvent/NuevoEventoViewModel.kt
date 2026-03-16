package com.itson.proyectoevento.ui.newEvent

import androidx.lifecycle.ViewModel
import com.itson.proyectoevento.data.model.Evento
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class NuevoEventoUiState(
    val nombre: String = "",
    val tipo: String = "",
    val fecha: String = "",
    val totalCosto: String = "",
    val adelanto: String = "",
    val nombreError: String? = null,
    val tipoError: String? = null,
    val fechaError: String? = null,
    val totalCostoError: String? = null,
    val adelantoError: String? = null
) {
    val porcentajeCalculado: Int
        get() {
            val costo = totalCosto.toDoubleOrNull() ?: return 0
            val pago = adelanto.toDoubleOrNull() ?: return 0
            if (costo <= 0) return 0
            return ((pago / costo) * 100).toInt().coerceIn(0, 100)
        }
}

class NuevoEventoViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NuevoEventoUiState())
    val uiState: StateFlow<NuevoEventoUiState> = _uiState.asStateFlow()

    val tiposDeEvento = listOf("Boda", "XV Años", "Corporativo", "Cumpleaños", "Graduación", "Otro")

    fun onNombreChange(value: String) {
        _uiState.value = _uiState.value.copy(nombre = value, nombreError = null)
    }

    fun onTipoChange(value: String) {
        _uiState.value = _uiState.value.copy(tipo = value, tipoError = null)
    }

    fun onFechaChange(value: String) {
        _uiState.value = _uiState.value.copy(fecha = value, fechaError = null)
    }

    fun onTotalCostoChange(value: String) {
        _uiState.value = _uiState.value.copy(totalCosto = value, totalCostoError = null, adelantoError = null)
    }

    fun onAdelantoChange(value: String) {
        _uiState.value = _uiState.value.copy(adelanto = value, adelantoError = null)
    }

    fun validarYCrearEvento(onEventoCreado: (Evento) -> Unit) {
        val state = _uiState.value
        var hayErrores = false

        if (state.nombre.isBlank()) {
            _uiState.value = _uiState.value.copy(nombreError = "El nombre es requerido")
            hayErrores = true
        }
        if (state.tipo.isBlank()) {
            _uiState.value = _uiState.value.copy(tipoError = "Selecciona un tipo")
            hayErrores = true
        }
        if (state.fecha.isBlank()) {
            _uiState.value = _uiState.value.copy(fechaError = "La fecha es requerida")
            hayErrores = true
        }
        val costo = state.totalCosto.toDoubleOrNull()
        if (costo == null || costo <= 0) {
            _uiState.value = _uiState.value.copy(totalCostoError = "Ingresa un costo válido")
            hayErrores = true
        }
        val adelantoPagado = state.adelanto.toDoubleOrNull() ?: 0.0
        if (state.adelanto.isNotBlank() && (adelantoPagado < 0 || (costo != null && adelantoPagado > costo))) {
            _uiState.value = _uiState.value.copy(adelantoError = "El anticipo no puede superar el costo total")
            hayErrores = true
        }

        if (!hayErrores) {
            val nuevoEvento = Evento(
                id = System.currentTimeMillis().toInt(),
                nombre = state.nombre,
                fecha = state.fecha,
                porcentajePagado = state.porcentajeCalculado,
                totalCosto = costo!!,
                tipo = state.tipo
            )
            onEventoCreado(nuevoEvento)
        }
    }
}
