package com.example.mpazpanol.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.mpazpanol.model.Equipo
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for managing [Equipo] operations in Room SQLite.
 */
@Dao
interface EquipoDao {

    @Query("SELECT * FROM equipos")
    fun getAllEquipos(): Flow<List<Equipo>>

    @Query("SELECT * FROM equipos WHERE categoria = :categoria")
    fun getEquiposByCategory(categoria: String): Flow<List<Equipo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEquipo(equipo: Equipo)

    @Update
    suspend fun updateEquipo(equipo: Equipo)

    @Delete
    suspend fun deleteEquipo(equipo: Equipo)
}
