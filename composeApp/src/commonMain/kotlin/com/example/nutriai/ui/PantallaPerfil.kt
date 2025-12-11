

package com.example.nutriai.ui

import com.example.nutriai.rememberGalleryLauncher
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import com.example.nutriai.services.GestorNutricional
import coil3.compose.AsyncImage

@Composable
fun PantallaPerfil(gestor: GestorNutricional, onLogout: () -> Unit) {
    // Usamos collectAsState() para que la UI se entere automaticamente cuando
    // los datos cambien en el Gestor (Observer Pattern).
    val pacienteState by gestor.pacienteState.collectAsState()
    val paciente = pacienteState ?: return // Si es null, no pintamos nada

    val nombreUsuario = gestor.usuarioActualId ?: "Usuario"

    // Estados para la edicion de datos
    var isEditing by remember { mutableStateOf(false) }

    // Inicializamos los campos con los datos actuales del paciente
    var editPeso by remember(paciente.peso) { mutableStateOf(paciente.peso.toString()) }
    var editEstatura by remember(paciente.estatura) { mutableStateOf(paciente.estatura.toString()) }

    // Estado para error de validacion visual
    var hayErrorValidacion by remember { mutableStateOf(false) }

    val fotoActual = paciente.fotoUri
    val abrirGaleria = rememberGalleryLauncher { path ->
        gestor.actualizarFoto(path)
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- SECCION FOTO DE PERFIL ---
        Box(contentAlignment = Alignment.BottomEnd) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .clickable { abrirGaleria() },
                contentAlignment = Alignment.Center
            ) {
                if (fotoActual != null) {
                    AsyncImage(
                        model = fotoActual,
                        contentDescription = "Foto de Perfil",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(70.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .border(2.dp, Color.White, CircleShape)
                    .clickable { abrirGaleria() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Edit, null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(paciente.nombre, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("@$nombreUsuario", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

        Spacer(Modifier.height(24.dp))

        // --- TARJETA DE DATOS ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(Modifier.padding(20.dp)) {

                Text("Información Personal", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Edad", color = Color.Gray)
                    Text("${paciente.edad} años", fontWeight = FontWeight.Bold)
                }
                Divider(Modifier.padding(vertical = 8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Género", color = Color.Gray)
                    Text(if (paciente.esHombre) "Hombre" else "Mujer", fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(24.dp))

                // Cabecera Datos Fisicos y Boton Editar/Guardar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Datos Físicos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    IconButton(onClick = {
                        if (isEditing) {
                            // Convertimos y verificamos rangos logicos antes de guardar.
                            val p = editPeso.toDoubleOrNull()
                            val e = editEstatura.toDoubleOrNull()

                            val pesoValido = p != null && p in 20.0..300.0 // Rango logico peso
                            val estaturaValida = e != null && e in 50.0..250.0 // Rango logico estatura

                            if (pesoValido && estaturaValida) {
                                // Datos correctos: Guardamos y salimos de edicion
                                gestor.actualizarFisico(p!!, e!!)
                                hayErrorValidacion = false
                                isEditing = false
                            } else {
                                // SI hay datos incorrectos: Activamos error visual
                                hayErrorValidacion = true
                            }
                        } else {
                            // Entrar a modo edicion: Cargamos valores actuales
                            editPeso = paciente.peso.toString()
                            editEstatura = paciente.estatura.toString()
                            hayErrorValidacion = false
                            isEditing = true
                        }
                    }) {
                        // Cambia el icono segun el estado
                        Icon(
                            imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                            contentDescription = null,
                            tint = if (hayErrorValidacion) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Divider(Modifier.padding(vertical = 12.dp))

                if (isEditing) {
                    // Modo Edicion (Inputs)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = editPeso,
                            onValueChange = { editPeso = it },
                            label = { Text("Peso (kg)") },
                            isError = hayErrorValidacion, // Se pone rojo si hay error
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = editEstatura,
                            onValueChange = { editEstatura = it },
                            label = { Text("Estatura (cm)") },
                            isError = hayErrorValidacion, // Se pone rojo si hay error
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (hayErrorValidacion) {
                        Text("Revisa los valores (20-300kg, 50-250cm)", color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(top=4.dp))
                    }
                } else {
                    // Modo Lectura (Textos)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        DatoPerfil("Peso", "${paciente.peso} kg")
                        DatoPerfil("Estatura", "${paciente.estatura} cm")
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Seccion IMC
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("Tu Índice de Masa Corporal (IMC)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    IMCGaugeMejorado(paciente.calcularIMC())
                }
            }
        }

        Spacer(Modifier.height(40.dp))

        Button(
            onClick = {
                gestor.cerrarSesion()
                onLogout()
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Cerrar Sesión")
        }

        Spacer(Modifier.height(20.dp))
    }
}

@Composable
fun DatoPerfil(titulo: String, valor: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(valor, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text(titulo, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }
}

@Composable
fun IMCGaugeMejorado(imc: Double) {
    val imcClamped = imc.coerceIn(10.0, 45.0)
    val imcRedondeado = (imc * 10).toInt() / 10.0

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {

        Box(
            modifier = Modifier
                .background(Color(0xFF212121), shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Tu IMC: $imcRedondeado",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val barHeight = 25f
                val arrowSize = 25f
                val width = size.width
                val sectionWidth = width / 4

                drawRect(Color(0xFFFFC107), Offset(0f, arrowSize), androidx.compose.ui.geometry.Size(sectionWidth, barHeight))
                drawRect(Color(0xFF4CAF50), Offset(sectionWidth, arrowSize), androidx.compose.ui.geometry.Size(sectionWidth, barHeight))
                drawRect(Color(0xFFFF9800), Offset(sectionWidth * 2, arrowSize), androidx.compose.ui.geometry.Size(sectionWidth, barHeight))
                drawRect(Color(0xFFF44336), Offset(sectionWidth * 3, arrowSize), androidx.compose.ui.geometry.Size(sectionWidth, barHeight))

                val minImc = 15.0
                val maxImc = 40.0
                val range = maxImc - minImc
                val normalizedImc = (imcClamped - minImc) / range
                val indicatorX = (normalizedImc * width).toFloat().coerceIn(0f, width)

                val path = Path().apply {
                    moveTo(indicatorX, arrowSize)
                    lineTo(indicatorX - 10f, 0f)
                    lineTo(indicatorX + 10f, 0f)
                    close()
                }
                drawPath(path, Color(0xFF424242))
            }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Bajo", fontSize = 10.sp, color = Color.Gray)
            Text("Normal", fontSize = 10.sp, color = Color.Gray)
            Text("Sobrepeso", fontSize = 10.sp, color = Color.Gray)
            Text("Obesidad", fontSize = 10.sp, color = Color.Gray)
        }
    }
}