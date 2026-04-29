package com.itson.proyectoevento.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.itson.proyectoevento.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel,
    onRegistroExitoso: () -> Unit,
    onIrALogin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colorPrincipal = Color(0xFF07505A)

    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmarContrasena by remember { mutableStateOf("") }
    var mostrarContrasena by remember { mutableStateOf(false) }
    var mostrarConfirmar by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear cuenta", color = colorPrincipal, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onIrALogin) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar", tint = colorPrincipal)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(id = R.drawable.icon),
                contentDescription = "Logo",
                modifier = Modifier.size(100.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Regístrate en RevelApp",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = colorPrincipal
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = {
                        nombre = it
                        viewModel.limpiarError()
                    },
                    label = { Text("Nombre completo") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null, tint = colorPrincipal)
                    },
                    isError = uiState.error != null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colorPrincipal,
                        unfocusedTextColor = colorPrincipal,
                        focusedBorderColor = colorPrincipal,
                        focusedLabelColor = colorPrincipal
                    )
                )

                OutlinedTextField(
                    value = correo,
                    onValueChange = {
                        correo = it
                        viewModel.limpiarError()
                    },
                    label = { Text("Correo electrónico") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    isError = uiState.error != null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colorPrincipal,
                        unfocusedTextColor = colorPrincipal,
                        focusedBorderColor = colorPrincipal,
                        focusedLabelColor = colorPrincipal
                    )
                )

                OutlinedTextField(
                    value = contrasena,
                    onValueChange = {
                        contrasena = it
                        viewModel.limpiarError()
                    },
                    label = { Text("Contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    visualTransformation = if (mostrarContrasena) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { mostrarContrasena = !mostrarContrasena }) {
                            Icon(
                                imageVector = if (mostrarContrasena) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                tint = colorPrincipal
                            )
                        }
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = colorPrincipal)
                    },
                    isError = uiState.error != null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colorPrincipal,
                        unfocusedTextColor = colorPrincipal,
                        focusedBorderColor = colorPrincipal,
                        focusedLabelColor = colorPrincipal
                    )
                )

                OutlinedTextField(
                    value = confirmarContrasena,
                    onValueChange = {
                        confirmarContrasena = it
                        viewModel.limpiarError()
                    },
                    label = { Text("Confirmar contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    visualTransformation = if (mostrarConfirmar) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { mostrarConfirmar = !mostrarConfirmar }) {
                            Icon(
                                imageVector = if (mostrarConfirmar) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                tint = colorPrincipal
                            )
                        }
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = colorPrincipal)
                    },
                    isError = uiState.error != null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colorPrincipal,
                        unfocusedTextColor = colorPrincipal,
                        focusedBorderColor = colorPrincipal,
                        focusedLabelColor = colorPrincipal
                    )
                )

                if (uiState.error != null) {
                    Text(
                        text = uiState.error!!,
                        color = Color.Red,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Button(
                    onClick = {
                        viewModel.registrar(correo, contrasena, confirmarContrasena, nombre, onRegistroExitoso)
                    },
                    enabled = !uiState.cargando,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorPrincipal)
                ) {
                    if (uiState.cargando) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("REGISTRARSE", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                TextButton(
                    onClick = onIrALogin,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "¿Ya tienes cuenta? Inicia sesión",
                        color = colorPrincipal
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
