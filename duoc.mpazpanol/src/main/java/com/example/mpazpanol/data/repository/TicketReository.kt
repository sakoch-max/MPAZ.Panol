package com.example.mpazpanol.data.repository

import com.example.mpazpanol.data.dao.TicketDao
import com.example.mpazpanol.model.Ticket
import kotlinx.coroutines.flow.Flow

/**
 * Repository that abstracts access to [TicketDao].
 */
class TicketRepository(private val ticketDao: TicketDao) {

    // Obtener todos los tickets
    val allTickets: Flow<List<Ticket>> = ticketDao.obtenerTodosLosTickets()

    // Obtener tickets de un usuario especifico Para la vista del profe
    fun getTicketsBySolicitante(idSolicitante: String): Flow<List<Ticket>> {
        return ticketDao.obtenerTicketsPorSolicitante(idSolicitante)
    }

    // nueva reserva
    suspend fun insert(ticket: Ticket) {
        ticketDao.insertarTicket(ticket)
    }

    // Actualizar el estado del ticket aceptada, rechazada, etc y una justificacion
    suspend fun updateEstado(ticketId: Int, nuevoEstado: String, comentario: String?) {
        ticketDao.actualizarEstadoTicket(ticketId, nuevoEstado, comentario)
    }
}