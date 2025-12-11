
package com.example.nutriai.services
import com.example.nutriai.models.Comida
import com.example.nutriai.models.Paciente

// Define la interfaz publica para guardar datos
expect class PersistenciaLocal() {
    // Metodos abstractos que seran implementados en la plataforma especifica (Android).
    fun guardarPaciente(paciente: Paciente, usuarioId: String)
    fun recuperarPaciente(usuarioId: String): Paciente?

    fun guardarComidas(comidas: List<Comida>, usuarioId: String)
    fun recuperarComidas(usuarioId: String): List<Comida>

    // Para el Login: Guardar mapa de usuario -> contraseña
    fun guardarCredencial(usuario: String, clave: String)
    fun validarCredencial(usuario: String, clave: String): Boolean
    fun existeUsuario(usuario: String): Boolean
}