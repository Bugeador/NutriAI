


package com.example.nutriai

import com.example.nutriai.services.GestorNutricional
import platform.UIKit.UIViewController
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import platform.Foundation.NSData
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970


@OptIn(ExperimentalForeignApi::class)
fun topViewController(): UIViewController? {
    return UIApplication.sharedApplication.keyWindow?.rootViewController
}

@OptIn(ExperimentalForeignApi::class)
class ImagePickerDelegate(
    private val gestor: GestorNutricional,
    private val picker: UIImagePickerController
) : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {

    override fun imagePickerController(
        picker: UIImagePickerController,
        didFinishPickingMediaWithInfo: Map<Any?, Any>
    ) {
        val originalImage = didFinishPickingMediaWithInfo["UIImagePickerControllerOriginalImage"] as? UIImage

        val imageData: NSData? = originalImage?.let {
            platform.UIKit.UIImageJPEGRepresentation(it, 0.8) // Convertir a JPEG con calidad 80%
        }

        if (imageData != null) {
            val bytes = imageData.toKmpByteArray()

            CoroutineScope(Dispatchers.Default).launch {
                println("Simulación iOS: Imagen tomada. Llamando al servicio Gemini...")

                val comida = gestor.geminiService.analizarImagenYEstimarCalorias(bytes)

                gestor.registrarComida(comida)
                println("Simulación iOS: Comida registrada: ${comida.nombre} - ${comida.caloriasEstimadas} kcal")
            }
        }

        picker.dismissViewControllerAnimated(true, completion = null)
    }

    override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
        picker.dismissViewControllerAnimated(true, completion = null)
    }
}

@OptIn(ExperimentalForeignApi::class)
actual fun launchCamera(gestor: GestorNutricional) {
    val topVC = topViewController() ?: run {
        println("Error: No se pudo obtener el ViewController principal.")
        return
    }

    val picker = UIImagePickerController()
    picker.sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera

    val delegate = ImagePickerDelegate(gestor, picker)
    picker.delegate = delegate



    topVC.presentViewController(picker, animated = true, completion = null)
}


@OptIn(ExperimentalForeignApi::class)
fun NSData.toKmpByteArray(): ByteArray = ByteArray(length.toInt()).apply {
    usePinned {
        platform.Foundation.memcpy(it.addressOf(0), this@toKmpByteArray.bytes, this@toKmpByteArray.length)
    }
}