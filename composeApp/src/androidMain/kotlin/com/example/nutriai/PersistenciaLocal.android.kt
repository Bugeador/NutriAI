
package com.example.nutriai.services

import android.content.Context
import android.content.SharedPreferences
import com.example.nutriai.models.Comida
import com.example.nutriai.models.Paciente
import org.json.JSONArray
import org.json.JSONObject


object AndroidContext {
    lateinit var context: Context
}

// Implementación concreta de la interfaz/clase expect definida para persistencia.
actual class PersistenciaLocal actual constructor() {

    // Accedemos a las preferencias del celular (PRIVADO)
    private val prefs: SharedPreferences by lazy {
        AndroidContext.context.getSharedPreferences("NutriAIData", Context.MODE_PRIVATE)
    }

    // --- PACIENTE (Por Usuario) ---
    // Métodos claros que representan operaciones del dominio sobre la entidad Paciente.
    // Este metodo guarda los datos de los pacientes obtenido en la pantalla de registro
    actual fun guardarPaciente(paciente: Paciente, usuarioId: String) {
        val json = JSONObject().apply {
            put("nombre", paciente.nombre)
            put("edad", paciente.edad)
            put("esHombre", paciente.esHombre)
            put("peso", paciente.peso)
            put("estatura", paciente.estatura)
            put("fotoUri", paciente.fotoUri ?: "") // --- NUEVO: Guardamos la foto ---
        }
        prefs.edit().putString("paciente_data_$usuarioId", json.toString()).apply()
    }

    actual fun recuperarPaciente(usuarioId: String): Paciente? {
        val jsonString = prefs.getString("paciente_data_$usuarioId", null) ?: return null
        return try {
            val json = JSONObject(jsonString)
            // Leemos la foto, si viene vacía o no existe, será null
            val foto = json.optString("fotoUri", "")

            Paciente(
                nombre = json.getString("nombre"),
                edad = json.getInt("edad"),
                esHombre = json.getBoolean("esHombre"),
                _peso = json.getDouble("peso"),
                _estatura = json.getDouble("estatura"),
                fotoUri = if (foto.isNotEmpty()) foto else null // --- NUEVO ---
            )
        } catch (e: Exception) { null }
    }

    // --- COMIDAS (Por Usuario) ---
    // Comportamiento específico para persistir listas de objetos (Comidas) vs objetos simples (Paciente)
    // Motodo que guarda los datos de las comidas en un archivo json
    actual fun guardarComidas(comidas: List<Comida>, usuarioId: String) {
        val array = JSONArray()
        comidas.forEach { comida ->
            val obj = JSONObject().apply {
                put("nombre", comida.nombre)
                put("calorias", comida.caloriasEstimadas)
                put("proteinas", comida.proteinas)
                put("carbohidratos", comida.carbohidratos)
                put("grasas", comida.grasas)
                put("fecha", comida.fecha)
            }
            array.put(obj)
        }
        prefs.edit().putString("comidas_list_$usuarioId", array.toString()).apply()
    }

    actual fun recuperarComidas(usuarioId: String): List<Comida> {
        val jsonString = prefs.getString("comidas_list_$usuarioId", null) ?: return emptyList()
        val lista = mutableListOf<Comida>()
        try {
            val array = JSONArray(jsonString)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                lista.add(Comida(
                    nombre = obj.getString("nombre"),
                    caloriasEstimadas = obj.getInt("calorias"),
                    proteinas = obj.optInt("proteinas", 0),
                    carbohidratos = obj.optInt("carbohidratos", 0),
                    grasas = obj.optInt("grasas", 0),
                    fecha = obj.getString("fecha")
                ))
            }
        } catch (e: Exception) { e.printStackTrace() }
        return lista
    }

    // --- CREDENCIALES (Login) ---
    // Credenciales para el ingreso de usuarios nuevos o ya existentes.
    actual fun guardarCredencial(usuario: String, clave: String) {
        prefs.edit().putString("auth_$usuario", clave).apply()
    }

    actual fun validarCredencial(usuario: String, clave: String): Boolean {
        val claveGuardada = prefs.getString("auth_$usuario", null)
        return claveGuardada == clave
    }

    actual fun existeUsuario(usuario: String): Boolean {
        return prefs.contains("auth_$usuario")
    }
}