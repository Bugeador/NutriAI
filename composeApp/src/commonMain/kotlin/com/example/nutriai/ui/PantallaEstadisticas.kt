
package com.example.nutriai.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nutriai.services.EstadoDia
import com.example.nutriai.services.GestorNutricional
import com.example.nutriai.services.obtenerFechaHoy

@Composable
fun PantallaEstadisticas(gestor: GestorNutricional) {
    val comidas by gestor.comidasState.collectAsState()

    // Delegamos la logica compleja de calculo de historial al servicio, manteniendo la UI limpia.
    val historialDias = remember(comidas) { gestor.obtenerHistorialDias() }
    val hoy = obtenerFechaHoy()
    val mesActualPrefix = if (hoy.length >= 7) hoy.substring(0, 7) else ""

    // Estado para el filtro (0=Diario, 1=Semanal, 2=Mensual)
    var filtroSeleccionado by remember { mutableStateOf(0) }
    val titulos = listOf("Diario", "Semanal", "Mensual")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Tu Progreso", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))

        // 1. CALENDARIO
        Text("Calendario de Hábitos", style = MaterialTheme.typography.titleMedium)
        Card(
            modifier = Modifier.fillMaxWidth().height(320.dp).padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            CalendarioSimple(historialDias, mesActualPrefix)
        }

        Spacer(Modifier.height(24.dp))

        // 2. ESTADÍSTICAS MACROS
        Text("Desglose Nutricional", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        // Selector de Pestañas
        TabRow(selectedTabIndex = filtroSeleccionado, containerColor = Color.Transparent) {
            titulos.forEachIndexed { index, titulo ->
                Tab(
                    selected = filtroSeleccionado == index,
                    onClick = { filtroSeleccionado = index },
                    text = { Text(titulo, color = if(filtroSeleccionado == index) MaterialTheme.colorScheme.primary else Color.Gray) }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Lógica de filtrado
        val comidasFiltradas = when (filtroSeleccionado) {
            0 -> comidas.filter { it.fecha == hoy } // Hoy
            1 -> comidas.takeLast(20) // Aprox una semana (últimos 20 registros)
            else -> comidas // Todo el historial
        }

        if (comidasFiltradas.isEmpty()) {
            Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                Text("No hay datos para este período.", color = Color.Gray)
            }
        } else {
            // Accedemos a las propiedades tipadas de la clase Comida (calorias, proteinas, etc).
            val totalCal = comidasFiltradas.sumOf { it.caloriasEstimadas }
            val totalProt = comidasFiltradas.sumOf { it.proteinas }
            val totalCarb = comidasFiltradas.sumOf { it.carbohidratos }
            val totalGras = comidasFiltradas.sumOf { it.grasas }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text("Total Calorías: $totalCal kcal", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(16.dp))

                    BarraMacro("Proteínas", totalProt, Color(0xFF1E88E5))
                    BarraMacro("Carbohidratos", totalCarb, Color(0xFFFB8C00))
                    BarraMacro("Grasas", totalGras, Color(0xFFE53935))
                }
            }
        }

        Spacer(Modifier.height(80.dp)) // Espacio final
    }
}

@Composable
// Funcion 'private' que encapsula logica visual reutilizable solo dentro de este archivo.
private fun BarraMacro(nombre: String, valor: Int, color: Color) {
    Column(Modifier.padding(vertical = 8.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(nombre, style = MaterialTheme.typography.bodyMedium)
            Text("${valor}g", fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { 1f },
            modifier = Modifier.fillMaxWidth().height(10.dp).clip(MaterialTheme.shapes.small),
            color = color.copy(alpha = 0.8f),
            trackColor = color.copy(alpha = 0.15f)
        )
    }
}

// --- CALENDARIO INTEGRADO AQUÍ ---
@Composable
fun CalendarioSimple(historial: Map<String, EstadoDia>, mesActualPrefix: String) {
    Column(Modifier.padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            LeyendaColor(Color(0xFF66BB6A), "Cumplido")
            LeyendaColor(Color(0xFFEF5350), "Exceso")
            LeyendaColor(Color.LightGray, "Vacío")
        }
        Spacer(Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(31) { index ->
                val dia = index + 1
                val diaStr = dia.toString().padStart(2, '0')
                val fechaClave = if(mesActualPrefix.isNotEmpty()) "$mesActualPrefix-$diaStr" else ""
                val estado = historial[fechaClave] ?: EstadoDia.VACIO

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .background(
                            when (estado) {
                                EstadoDia.CUMPLIDO -> Color(0xFF66BB6A)
                                EstadoDia.EXCEDIDO -> Color(0xFFEF5350)
                                EstadoDia.VACIO -> Color(0xFFF5F5F5)
                            }
                        )
                ) {
                    Text(
                        text = "$dia",
                        color = if (estado == EstadoDia.VACIO) Color.Gray else Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun LeyendaColor(color: Color, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(10.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(4.dp))
        Text(texto, fontSize = 10.sp, color = Color.Gray)
    }
}