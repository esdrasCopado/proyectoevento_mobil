package com.itson.proyectoevento.ui.paquetes

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.itson.proyectoevento.data.model.Paquete
import com.itson.proyectoevento.data.repository.PaquetesRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaquetesScreen(
    modifier: Modifier = Modifier,
    paqueteActual: Paquete? = null,
    onPaqueteSeleccionado: (Paquete) -> Unit = {},
    onCancelar: () -> Unit = {}
) {
    val colorPrincipal = Color(0xFF07505A)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TopAppBar(
            title = { Text("Seleccionar Paquete", color = Color.Black) },
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
                text = "Elige el paquete que mejor se adapte al evento",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            PaquetesRepository.paquetes.forEach { paquete ->
                PaqueteCard(
                    paquete = paquete,
                    seleccionado = paqueteActual?.id == paquete.id,
                    onSeleccionar = { onPaqueteSeleccionado(paquete) },
                    colorPrincipal = colorPrincipal
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun PaqueteCard(
    paquete: Paquete,
    seleccionado: Boolean,
    onSeleccionar: () -> Unit,
    colorPrincipal: Color
) {
    val containerColor = if (seleccionado) {
        Color(0xFFEEEEEE) // Gris para seleccionado
    } else {
        Color(0xFFF5F5F5) // Gris más claro para no seleccionado
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = Color.Black
        ),
        elevation = if (seleccionado) CardDefaults.cardElevation(defaultElevation = 2.dp) else CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Paquete ${paquete.nombre}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (seleccionado) colorPrincipal else Color.Black
                    )
                    Text(
                        text = paquete.descripcion,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black.copy(alpha = 0.6f)
                    )
                }
                if (seleccionado) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Seleccionado",
                        tint = colorPrincipal
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "$${"%.2f".format(paquete.precio)}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = colorPrincipal
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Incluye:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(4.dp))

            paquete.incluidos.forEach { item ->
                Text(
                    text = "• $item",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 1.dp),
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (seleccionado) {
                OutlinedButton(
                    onClick = onSeleccionar,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colorPrincipal),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Paquete seleccionado", fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = onSeleccionar,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorPrincipal,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Seleccionar este paquete", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
