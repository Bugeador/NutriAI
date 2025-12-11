
package com.example.nutriai.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- PALETA DE COLORES PERSONALIZADA ---
// Definicion centralizada de constantes visuales.
val GreenApplePrimary = Color(0xFF6CC551)
val GreenAppleDark = Color(0xFF388E3C)
val GreenBackground = Color(0xFFF2F8F2)
val WhiteCard = Color(0xFFFFFFFF)
val TextBlack = Color(0xFF1F1F1F)
val TextGrey = Color(0xFF757575)

val AIBlue = Color(0xFF2D9CDB)
val AIGreen = Color(0xFF6CC551)

private val LightColorScheme = lightColorScheme(
    primary = GreenApplePrimary,
    onPrimary = Color.White,
    secondary = GreenAppleDark,
    onSecondary = Color.White,
    background = GreenBackground,
    surface = WhiteCard,
    onSurface = TextBlack,
    surfaceVariant = Color(0xFFE8F5E9),
    error = Color(0xFFE53935)
)

// --- FORMAS ORGÁNICAS ---
val NutriAIShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),     // Botones / Inputs
    medium = RoundedCornerShape(20.dp),    // Tarjetas de comida
    large = RoundedCornerShape(32.dp),     // Contenedores grandes / BottomSheet
    extraLarge = RoundedCornerShape(48.dp)
)

// --- TIPOGRAFÍA ---
val NutriAITypography = Typography(
    headlineMedium = TextStyle(
        fontWeight = FontWeight.ExtraBold,
        fontSize = 28.sp,
        color = TextBlack
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        color = TextBlack
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        color = TextGrey
    )
)

@Composable
fun NutriAITheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        shapes = NutriAIShapes,
        typography = NutriAITypography,
        content = content
    )
}