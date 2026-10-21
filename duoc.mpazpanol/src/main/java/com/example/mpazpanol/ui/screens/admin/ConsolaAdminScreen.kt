package com.example.mpazpanol.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
            title = { Text("Rechazar Solicitud") },
            text = {
                OutlinedTextField(value = justificacion, onValueChange = { justificacion = it }, label = { Text("Justificación obligatoria") })
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.actualizarEstadoTicket(ticketARechazar!!, "Rechazada", justificacion)
                    ticketARechazar = null
                    justificacion = ""
                }) { Text("Confirmar Rechazo") }
            }
        )
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Panel de Administración TI") }) }) { padding ->
        LazyColumn(contentPadding = padding, modifier = Modifier.padding(16.dp)) {
            items(tickets) { ticket ->
                Card(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Docente: ${ticket.idSolicitante}", style = MaterialTheme.typography.titleMedium)
                        Text("Estado actual: ${ticket.estado}")

                        if (ticket.estado == "Pendiente") {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Button(onClick = {
                                    viewModel.actualizarEstadoTicket(ticket.id, "Aceptada", null)
                                    // Invoca la notificación nativa
                                    NotificationHelper.mostrarNotificacion(context, "Reserva Aprobada", "Se requiere devolución a las ${ticket.horaDevolucionEstimada}")
                                }) { Text("Aceptar") }

                                OutlinedButton(onClick = { ticketARechazar = ticket.id }) { Text("Rechazar") }
                            }
                        }
                    }
                }
            }
        }
    }
}