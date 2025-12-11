

package com.example.nutriai.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp // <--- IMPORTACIÓN AGREGADA
import com.example.nutriai.services.GestorNutricional
import nutriai.composeapp.generated.resources.Res
import nutriai.composeapp.generated.resources.logo_nutriai
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaLogin(
    gestor: GestorNutricional,
    onLoginSuccess: () -> Unit,
    onIrARegistro: () -> Unit
) {
    var usuario by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        // --- FONDO DECORATIVO ---
        Canvas(modifier = Modifier.fillMaxSize()) {
            val colorPrimario = Color(0xFF6CC551)
            val colorSecundario = Color(0xFF2D9CDB)
            drawCircle(
                color = colorPrimario.copy(alpha = 0.1f),
                radius = 400.dp.toPx(),
                center = Offset(x = 0f, y = 0f)
            )
            drawCircle(
                color = colorSecundario.copy(alpha = 0.05f),
                radius = 300.dp.toPx(),
                center = Offset(x = size.width, y = size.height)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // --- LOGO GRANDE ---
            Surface(
                modifier = Modifier
                    .size(140.dp)
                    .shadow(12.dp, CircleShape)
                    .clip(CircleShape),
                color = Color.White
            ) {
                Image(
                    painter = painterResource(Res.drawable.logo_nutriai),
                    contentDescription = "Logo NutriAI",
                    modifier = Modifier.padding(20.dp).fillMaxSize()
                )
            }

            Spacer(Modifier.height(32.dp))

            Text("NutriAI", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
            Text("Tu nutricionista inteligente", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)

            Spacer(Modifier.height(48.dp))

            // --- CAMPOS DE TEXTO ---
            OutlinedTextField(
                value = usuario,
                onValueChange = { usuario = it },
                label = { Text("Usuario") },
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = clave,
                onValueChange = { clave = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true
            )

            if (error.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    // La UI no sabe como se valida, delega la logica al GestorNutricional.
                    if (gestor.login(usuario, clave)) {
                        onLoginSuccess()
                    } else {
                        error = "Usuario o contraseña incorrectos"
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp).shadow(6.dp, MaterialTheme.shapes.small),
                shape = MaterialTheme.shapes.small
            ) {
                Text("Iniciar Sesión", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(16.dp))

            TextButton(onClick = onIrARegistro) {
                Text("¿No tienes cuenta? ", color = Color.Gray)
                Text("Regístrate aquí", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
        }
    }
}