package com.itson.proyectoevento.ui.detalle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.itson.proyectoevento.data.model.Evento
import com.itson.proyectoevento.data.model.Pago

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleEventoScreen(
    modifier: Modifier = Modifier,
    evento: Evento,
    onRegresar: () -> Unit = {},
    onRegistrarAbono: () -> Unit = {},
    onVerCotizacion: () -> Unit = {}
) {
    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(evento.nombre, maxLines = 1) },
            navigationIcon = {
                IconButton(onClick = onRegresar) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // ── Encabezado del evento ─────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(evento.tipo, style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary)
                Text("📅 ${evento.fecha}", style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // ── Datos del cliente ─────────────────────────────────────────
            if (evento.nombreCliente.isNotBlank() || evento.telefonoCliente.isNotBlank() || evento.correoCliente.isNotBlank()) {
                SeccionTitulo("Datos del Cliente")
                Spacer(modifier = Modifier.height(8.dp))

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        if (evento.nombreCliente.isNotBlank()) {
                            FilaInfo(Icons.Default.Person, evento.nombreCliente)
                        }
                        if (evento.telefonoCliente.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            FilaInfo(Icons.Default.Phone, evento.telefonoCliente)
                        }
                        if (evento.correoCliente.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            FilaInfo(Icons.Default.Email, evento.correoCliente)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // ── Paquete ───────────────────────────────────────────────────
            if (evento.paquete != null) {
                SeccionTitulo("Paquete Contratado")
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Paquete ${evento.paquete.nombre}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "$${"%.2f".format(evento.paquete.precio)}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        evento.paquete.incluidos.forEach { item ->
                            Text(
                                "• $item",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(vertical = 1.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // ── Resumen de pagos ──────────────────────────────────────────
            SeccionTitulo("Estado de Pago")
            Spacer(modifier = Modifier.height(8.dp))

            val totalPagado = evento.pagos.sumOf { it.monto }
            val restante = evento.totalCosto - totalPagado

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total del evento", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "$${"%.2f".format(evento.totalCosto)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total pagado", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "$${"%.2f".format(totalPagado)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Saldo pendiente", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "$${"%.2f".format(restante)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (restante > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { evento.porcentajePagado / 100f },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "${evento.porcentajePagado}% pagado",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Historial de pagos ────────────────────────────────────────
            SeccionTitulo("Historial de Pagos (${evento.pagos.size})")
            Spacer(modifier = Modifier.height(8.dp))

            if (evento.pagos.isEmpty()) {
                Text(
                    "Sin pagos registrados",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                evento.pagos.forEachIndexed { index, pago ->
                    PagoItem(pago = pago, numero = index + 1)
                    if (index < evento.pagos.lastIndex) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Acciones ──────────────────────────────────────────────────
            if (evento.porcentajePagado < 100) {
                Button(
                    onClick = onRegistrarAbono,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Registrar Abono")
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            OutlinedButton(
                onClick = onVerCotizacion,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver Cotización")
            }
        }
    }
}

@Composable
private fun SeccionTitulo(texto: String) {
    Text(texto, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
}

@Composable
private fun FilaInfo(icon: androidx.compose.ui.graphics.vector.ImageVector, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
        Text(texto, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun PagoItem(pago: Pago, numero: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "$numero. ${pago.concepto}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "📅 ${pago.fecha}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = "$${"%.2f".format(pago.monto)}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
