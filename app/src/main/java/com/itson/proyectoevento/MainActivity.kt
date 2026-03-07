package com.itson.proyectoevento

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.itson.proyectoevento.ui.inicio.InicioScreen
import com.itson.proyectoevento.ui.theme.ProyectoEventoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProyectoEventoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    InicioScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
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