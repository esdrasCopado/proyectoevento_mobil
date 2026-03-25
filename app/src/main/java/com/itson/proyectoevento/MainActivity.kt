package com.itson.proyectoevento

import android.os.Bundle
import com.itson.proyectoevento.ui.bienvenida.BienvenidaScreen
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.itson.proyectoevento.ui.abono.RegistrarAbonoScreen
import com.itson.proyectoevento.ui.cotizacion.CotizacionScreen
import com.itson.proyectoevento.ui.detalle.DetalleEventoScreen
import com.itson.proyectoevento.ui.inicio.InicioScreen
import com.itson.proyectoevento.ui.inicio.InicioViewModel
import com.itson.proyectoevento.ui.newEvent.NuevoEventoScreen
import com.itson.proyectoevento.ui.newEvent.NuevoEventoViewModel
import com.itson.proyectoevento.ui.paquetes.PaquetesScreen
import com.itson.proyectoevento.ui.theme.ProyectoEventoTheme

sealed class Pantalla {
    object Bienvenida : Pantalla()
    object Inicio : Pantalla()
    object NuevoEvento : Pantalla()
    object SeleccionarPaquete : Pantalla()
    data class DetalleEvento(val eventoId: Int) : Pantalla()
    data class EditarEvento(val eventoId: Int) : Pantalla()
    data class RegistrarAbono(val eventoId: Int) : Pantalla()
    data class Cotizacion(val eventoId: Int) : Pantalla()
}

class MainActivity : ComponentActivity() {

    private val inicioViewModel: InicioViewModel by viewModels()
    private val nuevoEventoViewModel: NuevoEventoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProyectoEventoTheme {
                var pantalla by remember { mutableStateOf<Pantalla>(Pantalla.Bienvenida) }
                val eventos by inicioViewModel.eventos.collectAsStateWithLifecycle()
                val nuevoEventoState by nuevoEventoViewModel.uiState.collectAsStateWithLifecycle()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    when (val p = pantalla) {

                        is Pantalla.Bienvenida -> BienvenidaScreen(
                            modifier = Modifier.padding(innerPadding),
                            onIniciarClick = { pantalla = Pantalla.Inicio }
                        )

                        is Pantalla.Inicio -> InicioScreen(
                            modifier = Modifier.padding(innerPadding),
                            viewModel = inicioViewModel,
                            onCrearEvento = { pantalla = Pantalla.NuevoEvento },
                            onEventoClick = { id -> pantalla = Pantalla.DetalleEvento(id) }
                        )

                        is Pantalla.NuevoEvento -> NuevoEventoScreen(
                            modifier = Modifier.padding(innerPadding),
                            viewModel = nuevoEventoViewModel,
                            onEventoCreado = { evento ->
                                inicioViewModel.agregarEvento(evento)
                                nuevoEventoViewModel.resetearFormulario()
                                pantalla = Pantalla.Inicio
                            },
                            onCancelar = {
                                nuevoEventoViewModel.resetearFormulario()
                                pantalla = Pantalla.Inicio
                            },
                            onSeleccionarPaquete = { pantalla = Pantalla.SeleccionarPaquete }
                        )

                        is Pantalla.EditarEvento -> {
                            val evento = eventos.find { it.id == p.eventoId }
                            if (evento != null) {
                                LaunchedEffect(p.eventoId) {
                                    nuevoEventoViewModel.cargarDesdeEvento(evento)
                                }
                                NuevoEventoScreen(
                                    modifier = Modifier.padding(innerPadding),
                                    viewModel = nuevoEventoViewModel,
                                    onEventoCreado = { eventoActualizado ->
                                        inicioViewModel.actualizarEvento(eventoActualizado)
                                        nuevoEventoViewModel.resetearFormulario()
                                        pantalla = Pantalla.DetalleEvento(p.eventoId)
                                    },
                                    onCancelar = {
                                        nuevoEventoViewModel.resetearFormulario()
                                        pantalla = Pantalla.DetalleEvento(p.eventoId)
                                    },
                                    onSeleccionarPaquete = { pantalla = Pantalla.SeleccionarPaquete }
                                )
                            } else {
                                pantalla = Pantalla.Inicio
                            }
                        }

                        is Pantalla.SeleccionarPaquete -> PaquetesScreen(
                            modifier = Modifier.padding(innerPadding),
                            paqueteActual = nuevoEventoState.paqueteSeleccionado,
                            onPaqueteSeleccionado = { paquete ->
                                nuevoEventoViewModel.onPaqueteSeleccionado(paquete)
                                // Return to whichever form launched the selector
                                pantalla = if (nuevoEventoState.esModoEdicion && nuevoEventoState.eventoIdEdicion != null) {
                                    Pantalla.EditarEvento(nuevoEventoState.eventoIdEdicion!!)
                                } else {
                                    Pantalla.NuevoEvento
                                }
                            },
                            onCancelar = {
                                pantalla = if (nuevoEventoState.esModoEdicion && nuevoEventoState.eventoIdEdicion != null) {
                                    Pantalla.EditarEvento(nuevoEventoState.eventoIdEdicion!!)
                                } else {
                                    Pantalla.NuevoEvento
                                }
                            }
                        )

                        is Pantalla.DetalleEvento -> {
                            val evento = eventos.find { it.id == p.eventoId }
                            if (evento != null) {
                                DetalleEventoScreen(
                                    modifier = Modifier.padding(innerPadding),
                                    evento = evento,
                                    onRegresar = { pantalla = Pantalla.Inicio },
                                    onRegistrarAbono = { pantalla = Pantalla.RegistrarAbono(p.eventoId) },
                                    onVerCotizacion = { pantalla = Pantalla.Cotizacion(p.eventoId) },
                                    onEditar = { pantalla = Pantalla.EditarEvento(p.eventoId) },
                                    onEliminar = {
                                        inicioViewModel.eliminarEvento(p.eventoId)
                                        pantalla = Pantalla.Inicio
                                    }
                                )
                            } else {
                                pantalla = Pantalla.Inicio
                            }
                        }

                        is Pantalla.RegistrarAbono -> RegistrarAbonoScreen(
                            modifier = Modifier.padding(innerPadding),
                            onAbonoRegistrado = { pago ->
                                inicioViewModel.registrarAbono(p.eventoId, pago)
                                pantalla = Pantalla.DetalleEvento(p.eventoId)
                            },
                            onCancelar = { pantalla = Pantalla.DetalleEvento(p.eventoId) }
                        )

                        is Pantalla.Cotizacion -> {
                            val evento = eventos.find { it.id == p.eventoId }
                            if (evento != null) {
                                CotizacionScreen(
                                    modifier = Modifier.padding(innerPadding),
                                    evento = evento,
                                    onRegresar = { pantalla = Pantalla.DetalleEvento(p.eventoId) }
                                )
                            } else {
                                pantalla = Pantalla.Inicio
                            }
                        }
                    }
                }
            }
        }
    }
}
