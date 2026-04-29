package com.itson.proyectoevento.ui.newEvent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
    val colorPrincipal = Color(0xFF07505A)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TopAppBar(
            title = { Text(if (uiState.esModoEdicion) "Editar Evento" else "Nuevo Evento", color = Color.Black) },
            navigationIcon = {
                IconButton(onClick = onCancelar) {
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
            // ── Datos del evento ──────────────────────────────────────────
            SeccionTitulo("Datos del evento", colorPrincipal)

            Spacer(modifier = Modifier.height(16.dp))

            CustomOutlinedTextField(
                value = uiState.nombre,
                onValueChange = viewModel::onNombreChange,
                label = "Nombre del evento",
                placeholder = "Ej: Boda Ana y Luis",
                isError = uiState.nombreError != null,
                supportingText = uiState.nombreError,
                colorPrincipal = colorPrincipal
            )

            Spacer(modifier = Modifier.height(12.dp))

            TipoEventoDropdown(
                tipoSeleccionado = uiState.tipo,
                opciones = viewModel.tiposDeEvento,
                onTipoSeleccionado = viewModel::onTipoChange,
                isError = uiState.tipoError != null,
                errorMessage = uiState.tipoError,
                colorPrincipal = colorPrincipal
            )

            Spacer(modifier = Modifier.height(12.dp))

            CustomOutlinedTextField(
                value = uiState.fecha,
                onValueChange = viewModel::onFechaChange,
                label = "Fecha del evento",
                placeholder = "DD/MM/AAAA",
                isError = uiState.fechaError != null,
                supportingText = uiState.fechaError,
                colorPrincipal = colorPrincipal,
                keyboardType = KeyboardType.Number
            )

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(16.dp))

            // ── Datos del cliente ─────────────────────────────────────────
            SeccionTitulo("Datos del cliente", colorPrincipal)

            Spacer(modifier = Modifier.height(16.dp))

            CustomOutlinedTextField(
                value = uiState.nombreCliente,
                onValueChange = viewModel::onNombreClienteChange,
                label = "Nombre completo del cliente",
                placeholder = "Ej: Ana García López",
                colorPrincipal = colorPrincipal
            )

            Spacer(modifier = Modifier.height(12.dp))

            CustomOutlinedTextField(
                value = uiState.telefonoCliente,
                onValueChange = viewModel::onTelefonoClienteChange,
                label = "Teléfono / WhatsApp",
                placeholder = "Ej: 6441234567",
                colorPrincipal = colorPrincipal,
                keyboardType = KeyboardType.Phone
            )

            Spacer(modifier = Modifier.height(12.dp))

            CustomOutlinedTextField(
                value = uiState.correoCliente,
                onValueChange = viewModel::onCorreoClienteChange,
                label = "Correo electrónico",
                placeholder = "Ej: cliente@email.com",
                colorPrincipal = colorPrincipal,
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(16.dp))

            // ── Paquete ───────────────────────────────────────────────────
            SeccionTitulo("Paquete", colorPrincipal)

            Spacer(modifier = Modifier.height(12.dp))

            val paqueteSeleccionado = uiState.paqueteSeleccionado
            if (paqueteSeleccionado != null) {
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
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = colorPrincipal
                            )
                            Text(
                                text = "  Paquete ${paqueteSeleccionado.nombre}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$${"%.2f".format(paqueteSeleccionado.precio)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colorPrincipal,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(
                            onClick = { viewModel.quitarPaquete() },
                            colors = ButtonDefaults.textButtonColors(contentColor = colorPrincipal)
                        ) {
                            Text("Cambiar paquete")
                        }
                    }
                }
            } else {
                OutlinedButton(
                    onClick = onSeleccionarPaquete,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colorPrincipal)
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Seleccionar paquete (opcional)")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(16.dp))

            // ── Costos y pago inicial ─────────────────────────────────────
            SeccionTitulo(if (uiState.esModoEdicion) "Costos" else "Costos y pago inicial", colorPrincipal)

            Spacer(modifier = Modifier.height(16.dp))

            CustomOutlinedTextField(
                value = uiState.totalCosto,
                onValueChange = viewModel::onTotalCostoChange,
                label = "Costo total",
                placeholder = "Ej: 50000",
                prefix = "$",
                isError = uiState.totalCostoError != null,
                supportingText = uiState.totalCostoError,
                colorPrincipal = colorPrincipal,
                keyboardType = KeyboardType.Decimal
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (!uiState.esModoEdicion) {
                CustomOutlinedTextField(
                    value = uiState.adelanto,
                    onValueChange = viewModel::onAdelantoChange,
                    label = "Anticipo / Adelanto (opcional)",
                    placeholder = "Ej: 10000",
                    prefix = "$",
                    isError = uiState.adelantoError != null,
                    supportingText = uiState.adelantoError ?: "Déjalo vacío si aún no hay anticipo",
                    colorPrincipal = colorPrincipal,
                    keyboardType = KeyboardType.Decimal
                )

                if (uiState.adelanto.isNotBlank() && uiState.totalCosto.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    ResumenPagoCard(
                        porcentaje = uiState.porcentajeCalculado,
                        adelanto = uiState.adelanto.toDoubleOrNull() ?: 0.0,
                        totalCosto = uiState.totalCosto.toDoubleOrNull() ?: 0.0,
                        colorPrincipal = colorPrincipal
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.validarYCrearEvento(onEventoCreado) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorPrincipal,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Guardar evento", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onCancelar,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = colorPrincipal),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cancelar")
            }
        }
    }
}

@Composable
private fun SeccionTitulo(texto: String, color: Color) {
    Text(
        texto,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = color
    )
}

@Composable
private fun CustomOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    isError: Boolean = false,
    supportingText: String? = null,
    prefix: String? = null,
    colorPrincipal: Color,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        isError = isError,
        supportingText = supportingText?.let { { Text(it) } },
        prefix = prefix?.let { { Text(it) } },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = colorPrincipal,
            focusedLabelColor = colorPrincipal,
            cursorColor = colorPrincipal
        )
    )
}

@Composable
private fun ResumenPagoCard(porcentaje: Int, adelanto: Double, totalCosto: Double, colorPrincipal: Color) {
    val restante = totalCosto - adelanto
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFEEEEEE),
            contentColor = Color.Black
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Resumen de pago",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = colorPrincipal
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { porcentaje / 100f },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = colorPrincipal,
                trackColor = colorPrincipal.copy(alpha = 0.1f)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Anticipo: $${"%.2f".format(adelanto)}  •  Restante: $${"%.2f".format(restante)}  •  $porcentaje%",
                style = MaterialTheme.typography.bodySmall
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
    errorMessage: String?,
    colorPrincipal: Color
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
                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorPrincipal,
                focusedLabelColor = colorPrincipal,
                cursorColor = colorPrincipal
            )
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
