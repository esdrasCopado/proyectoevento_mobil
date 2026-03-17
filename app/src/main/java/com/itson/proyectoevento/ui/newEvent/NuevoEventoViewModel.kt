package com.itson.proyectoevento.ui.newEvent

import androidx.lifecycle.ViewModel
import com.itson.proyectoevento.data.model.Evento
import com.itson.proyectoevento.data.model.Pago
import com.itson.proyectoevento.data.model.Paquete
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class NuevoEventoUiState(
    val nombre: String = "",
    val tipo: String = "",
    val fecha: String = "",
    val totalCosto: String = "",
    val adelanto: String = "",
    val nombreCliente: String = "",
    val telefonoCliente: String = "",
    val correoCliente: String = "",
    val paqueteSeleccionado: Paquete? = null,
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

    fun onNombreClienteChange(value: String) {
        _uiState.value = _uiState.value.copy(nombreCliente = value)
    }

    fun onTelefonoClienteChange(value: String) {
        _uiState.value = _uiState.value.copy(telefonoCliente = value)
    }

    fun onCorreoClienteChange(value: String) {
        _uiState.value = _uiState.value.copy(correoCliente = value)
    }

    fun onPaqueteSeleccionado(paquete: Paquete) {
        _uiState.value = _uiState.value.copy(paqueteSeleccionado = paquete)
    }

    fun quitarPaquete() {
        _uiState.value = _uiState.value.copy(paqueteSeleccionado = null)
    }

    fun resetearFormulario() {
        _uiState.value = NuevoEventoUiState()
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
            val pagoInicial = if (adelantoPagado > 0) {
                listOf(Pago(System.currentTimeMillis().toInt(), adelantoPagado, state.fecha, "Anticipo inicial"))
            } else emptyList()

            val nuevoEvento = Evento(
                id = System.currentTimeMillis().toInt(),
                nombre = state.nombre,
                fecha = state.fecha,
                porcentajePagado = state.porcentajeCalculado,
                totalCosto = costo!!,
                tipo = state.tipo,
                nombreCliente = state.nombreCliente,
                telefonoCliente = state.telefonoCliente,
                correoCliente = state.correoCliente,
                paquete = state.paqueteSeleccionado,
                pagos = pagoInicial
            )
            onEventoCreado(nuevoEvento)
        }
    }
}
