package com.example.mpazpanol.utils

import android.util.Patterns

/**
 * Helper con validaciones básicas de entradas de texto e insumos para MPAZ Pañol.
 */
object ValidadorEntradas {

    /**
     * Comprueba si el correo tiene un formato válido (ej: usuario@marcelapaz.cl).
     */
    fun esCorreoValido(correo: String): Boolean {
        val trimmed = correo.trim()
        if (trimmed.isBlank()) return false
        return Patterns.EMAIL_ADDRESS.matcher(trimmed).matches()
    }

    /**
     * Comprueba que el texto de un nombre contenga letras/espacios y NO contenga números.
     */
    fun esNombreValido(nombre: String): Boolean {
        val trimmed = nombre.trim()
        if (trimmed.isBlank()) return false
        return !trimmed.any { it.isDigit() }
    }

    /**
     * Comprueba que una sala o laboratorio no esté vacío.
     */
    fun esSalaValida(sala: String): Boolean {
        return sala.trim().isNotBlank()
    }

    /**
     * Comprueba que el valor ingresado para un stock sea un entero mayor o igual a 0.
     */
    fun esStockValido(stockStr: String): Boolean {
        val num = stockStr.toIntOrNull()
        return num != null && num >= 0
    }
}
