package com.example.mpazpanol.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mpazpanol.data.repository.EquipoRepository
import com.example.mpazpanol.model.Equipo
import com.example.mpazpanol.network.EquipoRemoto
import com.example.mpazpanol.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class InventarioViewModel(private val repository: EquipoRepository) : ViewModel() {

    private val _equipos = MutableStateFlow<List<Equipo>>(emptyList())
    val equipos: StateFlow<List<Equipo>> = _equipos.asStateFlow()

    init {
        cargarEquiposDesdeNube()
    }

    fun cargarEquiposDesdeNube() {
        viewModelScope.launch {
            try {
                val remotos = RetrofitClient.inventarioService.obtenerEquipos()
                // Convierte los datos de la nube (PostgreSQL) al modelo local Equipo
                _equipos.value = remotos.map { r ->
                    Equipo(
                        id = r.id?.toInt() ?: 0,
                        nombre = r.nombre,
                        descripcion = r.descripcion,
                        categoria = r.categoria,
                        stockTotal = r.stockDisponible,
                        stockDisponible = r.stockDisponible,
                        estado = if (r.stockDisponible > 0) "Disponible" else "Agotado",
                        ubicacion = r.ubicacion
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun insertEquipo(equipo: Equipo) {
        viewModelScope.launch {
            try {
                val nuevoRemoto = EquipoRemoto(
                    nombre = equipo.nombre,
                    categoria = equipo.categoria,
                    ubicacion = equipo.ubicacion,
                    stockDisponible = equipo.stockDisponible,
                    descripcion = equipo.descripcion
                )
                RetrofitClient.inventarioService.crearEquipo(nuevoRemoto)
                repository.insert(equipo)
                cargarEquiposDesdeNube() // Refresca la lista desde la nube
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateEquipo(equipo: Equipo) {
        viewModelScope.launch {
            _equipos.value = _equipos.value.map { if (it.id == equipo.id) equipo else it }
            repository.update(equipo)
        }
    }

    fun deleteEquipo(equipo: Equipo) {
        viewModelScope.launch {
            _equipos.value = _equipos.value.filter { it.id != equipo.id }
            repository.delete(equipo)
        }
    }

    @Suppress("unused")
    fun getEquiposByCategory(categoria: String): StateFlow<List<Equipo>> {
        return _equipos
            .map { lista ->
                if (categoria.equals("Todos", ignoreCase = true)) lista
                else lista.filter { it.categoria.equals(categoria, ignoreCase = true) }
            }
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