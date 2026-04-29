package com.itson.proyectoevento.data.model

data class Pago(
    val id: Int = 0,
    val monto: Double = 0.0,
    val fecha: String = "",
    val concepto: String = "Abono"
)
