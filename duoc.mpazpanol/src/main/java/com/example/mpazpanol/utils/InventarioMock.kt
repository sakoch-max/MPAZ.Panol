package com.example.mpazpanol.utils

import com.example.mpazpanol.model.Equipo

object InventarioMock {
    val equiposIniciales = listOf(
        Equipo(
            id = 1,
            nombre = "Proyector EPSON X41",
            descripcion = "Proyector HDMI / VGA de alta resolución con 3600 lúmenes para presentaciones en auditorio o salas de clases.",
            imagenUrl = "proyector_epson",
            categoria = "Audiovisual",
            stockTotal = 5,
            stockDisponible = 5,
            estado = "Disponible",
            ubicacion = "Estante A1"
        ),
        Equipo(
            id = 2,
            nombre = "Notebook HP ProBook 450",
            descripcion = "Laptop docente Intel i5, 8GB RAM, SSD 256GB con Windows 11 Pro y software educativo preinstalado.",
            imagenUrl = "notebook_hp",
            categoria = "Computación",
            stockTotal = 15,
            stockDisponible = 15,
            estado = "Disponible",
            ubicacion = "Gabinete B2"
        ),
        Equipo(
            id = 3,
            nombre = "Kit Robótica LEGO WeDo 2.0",
            descripcion = "Set educativo con motores, sensor de movimiento, sensor de inclinación y bloques de construcción.",
            imagenUrl = "lego_wedo",
            categoria = "Robótica",
            stockTotal = 8,
            stockDisponible = 8,
            estado = "Disponible",
            ubicacion = "Estante C1"
        ),
        Equipo(
            id = 4,
            nombre = "Microcontrolador Arduino Uno R3",
            descripcion = "Placa de desarrollo ATmega328P para prototipado electrónico, robótica y programación escolar.",
            imagenUrl = "arduino_uno",
            categoria = "Robótica",
            stockTotal = 20,
            stockDisponible = 20,
            estado = "Disponible",
            ubicacion = "Caja R1"
        ),
        Equipo(
            id = 5,
            nombre = "Placa ESP32 Wi-Fi & Bluetooth",
            descripcion = "Módulo SoC dual-core con conectividad inalámbrica para proyectos avanzados de Internet de las Cosas (IoT).",
            imagenUrl = "esp32",
            categoria = "Robótica",
            stockTotal = 10,
            stockDisponible = 10,
            estado = "Disponible",
            ubicacion = "Caja R2"
        ),
        Equipo(
            id = 6,
            nombre = "Switch Cisco Catalyst 2960",
            descripcion = "Switch administrable Gigabit Ethernet de 24 puertos para laboratorios de redes y conectividad.",
            imagenUrl = "switch_cisco",
            categoria = "Redes",
            stockTotal = 3,
            stockDisponible = 3,
            estado = "Disponible",
            ubicacion = "Rack Principal Sala Enlaces"
        ),
        Equipo(
            id = 7,
            nombre = "Parlante Portátil Bluetooth JBL",
            descripcion = "Sistema de audio amplificado portátil con micrófono inalámbrico para actos, eventos o clases al aire libre.",
            imagenUrl = "parlante_jbl",
            categoria = "Audiovisual",
            stockTotal = 4,
            stockDisponible = 4,
            estado = "Disponible",
            ubicacion = "Estante A2"
        ),
        Equipo(
            id = 8,
            nombre = "Cámara Digital Canon EOS Rebel",
            descripcion = "Cámara réflex 24.1 MP con lente 18-55mm para talleres de fotografía, periodismo escolar y eventos.",
            imagenUrl = "camara_canon",
            categoria = "Audiovisual",
            stockTotal = 3,
            stockDisponible = 3,
            estado = "Disponible",
            ubicacion = "Estante A3"
        ),
        Equipo(
            id = 9,
            nombre = "Tablet Samsung Galaxy Tab A8",
            descripcion = "Tablet de 10.5 pulgadas con 64GB y funda protectora para uso interactivo en aula y laboratorios.",
            imagenUrl = "tablet_samsung",
            categoria = "Computación",
            stockTotal = 12,
            stockDisponible = 12,
            estado = "Disponible",
            ubicacion = "Gabinete B1"
        )
    )

    val docentesIniciales = listOf(
        com.example.mpazpanol.model.Docente(
            id = 1,
            nombre = "Docente Demo Marcela Paz",
            correo = "docente@marcelapaz.cl",
            rut = "12.345.678-9"
        ),
        com.example.mpazpanol.model.Docente(
            id = 2,
            nombre = "Profesor Juan Pérez",
            correo = "jperez@marcelapaz.cl",
            rut = "15.678.901-2"
        ),
        com.example.mpazpanol.model.Docente(
            id = 3,
            nombre = "Profesora María González",
            correo = "mgonzalez@marcelapaz.cl",
            rut = "14.234.567-8"
        )
    )
}
