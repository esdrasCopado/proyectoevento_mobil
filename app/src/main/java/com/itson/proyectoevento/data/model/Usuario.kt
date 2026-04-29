package com.itson.proyectoevento.data.model

import com.google.firebase.Timestamp

data class Usuario(
    val uid: String = "",
    val email: String = "",
    val nombre: String = "",
    val rol: String = "cliente",
    val fechaRegistro: Timestamp? = null
) {
    fun esAdmin(): Boolean = rol == "admin"
}
