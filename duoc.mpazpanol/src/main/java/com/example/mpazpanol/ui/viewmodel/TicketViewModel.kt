package com.example.mpazpanol.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mpazpanol.data.repository.EquipoRepository
import com.example.mpazpanol.data.repository.TicketRepository
import com.example.mpazpanol.model.Equipo
import com.example.mpazpanol.model.Ticket
import com.example.mpazpanol.network.RegistroMovimientoRemoto
import com.example.mpazpanol.network.RetrofitClient
import com.example.mpazpanol.network.TicketRemoto
import com.example.mpazpanol.utils.ValidadorFechas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TicketViewModel(
    private val repository: TicketRepository,
    private val equipoRepository: EquipoRepository,
) : ViewModel() {

    private val _todosLosTickets = MutableStateFlow<List<Ticket>>(emptyList())
    val todosLosTickets: StateFlow<List<Ticket>> = _todosLosTickets.asStateFlow()

    private val _carrito = MutableStateFlow(emptyMap<Equipo, Int>())
    val carrito = _carrito.asStateFlow()
    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError.asStateFlow()

    init {
        cargarTicketsDesdeNube()
    }

    fun cargarTicketsDesdeNube() {
        viewModelScope.launch {
            try {
                val remotos = RetrofitClient.ticketsService.obtenerTodosLosTickets()
                _todosLosTickets.value = remotos.map { ticketRemoto ->
                    Ticket(
                        id = ticketRemoto.id?.toInt() ?: 0,
                        idSolicitante = ticketRemoto.docenteEmail,
                        detalleEquiposJson = ticketRemoto.equiposResumen,
                        sala = ticketRemoto.sala,
                        fechaPlanificada = ticketRemoto.fechaInicio,
                        horaInicio = ticketRemoto.fechaInicio.split(" ").lastOrNull() ?: "10:00",
                        horaDevolucionEstimada = ticketRemoto.fechaDevolucion,
                        fechaHoraCreacion = System.currentTimeMillis(),
                        fechaHoraEntregaFisica = null,
                        fechaHoraRecepcionFisica = null,
                        estado = ticketRemoto.estado,
                        comentarioGestion = ticketRemoto.motivoRechazo,
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun agregarAlCarrito(equipo: Equipo) {
        val actual = _carrito.value.toMutableMap()
        val cantidadActual = actual[equipo] ?: 0
        if (cantidadActual < equipo.stockDisponible) {
            actual[equipo] = cantidadActual + 1
            _carrito.value = actual
        }
    }

    fun decrementarDelCarrito(equipo: Equipo) {
        val actual = _carrito.value.toMutableMap()
        val cantidadActual = actual[equipo] ?: 0
        if (cantidadActual > 1) {
            actual[equipo] = cantidadActual - 1
            _carrito.value = actual
        } else {
            actual.remove(equipo)
            _carrito.value = actual
        }
    }

    fun removerDelCarrito(equipo: Equipo) {
        val actual = _carrito.value.toMutableMap()
        actual.remove(equipo)
        _carrito.value = actual
    }

    fun vaciarCarrito() {
        _carrito.value = emptyMap()
    }

    @Suppress("unused")
    fun totalItemsEnCarrito(): Int {
        return _carrito.value.values.sum()
    }

    fun generarDetalleEquipos(): String {
        if (_carrito.value.isEmpty()) return "Sin equipos seleccionados"
        return _carrito.value.entries.joinToString(", ") { (equipo, cant) ->
            "${cant}x ${equipo.nombre}"
        }
    }

    fun actualizarEstadoTicket(ticketId: Int, nuevoEstado: String, comentario: String?) {
        viewModelScope.launch {
            try {
                //  Actualiza el estado en microservicio Tickets
                val respuesta = RetrofitClient.ticketsService.actualizarEstado(
                    id = ticketId.toLong(),
                    nuevoEstado = nuevoEstado,
                    motivo = comentario,
                )

                if (respuesta.isSuccessful) {
                    //  Actualizar localmente en Room
                    repository.updateEstado(ticketId, nuevoEstado, comentario)

                    //  Registra la auditoria en el Historial
                    RetrofitClient.historialService.registrarMovimiento(
                        RegistroMovimientoRemoto(
                            ticketId = ticketId.toLong(),
                            accion = "TICKET_$nuevoEstado",
                            responsable = "admin@marcelapaz.cl",
                        )
                    )

                    // Refresca lista de tickets desde la nube
                    cargarTicketsDesdeNube()
                } else {
                    _mensajeError.value = "No se pudo actualizar el estado en el servidor."
                }
            } catch (e: Exception) {
                _mensajeError.value = "Error de red al actualizar el ticket."
                e.printStackTrace()
            }
        }
    }

    fun insertarTicketConValidacion(
        ticket: Ticket,
        fechaHoraPlanificada: String,
        onSuccess: () -> Unit = {}
    ) {
        if (ValidadorFechas.cumpleAnticipacionMinima(fechaHoraPlanificada)) {
            _mensajeError.value = null
            viewModelScope.launch {
                try {
                    //  Prepar y encia el ticket al microservicio de Tickets primero
                    val ticketNube = TicketRemoto(
                        docenteEmail = ticket.idSolicitante,
                        sala = ticket.sala,
                        fechaInicio = ticket.fechaPlanificada,
                        fechaDevolucion = ticket.horaDevolucionEstimada,
                        estado = "PENDIENTE",
                        equiposResumen = ticket.detalleEquiposJson,
                    )
                    val respuesta = RetrofitClient.ticketsService.crearTicket(ticketNube)

                    //  Solo si el servidor aceptó el ticket, descontamos stock y guarda
                    if (respuesta.isSuccessful) {
                        _carrito.value.forEach { (equipo, cantidad) ->
                            RetrofitClient.inventarioService.actualizarStock(
                                id = equipo.id.toLong(),
                                cambio = -cantidad,
                            )
                            equipoRepository.update(
                                equipo.copy(
                                    stockDisponible = (equipo.stockDisponible - cantidad).coerceAtLeast(0)
                                )
                            )
                        }

                        //  Inserta un respaldo en base de datos local Room
                        repository.insert(ticket)

                        //  Registra el movimiento en el microservicio del historial
                        val idCreado = respuesta.body()?.id ?: 0L
                        RetrofitClient.historialService.registrarMovimiento(
                            RegistroMovimientoRemoto(
                                ticketId = idCreado,
                                accion = "RESERVA_CREADA",
                                responsable = ticket.idSolicitante,
                            )
                        )


                        vaciarCarrito()
                        cargarTicketsDesdeNube()
                        onSuccess()
                    } else {
                        _mensajeError.value = "El servidor rechazó la reserva (Código ${respuesta.code()})."
                    }
                } catch (e: Exception) {
                    _mensajeError.value = "Error de conexión con el servidor en la nube."
                    e.printStackTrace()
                }
            }
        } else {
            _mensajeError.value = "La reserva debe realizarse con al menos 3 horas de anticipación."
        }
    }

    fun limpiarError() {
        _mensajeError.value = null
    }

    class Factory(
        private val repository: TicketRepository,
        private val equipoRepository: EquipoRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TicketViewModel::class.java)) {
                return TicketViewModel(repository, equipoRepository) as T
            }
            throw IllegalArgumentException("Clase ViewModel desconocida")
        }
    }
}