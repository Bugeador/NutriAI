
package com.example.nutriai

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.nutriai.services.GestorNutricional
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


// Función auxiliar para cámara
fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
    return stream.toByteArray()
}

// Función auxiliar para guardar imagen de galería en la memoria de la App
fun guardarImagenEnInterna(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        // Creamos un nombre de archivo único
        val fileName = "perfil_${System.currentTimeMillis()}.jpg"
        val file = File(context.filesDir, fileName)
        val outputStream = FileOutputStream(file)

        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        file.absolutePath // Devolvemos la ruta real del archivo guardado
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

// 1. CÁMARA
@Composable
actual fun rememberCameraLauncher(gestor: GestorNutricional): (String) -> Unit {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var tempDescription by remember { mutableStateOf("") }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            Toast.makeText(context, "Procesando foto con IA...", Toast.LENGTH_SHORT).show()
            scope.launch(Dispatchers.IO) {
                try {
                    val bytes = bitmapToByteArray(bitmap)
                    gestor.procesarImagen(bytes, tempDescription)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            Toast.makeText(context, "No se tomó ninguna foto", Toast.LENGTH_SHORT).show()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch()
        } else {
            Toast.makeText(context, "Se requiere permiso de cámara", Toast.LENGTH_LONG).show()
        }
    }

    return { descripcion ->
        tempDescription = descripcion
        permissionLauncher.launch(android.Manifest.permission.CAMERA)
    }
}

// 2. GALERÍA (MODIFICADO PARA GUARDAR ARCHIVO)
@Composable
actual fun rememberGalleryLauncher(onImageSelected: (String) -> Unit): () -> Unit {
    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            // Guardamos la imagen físicamente en la app
            val pathReal = guardarImagenEnInterna(context, uri)
            if (pathReal != null) {
                onImageSelected(pathReal)
            }
        }
    }

    return {
        galleryLauncher.launch("image/*")
    }
}