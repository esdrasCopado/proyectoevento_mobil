package com.itson.proyectoevento.data.model

data class Paquete(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val incluidos: List<String>
)
