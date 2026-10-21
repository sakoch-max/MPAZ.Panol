package com.example.mpazpanol.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.mpazpanol.ResourceItem
import com.example.mpazpanol.utils.LoanValidationUtils

data class CartItem(val resource: ResourceItem, var quantity: Int)

data class LoanTicket(
    val id: String,
    val userEmail: String,
    val room: String,
    val items: List<CartItem>,
    val date: String,
    val hoursInAdvance: Int,
    var status: String, // Pendiente, Aceptada, Rechazada, Entregada, Devuelta
    var rejectionReason: String? = null
)

class PanolViewModel : ViewModel() {

    // Perfil actual del usuario
    var currentUserRole by mutableStateOf("Solicitante") // "Solicitante" o "Administrador"
    var currentUserEmail by mutableStateOf("docente@marcelapaz.cl")

    // Lista global de recursos
    var resourcesList by mutableStateOf(
        listOf(
            ResourceItem("Kit LEGO WeDo 2.0", "Pañol Central - Estante A1", 8, 10),
            ResourceItem("Proyector Epson PowerLite", "Pañol Central - Repisa 3", 2, 5),
            ResourceItem("Notebook HP ProBook G8", "Carro Carga 1", 15, 20),
            ResourceItem("Placa ESP32 + Kit Sensores", "Pañol Central - Caja Robótica", 0, 6)
        )
    )

    // Carrito de Solicitud Interna
    var cartItems by mutableStateOf(listOf<CartItem>())
        private set

    // Lista global de Tickets/Solicitudes
    var loanTickets by mutableStateOf(
        listOf(
            LoanTicket(
                id = "TK-101",
                userEmail = "docente@marcelapaz.cl",
                room = "Sala 5° Básico",
                items = listOf(CartItem(ResourceItem("Proyector Epson PowerLite", "Pañol Central", 2, 5), 1)),
                date = "2026-08-31",
                hoursInAdvance = 4,
                status = "Pendiente"
            )
        )
    )

    fun addToCart(item: ResourceItem) {
        val existing = cartItems.find { it.resource.name == item.name }
        if (existing != null) {
            if (existing.quantity < item.availableStock) {
                existing.quantity++
            }
        } else {
            cartItems = cartItems + CartItem(item, 1)
        }
    }

    fun removeFromCart(cartItem: CartItem) {
        cartItems = cartItems.filter { it.resource.name != cartItem.resource.name }
    }

    fun clearCart() {
        cartItems = emptyList()
    }

    fun submitLoanRequest(room: String, hoursInAdvance: Int): Pair<Boolean, String?> {
        val roomValidation = LoanValidationUtils.validateRoom(room)
        if (!roomValidation.first) return roomValidation

        if (cartItems.isEmpty()) {
            return Pair(false, "El carrito de solicitudes está vacío.")
        }

        if (hoursInAdvance < 3) {
            return Pair(false, "Restricción: Las solicitudes deben ser realizadas con mínimo 3 horas de anticipación.")
        }

        val newTicket = LoanTicket(
            id = "TK-${(100..999).random()}",
            userEmail = currentUserEmail,
            room = room,
            items = cartItems.toList(),
            date = "2026-08-31",
            hoursInAdvance = hoursInAdvance,
            status = "Pendiente"
        )

        loanTickets = loanTickets + newTicket
        clearCart()
        return Pair(true, "Solicitud enviada correctamente.")
    }

    fun updateTicketStatus(ticketId: String, newStatus: String, reason: String? = null) {
        loanTickets = loanTickets.map { ticket ->
            if (ticket.id == ticketId) {
                ticket.copy(status = newStatus, rejectionReason = reason)
            } else {
                ticket
            }
        }
    }
}