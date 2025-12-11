
package com.example.nutriai.services

import com.example.nutriai.models.Comida
import com.example.nutriai.models.Paciente

// Implementación VACÍA para iOS (Stub)
// Esto elimina los errores rojos del proyecto, aunque no guarde nada en iPhone todavía.

actual class LocalStorage actual constructor() {

    actual fun guardarPaciente(paciente: Paciente) {
        // Por implementar en el futuro
        println("Guardado en iOS no implementado aún")
    }

    actual fun recuperarPaciente(): Paciente? {
        return null
    }

    actual fun guardarComidas(comidas: List<Comida>) {
        // Por implementar
    }

    actual fun recuperarComidas(): List<Comida> {
        return emptyList()
    }
}