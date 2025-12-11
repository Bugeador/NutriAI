

package com.example.nutriai.models

import com.example.nutriai.Models.Persona

// Paciente hereda de Persona, obteniendo sus atributos basicos.
// Representa al usuario principal de la aplicación, añadiendo datos biométricos.

class Paciente(
    nombre: String,
    edad: Int,
    esHombre: Boolean,
    private var _peso: Double, // Propiedades privadas que almacenan los datos (ENCAPSULAMIENTO).
    private var _estatura: Double, // Propiedades privadas que almacenan los datos (ENCAPSULAMIENTO).
    val fotoUri: String? = null
) : Persona(nombre, edad, esHombre) {

    // Getter publico (val) para acceder al dato privado (_peso).
    // Esto protege a la variable '_peso' de ser modificada directamente fuera de la clase.
    val peso: Double get() = _peso
    val estatura: Double get() = _estatura

    // Logica clave: calcula el Índice de Masa Corporal (IMC) del paciente.
    fun calcularIMC(): Double {
        val estaturaMetros = _estatura / 100.0
        return _peso / (estaturaMetros * estaturaMetros)
    }

    // Sobreescritura (override) del metodo 'presentarse()' de la clase padre (Persona).
    // El comportamiento es diferente: ahora incluye el IMC.
    override fun presentarse(): String {
        val imcFormateado = (calcularIMC() * 100).toInt() / 100.0
        return "${super.presentarse()} Mi IMC es $imcFormateado."
    }
}
