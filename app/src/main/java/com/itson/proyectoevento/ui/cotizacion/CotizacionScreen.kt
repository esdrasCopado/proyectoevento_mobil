package com.itson.proyectoevento.ui.cotizacion

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.itson.proyectoevento.data.model.Evento
import com.itson.proyectoevento.util.PdfGenerator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CotizacionScreen(
    modifier: Modifier = Modifier,
    evento: Evento,
    onRegresar: () -> Unit = {}
) {
    val context = LocalContext.current
    val totalPagado = evento.pagos.sumOf { it.monto }
    val saldoPendiente = evento.totalCosto - totalPagado
    val colorPrincipal = Color(0xFF07505A)

    fun compartirWhatsApp() {
        val texto = buildString {
            appendLine("*Cotización: ${evento.nombre}*")
            appendLine("Tipo: ${evento.tipo}  |  Fecha: ${evento.fecha}")
            if (evento.nombreCliente.isNotBlank()) appendLine("Cliente: ${evento.nombreCliente}")
            evento.paquete?.let { appendLine("Paquete: ${it.nombre} — $${"%.2f".format(it.precio)}") }
            appendLine()
            appendLine("*Resumen de pago:*")
            appendLine("Total: $${"%.2f".format(evento.totalCosto)}")
            appendLine("Pagado: $${"%.2f".format(totalPagado)}")
            append("Saldo: $${"%.2f".format(saldoPendiente)}")
        }
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://wa.me/?text=${Uri.encode(texto)}")
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, texto)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Compartir cotización"))
        }
    }

    fun exportarPdf() {
        val file = PdfGenerator.generarCotizacion(context, evento)
        val uri = FileProvider.getUriForFile(
            context,
            "com.itson.proyectoevento.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Exportar PDF"))
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TopAppBar(
            title = { Text("Cotización", color = Color.Black) },
            navigationIcon = {
                IconButton(onClick = onRegresar) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar", tint = Color.Black)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // ── Encabezado ────────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = colorPrincipal,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "COTIZACIÓN DE EVENTO",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        evento.nombre,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Información general ───────────────────────────────────────
            SeccionCotizacion("Información del Evento", colorPrincipal)
            Spacer(modifier = Modifier.height(8.dp))
            FilaCotizacion("Tipo de evento", evento.tipo)
            FilaCotizacion("Fecha", evento.fecha)

            Spacer(modifier = Modifier.height(16.dp))

            // ── Datos del cliente ─────────────────────────────────────────
            if (evento.nombreCliente.isNotBlank()) {
                SeccionCotizacion("Datos del Cliente", colorPrincipal)
                Spacer(modifier = Modifier.height(8.dp))
                FilaCotizacion("Nombre", evento.nombreCliente)
                if (evento.telefonoCliente.isNotBlank()) FilaCotizacion("Teléfono", evento.telefonoCliente)
                if (evento.correoCliente.isNotBlank()) FilaCotizacion("Correo", evento.correoCliente)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // ── Paquete ───────────────────────────────────────────────────
            if (evento.paquete != null) {
                SeccionCotizacion("Paquete: ${evento.paquete.nombre}", colorPrincipal)
                Spacer(modifier = Modifier.height(8.dp))
                evento.paquete.incluidos.forEach { item ->
                    Text(
                        "✓  $item",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = Color.Black
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // ── Desglose de costos ────────────────────────────────────────
            SeccionCotizacion("Desglose de Costos", colorPrincipal)
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFEEEEEE),
                    contentColor = Color.Black
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (evento.paquete != null) {
                        FilaImporte("Paquete ${evento.paquete.nombre}", evento.paquete.precio)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray)
                    }
                    FilaImporte("Total del evento", evento.totalCosto, negrita = true)
                    Spacer(modifier = Modifier.height(8.dp))
                    FilaImporte("Total pagado", totalPagado, color = colorPrincipal)
                    FilaImporte(
                        "Saldo pendiente", saldoPendiente,
                        color = if (saldoPendiente > 0) Color.Red else colorPrincipal,
                        negrita = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Historial de pagos ────────────────────────────────────────
            if (evento.pagos.isNotEmpty()) {
                SeccionCotizacion("Historial de Pagos", colorPrincipal)
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFEEEEEE),
                        contentColor = Color.Black
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        evento.pagos.forEachIndexed { index, pago ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(pago.concepto, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                    Text(pago.fecha, style = MaterialTheme.typography.bodySmall,
                                        color = Color.Black.copy(alpha = 0.6f))
                                }
                                Text(
                                    "$${"%.2f".format(pago.monto)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = colorPrincipal
                                )
                            }
                            if (index < evento.pagos.lastIndex) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // ── Estado final ──────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (saldoPendiente <= 0) colorPrincipal else Color(0xFFFFEBEE),
                    contentColor = if (saldoPendiente <= 0) Color.White else Color.Red
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (saldoPendiente <= 0) "✓ Evento liquidado"
                    else "⚠ Saldo pendiente: $${"%.2f".format(saldoPendiente)}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Botones de acción ─────────────────────────────────────────
            Button(
                onClick = { compartirWhatsApp() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF25D366), // WhatsApp green
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("📲  Compartir por WhatsApp", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = { exportarPdf() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = colorPrincipal),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("📄  Exportar PDF", fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SeccionCotizacion(titulo: String, color: Color) {
    Column {
        Text(titulo, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.ExtraBold,
            color = color)
        HorizontalDivider(modifier = Modifier.padding(top = 4.dp), color = color.copy(alpha = 0.3f))
    }
}

@Composable
private fun FilaCotizacion(etiqueta: String, valor: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(etiqueta, style = MaterialTheme.typography.bodyMedium,
            color = Color.Black.copy(alpha = 0.6f))
        Text(valor, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color.Black)
    }
}

@Composable
private fun FilaImporte(
    etiqueta: String,
    monto: Double,
    negrita: Boolean = false,
    color: Color = Color.Black
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            etiqueta, style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (negrita) FontWeight.Bold else FontWeight.Normal,
            color = Color.Black
        )
        Text(
            "$${"%.2f".format(monto)}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (negrita) FontWeight.Bold else FontWeight.Normal,
            color = color
        )
    }
}
