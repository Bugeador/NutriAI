

package com.example.nutriai.models

import kotlinx.serialization.Serializable

// Define la estructura de datos que representa una entidad del dominio (un plato de comida).
@Serializable
data class Comida(
    val nombre: String, // Nombre del plato (ej: "Ensalada César")
    val caloriasEstimadas: Int, // Valor calórico principal estimado por la IA.
    val proteinas: Int, // Macronutrientes: sirven para el cálculo nutricional diario.
    val carbohidratos: Int, // Macronutrientes: sirven para el cálculo nutricional diario.
    val grasas: Int, // Macronutrientes: sirven para el cálculo nutricional diario.
    val fecha: String, // Fecha del registro de consumo.
    val descripcionIA: String? = null // Descripción opcional si el usuario la proporcionó a la IA.
)