package com.itson.proyectoevento.ui.abono

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.itson.proyectoevento.data.model.Pago

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrarAbonoScreen(
    modifier: Modifier = Modifier,
    onAbonoRegistrado: (Pago) -> Unit = {},
    onCancelar: () -> Unit = {}
) {
    var monto by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var concepto by remember { mutableStateOf("Abono") }
    var montoError by remember { mutableStateOf<String?>(null) }
    var fechaError by remember { mutableStateOf<String?>(null) }
    val colorPrincipal = Color(0xFF07505A)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TopAppBar(
            title = { Text("Registrar Abono", color = Color.Black) },
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
            Text(
                "Nuevo abono",
                style = MaterialTheme.typography.titleMedium,
                color = colorPrincipal,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = monto,
                onValueChange = {
                    monto = it
                    montoError = null
                },
                label = { Text("Monto del abono") },
                placeholder = { Text("Ej: 5000") },
                prefix = { Text("$") },
                isError = montoError != null,
                supportingText = montoError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorPrincipal,
                    focusedLabelColor = colorPrincipal,
                    cursorColor = colorPrincipal
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = fecha,
                onValueChange = {
                    fecha = it
                    fechaError = null
                },
                label = { Text("Fecha del pago") },
                placeholder = { Text("DD/MM/AAAA") },
                isError = fechaError != null,
                supportingText = fechaError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorPrincipal,
                    focusedLabelColor = colorPrincipal,
                    cursorColor = colorPrincipal
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = concepto,
                onValueChange = { concepto = it },
                label = { Text("Concepto / Nota") },
                placeholder = { Text("Ej: Segundo abono, Pago final...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorPrincipal,
                    focusedLabelColor = colorPrincipal,
                    cursorColor = colorPrincipal
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    var hayErrores = false
                    val montoDouble = monto.toDoubleOrNull()
                    if (montoDouble == null || montoDouble <= 0) {
                        montoError = "Ingresa un monto válido"
                        hayErrores = true
                    }
                    if (fecha.isBlank()) {
                        fechaError = "La fecha es requerida"
                        hayErrores = true
                    }
                    if (!hayErrores) {
                        val pago = Pago(
                            id = System.currentTimeMillis().toInt(),
                            monto = montoDouble!!,
                            fecha = fecha,
                            concepto = concepto.ifBlank { "Abono" }
                        )
                        onAbonoRegistrado(pago)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorPrincipal,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Guardar abono", fontWeight = FontWeight.Bold)
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
