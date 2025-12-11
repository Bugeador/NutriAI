

package com.example.nutriai.services
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Implementación real usando Java (Nativa de Android)
actual fun obtenerFechaHoy(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(Date())
}