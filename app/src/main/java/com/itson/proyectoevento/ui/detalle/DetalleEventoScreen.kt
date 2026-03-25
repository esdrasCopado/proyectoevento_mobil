package com.itson.proyectoevento.ui.detalle

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.itson.proyectoevento.R
import com.itson.proyectoevento.data.model.Evento
import com.itson.proyectoevento.data.model.Pago

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleEventoScreen(
    modifier: Modifier = Modifier,
    evento: Evento,
    onRegresar: () -> Unit = {},
    onRegistrarAbono: () -> Unit = {},
    onVerCotizacion: () -> Unit = {},
    onEditar: () -> Unit = {},
    onEliminar: () -> Unit = {}
) {
    var menuExpandido by remember { mutableStateOf(false) }
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }
    val colorPrincipal = Color(0xFF07505A)

    if (mostrarDialogoEliminar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminar = false },
            title = { Text("Eliminar evento") },
            text = { Text("¿Estás seguro de que deseas eliminar \"${evento.nombre}\"? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoEliminar = false
                        onEliminar()
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoEliminar = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TopAppBar(
            title = { Text(evento.nombre, maxLines = 1) },
            navigationIcon = {
                IconButton(onClick = onRegresar) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                }
            },
            actions = {
                IconButton(onClick = { menuExpandido = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Opciones")
                }
                DropdownMenu(
                    expanded = menuExpandido,
                    onDismissRequest = { menuExpandido = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Editar evento") },
                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                        onClick = {
                            menuExpandido = false
                            onEditar()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Eliminar evento", color = MaterialTheme.colorScheme.error) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        },
                        onClick = {
                            menuExpandido = false
                            mostrarDialogoEliminar = true
                        }
                    )
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Image(
                painter = painterResource(id = R.drawable.banner1),
                contentDescription = "Banner del evento",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(evento.tipo, style = MaterialTheme.typography.labelLarge,
                    color = colorPrincipal, fontWeight = FontWeight.Bold)
                Text("📅 ${evento.fecha}", style = MaterialTheme.typography.bodyMedium, color = Color.Black)
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // ── Datos del cliente ─────────────────────────────────────────
            if (evento.nombreCliente.isNotBlank() || evento.telefonoCliente.isNotBlank() || evento.correoCliente.isNotBlank()) {
                SeccionTitulo("Datos del Cliente", colorPrincipal)
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFEEEEEE),
                        contentColor = Color.Black
                    )
                ) {
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
                SeccionTitulo("Paquete Contratado", colorPrincipal)
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFEEEEEE),
                        contentColor = Color.Black
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
                                color = colorPrincipal
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
            SeccionTitulo("Estado de Pago", colorPrincipal)
            Spacer(modifier = Modifier.height(8.dp))

            val totalPagado = evento.pagos.sumOf { it.monto }
            val restante = evento.totalCosto - totalPagado

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFEEEEEE),
                    contentColor = Color.Black
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    FilaImporte("Total del evento", evento.totalCosto, negrita = true)
                    Spacer(modifier = Modifier.height(4.dp))
                    FilaImporte("Total pagado", totalPagado, color = colorPrincipal)
                    Spacer(modifier = Modifier.height(4.dp))
                    FilaImporte(
                        "Saldo pendiente", restante,
                        color = if (restante > 0) MaterialTheme.colorScheme.error else colorPrincipal,
                        negrita = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { evento.porcentajePagado / 100f },
                        modifier = Modifier.fillMaxWidth(),
                        color = colorPrincipal,
                        trackColor = colorPrincipal.copy(alpha = 0.1f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "${evento.porcentajePagado}% pagado",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Historial de pagos ────────────────────────────────────────
            SeccionTitulo("Historial de Pagos (${evento.pagos.size})", colorPrincipal)
            Spacer(modifier = Modifier.height(8.dp))

            if (evento.pagos.isEmpty()) {
                Text(
                    "Sin pagos registrados",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Black
                )
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFEEEEEE),
                        contentColor = Color.Black
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        evento.pagos.forEachIndexed { index, pago ->
                            PagoItem(pago = pago, numero = index + 1, accentColor = colorPrincipal)
                            if (index < evento.pagos.lastIndex) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Acciones ──────────────────────────────────────────────────
            if (evento.porcentajePagado < 100) {
                Button(
                    onClick = onRegistrarAbono,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorPrincipal,
                        contentColor = Color.White
                    )
                ) {
                    Text("Registrar Abono")
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            OutlinedButton(
                onClick = onVerCotizacion,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = colorPrincipal
                )
            ) {
                Text("Ver Cotización")
            }
        }
    }
}

@Composable
private fun SeccionTitulo(texto: String, color: Color) {
    Text(texto, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = color)
}

@Composable
private fun FilaInfo(icon: androidx.compose.ui.graphics.vector.ImageVector, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
        Text(texto, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun FilaImporte(
    label: String,
    monto: Double,
    negrita: Boolean = false,
    color: Color = Color.Black
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label, style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (negrita) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            "$${"%.2f".format(monto)}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (negrita) FontWeight.Bold else FontWeight.Normal,
            color = color
        )
    }
}

@Composable
private fun PagoItem(pago: Pago, numero: Int, accentColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("$numero. ${pago.concepto}", style = MaterialTheme.typography.bodyMedium)
            Text(
                "📅 ${pago.fecha}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black.copy(alpha = 0.6f)
            )
        }
        Text(
            "$${"%.2f".format(pago.monto)}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = accentColor
        )
    }
}
