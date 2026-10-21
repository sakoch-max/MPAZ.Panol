package com.example.mpazpanol.ui.screens.carrito

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mpazpanol.model.Ticket
import com.example.mpazpanol.ui.viewmodel.TicketViewModel
import com.example.mpazpanol.utils.VibrationHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoScreen(navController: NavController, viewModel: TicketViewModel) {
    val context = LocalContext.current
    val mensajeError by viewModel.mensajeError.collectAsState()

    var sala by remember { mutableStateOf("") }
    var fechaHora by remember { mutableStateOf("2026-10-22 10:00") } // Formato requerido por el Validador

    // Escucha errores del backend para activar la vibracion
    LaunchedEffect(mensajeError) {
        mensajeError?.let {
            VibrationHelper.vibrarError(context)
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.limpiarError()
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Nueva Reserva") }) }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("Complete los datos de la reserva (Min. 3 hrs anticipación)")
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(value = sala, onValueChange = { sala = it }, label = { Text("Sala de uso") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = fechaHora, onValueChange = { fechaHora = it }, label = { Text("Fecha y Hora (yyyy-MM-dd HH:mm)") }, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    val nuevoTicket = Ticket(
                        idSolicitante = "Docente_Demo",
                        detalleEquiposJson = "1x Proyector EPSON",
                        sala = sala,
                        fechaPlanificada = fechaHora,
                        horaInicio = fechaHora.split(" ").last(),
                        horaDevolucionEstimada = "12:00",
                        fechaHoraCreacion = System.currentTimeMillis(),
                        fechaHoraEntregaFisica = null,
                        fechaHoraRecepcionFisica = null
                    )
                    viewModel.insertarTicketConValidacion(nuevoTicket, fechaHora)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirmar Reserva")
            }
        }
    }
}