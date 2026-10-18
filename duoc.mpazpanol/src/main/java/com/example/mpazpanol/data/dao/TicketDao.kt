package com.example.mpazpanol.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mpazpanol.model.Ticket
import kotlinx.coroutines.flow.Flow

@Dao
interface TicketDao {

    @Query("SELECT * FROM tickets ORDER BY fechaHoraCreacion DESC")
    fun obtenerTodosLosTickets(): Flow<List<Ticket>>

    // Para que un docente vea solo su historial
    @Query("SELECT * FROM tickets WHERE idSolicitante = :idSolicitante ORDER BY fechaHoraCreacion DESC")
    fun obtenerTicketsPorSolicitante(idSolicitante: String): Flow<List<Ticket>>

    // Para guardar una nueva reserva
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTicket(ticket: Ticket)

    // actualiza el estado de los tickets
    @Query("UPDATE tickets SET estado = :nuevoEstado, comentarioGestion = :comentario WHERE id = :ticketId")
    suspend fun actualizarEstadoTicket(ticketId: Int, nuevoEstado: String, comentario: String?)
}