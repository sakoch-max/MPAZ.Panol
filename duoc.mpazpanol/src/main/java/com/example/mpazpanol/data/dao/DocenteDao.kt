package com.example.mpazpanol.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mpazpanol.model.Docente
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for managing [Docente] authorized accounts in Room SQLite.
 */
@Dao
interface DocenteDao {

    @Query("SELECT * FROM docentes")
    fun obtenerTodosLosDocentes(): Flow<List<Docente>>

    @Query("SELECT * FROM docentes WHERE LOWER(correo) = LOWER(:correo) LIMIT 1")
    suspend fun buscarPorCorreo(correo: String): Docente?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarDocente(docente: Docente)

    @Query("DELETE FROM docentes WHERE id = :id")
    suspend fun eliminarDocente(id: Int)
}
