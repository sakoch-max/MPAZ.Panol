package com.example.mpazpanol.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing an authorized teacher/docente in the MPAZ Pañol system.
 */
@Entity(tableName = "docentes")
data class Docente(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val correo: String,
    val rut: String = "",
    val estado: String = "Activo"
)
