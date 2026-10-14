package com.example.mpazpanol.data.repository

import com.example.mpazpanol.data.dao.EquipoDao
import com.example.mpazpanol.data.model.Equipo
import kotlinx.coroutines.flow.Flow

/**
 * Repository that abstracts access to [EquipoDao].
 */
class EquipoRepository(private val equipoDao: EquipoDao) {

    val allEquipos: Flow<List<Equipo>> = equipoDao.getAllEquipos()

    fun getEquiposByCategory(categoria: String): Flow<List<Equipo>> {
        return equipoDao.getEquiposByCategory(categoria)
    }

    suspend fun insert(equipo: Equipo) {
        equipoDao.insertEquipo(equipo)
    }

    suspend fun update(equipo: Equipo) {
        equipoDao.updateEquipo(equipo)
    }
}
