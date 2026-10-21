package com.example.mpazpanol.ui.screens.carrito

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mpazpanol.model.Ticket
import com.example.mpazpanol.ui.viewmodel.TicketViewModel
import com.example.mpazpanol.utils.VibrationHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoScreen(navController: NavController, viewModel: TicketViewModel) {
    val context = LocalContext.current
    val carrito by viewModel.carrito.collectAsState()
    val mensajeError by viewModel.mensajeError.collectAsState()

    var sala by remember { mutableStateOf("") }
    var fechaHora by remember { mutableStateOf("2026-10-22 10:00") } // Formato requerido por ValidadorFechas
    var mostrarResumenModal by remember { mutableStateOf(false) }

    // Escucha errores del backend para activar la vibración
    LaunchedEffect(mensajeError) {
        mensajeError?.let {
            VibrationHelper.vibrarError(context)
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.limpiarError()
        }
    }

    // Modal del Resumen de Reserva
    if (mostrarResumenModal) {
        AlertDialog(
            onDismissRequest = { mostrarResumenModal = false },
            title = {
                Text(
                    text = "Resumen de Solicitud de Reserva",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Verifique los datos antes de enviar la solicitud al Pañol TI:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    HorizontalDivider()

                    Row {
                        Text("📍 Sala/Lugar: ", fontWeight = FontWeight.Bold)
                        Text(sala.ifBlank { "No especificada" })
                    }

                    Row {
                        Text("📅 Fecha y Hora: ", fontWeight = FontWeight.Bold)
                        Text(fechaHora)
                    }

                    Text("💻 Equipos Seleccionados:", fontWeight = FontWeight.Bold)

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            if (carrito.isEmpty()) {
                                Text("Sin equipos seleccionados")
                            } else {
                                carrito.forEach { (equipo, cant) ->
                                    Text("• $cant x ${equipo.nombre} (${equipo.categoria})")
                                }
                            }
                        }
                    }

                    Text(
                        text = "⚠️ Se verificará la anticipación mínima de 3 horas.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarResumenModal = false
                        val detalleStr = if (carrito.isNotEmpty()) {
                            viewModel.generarDetalleEquipos()
                        } else {
                            "1x Proyector EPSON (Reserva Directa)"
                        }

                        val nuevoTicket = Ticket(
                            idSolicitante = "Docente_Demo",
                            detalleEquiposJson = detalleStr,
                            sala = sala,
                            fechaPlanificada = fechaHora,
                            horaInicio = fechaHora.split(" ").lastOrNull() ?: "10:00",
                            horaDevolucionEstimada = "12:00",
                            fechaHoraCreacion = System.currentTimeMillis(),
                            fechaHoraEntregaFisica = null,
                            fechaHoraRecepcionFisica = null
                        )

                        viewModel.insertarTicketConValidacion(nuevoTicket, fechaHora) {
                            Toast.makeText(context, "¡Reserva enviada exitosamente!", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Enviar Solicitud")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarResumenModal = false }) {
                    Text("Modificar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Mi Carrito y Reserva", fontWeight = FontWeight.Bold)
                        Text(
                            "Escuela Marcela Paz — Pañol TI",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sección Equipos en el Carrito
            Text(
                text = "Equipos Seleccionados (${carrito.values.sum()})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            if (carrito.isEmpty()) {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Tu carrito está vacío",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Selecciona equipos en el catálogo para agregarlos.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(onClick = { navController.navigate("catalogo") }) {
                            Text("Ir al Catálogo")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 220.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(carrito.entries.toList()) { (equipo, cantidad) ->
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Devices,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        equipo.nombre,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        equipo.categoria,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    IconButton(
                                        onClick = { viewModel.decrementarDelCarrito(equipo) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Remove, contentDescription = "Menos", modifier = Modifier.size(16.dp))
                                    }

                                    Text(
                                        text = cantidad.toString(),
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium
                                    )

                                    IconButton(
                                        onClick = { viewModel.agregarAlCarrito(equipo) },
                                        modifier = Modifier.size(28.dp),
                                        enabled = cantidad < equipo.stockDisponible
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = "Más", modifier = Modifier.size(16.dp))
                                    }

                                    IconButton(
                                        onClick = { viewModel.removerDelCarrito(equipo) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Eliminar",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            HorizontalDivider()

            // Datos de la Reserva (Lugar y Fecha)
            Text(
                text = "Lugar y Fecha de Reserva",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = sala,
                onValueChange = { sala = it },
                label = { Text("Sala o Laboratorio de uso") },
                leadingIcon = { Icon(Icons.Default.MeetingRoom, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = fechaHora,
                onValueChange = { fechaHora = it },
                label = { Text("Fecha y Hora (yyyy-MM-dd HH:mm)") },
                leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (sala.isBlank()) {
                        Toast.makeText(context, "Por favor ingrese la sala de uso", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (carrito.isEmpty()) {
                        Toast.makeText(context, "Seleccione al menos un equipo del catálogo", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    mostrarResumenModal = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(Icons.Default.Info, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Revisar Resumen y Confirmar", fontWeight = FontWeight.Bold)
            }
        }
    }
}
