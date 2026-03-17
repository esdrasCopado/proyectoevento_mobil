package com.itson.proyectoevento.data.model

data class Evento(
    val id: Int,
    val nombre: String,
    val fecha: String,
    val porcentajePagado: Int,
    val totalCosto: Double,
    val tipo: String,
    val nombreCliente: String = "",
    val telefonoCliente: String = "",
    val correoCliente: String = "",
    val paquete: Paquete? = null,
    val pagos: List<Pago> = emptyList()
)
