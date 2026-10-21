package com.example.mpazpanol.ui.screens.admin

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Room
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mpazpanol.model.Equipo
import com.example.mpazpanol.ui.viewmodel.InventarioViewModel
import com.example.mpazpanol.ui.viewmodel.TicketViewModel
import com.example.mpazpanol.utils.NotificationHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsolaAdminScreen(
    navController: NavController,
    ticketViewModel: TicketViewModel,
    inventarioViewModel: InventarioViewModel
) {
    val context = LocalContext.current
    val tickets by ticketViewModel.todosLosTickets.collectAsState(initial = emptyList())
    val equipos by inventarioViewModel.equipos.collectAsState()

    var pestanaSeleccionada by remember { mutableIntStateOf(0) } // 0: Tickets, 1: Gestion de Stock

    var ticketARechazar by remember { mutableStateOf<Int?>(null) }
    var justificacion by remember { mutableStateOf("") }

    var equipoAEditar by remember { mutableStateOf<Equipo?>(null) }
    var stockDispEdit by remember { mutableStateOf("") }
    var stockTotalEdit by remember { mutableStateOf("") }

    // Modal Rechazar Solicitud
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
                            ticketViewModel.actualizarEstadoTicket(ticketARechazar!!, "Rechazada", justificacion)
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

    // Modal Editar Stock
    if (equipoAEditar != null) {
        AlertDialog(
            onDismissRequest = { equipoAEditar = null },
            title = { Text("Actualizar Stock — ${equipoAEditar?.nombre}", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Ajuste el stock disponible y total de este equipo en la base de datos:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = stockDispEdit,
                        onValueChange = { stockDispEdit = it.filter { char -> char.isDigit() } },
                        label = { Text("Stock Disponible") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = stockTotalEdit,
                        onValueChange = { stockTotalEdit = it.filter { char -> char.isDigit() } },
                        label = { Text("Stock Total") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val eq = equipoAEditar
                        val nuevoDisp = stockDispEdit.toIntOrNull()
                        val nuevoTotal = stockTotalEdit.toIntOrNull()

                        if (eq != null && nuevoDisp != null && nuevoTotal != null) {
                            val equipoActualizado = eq.copy(
                                stockDisponible = nuevoDisp,
                                stockTotal = nuevoTotal
                            )
                            inventarioViewModel.updateEquipo(equipoActualizado)
                            Toast.makeText(context, "Stock actualizado exitosamente", Toast.LENGTH_SHORT).show()
                            equipoAEditar = null
                        } else {
                            Toast.makeText(context, "Ingrese valores numéricos válidos", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Guardar Cambios")
                }
            },
            dismissButton = {
                TextButton(onClick = { equipoAEditar = null }) {
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
                            "Gestión de Solicitudes y Stock",
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
        ) {
            // Pestañas Administrador
            PrimaryTabRow(selectedTabIndex = pestanaSeleccionada) {
                Tab(
                    selected = pestanaSeleccionada == 0,
                    onClick = { pestanaSeleccionada = 0 },
                    text = { Text("Solicitudes (${tickets.size})") },
                    icon = { Icon(Icons.Default.Receipt, contentDescription = null) }
                )
                Tab(
                    selected = pestanaSeleccionada == 1,
                    onClick = { pestanaSeleccionada = 1 },
                    text = { Text("Stock (${equipos.size})") },
                    icon = { Icon(Icons.Default.Inventory, contentDescription = null) }
                )
            }

            if (pestanaSeleccionada == 0) {
                // Pestaña Solicitudes / Tickets
                if (tickets.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
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
                        contentPadding = PaddingValues(16.dp),
                        modifier = Modifier.fillMaxSize(),
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

                                    if (ticket.detalleEquiposJson.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
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
                                                    ticketViewModel.actualizarEstadoTicket(ticket.id, "Aceptada", null)
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
            } else {
                // Pestaña Gestión de Stock e Inventario
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(equipos) { equipo ->
                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        equipo.nombre,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "Categoría: ${equipo.categoria} • Ubicación: ${equipo.ubicacion}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            shape = MaterialTheme.shapes.small
                                        ) {
                                            Text(
                                                "Disponible: ${equipo.stockDisponible}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        Surface(
                                            color = MaterialTheme.colorScheme.secondaryContainer,
                                            shape = MaterialTheme.shapes.small
                                        ) {
                                            Text(
                                                "Total: ${equipo.stockTotal}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }

                                OutlinedButton(
                                    onClick = {
                                        equipoAEditar = equipo
                                        stockDispEdit = equipo.stockDisponible.toString()
                                        stockTotalEdit = equipo.stockTotal.toString()
                                    }
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar Stock", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Editar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
