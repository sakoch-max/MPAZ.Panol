package com.example.mpazpanol.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mpazpanol.model.Ticket
import com.example.mpazpanol.ui.viewmodel.InventarioViewModel
import com.example.mpazpanol.ui.viewmodel.TicketViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsolaAdminScreen(
    navController: NavController,
    ticketViewModel: TicketViewModel,
    inventarioViewModel: InventarioViewModel
) {

    // Lista temporal (Mock) para que pruebes la vista antes de conectar la BD
    val tickets = listOf(
        Ticket(idSolicitante = "profe_juan", detalleEquiposJson = "Proyector EPSON", sala = "Lab 1", fechaPlanificada = "2026-10-22", horaInicio = "10:00", horaDevolucionEstimada = "12:00", fechaHoraCreacion = System.currentTimeMillis(), fechaHoraEntregaFisica = null, fechaHoraRecepcionFisica = null)
    )

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Consola TI - Solicitudes") })
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues).fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(tickets) { ticket ->
                TicketAdminCard(ticket = ticket)
            }
        }
    }
}

@Composable
fun TicketAdminCard(ticket: Ticket) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Solicitante: ${ticket.idSolicitante}", style = MaterialTheme.typography.titleMedium)
            Text("Equipos: ${ticket.detalleEquiposJson}")
            Text("Sala: ${ticket.sala} | Uso: ${ticket.horaInicio} - ${ticket.horaDevolucionEstimada}")
            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Aceptar")
                }
                OutlinedButton(
                    onClick = { }
                ) {
                    Text("Rechazar")
                }
            }
        }
    }
}