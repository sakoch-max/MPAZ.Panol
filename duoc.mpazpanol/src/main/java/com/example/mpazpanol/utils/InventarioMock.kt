package com.example.mpazpanol.utils

import com.example.mpazpanol.model.Equipo

object InventarioMock {
    val equiposIniciales = listOf(
        Equipo(
            nombre = "Proyector EPSON X41",
            categoria = "Proyector",
            stockTotal = 5,
            stockDisponible = 5,
            estado = "Disponible",
            ubicacion = "Estante A1"
        ),
        Equipo(
            nombre = "Notebook HP ProBook",
            categoria = "Notebook",
            stockTotal = 15,
            stockDisponible = 15,
            estado = "Disponible",
            ubicacion = "Gabinete B2"
        ),
        Equipo(
            nombre = "Kit Robótica LEGO WeDo 2.0",
            categoria = "Robótica",
            stockTotal = 8,
            stockDisponible = 8,
            estado = "Disponible",
            ubicacion = "Estante C1"
        ),
        Equipo(
            nombre = "Microcontrolador Arduino Uno",
            categoria = "Robótica",
            stockTotal = 20,
            stockDisponible = 20,
            estado = "Disponible",
            ubicacion = "Caja R1"
        ),
        Equipo(
            nombre = "Placa ESP32",
            categoria = "Robótica",
            stockTotal = 10,
            stockDisponible = 10,
            estado = "Disponible",
            ubicacion = "Caja R2"
        ),
        Equipo(
            nombre = "Switch Cisco Catalyst 2960",
            categoria = "Redes",
            stockTotal = 3,
            stockDisponible = 3,
            estado = "Disponible",
            ubicacion = "Rack Principal Sala Enlaces"
        )
    )
}