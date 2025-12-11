
package com.example.nutriai.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nutriai.models.Comida
import com.example.nutriai.services.GestorNutricional
import com.example.nutriai.services.obtenerFechaHoy
import io.github.alexzhirkevich.compottie.LottieAnimation
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.LottieConstants
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import nutriai.composeapp.generated.resources.Res
import nutriai.composeapp.generated.resources.logo_nutriai
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
@Composable
fun PantallaResultados(
    gestor: GestorNutricional,
    onLaunchCamera: (String) -> Unit
) {
    val paciente = gestor.paciente ?: return

    // Estados del Gestor
    val isLoading by gestor.isLoading.collectAsState()
    val comidasRegistradas by gestor.comidasState.collectAsState()

    // --- FECHAS Y DATOS ---
    val hoy = obtenerFechaHoy()
    val consumoHoy = comidasRegistradas.filter { it.fecha == hoy }.sumOf { it.caloriasEstimadas }

    // Calculo polimorfico: el gestor usa la calculadora TMB configurada internamente.
    val maximoCalorico = gestor.getCaloriasMaximas()

    // --- ALERTAS ---
    var mostrarAlertaExceso by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(consumoHoy) {
        if (maximoCalorico > 0 && consumoHoy > maximoCalorico && !gestor.yaSeMostroAlertaDiaria) {
            mostrarAlertaExceso = true
            gestor.yaSeMostroAlertaDiaria = true
        }
    }

    // --- GRADIENTE IA ---
    val brushIA = Brush.horizontalGradient(listOf(Color(0xFF6CC551), Color(0xFF2D9CDB)))

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color.Transparent, // Usamos el fondo del tema
            topBar = {
                // HEADER PERSONALIZADO
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, start = 20.dp, end = 20.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Hoy", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
                        Text("Hola, ${paciente.nombre}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    }
                    // Logo en esquina
                    Surface(
                        shape = CircleShape,
                        shadowElevation = 4.dp,
                        modifier = Modifier.size(45.dp),
                        color = Color.White
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.logo_nutriai),
                            contentDescription = "Logo",
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        ) { padding ->

            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item { Spacer(Modifier.height(4.dp)) }

                // 1. TARJETA RESUMEN
                item {
                    CardResumenCalorias(consumoHoy, maximoCalorico)
                }

                // 2. INPUT DESCRIPCION + BOTÓN IA
                item {
                    var descripcionComida by remember { mutableStateOf("") }

                    Card(
                        shape = MaterialTheme.shapes.medium,
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.shadow(4.dp, MaterialTheme.shapes.medium)
                    ) {
                        Column(Modifier.padding(20.dp)) {
                            Text("Nueva Comida", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(Modifier.height(12.dp))

                            OutlinedTextField(
                                value = descripcionComida,
                                onValueChange = { descripcionComida = it },
                                placeholder = { Text("Ej: Ensalada César con pollo...") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.small,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)
                                ),
                                singleLine = true
                            )

                            Spacer(Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    onLaunchCamera(descripcionComida)
                                    descripcionComida = ""
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                                    .shadow(8.dp, MaterialTheme.shapes.small),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                contentPadding = PaddingValues(),
                                shape = MaterialTheme.shapes.small
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(brushIA)
                                        .padding(horizontal = 16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = Color.White)
                                        Spacer(Modifier.width(8.dp))
                                        Text("Escanear con IA", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Text(
                        "Historial de hoy",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )
                }

                // 3. LISTA DE COMIDAS ANIMADA
                val comidasHoy = comidasRegistradas.reversed().filter { it.fecha == hoy }
                if (comidasHoy.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                            Text("Aún no has registrado comidas hoy 🌱", color = Color.Gray)
                        }
                    }
                } else {
                    items(comidasHoy) { comida ->
                        AnimatedVisibility(
                            visible = true,
                            enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(animationSpec = tween(500))
                        ) {
                            CardComidaStylized(comida)
                        }
                    }
                }

                item { Spacer(Modifier.height(80.dp)) }
            }
        }

        // PANTALLA DE CARGA (Overlay)
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f))
                    .clickable(enabled = false) {}, // Bloquear clicks
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    var jsonString by remember { mutableStateOf<String?>(null) }
                    LaunchedEffect(Unit) {
                        try {
                            val bytes = Res.readBytes("files/loading_food.json")
                            jsonString = bytes.decodeToString()
                        } catch (e: Exception) { e.printStackTrace() }
                    }

                    if (jsonString != null) {
                        val composition by rememberLottieComposition(
                            LottieCompositionSpec.JsonString(jsonString!!)
                        )
                        LottieAnimation(
                            composition = composition,
                            iterations = LottieConstants.IterateForever,
                            modifier = Modifier.size(220.dp)
                        )
                    } else {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }

                    Spacer(Modifier.height(24.dp))
                    Text(
                        "Analizando nutrientes...",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // ALERTA DE EXCESO
        if (mostrarAlertaExceso) {
            AlertDialog(
                onDismissRequest = { mostrarAlertaExceso = false },
                icon = { Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.error) },
                title = { Text("¡Límite Excedido!") },
                text = { Text("Has superado tu consumo diario de calorías ($maximoCalorico kcal).") },
                confirmButton = {
                    TextButton(onClick = { mostrarAlertaExceso = false }) { Text("Entendido") }
                },
                containerColor = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.medium
            )
        }
    }
}

@Composable
fun CardResumenCalorias(consumido: Int, meta: Int) {
    val progreso = if (meta > 0) consumido.toFloat() / meta.toFloat() else 0f
    val animatedProgress by animateFloatAsState(targetValue = progreso.coerceIn(0f, 1f), label = "progreso")

    val colorBarra = if (progreso > 1.0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
    val restantes = (meta - consumido).coerceAtLeast(0)

    Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF263238)), // Gris muy oscuro
        modifier = Modifier.fillMaxWidth().shadow(6.dp, MaterialTheme.shapes.medium)
    ) {
        Row(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Calorías Restantes", color = Color.Gray, fontSize = 12.sp)
                Text(
                    "$restantes",
                    color = Color.White,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text("Meta: $meta kcal", color = Color(0xFFB0BEC5), fontSize = 14.sp)
            }

            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(90.dp)) {
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Gray.copy(alpha = 0.3f),
                    strokeWidth = 8.dp,
                )
                CircularProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.fillMaxSize(),
                    color = colorBarra,
                    strokeWidth = 8.dp,
                )
                Text(
                    "${(progreso * 100).toInt()}%",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun CardComidaStylized(comida: Comida) {
    Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth().padding(bottom = 2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = comida.nombre.take(1).uppercase(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(comida.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text(
                            "${comida.caloriasEstimadas} kcal",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            Divider(color = Color.LightGray.copy(alpha = 0.2f))
            Spacer(Modifier.height(12.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                MacroItem("Prot", "${comida.proteinas}g", Color(0xFF1E88E5))
                MacroItem("Carbs", "${comida.carbohidratos}g", Color(0xFFFB8C00))
                MacroItem("Grasas", "${comida.grasas}g", Color(0xFFE53935))
            }
        }
    }
}

@Composable
fun MacroItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold, color = color, fontSize = 14.sp)
        Text(label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }
}