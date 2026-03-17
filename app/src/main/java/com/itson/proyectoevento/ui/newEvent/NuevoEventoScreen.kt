package com.itson.proyectoevento.ui.newEvent

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.itson.proyectoevento.data.model.Evento

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoEventoScreen(
    modifier: Modifier = Modifier,
    viewModel: NuevoEventoViewModel = viewModel(),
    onEventoCreado: (Evento) -> Unit = {},
    onCancelar: () -> Unit = {},
    onSeleccionarPaquete: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Nuevo Evento") },
            navigationIcon = {
                IconButton(onClick = onCancelar) {
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
            // ── Datos del evento ──────────────────────────────────────────
            Text("Datos del evento", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.nombre,
                onValueChange = viewModel::onNombreChange,
                label = { Text("Nombre del evento") },
                placeholder = { Text("Ej: Boda Ana y Luis") },
                isError = uiState.nombreError != null,
                supportingText = uiState.nombreError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            TipoEventoDropdown(
                tipoSeleccionado = uiState.tipo,
                opciones = viewModel.tiposDeEvento,
                onTipoSeleccionado = viewModel::onTipoChange,
                isError = uiState.tipoError != null,
                errorMessage = uiState.tipoError
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.fecha,
                onValueChange = viewModel::onFechaChange,
                label = { Text("Fecha del evento") },
                placeholder = { Text("DD/MM/AAAA") },
                isError = uiState.fechaError != null,
                supportingText = uiState.fechaError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // ── Datos del cliente ─────────────────────────────────────────
            Text("Datos del cliente", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.nombreCliente,
                onValueChange = viewModel::onNombreClienteChange,
                label = { Text("Nombre completo del cliente") },
                placeholder = { Text("Ej: Ana García López") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.telefonoCliente,
                onValueChange = viewModel::onTelefonoClienteChange,
                label = { Text("Teléfono / WhatsApp") },
                placeholder = { Text("Ej: 6441234567") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.correoCliente,
                onValueChange = viewModel::onCorreoClienteChange,
                label = { Text("Correo electrónico") },
                placeholder = { Text("Ej: cliente@email.com") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // ── Paquete ───────────────────────────────────────────────────
            Text("Paquete", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(12.dp))

            val paqueteSeleccionado = uiState.paqueteSeleccionado
            if (paqueteSeleccionado != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "  Paquete ${paqueteSeleccionado.nombre}",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$${"%.2f".format(paqueteSeleccionado.precio)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        TextButton(onClick = { viewModel.quitarPaquete() }) {
                            Text("Cambiar paquete")
                        }
                    }
                }
            } else {
                OutlinedButton(
                    onClick = onSeleccionarPaquete,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("  Seleccionar paquete (opcional)")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // ── Costos y pago inicial ─────────────────────────────────────
            Text("Costos y pago inicial", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.totalCosto,
                onValueChange = viewModel::onTotalCostoChange,
                label = { Text("Costo total") },
                placeholder = { Text("Ej: 50000") },
                prefix = { Text("$") },
                isError = uiState.totalCostoError != null,
                supportingText = uiState.totalCostoError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.adelanto,
                onValueChange = viewModel::onAdelantoChange,
                label = { Text("Anticipo / Adelanto (opcional)") },
                placeholder = { Text("Ej: 10000") },
                prefix = { Text("$") },
                isError = uiState.adelantoError != null,
                supportingText = uiState.adelantoError?.let { { Text(it) } }
                    ?: { Text("Déjalo vacío si aún no hay anticipo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            if (uiState.adelanto.isNotBlank() && uiState.totalCosto.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                ResumenPagoCard(
                    porcentaje = uiState.porcentajeCalculado,
                    adelanto = uiState.adelanto.toDoubleOrNull() ?: 0.0,
                    totalCosto = uiState.totalCosto.toDoubleOrNull() ?: 0.0
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.validarYCrearEvento(onEventoCreado) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar evento")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onCancelar,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar")
            }
        }
    }
}

@Composable
private fun ResumenPagoCard(porcentaje: Int, adelanto: Double, totalCosto: Double) {
    val restante = totalCosto - adelanto
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Resumen de pago",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { porcentaje / 100f },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Anticipo: $${"%.2f".format(adelanto)}  •  Restante: $${"%.2f".format(restante)}  •  $porcentaje%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TipoEventoDropdown(
    tipoSeleccionado: String,
    opciones: List<String>,
    onTipoSeleccionado: (String) -> Unit,
    isError: Boolean,
    errorMessage: String?
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = tipoSeleccionado,
            onValueChange = {},
            readOnly = true,
            label = { Text("Tipo de evento") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            isError = isError,
            supportingText = errorMessage?.let { { Text(it) } },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = {
                        onTipoSeleccionado(opcion)
                        expanded = false
                    }
                )
            }
        }
    }
}
