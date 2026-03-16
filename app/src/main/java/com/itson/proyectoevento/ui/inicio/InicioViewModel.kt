package com.itson.proyectoevento.ui.inicio

import androidx.lifecycle.ViewModel
import com.itson.proyectoevento.data.model.Evento
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class InicioViewModel: ViewModel() {

    private val _eventos = MutableStateFlow<List<Evento>>(emptyList())
    val eventos: StateFlow<List<Evento>> = _eventos.asStateFlow()

    private val _uiState = MutableStateFlow(InicioUiState())
    val uiState: StateFlow<InicioUiState> = _uiState.asStateFlow()


    init {
        cargarEventos()
    }

    private fun cargarEventos(){
        val eventosDePrueba = listOf(
            Evento(1, "Boda Ana y Luis", "20/10/2026", 40, 50000.0, "Boda"),
            Evento(2, "XV Sofía", "15/03/2026", 75, 30000.0, "XV Años"),
            Evento(3, "Corporativo TechCorp", "05/12/2025", 100, 80000.0, "Corporativo")
        )
        _eventos.value = eventosDePrueba
        actualizarResumen()
    }

    fun agregarEvento(evento: Evento) {
        _eventos.value = _eventos.value + evento
        actualizarResumen()
    }

    private fun actualizarResumen() {
        val lista = _eventos.value
        _uiState.value = InicioUiState(
            totalEventos = lista.size,
            eventosPendientesPago = lista.count { it.porcentajePagado < 100 },
            proximoEvento = lista.firstOrNull()
        )
    }


}
data class InicioUiState(
    val totalEventos: Int = 0,
    val eventosPendientesPago: Int = 0,
    val proximoEvento: Evento? = null,
    val isLoading: Boolean = false
)