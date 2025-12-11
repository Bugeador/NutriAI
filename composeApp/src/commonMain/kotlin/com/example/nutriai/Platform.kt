
package com.example.nutriai

import androidx.compose.runtime.Composable
import com.example.nutriai.services.GestorNutricional

// 'expect fun' define un contrato. Declaramos QUE queremos hacer (lanzar camara),
@Composable
expect fun rememberCameraLauncher(gestor: GestorNutricional): (String) -> Unit

// --- Agregamos esto para la Galería ---
@Composable
expect fun rememberGalleryLauncher(onImageSelected: (String) -> Unit): () -> Unit