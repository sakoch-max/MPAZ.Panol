package com.example.mpazpanol.network

data class EquipoRemoto(
    val id: Long? = null,
    val nombre: String,
    val categoria: String,
    val ubicacion: String,
    val stockDisponible: Int,
    val descripcion: String
)

data class TicketRemoto(
    val id: Long? = null,
    val docenteEmail: String,
    val sala: String,
    val fechaInicio: String,
    val fechaDevolucion: String,
    val estado: String = "PENDIENTE",
    val motivoRechazo: String? = null,
    val equiposResumen: String
)

data class RegistroMovimientoRemoto(
    val id: Long? = null,
    val ticketId: Long,
    val accion: String,
    val responsable: String,
    val fechaHora: String? = null
)