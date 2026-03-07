package com.itson.proyectoevento.ui.inicio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.itson.proyectoevento.data.model.Evento

@Composable
fun InicioScreen(
    modifier: Modifier = Modifier,
    viewModel: InicioViewModel = viewModel(),
    onCrearEvento: () -> Unit = {}
) {
    val eventos by viewModel.eventos.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        HeaderSection(uiState)

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onCrearEvento,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Crear Evento")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Eventos registrados",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        eventos.forEach { evento ->
            EventoCard(evento = evento)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }  // ✅ solo una llave aquí
}

@Composable
fun HeaderSection(uiState: InicioUiState) {  // ✅ InicioUiState, no HomeUiState
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ResumenCard("Total eventos", uiState.totalEventos.toString())
        ResumenCard("Pendientes de pago", uiState.eventosPendientesPago.toString())
    }
}

@Composable
fun ResumenCard(titulo: String, valor: String) {
    Card(modifier = Modifier.padding(4.dp)) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(valor, style = MaterialTheme.typography.headlineMedium)
            Text(titulo, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun EventoCard(evento: Evento) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Nombre y tipo del evento
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(evento.nombre, style = MaterialTheme.typography.titleSmall)
                Text(evento.tipo, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Fecha
            Text(
                text = "📅 ${evento.fecha}",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Porcentaje de pago
            Text(
                text = "Pago: ${evento.porcentajePagado}%",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Barra de progreso
            LinearProgressIndicator(
                progress = { evento.porcentajePagado / 100f },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}