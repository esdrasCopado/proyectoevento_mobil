package com.itson.proyectoevento.data.model

data class Paquete(
    val id: Int = 0,
    val nombre: String = "",
    val descripcion: String = "",
    val precio: Double = 0.0,
    val incluidos: List<String> = emptyList()
)
