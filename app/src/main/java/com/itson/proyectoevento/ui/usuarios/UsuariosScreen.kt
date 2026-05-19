package com.itson.proyectoevento.ui.usuarios

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.itson.proyectoevento.R
import com.itson.proyectoevento.data.model.Usuario

private val ColorPrimary     = Color(0xFF07505A)
private val ColorPrimaryFaint = Color(0xFFE6F2F3)
private val ColorSurface     = Color(0xFFF4F8F9)
private val ColorAdmin       = Color(0xFF07505A)
private val ColorAdminText   = Color.White
private val ColorCliente     = Color(0xFFDFF2F0)
private val ColorClienteText = Color(0xFF004D40)
private val ColorDanger      = Color(0xFFD32F2F)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuariosScreen(
    modifier: Modifier = Modifier,
    viewModel: UsuariosViewModel,
    onRegresar: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddDialog    by remember { mutableStateOf(false) }
    var usuarioAEditar   by remember { mutableStateOf<Usuario?>(null) }
    var usuarioAEliminar by remember { mutableStateOf<Usuario?>(null) }
    var mensajeError     by remember { mutableStateOf<String?>(null) }

    BackHandler { onRegresar() }

    // Inicia el listener solo cuando la pantalla se muestra (el usuario ya está autenticado)
    LaunchedEffect(Unit) { viewModel.iniciar() }

    LaunchedEffect(uiState) {
        if (uiState is UsuariosState.Error) {
            mensajeError = (uiState as UsuariosState.Error).mensaje
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Gestión de Usuarios",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ColorPrimary),
                navigationIcon = {
                    IconButton(onClick = onRegresar) {
                        Image(
                            painter = painterResource(id = R.drawable.icon),
                            contentDescription = "Regresar",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = ColorPrimary,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Nuevo Usuario", fontWeight = FontWeight.SemiBold) }
            )
        },
        containerColor = ColorSurface
    ) { padding ->
        when (val state = uiState) {
            is UsuariosState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        CircularProgressIndicator(color = ColorPrimary, strokeWidth = 3.dp)
                        Text("Cargando usuarios...", color = Color.Gray, fontSize = 14.sp)
                    }
                }
            }
            is UsuariosState.Success -> {
                if (state.lista.isEmpty()) {
                    EmptyState(modifier = Modifier.padding(padding))
                } else {
                    LazyColumn(
                        modifier = modifier.padding(padding).fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item { StatsRow(lista = state.lista) }
                        items(state.lista) { usuario ->
                            UsuarioItem(
                                usuario = usuario,
                                onEdit = { usuarioAEditar = usuario },
                                onDelete = { usuarioAEliminar = usuario }
                            )
                        }
                        item { Spacer(Modifier.height(72.dp)) }
                    }
                }
            }
            is UsuariosState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Hubo un problema. Revisa el aviso de error.", color = Color.Gray)
                }
            }
        }
    }

    // ── Dialogs ──────────────────────────────────────────────────────────────

    if (mensajeError != null) {
        AlertDialog(
            onDismissRequest = { mensajeError = null; viewModel.limpiarError() },
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = ColorDanger) },
            title = { Text("Algo salió mal", fontWeight = FontWeight.Bold) },
            text = { Text(mensajeError ?: "Error desconocido.") },
            confirmButton = {
                Button(
                    onClick = { mensajeError = null; viewModel.limpiarError() },
                    colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) { Text("Entendido") }
            }
        )
    }

    usuarioAEliminar?.let { usuario ->
        AlertDialog(
            onDismissRequest = { usuarioAEliminar = null },
            icon = { Icon(Icons.Default.DeleteForever, contentDescription = null, tint = ColorDanger) },
            title = { Text("Eliminar usuario", fontWeight = FontWeight.Bold) },
            text = {
                Text("¿Deseas eliminar permanentemente la cuenta de ${usuario.nombre}?\nEsta acción no se puede deshacer.")
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.eliminarUsuario(usuario.uid); usuarioAEliminar = null },
                    colors = ButtonDefaults.buttonColors(containerColor = ColorDanger),
                    shape = RoundedCornerShape(10.dp)
                ) { Text("Eliminar") }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { usuarioAEliminar = null },
                    shape = RoundedCornerShape(10.dp)
                ) { Text("Cancelar") }
            }
        )
    }

    if (showAddDialog) {
        AddUserDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { nombre, correo, pass ->
                viewModel.crearUsuario(nombre, correo, pass)
                showAddDialog = false
            }
        )
    }

    usuarioAEditar?.let { usuario ->
        EditUserDialog(
            usuario = usuario,
            onDismiss = { usuarioAEditar = null },
            onConfirm = { uid, nuevoNombre, nuevoRol ->
                viewModel.actualizarUsuario(uid, nuevoNombre, nuevoRol)
                usuarioAEditar = null
            }
        )
    }
}

