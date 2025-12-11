
package com.example.nutriai.services

import com.example.nutriai.models.Comida

// 👇 SIN importaciones de kotlinx.datetime para evitar conflictos en iOS

actual class GeminiAlimenticio actual constructor(private val apiKey: String) {

    actual suspend fun analizarImagenYEstimarCalorias(imagenBytes: ByteArray, descripcionUsuario: String): Comida {

        println("Simulación iOS: Analizando imagen. Usuario dice: '$descripcionUsuario'")

        val hoy = "2024-05-20"

        return Comida(
            nombre = "Simulación iOS (350 kcal)",
            caloriasEstimadas = 350,
            fecha = hoy
        )
    }
}
