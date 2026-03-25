package com.itson.proyectoevento.ui.inicio

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.itson.proyectoevento.R
import com.itson.proyectoevento.data.model.Evento

@Composable
fun InicioScreen(
    modifier: Modifier = Modifier,
    viewModel: InicioViewModel = viewModel(),
    onCrearEvento: () -> Unit = {},
    onEventoClick: (Int) -> Unit = {}
) {
    val eventos by viewModel.eventos.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colorPrincipal = Color(0xFF07505A) // El color de tu imagen

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Banner Completo
        Image(
            painter = painterResource(id = R.drawable.banner1),
            contentDescription = "Banner Principal",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeaderSection(uiState)

            Spacer(modifier = Modifier.height(24.dp))

            // Botón con el nuevo color #07505a
            Button(
                onClick = onCrearEvento,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorPrincipal,
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Crear Evento")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "Eventos registrados",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            eventos.forEach { evento ->
                EventoCard(
                    evento = evento, 
                    onClick = { onEventoClick(evento.id) },
                    accentColor = colorPrincipal
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun HeaderSection(uiState: InicioUiState) {
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
    Card(
        modifier = Modifier.padding(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5), // Color gris claro
            contentColor = Color.Black
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
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
fun EventoCard(evento: Evento, onClick: () -> Unit = {}, accentColor: Color = MaterialTheme.colorScheme.primary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFEEEEEE), // Un gris más definido
            contentColor = Color.Black // Texto en negro para legibilidad
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(evento.nombre, style = MaterialTheme.typography.titleSmall)
                Text(evento.tipo, style = MaterialTheme.typography.labelSmall, color = accentColor)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "📅 ${evento.fecha}",
                style = MaterialTheme.typography.bodySmall
            )

            if (evento.nombreCliente.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "👤 ${evento.nombreCliente}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Pago: ${evento.porcentajePagado}%",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(4.dp))

            LinearProgressIndicator(
                progress = { evento.porcentajePagado / 100f },
                modifier = Modifier.fillMaxWidth(),
                color = accentColor,
                trackColor = accentColor.copy(alpha = 0.1f)
            )
        }
    }
}
