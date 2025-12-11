package com.example.nutriai

// Esto es pseudocódigo Swift/Kotlin para el punto de entrada
// El apiKey debe obtenerse de un archivo de configuración SEGURO (plist, Secrets.swift, etc.)
// Revisa tu MainViewController.kt o AppDelegate.swift.


import com.example.nutriai.services.GeminiAlimenticio

// 1. Obtener la clave (de forma segura, ej: de un plist)
val GEMINI_API_KEY = "TU_CLAVE_REAL_AQUI"

// 2. Crear el servicio de IA
val geminiService = GeminiAlimenticio(GEMINI_API_KEY)

// 3. Pasar el servicio al composable principal
ComposeApp(geminiService, onLaunchCamera = { /* implementación de launchCamera */ })