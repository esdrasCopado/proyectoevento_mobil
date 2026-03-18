package com.itson.proyectoevento.data.repository

import com.itson.proyectoevento.data.model.Paquete

object PaquetesRepository {
    val paquetes = listOf(
        Paquete(
            id = 1,
            nombre = "Básico",
            descripcion = "Ideal para eventos íntimos y celebraciones pequeñas.",
            precio = 15000.0,
            incluidos = listOf(
                "Hasta 50 personas",
                "5 horas de evento",
                "Buffet básico",
                "Sillas y mesas",
                "Decoración simple",
                "Estacionamiento incluido"
            )
        ),
        Paquete(
            id = 2,
            nombre = "Estándar",
            descripcion = "La opción más popular para eventos medianos.",
            precio = 35000.0,
            incluidos = listOf(
                "Hasta 100 personas",
                "8 horas de evento",
                "Menú de 3 tiempos",
                "Mobiliario completo",
                "Decoración temática",
                "Barra de bebidas sin alcohol",
                "Permiso de alcoholes incluido",
                "Sonido básico",
                "Estacionamiento incluido"
            )
        ),
        Paquete(
            id = 3,
            nombre = "Premium",
            descripcion = "Experiencia de lujo para los eventos más especiales.",
            precio = 65000.0,
            incluidos = listOf(
                "Hasta 200 personas",
                "12 horas de evento",
                "Menú gourmet de 5 tiempos",
                "Mobiliario premium",
                "Decoración personalizada premium",
                "Barra abierta (alcohol incluido)",
                "DJ profesional",
                "Iluminación especial",
                "Valet parking"
            )
        )
    )
}
