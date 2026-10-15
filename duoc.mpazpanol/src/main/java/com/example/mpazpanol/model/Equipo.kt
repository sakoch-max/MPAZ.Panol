package com.example.mpazpanol.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a tech inventory item in the MPAZ Pañol system.
 */
@Entity(tableName = "equipos")
data class Equipo(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val categoria: String,
    val stockTotal: Int,
    val stockDisponible: Int,
    val estado: String,
    val ubicacion: String
)
