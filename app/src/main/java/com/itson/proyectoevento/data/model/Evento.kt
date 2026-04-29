package com.itson.proyectoevento.data.model

data class Evento(
    val firestoreId: String = "",
    val id: Int = 0,
    val nombre: String = "",
    val fecha: String = "",
    val porcentajePagado: Int = 0,
    val totalCosto: Double = 0.0,
    val tipo: String = "",
    val nombreCliente: String = "",
    val telefonoCliente: String = "",
    val correoCliente: String = "",
    val paquete: Paquete? = null,
    val pagos: List<Pago> = emptyList(),
    val propietarioUid: String = ""
)
