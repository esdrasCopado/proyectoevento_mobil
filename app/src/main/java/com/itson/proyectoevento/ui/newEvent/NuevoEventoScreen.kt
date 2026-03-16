package com.itson.proyectoevento.ui.newEvent

import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    onCancelar: () -> Unit = {}
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
            Text(
                text = "Datos del evento",
                style = MaterialTheme.typography.titleMedium
            )

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

            Spacer(modifier = Modifier.height(12.dp))

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
