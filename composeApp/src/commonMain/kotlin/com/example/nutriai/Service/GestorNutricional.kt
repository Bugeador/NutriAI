
package com.example.nutriai.services

import com.example.nutriai.models.Comida
import com.example.nutriai.models.Paciente
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


// Clase controladora que coordina la IA, los calculos y la persistencia.
class GestorNutricional(
    val geminiService: GeminiAlimenticio,
    private val calculadora: CalculadoraTMB = CalculadoraMifflinStJeor(),
    private val persistencia: PersistenciaLocal = PersistenciaLocal()
) {

    var yaSeMostroAlertaDiaria: Boolean = false

    // Propiedad privada (_pacienteState) para que nadie modifique el estado directamente desde fuera.
    private val _pacienteState = MutableStateFlow<Paciente?>(null)

    // Propiedad publica de solo lectura (StateFlow) para exponer los datos de forma segura.
    val pacienteState: StateFlow<Paciente?> = _pacienteState.asStateFlow()

    // Mismo patron de encapsulamiento para la lista de comidas.
    private val _comidasState = MutableStateFlow<List<Comida>>(emptyList())
    val comidasState: StateFlow<List<Comida>> = _comidasState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Getter simple para acceder al valor actual del estado.
    val paciente: Paciente? get() = _pacienteState.value

    // --- USUARIO ACTUAL (Login) ---
    var usuarioActualId: String? = null

    // Logica de negocio para autenticacion
    fun login(usuario: String, clave: String): Boolean {
        if (persistencia.validarCredencial(usuario, clave)) {
            usuarioActualId = usuario
            cargarDatosUsuario(usuario)
            return true
        }
        return false
    }

    fun registrarUsuarioNuevo(usuario: String, clave: String, paciente: Paciente) {
        if (persistencia.existeUsuario(usuario)) return // Ya existe

        persistencia.guardarCredencial(usuario, clave)
        usuarioActualId = usuario

        // Guardar datos iniciales
        setPaciente(paciente)
    }

    fun cerrarSesion() {
        usuarioActualId = null
        _pacienteState.value = null
        _comidasState.value = emptyList()
    }

    // Metodo privado interno para coordinar la carga de datos desde persistencia.
    private fun cargarDatosUsuario(usuario: String) {
        val p = persistencia.recuperarPaciente(usuario)
        if (p != null) _pacienteState.value = p
        val c = persistencia.recuperarComidas(usuario)
        _comidasState.value = c
    }

    fun setPaciente(p: Paciente) {
        val usuario = usuarioActualId ?: return
        _pacienteState.update { p }
        persistencia.guardarPaciente(p, usuario)
    }

    fun registrarComida(comida: Comida) {
        val usuario = usuarioActualId ?: return
        _comidasState.update { currentList ->
            val nuevaLista = currentList + comida
            persistencia.guardarComidas(nuevaLista, usuario)
            nuevaLista
        }
    }

    fun getCaloriasConsumidas(): Int = _comidasState.value.sumOf { it.caloriasEstimadas }

    fun getCaloriasMaximas(): Int {
        val p = paciente ?: return 0
        return calculadora.calcularRequerimientoDiario(p)
    }

    // Logica para transformar la lista plana de comidas en un mapa agrupado por fecha.
    fun obtenerHistorialDias(): Map<String, EstadoDia> {
        val maxCalorias = getCaloriasMaximas()
        if (maxCalorias == 0) return emptyMap()
        val resumenPorDia = _comidasState.value.groupBy { it.fecha }
        val historial = mutableMapOf<String, EstadoDia>()
        resumenPorDia.forEach { (fecha, comidas) ->
            val totalCalorias = comidas.sumOf { it.caloriasEstimadas }
            historial[fecha] = if (totalCalorias > maxCalorias) EstadoDia.EXCEDIDO else EstadoDia.CUMPLIDO
        }
        return historial
    }

    suspend fun procesarImagen(imagenBytes: ByteArray, descripcionUsuario: String) {
        _isLoading.value = true
        try {
            val comida = geminiService.analizarImagenYEstimarCalorias(imagenBytes, descripcionUsuario)
            registrarComida(comida)
        } catch (e: Exception) {
            println("Error: ${e.message}")
        } finally {
            _isLoading.value = false
        }
    }

    // Actualiza solo la foto manteniendo los otros datos del paciente.
    fun actualizarFoto(uri: String) {
        val p = paciente ?: return
        val usuario = usuarioActualId ?: return

        // Creamos copia del paciente con la nueva foto
        val pacienteActualizado = Paciente(
            nombre = p.nombre,
            edad = p.edad,
            esHombre = p.esHombre,
            _peso = p.peso,
            _estatura = p.estatura,
            fotoUri = uri // Guardamos la URI
        )

        _pacienteState.value = pacienteActualizado
        persistencia.guardarPaciente(pacienteActualizado, usuario)
    }

    // Actualiza datos biometricos manteniendo la foto y nombre.
    fun actualizarFisico(nuevoPeso: Double, nuevaEstatura: Double) {
        val p = paciente ?: return
        val usuario = usuarioActualId ?: return

        val pacienteActualizado = Paciente(
            nombre = p.nombre,
            edad = p.edad,
            esHombre = p.esHombre,
            _peso = nuevoPeso,
            _estatura = nuevaEstatura,
            fotoUri = p.fotoUri // Mantenemos la foto que ya tenía
        )

        _pacienteState.value = pacienteActualizado
        persistencia.guardarPaciente(pacienteActualizado, usuario)
    }

}

enum class EstadoDia {
    CUMPLIDO, EXCEDIDO, VACIO
}

