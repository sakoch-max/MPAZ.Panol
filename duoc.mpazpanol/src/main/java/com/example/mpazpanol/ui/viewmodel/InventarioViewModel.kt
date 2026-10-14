package com.example.mpazpanol.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mpazpanol.data.model.Equipo
import com.example.mpazpanol.data.repository.EquipoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel managing inventory state and operations for MPAZ Pañol.
 */
class InventarioViewModel(private val repository: EquipoRepository) : ViewModel() {

    val equipos: StateFlow<List<Equipo>> = repository.allEquipos
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun insertEquipo(equipo: Equipo) {
        viewModelScope.launch {
            repository.insert(equipo)
        }
    }

    fun updateEquipo(equipo: Equipo) {
        viewModelScope.launch {
            repository.update(equipo)
        }
    }

    fun getEquiposByCategory(categoria: String): StateFlow<List<Equipo>> {
        return repository.getEquiposByCategory(categoria)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    class Factory(private val repository: EquipoRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(InventarioViewModel::class.java)) {
                return InventarioViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
