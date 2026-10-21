package com.example.mpazpanol.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Room
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mpazpanol.ui.viewmodel.TicketViewModel
import com.example.mpazpanol.utils.NotificationHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsolaAdminScreen(navController: NavController, viewModel: TicketViewModel) {
    val context = LocalContext.current
    val tickets by viewModel.todosLosTickets.collectAsState(initial = emptyList())
    var ticketARechazar by remember { mutableStateOf<Int?>(null) }
    var justificacion by remember { mutableStateOf("") }

    if (ticketARechazar != null) {
        AlertDialog(
            onDismissRequest = { ticketARechazar = null },
            title = { Text("Rechazar Solicitud", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Indique la causa o motivo del rechazo:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = justificacion,
                        onValueChange = { justificacion = it },
                        label = { Text("Justificación obligatoria") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (justificacion.isNotBlank()) {
                            viewModel.actualizarEstadoTicket(ticketARechazar!!, "Rechazada", justificacion)
                            ticketARechazar = null
                            justificacion = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Confirmar Rechazo")
                }
            },
            dismissButton = {
                TextButton(onClick = { ticketARechazar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Panel Administrador TI", fontWeight = FontWeight.Bold)
                        Text(
                            "Gestión de Solicitudes — Pañol",
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
        if (tickets.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No hay solicitudes registradas",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                contentPadding = padding,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(tickets) { ticket ->
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "Docente: ${ticket.idSolicitante}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                val (statusBg, statusFg) = when (ticket.estado) {
                                    "Aceptada" -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
                                    "Rechazada" -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
                                    else -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
                                }

                                Surface(
                                    color = statusBg,
                                    shape = MaterialTheme.shapes.small
                                ) {
                                    Text(
                                        text = ticket.estado,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = statusFg,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Room,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "Sala: ${ticket.sala} — Fecha: ${ticket.fechaPlanificada}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (!ticket.detalleEquiposJson.isNull_or_blank()) {
                                Text(
                                    "Equipos: ${ticket.detalleEquiposJson}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            if (ticket.estado == "Pendiente") {
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            viewModel.actualizarEstadoTicket(ticket.id, "Aceptada", null)
                                            NotificationHelper.mostrarNotificacion(
                                                context,
                                                "Reserva Aprobada",
                                                "Se requiere devolución a las ${ticket.horaDevolucionEstimada}"
                                            )
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Aceptar")
                                    }

                                    OutlinedButton(
                                        onClick = { ticketARechazar = ticket.id },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = MaterialTheme.colorScheme.error
                                        )
                                    ) {
                                        Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Rechazar")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Extension helper for String check
private fun String?.isNull_or_blank(): Boolean = this.isNullOrBlank()
