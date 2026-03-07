package com.itson.proyectoevento.data.model

data class Evento(
    val id: Int,
    val nombre: String,
    val fecha: String,
    val porcentajePagado: Int,
    val totalCosto: Double,
    val tipo: String
)
