


package com.example.nutriai.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.nutriai.models.Paciente
import org.jetbrains.compose.resources.painterResource
import nutriai.composeapp.generated.resources.Res
import nutriai.composeapp.generated.resources.logo_nutriai

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRegistroCompleto(
    onRegistroCompletado: (String, String, Paciente) -> Unit,
    onVolver: () -> Unit
) {
    // Datos de Cuenta
    var usuario by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }

    // Datos Personales
    var nombre by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var peso by remember { mutableStateOf("") }
    var estatura by remember { mutableStateOf("") }
    var esHombre by remember { mutableStateOf(true) }

    // --- VALIDACION ---
    val edadInt = edad.toIntOrNull()
    val estaturaDouble = estatura.toDoubleOrNull()
    val pesoDouble = peso.toDoubleOrNull()

    // 1. Reglas de la cuenta
    val esClaveValida = clave.length >= 4
    val esCuentaValida = usuario.isNotBlank() && esClaveValida

    // Calculamos si debemos mostrar el error (solo si ya escribió algo y es menor a 4)
    val mostrarErrorClave = clave.isNotEmpty() && !esClaveValida

    // 2. Reglas de los datos personales
    val esDatosValidos = nombre.isNotBlank() &&
            (edadInt != null && edadInt in 3..110) &&
            (estaturaDouble != null && estaturaDouble in 50.0..250.0) &&
            (pesoDouble != null && pesoDouble in 10.0..300.0)

    val isFormValid = esCuentaValida && esDatosValidos

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(Res.drawable.logo_nutriai),
                            contentDescription = "Logo",
                            modifier = Modifier.size(32.dp).padding(end = 8.dp)
                        )
                        Text("NutriAI - Registro")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Crea tu cuenta", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))

            // --- SECCION CUENTA ---
            OutlinedTextField(
                value = usuario,
                onValueChange = { usuario = it },
                label = { Text("Usuario") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = clave,
                onValueChange = { clave = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                isError = mostrarErrorClave, // Ahora sí existe esta variable
                supportingText = {
                    if (mostrarErrorClave) {
                        Text("Mínimo 4 caracteres", color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Divider(Modifier.padding(vertical = 16.dp))
            Text("Datos Personales", style = MaterialTheme.typography.titleMedium)

            // --- SECCIÓN DATOS ---
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre Real") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = edad,
                onValueChange = { if (it.length <= 3) edad = it },
                label = { Text("Edad") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Género:")
                Spacer(Modifier.width(16.dp))
                RadioButton(selected = esHombre, onClick = { esHombre = true })
                Text("Hombre", Modifier.padding(end = 16.dp))
                RadioButton(selected = !esHombre, onClick = { esHombre = false })
                Text("Mujer")
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                OutlinedTextField(
                    value = peso,
                    onValueChange = { peso = it },
                    label = { Text("Peso (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                )
                OutlinedTextField(
                    value = estatura,
                    onValueChange = { estatura = it },
                    label = { Text("Estatura (cm)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f).padding(start = 8.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    // Creamos una instancia concreta de la clase Paciente con los datos validados.
                    val nuevoPaciente = Paciente(
                        nombre = nombre,
                        edad = edadInt ?: 0,
                        esHombre = esHombre,
                        _peso = pesoDouble ?: 0.0,
                        _estatura = estaturaDouble ?: 0.0
                    )
                    // Llamamos al callback con los datos listos para ser procesados por el Gestor.
                    onRegistroCompletado(usuario, clave, nuevoPaciente)
                },
                enabled = isFormValid,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Registrarme")
            }

            Spacer(Modifier.height(8.dp))
            TextButton(onClick = onVolver) {
                Text("Ya tengo cuenta, volver al Login")
            }
        }
    }
}
