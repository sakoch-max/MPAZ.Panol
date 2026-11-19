package com.example.mpazpanol.ui.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mpazpanol.ui.components.EscuelaMarcelaPazLogo
import com.example.mpazpanol.ui.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    onLoginAsAdmin: (Boolean) -> Unit,
    loginViewModel: LoginViewModel,
) {
    val uiState by loginViewModel.uiState.collectAsState()

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
                    value = uiState.usuario,
                    onValueChange = { loginViewModel.onUsuarioChange(it) },
                    label = { Text("Correo Institucional") },
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = null)
                    },
                    isError = uiState.isError,
                    trailingIcon = {
                        if (uiState.isError) Icon(Icons.Default.Warning, contentDescription = "Error")
                    },
                    supportingText = {
                        if (uiState.isError) {
                            Text(
                                uiState.mensajeError ?: "El correo o usuario no puede estar vacío",
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
                        loginViewModel.iniciarSesion { esAdmin ->
                            onLoginAsAdmin(esAdmin)
                            if (esAdmin) {
                                navController.navigate("admin")
                            } else {
                                navController.navigate("catalogo")
                            }
                        }
                    },
                    enabled = !uiState.cargando,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    if (uiState.cargando) {
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