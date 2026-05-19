package com.itson.proyectoevento.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.itson.proyectoevento.R
import com.itson.proyectoevento.ui.usuarios.UsuariosState
import com.itson.proyectoevento.ui.usuarios.UsuariosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    modifier: Modifier = Modifier,
    viewModel: UsuariosViewModel,
    onRegistroExitoso: () -> Unit,
    onRegresar: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmarContrasena by remember { mutableStateOf("") }
    var errorLocal by remember { mutableStateOf<String?>(null) }
    
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colorPrincipal = Color(0xFF07505A)
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) { viewModel.iniciar() }

    // Detectar transición Loading → Success para navegar tras registro exitoso
    var estabaEnLoading by remember { mutableStateOf(false) }
    LaunchedEffect(uiState) {
        when (uiState) {
            is UsuariosState.Loading -> estabaEnLoading = true
            is UsuariosState.Success -> {
                if (estabaEnLoading) {
                    estabaEnLoading = false
                    snackbarHostState.showSnackbar(
                        message = "¡Usuario registrado con éxito!",
                        duration = SnackbarDuration.Short
                    )
                    onRegistroExitoso()
                }
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    containerColor = colorPrincipal,
                    contentColor = Color.White,
                    snackbarData = data
                )
            }
        },
        topBar = {
            TopAppBar(
                title = { Text("Crear Cuenta", color = colorPrincipal, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onRegresar) {
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
            Spacer(modifier = Modifier.height(24.dp))

            Image(
                painter = painterResource(id = R.drawable.icon),
                contentDescription = "Logo RevelApp",
                modifier = Modifier.size(100.dp),
                contentScale = ContentScale.Fit
            )

            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Regístrate en RevelApp",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorPrincipal
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre completo") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colorPrincipal,
                        unfocusedTextColor = colorPrincipal,
                        focusedBorderColor = colorPrincipal,
                        focusedLabelColor = colorPrincipal
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = { Text("Correo electrónico") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colorPrincipal,
                        unfocusedTextColor = colorPrincipal,
                        focusedBorderColor = colorPrincipal,
                        focusedLabelColor = colorPrincipal
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = contrasena,
                    onValueChange = { contrasena = it },
                    label = { Text("Contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colorPrincipal,
                        unfocusedTextColor = colorPrincipal,
                        focusedBorderColor = colorPrincipal,
                        focusedLabelColor = colorPrincipal
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = confirmarContrasena,
                    onValueChange = { confirmarContrasena = it },
                    label = { Text("Confirmar contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colorPrincipal,
                        unfocusedTextColor = colorPrincipal,
                        focusedBorderColor = colorPrincipal,
                        focusedLabelColor = colorPrincipal
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                val errorState = (uiState as? UsuariosState.Error)?.mensaje
                val mostrarError = errorLocal ?: errorState

                if (mostrarError != null) {
                    Text(
                        text = mostrarError,
                        color = Color.Red,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                Button(
                    onClick = {
                        if (nombre.isBlank() || correo.isBlank() || contrasena.isBlank()) {
                            errorLocal = "Por favor llena todos los campos"
                        } else if (contrasena.length < 6) {
                            errorLocal = "La contraseña debe tener al menos 6 caracteres"
                        } else if (contrasena != confirmarContrasena) {
                            errorLocal = "Las contraseñas no coinciden"
                        } else {
                            errorLocal = null
                            viewModel.crearUsuario(nombre, correo, contrasena)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorPrincipal),
                    enabled = uiState !is UsuariosState.Loading
                ) {
                    if (uiState is UsuariosState.Loading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("REGISTRARSE", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}
