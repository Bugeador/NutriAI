

package com.example.nutriai.Models

// Clase padre que define los atributos básicos de cualquier persona.
// Usamos 'open class' para que esta clase pueda ser heredada por otras clases (ej: Paciente).
open class Persona(
    // Atributos basicos y comunes
    val nombre: String,
    val edad: Int,
    val esHombre: Boolean
) {
    // Metodo 'open' que establece un comportamiento por defecto para ser sobreescrito (POLIMORFISMO).
    open fun presentarse(): String {
        return "Hola, soy $nombre y tengo $edad años."
    }
}
