
package com.example.nutriai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.nutriai.services.AndroidContext
import com.example.nutriai.services.GeminiAlimenticio


// ----- CLAVE API GEMINI -----
private const val GEMINI_API_KEY = "?????" // Se quito la API Key por temas de seguridad ya que el repositorio
                                           // De la app esta publico

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidContext.context = applicationContext
        val geminiService = GeminiAlimenticio(GEMINI_API_KEY)
        setContent {
            App(
                geminiService = geminiService,
            )
        }
    }
}
