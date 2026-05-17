package com.itson.proyectoevento.ui.usuarios

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.itson.proyectoevento.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuariosScreen(
    modifier: Modifier = Modifier,
    viewModel: UsuariosViewModel,
    onRegresar: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }
    var usuarioAEditar by remember { mutableStateOf<Usuario?>(null) }
    val colorPrincipal = Color(0xFF07505A)
    var usuarioAEliminar by remember { mutableStateOf<Usuario?>(null) }
    var mensajeError by remember { mutableStateOf<String?>(null) }


    LaunchedEffect(uiState) {
        if (uiState is UsuariosState.Error) {
            mensajeError = (uiState as UsuariosState.Error).mensaje
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Usuarios", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorPrincipal),
                navigationIcon = {
                    IconButton(onClick = onRegresar) {
                        Image(
                            painter = painterResource(id = R.drawable.icon),
                            contentDescription = "Logo",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = colorPrincipal,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Person, contentDescription = "Agregar Usuario")
            }
        }
    ) { padding ->
        Column(modifier = modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is UsuariosState.Idle, is UsuariosState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = colorPrincipal)
                    }
                }
                is UsuariosState.Success -> {
                    if (state.lista.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No hay usuarios registrados", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                            items(state.lista) { usuario ->
                                UsuarioItem(
                                    usuario = usuario,
                                    onEdit = { usuarioAEditar = usuario },
                                    onDelete = { usuarioAEliminar = usuario }
                                )
                            }
                        }
                    }
                }
                is UsuariosState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Hubo un problema. Revisa la ventana de error.", color = Color.Gray)
                    }
                }
            }
        }
    }

    if (mensajeError != null) {
        AlertDialog(
            onDismissRequest = { mensajeError = null },
            title = { Text("¡Ups! Algo salió mal", fontWeight = FontWeight.Bold) },
            text = { Text(mensajeError ?: "Error desconocido.") },
            confirmButton = {
                Button(
                    onClick = { mensajeError = null },
                    colors = ButtonDefaults.buttonColors(containerColor = colorPrincipal)
                ) {
                    Text("Entendido", color = Color.White)
                }
            }
        )
    }

    usuarioAEliminar?.let { usuario ->
        AlertDialog(
            onDismissRequest = { usuarioAEliminar = null },
            title = { Text("Confirmar Eliminación", fontWeight = FontWeight.Bold) },
            text = { Text("¿Estás seguro de que deseas eliminar permanentemente a ${usuario.nombre}? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.eliminarUsuario(usuario.id)
                        usuarioAEliminar = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Eliminar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { usuarioAEliminar = null }) {
                    Text("Cancelar", color = colorPrincipal)
                }
            }
        )
    }

    if (showDialog) {
        AddUserDialog(
            onDismiss = { showDialog = false },
            onConfirm = { nombre, correo, pass ->
                viewModel.crearUsuario(nombre, correo, pass)
                showDialog = false
            }
        )
    }
    usuarioAEditar?.let { usuario ->
        EditUserDialog(
            usuario = usuario,
            onDismiss = { usuarioAEditar = null },
            onConfirm = { id, nuevoNombre, nuevoRol ->
                viewModel.actualizarUsuario(id, nuevoNombre, nuevoRol)
                usuarioAEditar = null
            }
        )
    }
}

@Composable
fun EditUserDialog(
    usuario: Usuario,
    onDismiss: () -> Unit,
    onConfirm: (id: String, nombre: String, rol: String) -> Unit
) {
    var nombre by remember { mutableStateOf(usuario.nombre) }
    var rol by remember { mutableStateOf(usuario.rol) }
    val colorPrincipal = Color(0xFF07505A)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Usuario", color = colorPrincipal, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(
                    value = usuario.correo,
                    onValueChange = { },
                    label = { Text("Correo (No editable)") },
                    enabled = false,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = Color.Gray,
                        disabledBorderColor = Color.LightGray,
                        disabledLabelColor = Color.Gray
                    )
                )
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colorPrincipal,
                        unfocusedTextColor = colorPrincipal,
                        focusedBorderColor = colorPrincipal,
                        focusedLabelColor = colorPrincipal
                    )
                )
                OutlinedTextField(
                    value = rol,
                    onValueChange = { rol = it },
                    label = { Text("Rol (ej. admin o cliente)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colorPrincipal,
                        unfocusedTextColor = colorPrincipal,
                        focusedBorderColor = colorPrincipal,
                        focusedLabelColor = colorPrincipal
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(usuario.id, nombre, rol) },
                colors = ButtonDefaults.buttonColors(containerColor = colorPrincipal)
            ) { Text("Actualizar", color = Color.White) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = colorPrincipal) }
        }
    )
}

@Composable
fun UsuarioItem(usuario: Usuario, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F4F4))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(usuario.nombre, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF07505A))
                Text(usuario.correo, color = Color.Gray, fontSize = 14.sp)
                Text("Rol: ${usuario.rol}", fontSize = 12.sp, color = Color(0xFF07505A).copy(alpha = 0.7f))
            }
            IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, "Editar", tint = Color(0xFF07505A)) }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, "Eliminar", tint = Color.Red) }
        }
    }
}

@Composable
fun AddUserDialog(onDismiss: () -> Unit, onConfirm: (String, String, String) -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    val colorPrincipal = Color(0xFF07505A)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Usuario", color = colorPrincipal, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colorPrincipal,
                        unfocusedTextColor = colorPrincipal,
                        focusedBorderColor = colorPrincipal,
                        focusedLabelColor = colorPrincipal
                    )
                )
                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = { Text("Correo") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colorPrincipal,
                        unfocusedTextColor = colorPrincipal,
                        focusedBorderColor = colorPrincipal,
                        focusedLabelColor = colorPrincipal
                    )
                )
                OutlinedTextField(
                    value = pass,
                    onValueChange = { pass = it },
                    label = { Text("Contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colorPrincipal,
                        unfocusedTextColor = colorPrincipal,
                        focusedBorderColor = colorPrincipal,
                        focusedLabelColor = colorPrincipal
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(nombre, correo, pass) },
                colors = ButtonDefaults.buttonColors(containerColor = colorPrincipal)
            ) { Text("Guardar", color = Color.White) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = colorPrincipal) }
        }
    )
}
