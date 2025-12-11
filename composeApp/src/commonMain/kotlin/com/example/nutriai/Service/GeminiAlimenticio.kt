
package com.example.nutriai.services
import com.example.nutriai.models.Comida

// Define la estructura del servicio de IA sin mostrar su codigo interno aqui.
// Separa la definicion de la implementacion concreta.
expect class GeminiAlimenticio(apiKey: String) {
    suspend fun analizarImagenYEstimarCalorias(imagenBytes: ByteArray, descripcionUsuario: String): Comida
}