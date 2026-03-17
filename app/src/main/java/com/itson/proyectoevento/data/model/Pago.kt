package com.itson.proyectoevento.data.model

data class Pago(
    val id: Int,
    val monto: Double,
    val fecha: String,
    val concepto: String = "Abono"
)
