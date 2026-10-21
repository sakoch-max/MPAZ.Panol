package com.example.mpazpanol.utils

import java.time.Duration
import java.time.LocalDateTime

object LoanValidationUtils {

    /**
     * Valida si la hora planificada cumple con el mínimo de 3 horas de anticipación.
     */
    fun validateAdvanceTime(plannedStartDateTime: LocalDateTime): Pair<Boolean, String?> {
        val now = LocalDateTime.now()
        val hoursDifference = Duration.between(now, plannedStartDateTime).toHours()

        return if (hoursDifference < 3) {
            Pair(false, "Restricción: Las reservas requieren mínimo 3 horas de anticipación.")
        } else {
            Pair(true, null)
        }
    }

    /**
     * Valida que la sala no esté vacía.
     */
    fun validateRoom(room: String): Pair<Boolean, String?> {
        return if (room.isBlank()) {
            Pair(false, "Debe especificar la sala o espacio de destino.")
        } else {
            Pair(true, null)
        }
    }
}