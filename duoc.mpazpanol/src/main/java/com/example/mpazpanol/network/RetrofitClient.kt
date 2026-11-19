package com.example.mpazpanol.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val URL_INVENTARIO = "https://mpaz-inventario-service.onrender.com/"
    private const val URL_TICKETS = "https://mpaz-tickets-service.onrender.com/"
    private const val URL_HISTORIAL = "https://mpaz-historial-service.onrender.com/"

    val inventarioService: InventarioApiService by lazy {
        Retrofit.Builder()
            .baseUrl(URL_INVENTARIO)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(InventarioApiService::class.java)
    }

    val ticketsService: TicketsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(URL_TICKETS)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TicketsApiService::class.java)
    }

    val historialService: HistorialApiService by lazy {
        Retrofit.Builder()
            .baseUrl(URL_HISTORIAL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HistorialApiService::class.java)
    }
}