package com.example.mpazpanol.ui.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mpazpanol.ui.components.EscuelaMarcelaPazLogo
import com.example.mpazpanol.ui.viewmodel.DocenteViewModel
import com.example.mpazpanol.utils.ValidadorEntradas
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    navController: NavController,
    onLoginAsAdmin: (Boolean) -> Unit,
    docenteViewModel: DocenteViewModel,
) {
    var usuario by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    var mensajeErrorCustom by remember { mutableStateOf<String?>(null) }
    var cargando by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Emblema original del Colegio Escritora Marcela Paz E-764
                EscuelaMarcelaPazLogo(size = 96.dp)

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Sistema Pañol TI",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = usuario,
                    onValueChange = {
                        usuario = it
                        error = false
                        mensajeErrorCustom = null
                    },
                    label = { Text("Correo Institucional") },
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = null)
                    },
                    isError = error,
                    trailingIcon = {
                        if (error) Icon(Icons.Default.Warning, contentDescription = "Error")
                    },
                    supportingText = {
                        if (error) {
                            Text(
                                mensajeErrorCustom ?: "El correo o usuario no puede estar vacío",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val input = usuario.trim().lowercase()
                        if (input.isBlank()) {
                            error = true
                            mensajeErrorCustom = "El campo no puede estar vacío"
                            return@Button
                        }

                        if (input == "admin") {
                            onLoginAsAdmin(true)
                            navController.navigate("admin")
                        } else {
                            if (!ValidadorEntradas.esCorreoValido(input)) {
                                error = true
                                mensajeErrorCustom = "Ingrese un formato de correo válido (ej: docente@marcelapaz.cl)"
                                return@Button
                            }

                            cargando = true
                            scope.launch {
                                val autorizado = docenteViewModel.esCorreoAutorizado(input)
                                cargando = false
                                if (autorizado) {
                                    onLoginAsAdmin(false)
                                    navController.navigate("catalogo")
                                } else {
                                    error = true
                                    mensajeErrorCustom = "El correo '$input' no está autorizado por el Administrador del colegio."
                                }
                            }
                        }
                    },
                    enabled = !cargando,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    if (cargando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Ingresar al Sistema", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Solo cuentas de docente previamente autorizadas por el Administrador TI",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