// ── Stats row ────────────────────────────────────────────────────────────────

@Composable
private fun StatsRow(lista: List<Usuario>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatCard("Total",    lista.size,                          Modifier.weight(1f))
        StatCard("Admins",   lista.count { it.rol == "admin" },   Modifier.weight(1f))
        StatCard("Clientes", lista.count { it.rol == "cliente" }, Modifier.weight(1f))
    }
}

@Composable
private fun StatCard(label: String, value: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value.toString(), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = ColorPrimary)
            Text(label, fontSize = 11.sp, color = Color.Gray)
        }
    }
}

// ── Empty state ──────────────────────────────────────────────────────────────

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(ColorPrimaryFaint),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Group, contentDescription = null, tint = ColorPrimary, modifier = Modifier.size(44.dp))
            }
            Text("Sin usuarios registrados", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color.DarkGray)
            Text("Toca + para agregar el primero", fontSize = 13.sp, color = Color.Gray)
        }
    }
}

// ── User card ────────────────────────────────────────────────────────────────

@Composable
fun UsuarioItem(usuario: Usuario, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circular avatar with first letter
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(ColorPrimary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = usuario.nombre.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                    color = Color.White,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(usuario.nombre, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF111111))
                Text(usuario.email, color = Color.Gray, fontSize = 12.sp, maxLines = 1)
                Spacer(Modifier.height(6.dp))
                RolBadge(rol = usuario.rol)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Edit, "Editar", tint = ColorPrimary, modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.height(4.dp))
                IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Delete, "Eliminar", tint = ColorDanger, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun RolBadge(rol: String) {
    val isAdmin   = rol == "admin"
    val bgColor   = if (isAdmin) ColorAdmin    else ColorCliente
    val textColor = if (isAdmin) ColorAdminText else ColorClienteText
    val label     = if (isAdmin) "Admin"        else "Cliente"

    Surface(color = bgColor, shape = RoundedCornerShape(50)) {
        Text(
            text = label,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
        )
    }
}

// ── Shared dialog field ───────────────────────────────────────────────────────

@Composable
private fun DialogField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    enabled: Boolean = true,
    isError: Boolean = false,
    supportingText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = {
            Icon(icon, contentDescription = null, tint = if (enabled) ColorPrimary else Color.LightGray)
        },
        enabled = enabled,
        isError = isError,
        supportingText = if (supportingText != null) { { Text(supportingText) } } else null,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ColorPrimary,
            focusedLabelColor = ColorPrimary,
            focusedTextColor = Color(0xFF111111),
            unfocusedTextColor = Color(0xFF111111),
            disabledTextColor = Color.Gray,
            disabledBorderColor = Color.LightGray,
            disabledLabelColor = Color.Gray,
            disabledLeadingIconColor = Color.LightGray
        )
    )
}

