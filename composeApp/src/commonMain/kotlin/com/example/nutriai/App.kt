

package com.example.nutriai

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.nutriai.services.GeminiAlimenticio
import com.example.nutriai.services.GestorNutricional
import com.example.nutriai.ui.*
import com.example.nutriai.ui.NutriAITheme

enum class PantallaGlobal {
    SPLASH,
    LOGIN,
    REGISTRO,
    APP_PRINCIPAL
}

data class ItemNavegacion(val titulo: String, val iconoOutlined: ImageVector, val iconoFilled: ImageVector)

@Composable
fun App(geminiService: GeminiAlimenticio) {
    val gestor = remember { GestorNutricional(geminiService) }
    val cameraTrigger = rememberCameraLauncher(gestor)

    NutriAITheme {
        var pantallaActual by remember { mutableStateOf(PantallaGlobal.SPLASH) }
        var tabSeleccionada by remember { mutableIntStateOf(1) }

        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(1000)
            if (gestor.usuarioActualId != null) {
                pantallaActual = PantallaGlobal.APP_PRINCIPAL
            } else {
                pantallaActual = PantallaGlobal.LOGIN
            }
        }

        when (pantallaActual) {
            PantallaGlobal.SPLASH -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            PantallaGlobal.LOGIN -> {
                PantallaLogin(
                    gestor = gestor,
                    onLoginSuccess = {
                        if (gestor.paciente != null) {
                            pantallaActual = PantallaGlobal.APP_PRINCIPAL
                        } else {
                            pantallaActual = PantallaGlobal.REGISTRO
                        }
                    },
                    onIrARegistro = {
                        pantallaActual = PantallaGlobal.REGISTRO
                    }
                )
            }
            PantallaGlobal.REGISTRO -> {
                PantallaRegistroCompleto(
                    onRegistroCompletado = { usuario, clave, paciente ->
                        gestor.registrarUsuarioNuevo(usuario, clave, paciente)
                        pantallaActual = PantallaGlobal.APP_PRINCIPAL
                    },
                    onVolver = {
                        pantallaActual = PantallaGlobal.LOGIN
                    }
                )
            }
            PantallaGlobal.APP_PRINCIPAL -> {
                val items = listOf(
                    ItemNavegacion("Perfil", Icons.Outlined.Person, Icons.Default.Person),
                    ItemNavegacion("Diario", Icons.Outlined.Restaurant, Icons.Default.Restaurant),
                    ItemNavegacion("Estadísticas", Icons.Outlined.DateRange, Icons.Default.DateRange)
                )

                val verdeManzanaFuerte = Color(0xFF6CC551)

                Scaffold(
                    bottomBar = {
                        NavigationBar(
                            containerColor = Color.White,
                            tonalElevation = 10.dp
                        ) {
                            // --- ITEM 1: PERFIL ---
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        imageVector = if (tabSeleccionada == 0) items[0].iconoFilled else items[0].iconoOutlined,
                                        contentDescription = "Perfil"
                                    )
                                },
                                label = { Text("Perfil", fontWeight = if(tabSeleccionada == 0) FontWeight.Bold else FontWeight.Normal) },
                                selected = tabSeleccionada == 0,
                                onClick = { tabSeleccionada = 0 },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = verdeManzanaFuerte,
                                    selectedTextColor = verdeManzanaFuerte,
                                    indicatorColor = Color.Transparent,
                                    unselectedIconColor = Color.Gray,
                                    unselectedTextColor = Color.Gray
                                )
                            )

                            // --- ITEM 2: DIARIO ---
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        imageVector = if (tabSeleccionada == 1) items[1].iconoFilled else items[1].iconoOutlined,
                                        contentDescription = "Diario"
                                    )
                                },
                                label = { Text("Diario", fontWeight = if(tabSeleccionada == 1) FontWeight.Bold else FontWeight.Normal) },
                                selected = tabSeleccionada == 1,
                                onClick = { tabSeleccionada = 1 },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = verdeManzanaFuerte,
                                    selectedTextColor = verdeManzanaFuerte,
                                    indicatorColor = Color.Transparent,
                                    unselectedIconColor = Color.Gray,
                                    unselectedTextColor = Color.Gray
                                )
                            )

                            // --- ITEM 3: REPORTES ---
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        imageVector = if (tabSeleccionada == 2) items[2].iconoFilled else items[2].iconoOutlined,
                                        contentDescription = "Reportes"
                                    )
                                },
                                label = { Text("Reportes", fontWeight = if(tabSeleccionada == 2) FontWeight.Bold else FontWeight.Normal) },
                                selected = tabSeleccionada == 2,
                                onClick = { tabSeleccionada = 2 },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = verdeManzanaFuerte,
                                    selectedTextColor = verdeManzanaFuerte,
                                    indicatorColor = Color.Transparent,
                                    unselectedIconColor = Color.Gray,
                                    unselectedTextColor = Color.Gray
                                )
                            )
                        }
                    }
                ) { padding ->
                    Surface(modifier = Modifier.padding(padding), color = MaterialTheme.colorScheme.background) {
                        Crossfade(
                            targetState = tabSeleccionada,
                            animationSpec = tween(durationMillis = 400),
                            label = "CrossfadeTabs"
                        ) { tab ->
                            when (tab) {
                                0 -> PantallaPerfil(gestor, onLogout = { pantallaActual = PantallaGlobal.LOGIN })
                                1 -> PantallaResultados(gestor, onLaunchCamera = { cameraTrigger(it) })
                                2 -> PantallaEstadisticas(gestor)
                            }
                        }
                    }
                }
            }
        }
    }
}