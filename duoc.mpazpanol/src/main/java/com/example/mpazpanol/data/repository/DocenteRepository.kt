package com.example.mpazpanol.data.repository

import com.example.mpazpanol.data.dao.DocenteDao
import com.example.mpazpanol.model.Docente
import kotlinx.coroutines.flow.Flow

/**
 * Repository that abstracts access to [DocenteDao].
 */
class DocenteRepository(private val docenteDao: DocenteDao) {

    val todosLosDocentes: Flow<List<Docente>> = docenteDao.obtenerTodosLosDocentes()

    suspend fun buscarPorCorreo(correo: String): Docente? {
        return docenteDao.buscarPorCorreo(correo)
    }

    suspend fun registrarDocente(docente: Docente) {
        docenteDao.insertarDocente(docente)
    }

    suspend fun eliminarDocente(id: Int) {
        docenteDao.eliminarDocente(id)
    }
}
