package com.example.mpazpanol.ui.screens.admin

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.People
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
import com.example.mpazpanol.model.Docente
import com.example.mpazpanol.model.Equipo
import com.example.mpazpanol.ui.components.EscuelaMarcelaPazLogo
import com.example.mpazpanol.ui.viewmodel.DocenteViewModel
import com.example.mpazpanol.ui.viewmodel.InventarioViewModel
import com.example.mpazpanol.ui.viewmodel.TicketViewModel
import com.example.mpazpanol.utils.NotificationHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsolaAdminScreen(
    navController: NavController,
    ticketViewModel: TicketViewModel,
    inventarioViewModel: InventarioViewModel,
    docenteViewModel: DocenteViewModel
) {
    val context = LocalContext.current
    val tickets by ticketViewModel.todosLosTickets.collectAsState(initial = emptyList())
    val equipos by inventarioViewModel.equipos.collectAsState()
    val docentes by docenteViewModel.docentes.collectAsState()

    var pestanaSeleccionada by remember { mutableIntStateOf(0) } // 0: Resumen, 1: Solicitudes, 2: Inventario, 3: Docentes

    var ticketARechazar by remember { mutableStateOf<Int?>(null) }
    var justificacion by remember { mutableStateOf("") }

    var equipoAEditar by remember { mutableStateOf<Equipo?>(null) }
    var stockDispEdit by remember { mutableStateOf("") }
    var stockTotalEdit by remember { mutableStateOf("") }

    var mostrarModalAgregarEquipo by remember { mutableStateOf(false) }
    var nuevoNombre by remember { mutableStateOf("") }
    var nuevaCategoria by remember { mutableStateOf("Audiovisual") }
    var nuevaDescripcion by remember { mutableStateOf("") }
    var nuevaUbicacion by remember { mutableStateOf("") }
    var nuevoStockTotal by remember { mutableStateOf("5") }

    var equipoAEliminar by remember { mutableStateOf<Equipo?>(null) }
    var filtroEstadoTicket by remember { mutableStateOf("Todas") }

    // Estado Formulario Registrar Docente
    var mostrarModalAgregarDocente by remember { mutableStateOf(false) }
    var nuevoNombreDocente by remember { mutableStateOf("") }
    var nuevoCorreoDocente by remember { mutableStateOf("") }
    var nuevoRutDocente by remember { mutableStateOf("") }
    var docenteAEliminar by remember { mutableStateOf<Docente?>(null) }

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
                            Toast.makeText(context, "Solicitud rechazada", Toast.LENGTH_SHORT).show()
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
                        text = "Ajuste el stock disponible y total de este equipo:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = stockDispEdit,
                        onValueChange = { stockDispEdit = it.filter { c -> c.isDigit() } },
                        label = { Text("Stock Disponible") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = stockTotalEdit,
                        onValueChange = { stockTotalEdit = it.filter { c -> c.isDigit() } },
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

    // Modal Agregar Nuevo Equipo
    if (mostrarModalAgregarEquipo) {
        AlertDialog(
            onDismissRequest = { mostrarModalAgregarEquipo = false },
            title = { Text("Añadir Nuevo Equipo al Pañol", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = nuevoNombre,
                        onValueChange = { nuevoNombre = it },
                        label = { Text("Nombre del Equipo") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = nuevaCategoria,
                        onValueChange = { nuevaCategoria = it },
                        label = { Text("Categoría (Audiovisual, Computación, Robótica, Redes)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = nuevaDescripcion,
                        onValueChange = { nuevaDescripcion = it },
                        label = { Text("Descripción / Especificaciones") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = nuevaUbicacion,
                        onValueChange = { nuevaUbicacion = it },
                        label = { Text("Ubicación en Pañol (ej: Estante A3)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = nuevoStockTotal,
                        onValueChange = { nuevoStockTotal = it.filter { c -> c.isDigit() } },
                        label = { Text("Cantidad / Stock Inicial") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val stockNum = nuevoStockTotal.toIntOrNull() ?: 1
                        if (nuevoNombre.isNotBlank()) {
                            val nuevoEq = Equipo(
                                nombre = nuevoNombre,
                                categoria = nuevaCategoria.ifBlank { "General" },
                                descripcion = nuevaDescripcion,
                                ubicacion = nuevaUbicacion.ifBlank { "Pañol Central" },
                                stockTotal = stockNum,
                                stockDisponible = stockNum,
                                estado = "Disponible"
                            )
                            inventarioViewModel.insertEquipo(nuevoEq)
                            Toast.makeText(context, "Equipo registrado con éxito", Toast.LENGTH_SHORT).show()
                            mostrarModalAgregarEquipo = false
                            nuevoNombre = ""
                            nuevaDescripcion = ""
                            nuevaUbicacion = ""
                        } else {
                            Toast.makeText(context, "Ingrese el nombre del equipo", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Crear Equipo")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarModalAgregarEquipo = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Modal Eliminar Equipo
    if (equipoAEliminar != null) {
        AlertDialog(
            onDismissRequest = { equipoAEliminar = null },
            title = { Text("Eliminar Equipo") },
            text = { Text("¿Está seguro que desea eliminar '${equipoAEliminar?.nombre}' del inventario?") },
            confirmButton = {
                Button(
                    onClick = {
                        equipoAEliminar?.let { inventarioViewModel.deleteEquipo(it) }
                        Toast.makeText(context, "Equipo eliminado", Toast.LENGTH_SHORT).show()
                        equipoAEliminar = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { equipoAEliminar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Modal Registrar Nuevo Docente Autorizado
    if (mostrarModalAgregarDocente) {
        AlertDialog(
            onDismissRequest = { mostrarModalAgregarDocente = false },
            title = { Text("Registrar Nuevo Docente Autorizado", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Solo los docentes registrados por el Administrador TI podrán iniciar sesión en la app:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = nuevoNombreDocente,
                        onValueChange = { nuevoNombreDocente = it },
                        label = { Text("Nombre Completo") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = nuevoCorreoDocente,
                        onValueChange = { nuevoCorreoDocente = it },
                        label = { Text("Correo Institucional (ej: profe@marcelapaz.cl)") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = nuevoRutDocente,
                        onValueChange = { nuevoRutDocente = it },
                        label = { Text("RUT (Opcional)") },
                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val correoStr = nuevoCorreoDocente.trim().lowercase()
                        val nombreStr = nuevoNombreDocente.trim()

                        if (!com.example.mpazpanol.utils.ValidadorEntradas.esNombreValido(nombreStr)) {
                            Toast.makeText(context, "El nombre del docente no debe contener números ni estar vacío", Toast.LENGTH_LONG).show()
                            return@Button
                        }

                        if (!com.example.mpazpanol.utils.ValidadorEntradas.esCorreoValido(correoStr)) {
                            Toast.makeText(context, "Ingrese un formato de correo institucional válido", Toast.LENGTH_LONG).show()
                            return@Button
                        }

                        val nuevoDocente = Docente(
                            nombre = nombreStr,
                            correo = correoStr,
                            rut = nuevoRutDocente.trim()
                        )
                        docenteViewModel.registrarDocente(nuevoDocente) {
                            Toast.makeText(context, "Docente registrado con éxito", Toast.LENGTH_SHORT).show()
                            mostrarModalAgregarDocente = false
                            nuevoNombreDocente = ""
                            nuevoCorreoDocente = ""
                            nuevoRutDocente = ""
                        }
                    }
                ) {
                    Text("Registrar Docente")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarModalAgregarDocente = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Modal Eliminar Docente
    if (docenteAEliminar != null) {
        AlertDialog(
            onDismissRequest = { docenteAEliminar = null },
            title = { Text("Desactivar Cuenta Docente") },
            text = { Text("¿Desea revocar el acceso de '${docenteAEliminar?.nombre}' (${docenteAEliminar?.correo})?") },
            confirmButton = {
                Button(
                    onClick = {
                        docenteAEliminar?.let { docenteViewModel.eliminarDocente(it.id) }
                        Toast.makeText(context, "Acceso de docente revocado", Toast.LENGTH_SHORT).show()
                        docenteAEliminar = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Revocar Acceso")
                }
            },
            dismissButton = {
                TextButton(onClick = { docenteAEliminar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        EscuelaMarcelaPazLogo(size = 36.dp, showSubtitle = false)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Consola Administrador TI", fontWeight = FontWeight.Bold)
                            Text(
                                "Colegio Escritora Marcela Paz E-764",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            if (pestanaSeleccionada == 2) {
                ExtendedFloatingActionButton(
                    onClick = { mostrarModalAgregarEquipo = true },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Nuevo Equipo") },
                    containerColor = MaterialTheme.colorScheme.primary
                )
            } else if (pestanaSeleccionada == 3) {
                ExtendedFloatingActionButton(
                    onClick = { mostrarModalAgregarDocente = true },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Nuevo Docente") },
                    containerColor = MaterialTheme.colorScheme.primary
                )
            }
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
                    text = { Text("Resumen") },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = null) }
                )
                Tab(
                    selected = pestanaSeleccionada == 1,
                    onClick = { pestanaSeleccionada = 1 },
                    text = { Text("Solicitudes (${tickets.size})") },
                    icon = { Icon(Icons.Default.Receipt, contentDescription = null) }
                )
                Tab(
                    selected = pestanaSeleccionada == 2,
                    onClick = { pestanaSeleccionada = 2 },
                    text = { Text("Stock (${equipos.size})") },
                    icon = { Icon(Icons.Default.Inventory, contentDescription = null) }
                )
                Tab(
                    selected = pestanaSeleccionada == 3,
                    onClick = { pestanaSeleccionada = 3 },
                    text = { Text("Docentes (${docentes.size})") },
                    icon = { Icon(Icons.Default.People, contentDescription = null) }
                )
            }

            when (pestanaSeleccionada) {
                0 -> {
                    // Pestaña Dashboard / Resumen General
                    val totalEquiposCount = equipos.sumOf { it.stockTotal }
                    val stockDisponibleCount = equipos.sumOf { it.stockDisponible }
                    val pendientesCount = tickets.count { it.estado == "Pendiente" }
                    val aceptadasCount = tickets.count { it.estado == "Aceptada" }

                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Text(
                                text = "Estadísticas del Pañol TI",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                ElevatedCard(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.elevatedCardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Icon(Icons.Default.Devices, contentDescription = null)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(totalEquiposCount.toString(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                                        Text("Total Equipos Físicos", style = MaterialTheme.typography.bodySmall)
                                    }
                                }

                                ElevatedCard(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.elevatedCardColors(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                                    )
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(stockDisponibleCount.toString(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                                        Text("Stock Disponible", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                ElevatedCard(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.elevatedCardColors(
                                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                                    )
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Icon(Icons.Default.Receipt, contentDescription = null)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(pendientesCount.toString(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                                        Text("Solicitudes Pendientes", style = MaterialTheme.typography.bodySmall)
                                    }
                                }

                                ElevatedCard(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.elevatedCardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(aceptadasCount.toString(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                                        Text("Reservas Aprobadas", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }

                        item {
                            ElevatedCard(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.elevatedCardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        "Docentes Autorizados (${docentes.size})",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "Solo los correos registrados en la pestaña 'Docentes' podrán ingresar al sistema.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Button(onClick = { pestanaSeleccionada = 3 }) {
                                        Text("Gestionar Cuentas Docentes")
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // Pestaña Solicitudes / Tickets
                    val ticketsFiltrados = when (filtroEstadoTicket) {
                        "Pendientes" -> tickets.filter { it.estado == "Pendiente" }
                        "Aceptadas" -> tickets.filter { it.estado == "Aceptada" }
                        "Rechazadas" -> tickets.filter { it.estado == "Rechazada" }
                        else -> tickets
                    }

                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Todas", "Pendientes", "Aceptadas", "Rechazadas").forEach { f ->
                                FilterChip(
                                    selected = filtroEstadoTicket == f,
                                    onClick = { filtroEstadoTicket = f },
                                    label = { Text(f) }
                                )
                            }
                        }

                        if (ticketsFiltrados.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "No hay solicitudes en esta categoría",
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
                                items(
                                    items = ticketsFiltrados,
                                    key = { it.id }
                                ) { ticket ->
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

                                            if (ticket.comentarioGestion != null) {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    "Motivo: ${ticket.comentarioGestion}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.error
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
                                                            Toast.makeText(context, "Reserva aceptada", Toast.LENGTH_SHORT).show()
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

                2 -> {
                    // Pestaña Gestión de Inventario & Equipos
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = equipos,
                            key = { it.id }
                        ) { equipo ->
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
                                        }

                                        IconButton(onClick = { equipoAEliminar = equipo }) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Eliminar",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }

                                    if (equipo.descripcion.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            equipo.descripcion,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
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

                                        OutlinedButton(
                                            onClick = {
                                                equipoAEditar = equipo
                                                stockDispEdit = equipo.stockDisponible.toString()
                                                stockTotalEdit = equipo.stockTotal.toString()
                                            }
                                        ) {
                                            Icon(Icons.Default.Edit, contentDescription = "Editar Stock", modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Editar Stock")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                3 -> {
                    // Pestaña Gestión de Cuentas Docentes
                    Column(modifier = Modifier.fillMaxSize()) {
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            colors = CardDefaults.elevatedCardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "Control de Acceso Institucional",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Solo los docentes con correos registrados en esta lista podrán iniciar sesión en la aplicación.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        if (docentes.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "No hay docentes autorizados registrados",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(
                                    items = docentes,
                                    key = { it.id }
                                ) { docente ->
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
                                            Surface(
                                                shape = MaterialTheme.shapes.small,
                                                color = MaterialTheme.colorScheme.secondaryContainer,
                                                modifier = Modifier.size(44.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(
                                                        Icons.Default.Person,
                                                        contentDescription = null,
                                                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.width(16.dp))

                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    docente.nombre,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    docente.correo,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                                if (docente.rut.isNotBlank()) {
                                                    Text(
                                                        "RUT: ${docente.rut}",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }

                                            IconButton(onClick = { docenteAEliminar = docente }) {
                                                Icon(
                                                    Icons.Default.Delete,
                                                    contentDescription = "Revocar Acceso",
                                                    tint = MaterialTheme.colorScheme.error
                                                )
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
    }
}
