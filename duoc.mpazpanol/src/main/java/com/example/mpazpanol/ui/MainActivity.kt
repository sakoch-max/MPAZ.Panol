package com.example.mpazpanol.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.mpazpanol.data.database.PanolDatabase
import com.example.mpazpanol.data.repository.EquipoRepository
import com.example.mpazpanol.data.repository.TicketRepository
import com.example.mpazpanol.ui.viewmodel.InventarioViewModel
import com.example.mpazpanol.ui.viewmodel.TicketViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val database = PanolDatabase.getDatabase(this)


        val equipoRepository = EquipoRepository(database.EquipoDao())
        val ticketRepository = TicketRepository(database.ticketDao())


        val inventarioViewModel: InventarioViewModel by viewModels {
            InventarioViewModel.Factory(equipoRepository)
        }
        val ticketViewModel: TicketViewModel by viewModels {
            TicketViewModel.Factory(ticketRepository)
        }

        setContent {
            // Aqui va el mNavHost de Compose que hiciste luci
            // Pasale ViewModels a tus pantallas.
        }
    }
}