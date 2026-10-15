package com.example.mpazpanol.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


object ValidadorFechas {

    /** Esta parte valida que la reserva se realice con al menos 3 horas de anticipación */

    fun cumpleAnticipacionMinima(fechaHoraReserva: String): Boolean {
        return try {
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val fechaObjetivoDate = formatter.parse(fechaHoraReserva) ?: return false
            val fechaObjetivo = Calendar.getInstance().apply { time = fechaObjetivoDate }
            val ahora = Calendar.getInstance()

            // Sumar 3 horas a la fecha actual para comparar si la reserva es al menos 3 horas después
            ahora.add(Calendar.HOUR_OF_DAY, 3)

            // Retorna true si fechaObjetivo es posterior o igual a (ahora + 3 horas)
            !fechaObjetivo.before(ahora)
        } catch (_: Exception) {
            // Si hay un error de formato en la fecha, se bloquea la app por seguridad
            false
        }
    }
}
