package com.itson.proyectoevento.ui.inicio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.itson.proyectoevento.data.model.Evento
import com.itson.proyectoevento.data.model.Pago
import com.itson.proyectoevento.data.repository.AuthRepository
import com.itson.proyectoevento.data.repository.EventosRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class InicioViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val eventosRepo = EventosRepository()
    private val authRepo = AuthRepository()

    private val _eventos = MutableStateFlow<List<Evento>>(emptyList())
    val eventos: StateFlow<List<Evento>> = _eventos.asStateFlow()

    private val _uiState = MutableStateFlow(InicioUiState())
    val uiState: StateFlow<InicioUiState> = _uiState.asStateFlow()

    private var observerJob: Job? = null

    init {
        // Si ya hay sesión activa al iniciar la app, empezamos a observar
        if (auth.currentUser != null) inicializar()
    }

    fun onUsuarioLoggedIn() = inicializar()

    fun limpiar() {
        observerJob?.cancel()
        _eventos.value = emptyList()
        _uiState.value = InicioUiState()
    }

    private fun inicializar() {
        val uid = auth.currentUser?.uid ?: return
        observerJob?.cancel()
        _uiState.value = _uiState.value.copy(isLoading = true)
        observerJob = viewModelScope.launch {
            val usuario = authRepo.obtenerUsuario(uid).getOrNull()
            val esAdmin = usuario?.esAdmin() ?: false
            _uiState.value = _uiState.value.copy(esAdmin = esAdmin)
            eventosRepo.observarEventos(uid, esAdmin)
                .catch { _uiState.value = _uiState.value.copy(isLoading = false) }
                .collect { lista ->
                    _eventos.value = lista
                    actualizarResumen()
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
        }
    }

    fun agregarEvento(evento: Evento) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            eventosRepo.crearEvento(evento.copy(propietarioUid = uid))
        }
    }

    fun actualizarEvento(evento: Evento) {
        viewModelScope.launch {
            eventosRepo.actualizarEvento(evento)
        }
    }

    fun eliminarEvento(eventoId: Int) {
        val evento = _eventos.value.find { it.id == eventoId } ?: return
        viewModelScope.launch {
            eventosRepo.eliminarEvento(evento.firestoreId)
        }
    }

    fun obtenerEventoPorId(id: Int): Evento? = _eventos.value.find { it.id == id }

    fun registrarAbono(eventoId: Int, pago: Pago) {
        val evento = _eventos.value.find { it.id == eventoId } ?: return
        val nuevosPagos = evento.pagos + pago
        val totalPagado = nuevosPagos.sumOf { it.monto }
        val nuevoPorcentaje = if (evento.totalCosto > 0) {
            (totalPagado / evento.totalCosto * 100).toInt().coerceIn(0, 100)
        } else 0
        viewModelScope.launch {
            eventosRepo.actualizarEvento(
                evento.copy(pagos = nuevosPagos, porcentajePagado = nuevoPorcentaje)
            )
        }
    }

    private fun actualizarResumen() {
        val lista = _eventos.value
        _uiState.value = _uiState.value.copy(
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
    val isLoading: Boolean = false,
    val esAdmin: Boolean = false
)
