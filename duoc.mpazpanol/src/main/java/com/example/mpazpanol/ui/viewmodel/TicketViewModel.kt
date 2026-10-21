package com.example.mpazpanol.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mpazpanol.data.repository.TicketRepository
import com.example.mpazpanol.model.Ticket
import com.example.mpazpanol.utils.ValidadorFechas
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TicketViewModel(private val repository: TicketRepository) : ViewModel() {

    val todosLosTickets: Flow<List<Ticket>> = repository.allTickets

    // sirve para notificar errores a la interfaz visual
    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError

    fun actualizarEstadoTicket(ticketId: Int, nuevoEstado: String, comentario: String?) {
        viewModelScope.launch {
            repository.updateEstado(ticketId, nuevoEstado, comentario)
        }
    }

    // Luci esta funcion se usa cuando el usuario presiona el boton de que hipoteticamente hiciste. creo XD -Samu
    fun insertarTicketConValidacion(ticket: Ticket, fechaHoraPlanificada: String) {

        // Validación de la anticipacion
        if (ValidadorFechas.cumpleAnticipacionMinima(fechaHoraPlanificada)) {
            _mensajeError.value = null // Limpiamos errores
            viewModelScope.launch {
                repository.insert(ticket)
            }
        } else {
            // Si no cuumple se rechaza.
            _mensajeError.value = "La reserva debe realizarse con al menos 3 horas de anticipación."
        }
    }

    fun limpiarError() {
        _mensajeError.value = null
    }


    class Factory(private val repository: TicketRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TicketViewModel::class.java)) {
                return TicketViewModel(repository) as T
            }
            throw IllegalArgumentException("Clase ViewModel desconocida")
        }
    }
}