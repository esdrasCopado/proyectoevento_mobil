package com.itson.proyectoevento

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.itson.proyectoevento.ui.inicio.InicioScreen
import com.itson.proyectoevento.ui.inicio.InicioViewModel
import com.itson.proyectoevento.ui.newEvent.NuevoEventoScreen
import com.itson.proyectoevento.ui.theme.ProyectoEventoTheme

class MainActivity : ComponentActivity() {

    private val inicioViewModel: InicioViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProyectoEventoTheme {
                var mostrarNuevoEvento by remember { mutableStateOf(false) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (mostrarNuevoEvento) {
                        NuevoEventoScreen(
                            modifier = Modifier.padding(innerPadding),
                            onEventoCreado = { evento ->
                                inicioViewModel.agregarEvento(evento)
                                mostrarNuevoEvento = false
                            },
                            onCancelar = { mostrarNuevoEvento = false }
                        )
                    } else {
                        InicioScreen(
                            modifier = Modifier.padding(innerPadding),
                            viewModel = inicioViewModel,
                            onCrearEvento = { mostrarNuevoEvento = true }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InicioPreview() {
    ProyectoEventoTheme {
        InicioScreen()
    }
}
