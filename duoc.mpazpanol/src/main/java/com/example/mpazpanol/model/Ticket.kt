package com.example.mpazpanol.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tickets")
data class Ticket(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val idSolicitante: String,
    val detalleEquiposJson: String,
    val sala: String,
    val fechaPlanificada: String,
    val horaInicio: String, // Hora exacta de inicio
    val horaDevolucionEstimada: String, // Hora de termino
    val fechaHoraCreacion: Long,
    val fechaHoraEntregaFisica: Long?, // Timestamp de entrega en pañol
    val fechaHoraRecepcionFisica: Long?, // Timestamp de devolucion
    val estado: String = "Pendiente", // Estados: Pendiente, Aceptada, Rechazada, Entregada, Devuelta
    val comentarioGestion: String? = null // Justificación obligatoria en si se rechazo el pedido
)