// ── Edit user dialog ──────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditUserDialog(
    usuario: Usuario,
    onDismiss: () -> Unit,
    onConfirm: (uid: String, nombre: String, rol: String) -> Unit
) {
    var nombre      by remember { mutableStateOf(usuario.nombre) }
    var rol         by remember { mutableStateOf(usuario.rol) }
    var expandedRol by remember { mutableStateOf(false) }
    val roles = listOf("cliente", "admin")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(ColorPrimaryFaint),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = ColorPrimary, modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.width(10.dp))
                Text("Editar Usuario", fontWeight = FontWeight.Bold, color = ColorPrimary)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DialogField(
                    value = usuario.email,
                    onValueChange = {},
                    label = "Correo",
                    icon = Icons.Default.Email,
                    enabled = false
                )
                DialogField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = "Nombre",
                    icon = Icons.Default.Person
                )
                ExposedDropdownMenuBox(
                    expanded = expandedRol,
                    onExpandedChange = { expandedRol = it }
                ) {
                    OutlinedTextField(
                        value = rol.replaceFirstChar { it.uppercase() },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Rol") },
                        leadingIcon = {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = ColorPrimary)
                        },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRol) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ColorPrimary,
                            focusedLabelColor = ColorPrimary,
                            focusedTextColor = Color(0xFF111111),
                            unfocusedTextColor = Color(0xFF111111)
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandedRol,
                        onDismissRequest = { expandedRol = false }
                    ) {
                        roles.forEach { opcion ->
                            DropdownMenuItem(
                                text = { Text(opcion.replaceFirstChar { it.uppercase() }) },
                                leadingIcon = {
                                    Icon(
                                        if (opcion == "admin") Icons.Default.AdminPanelSettings else Icons.Default.Person,
                                        contentDescription = null,
                                        tint = ColorPrimary
                                    )
                                },
                                onClick = { rol = opcion; expandedRol = false }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(usuario.uid, nombre, rol) },
                colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary),
                shape = RoundedCornerShape(10.dp)
            ) { Text("Actualizar") }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(10.dp)) {
                Text("Cancelar")
            }
        }
    )
}

// ── Add user dialog ───────────────────────────────────────────────────────────

@Composable
fun AddUserDialog(onDismiss: () -> Unit, onConfirm: (String, String, String) -> Unit) {
    var nombre       by remember { mutableStateOf("") }
    var correo       by remember { mutableStateOf("") }
    var pass         by remember { mutableStateOf("") }
    var errorNombre  by remember { mutableStateOf(false) }
    var errorCorreo  by remember { mutableStateOf(false) }
    var errorPass    by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(ColorPrimaryFaint),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, tint = ColorPrimary, modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.width(10.dp))
                Text("Nuevo Usuario", fontWeight = FontWeight.Bold, color = ColorPrimary)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                DialogField(
                    value = nombre,
                    onValueChange = { nombre = it; errorNombre = false },
                    label = "Nombre completo",
                    icon = Icons.Default.Person,
                    isError = errorNombre,
                    supportingText = if (errorNombre) "El nombre es obligatorio" else null
                )
                DialogField(
                    value = correo,
                    onValueChange = { correo = it; errorCorreo = false },
                    label = "Correo electrónico",
                    icon = Icons.Default.Email,
                    isError = errorCorreo,
                    supportingText = if (errorCorreo) "Ingresa un correo válido" else null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
                DialogField(
                    value = pass,
                    onValueChange = { pass = it; errorPass = false },
                    label = "Contraseña",
                    icon = Icons.Default.Lock,
                    isError = errorPass,
                    supportingText = if (errorPass) "Mínimo 6 caracteres" else null,
                    visualTransformation = PasswordVisualTransformation()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    errorNombre = nombre.isBlank()
                    errorCorreo = correo.isBlank() ||
                        !android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()
                    errorPass = pass.length < 6
                    if (!errorNombre && !errorCorreo && !errorPass) {
                        onConfirm(nombre.trim(), correo.trim(), pass)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary),
                shape = RoundedCornerShape(10.dp)
            ) { Text("Crear usuario") }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(10.dp)) {
                Text("Cancelar")
            }
        }
    )
}
