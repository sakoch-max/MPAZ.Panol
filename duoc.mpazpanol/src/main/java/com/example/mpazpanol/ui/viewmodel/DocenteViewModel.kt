package com.example.mpazpanol.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mpazpanol.data.repository.DocenteRepository
import com.example.mpazpanol.model.Docente
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DocenteViewModel(private val repository: DocenteRepository) : ViewModel() {

    val docentes: StateFlow<List<Docente>> = repository.todosLosDocentes
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    suspend fun esCorreoAutorizado(correo: String): Boolean {
        if (correo.trim().lowercase() == "admin") return true
        val docente = repository.buscarPorCorreo(correo.trim())
        return docente != null && docente.estado == "Activo"
    }

    fun registrarDocente(docente: Docente, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            repository.registrarDocente(docente)
            onSuccess()
        }
    }

    fun eliminarDocente(id: Int) {
        viewModelScope.launch {
            repository.eliminarDocente(id)
        }
    }

    class Factory(private val repository: DocenteRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DocenteViewModel::class.java)) {
                return DocenteViewModel(repository) as T
            }
            throw IllegalArgumentException("Clase ViewModel desconocida")
        }
    }
}
