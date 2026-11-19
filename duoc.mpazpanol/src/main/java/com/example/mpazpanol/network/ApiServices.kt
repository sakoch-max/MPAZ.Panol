package com.example.mpazpanol.network

import retrofit2.Response
import retrofit2.http.*

interface InventarioApiService {
    @GET("api/equipos")
    suspend fun obtenerEquipos(): List<EquipoRemoto>

    @POST("api/equipos")
    suspend fun crearEquipo(@Body equipo: EquipoRemoto): EquipoRemoto

    @PUT("api/equipos/{id}/stock")
    suspend fun actualizarStock(
        @Path("id") id: Long,
        @Query("cambio") cambio: Int
    ): Response<EquipoRemoto>
}

interface TicketsApiService {
    @GET("api/tickets")
    suspend fun obtenerTodosLosTickets(): List<TicketRemoto>

    @GET("api/tickets/docente/{email}")
    suspend fun obtenerTicketsPorDocente(@Path("email") email: String): List<TicketRemoto>

    @POST("api/tickets")
    suspend fun crearTicket(@Body ticket: TicketRemoto): Response<TicketRemoto>

    @PUT("api/tickets/{id}/estado")
    suspend fun actualizarEstado(
        @Path("id") id: Long,
        @Query("nuevoEstado") nuevoEstado: String,
        @Query("motivo") motivo: String? = null
    ): Response<TicketRemoto>
}

interface HistorialApiService {
    @GET("api/historial")
    suspend fun obtenerHistorial(): List<RegistroMovimientoRemoto>

    @POST("api/historial")
    suspend fun registrarMovimiento(@Body movimiento: RegistroMovimientoRemoto): RegistroMovimientoRemoto
}