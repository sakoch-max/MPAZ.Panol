package com.example.mpazpanol.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mpazpanol.data.repository.EquipoRepository
import com.example.mpazpanol.data.repository.TicketRepository
import com.example.mpazpanol.model.Equipo
import com.example.mpazpanol.model.Ticket
import com.example.mpazpanol.utils.ValidadorFechas
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TicketViewModel(
    private val repository: TicketRepository,
    private val equipoRepository: EquipoRepository,
) : ViewModel() {

    val todosLosTickets: Flow<List<Ticket>> = repository.allTickets

    // Estado del Carrito de Selección de Equipos (Equipo -> Cantidad)
    private val _carrito = MutableStateFlow<Map<Equipo, Int>>(emptyMap())
    val carrito: StateFlow<Map<Equipo, Int>> = _carrito.asStateFlow()

    // Sirve para notificar errores a la interfaz visual
    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError

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
            repository.updateEstado(ticketId, nuevoEstado, comentario)
        }
    }

    fun insertarTicketConValidacion(ticket: Ticket, fechaHoraPlanificada: String, onSuccess: () -> Unit = {}) {
        if (ValidadorFechas.cumpleAnticipacionMinima(fechaHoraPlanificada)) {
            _mensajeError.value = null
            viewModelScope.launch {
                // 1. Descontar el stock en la base de datos para cada equipo en el carrito
                _carrito.value.forEach { (equipo, cantidad) ->
                    val nuevoStockDisponibilidad = (equipo.stockDisponible - cantidad).coerceAtLeast(0)
                    val equipoActualizado = equipo.copy(stockDisponible = nuevoStockDisponibilidad)
                    equipoRepository.update(equipoActualizado)
                }
                // 2. Insertar ticket de reserva
                repository.insert(ticket)
                // 3. Vaciar el carrito
                vaciarCarrito()
                onSuccess()
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
        private val equipoRepository: EquipoRepository
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
