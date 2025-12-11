

package com.example.nutriai.services

import com.example.nutriai.models.Comida
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import android.graphics.BitmapFactory
import org.json.JSONObject
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime



// 'actual class' es la implementación concreta de una abstracción definida en commonMain via 'expect class'.
actual class GeminiAlimenticio actual constructor(private val apiKey: String) {
    // Propiedad 'private' para proteger el modelo generativo del acceso externo (Proteger la API KEY).
    private val model = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = apiKey
    )

    // --- EL PROMPT DE LA APP ---
    // El prompt del sistema es interno y no debe ser modificado desde fuera (ENCAPSULAMIENTO).
    private val systemPrompt = """
        Eres un nutricionista experto. Tu trabajo es analizar imágenes de comida.
        Debes responder ESTRICTAMENTE en formato JSON con los siguientes campos exactos:
        1. "nombre_plato": Breve descripción.
        2. "calorias_estimadas": Un número entero (int).
        3. "proteinas_g": Un número entero aproximado en gramos (int).
        4. "carbohidratos_g": Un número entero aproximado en gramos (int).
        5. "grasas_g": Un número entero aproximado en gramos (int).
        
        Si no es comida, responde con todos los valores numéricos en 0.
        No incluyas bloques de código markdown, solo el JSON plano.
    """.trimIndent()


    // Este metodo encapsula la lógica compleja de IA, transformando bytes en objetos de dominio (Comida).
    actual suspend fun analizarImagenYEstimarCalorias(imagenBytes: ByteArray, descripcionUsuario: String): Comida {

        // 1. Construir el prompt dinámico
        val promptUsuario = if (descripcionUsuario.isNotBlank()) {
            "Analiza esta imagen. El usuario indica que es: '$descripcionUsuario'. Úsalo para mejorar la precisión."
        } else {
            "Analiza esta imagen de comida, estima calorías y macronutrientes."
        }

        // 2. Obtener fecha
        val hoy = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()

        // 3. Convertir imagen
        val imagenBitmap = BitmapFactory.decodeByteArray(imagenBytes, 0, imagenBytes.size)

        // 4. Enviar a Gemini
        val response = model.generateContent(
            content {
                image(imagenBitmap)
                text(systemPrompt)
                text(promptUsuario)
            }
        )

        val jsonText = response.text?.trim() ?: throw RuntimeException("Error: La IA no devolvió texto.")
        return try {
            val cleanJson = jsonText.replace("```json", "").replace("```", "").trim()
            val json = JSONObject(cleanJson)

            // --- EXTRACCIÓN DE DATOS ---
            val nombre = json.optString("nombre_plato", "Desconocido")
            val calorias = json.optInt("calorias_estimadas", 0)
            val proteinas = json.optInt("proteinas_g", 0)
            val carbohidratos = json.optInt("carbohidratos_g", 0)
            val grasas = json.optInt("grasas_g", 0)

            Comida(
                nombre = nombre,
                caloriasEstimadas = calorias,
                proteinas = proteinas,
                carbohidratos = carbohidratos,
                grasas = grasas,
                fecha = hoy
            )
        } catch (e: Exception) {
            println("Error al parsear JSON de Gemini: ${e.message}")
            // Retornamos objeto vacío si no hay comida
            Comida(
                nombre = "Error IA / No comida",
                caloriasEstimadas = 0,
                proteinas = 0,
                carbohidratos = 0,
                grasas = 0,
                fecha = hoy
            )
        }
    }
}