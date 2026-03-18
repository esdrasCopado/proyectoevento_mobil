package com.itson.proyectoevento.ui.inicio

import androidx.lifecycle.ViewModel
import com.itson.proyectoevento.data.model.Evento
import com.itson.proyectoevento.data.model.Pago
import com.itson.proyectoevento.data.model.Paquete
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class InicioViewModel : ViewModel() {

    private val _eventos = MutableStateFlow<List<Evento>>(emptyList())
    val eventos: StateFlow<List<Evento>> = _eventos.asStateFlow()

    private val _uiState = MutableStateFlow(InicioUiState())
    val uiState: StateFlow<InicioUiState> = _uiState.asStateFlow()

    init {
        cargarEventos()
    }

    private fun cargarEventos() {
        val paqueteEstandar = Paquete(
            2, "Estándar", "La opción más popular.", 35000.0,
            listOf("100 personas", "8 horas", "Menú 3 tiempos", "Mobiliario completo", "Permiso de alcoholes incluido", "Sonido básico")
        )
        val paquetePremium = Paquete(
            3, "Premium", "Experiencia de lujo.", 65000.0,
            listOf("200 personas", "12 horas", "Menú gourmet", "Barra abierta (alcohol incluido)", "DJ profesional", "Valet parking")
        )

        val eventosDePrueba = listOf(
            Evento(
                id = 1,
                nombre = "Boda Ana y Luis",
                fecha = "20/10/2026",
                porcentajePagado = 40,
                totalCosto = 50000.0,
                tipo = "Boda",
                nombreCliente = "Ana García",
                telefonoCliente = "6441234567",
                correoCliente = "ana.garcia@email.com",
                paquete = paquetePremium,
                pagos = listOf(Pago(1, 20000.0, "10/01/2026", "Anticipo inicial"))
            ),
            Evento(
                id = 2,
                nombre = "XV Sofía",
                fecha = "15/03/2026",
                porcentajePagado = 75,
                totalCosto = 30000.0,
                tipo = "XV Años",
                nombreCliente = "Martha Rodríguez",
                telefonoCliente = "6449876543",
                correoCliente = "martha.rodriguez@email.com",
                paquete = paqueteEstandar,
                pagos = listOf(
                    Pago(2, 15000.0, "05/12/2025", "Anticipo inicial"),
                    Pago(3, 7500.0, "15/01/2026", "Segundo abono")
                )
            ),
            Evento(
                id = 3,
                nombre = "Corporativo TechCorp",
                fecha = "05/12/2025",
                porcentajePagado = 100,
                totalCosto = 80000.0,
                tipo = "Corporativo",
                nombreCliente = "Carlos Mendoza",
                telefonoCliente = "6442223333",
                correoCliente = "carlos@techcorp.com",
                paquete = paquetePremium,
                pagos = listOf(
                    Pago(4, 40000.0, "01/09/2025", "Anticipo inicial"),
                    Pago(5, 40000.0, "15/11/2025", "Pago final")
                )
            )
        )
        _eventos.value = eventosDePrueba
        actualizarResumen()
    }

    fun agregarEvento(evento: Evento) {
        _eventos.value = _eventos.value + evento
        actualizarResumen()
    }

    fun actualizarEvento(evento: Evento) {
        _eventos.value = _eventos.value.map { if (it.id == evento.id) evento else it }
        actualizarResumen()
    }

    fun eliminarEvento(eventoId: Int) {
        _eventos.value = _eventos.value.filter { it.id != eventoId }
        actualizarResumen()
    }

    fun obtenerEventoPorId(id: Int): Evento? = _eventos.value.find { it.id == id }

    fun registrarAbono(eventoId: Int, pago: Pago) {
        _eventos.value = _eventos.value.map { evento ->
            if (evento.id == eventoId) {
                val nuevosPagos = evento.pagos + pago
                val totalPagado = nuevosPagos.sumOf { it.monto }
                val nuevoPorcentaje = if (evento.totalCosto > 0) {
                    (totalPagado / evento.totalCosto * 100).toInt().coerceIn(0, 100)
                } else 0
                evento.copy(pagos = nuevosPagos, porcentajePagado = nuevoPorcentaje)
            } else evento
        }
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
