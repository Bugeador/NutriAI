

package com.example.nutriai.services

import com.example.nutriai.models.Paciente

// Utilizamos una interfaz para definir un contrato.
// Esto permite que el sistema no dependa de una formula especifica, sino de la abstraccion.
interface CalculadoraTMB {
    fun calcularRequerimientoDiario(paciente: Paciente): Int
}

// Clase concreta que implementa la logica especifica de Mifflin-St Jeor.
// La logica especifica de Mifflin-St Jeor, es una ecuacion para estimar el gasto energetico cuando una
// persona se encuetra en reposo.
class CalculadoraMifflinStJeor : CalculadoraTMB {
    override fun calcularRequerimientoDiario(paciente: Paciente): Int {

        val tmb = (10.0 * paciente.peso) + (6.25 * paciente.estatura) - (5.0 * paciente.edad) +
                if (paciente.esHombre) 5.0 else -161.0

        val requerimientoTotal = tmb * 1.375

        return requerimientoTotal.toInt()
    }
}