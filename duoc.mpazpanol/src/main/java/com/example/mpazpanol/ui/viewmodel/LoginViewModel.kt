package com.example.mpazpanol.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mpazpanol.data.repository.DocenteRepository
import com.example.mpazpanol.utils.ValidadorEntradas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val usuario: String = "",
    val isError: Boolean = false,
    val mensajeError: String? = null,
    val cargando: Boolean = false,
)

class LoginViewModel(private val repository: DocenteRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onUsuarioChange(nuevoUsuario: String) {
        _uiState.update {
            it.copy(
                usuario = nuevoUsuario,
                isError = false,
                mensajeError = null
            )
        }
    }

    fun iniciarSesion(
        onLoginSuccess: (esAdmin: Boolean) -> Unit
    ) {
        val input = _uiState.value.usuario.trim().lowercase()

        if (input.isBlank()) {
            _uiState.update {
                it.copy(isError = true, mensajeError = "El campo no puede estar vacío")
            }
            return
        }

        if (input == "admin") {
            onLoginSuccess(true)
            return
        }

        if (!ValidadorEntradas.esCorreoValido(input)) {
            _uiState.update {
                it.copy(
                    isError = true,
                    mensajeError = "Ingrese un formato de correo válido (ej: docente@marcelapaz.cl)"
                )
            }
            return
        }

        _uiState.update { it.copy(cargando = true, isError = false, mensajeError = null) }

        viewModelScope.launch {
            val docente = repository.buscarPorCorreo(input)
            val autorizado = docente != null && docente.estado == "Activo"

            if (autorizado) {
                _uiState.update { it.copy(cargando = false) }
                onLoginSuccess(false)
            } else {
                _uiState.update {
                    it.copy(
                        cargando = false,
                        isError = true,
                        mensajeError = "El correo '$input' no está autorizado por el Administrador del colegio."
                    )
                }
            }
        }
    }

    class Factory(private val repository: DocenteRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                return LoginViewModel(repository) as T
            }
            throw IllegalArgumentException("Clase ViewModel desconocida")
        }
    }
}