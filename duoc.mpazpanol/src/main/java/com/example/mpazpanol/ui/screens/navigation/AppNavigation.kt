package com.example.mpazpanol.ui.screens.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mpazpanol.ui.screens.admin.ConsolaAdminScreen
import com.example.mpazpanol.ui.screens.carrito.CarritoScreen
import com.example.mpazpanol.ui.screens.catalogo.CatalogoScreen
import com.example.mpazpanol.ui.screens.login.LoginScreen
import com.example.mpazpanol.ui.viewmodel.InventarioViewModel
import com.example.mpazpanol.ui.viewmodel.TicketViewModel

@Composable
fun AppNavigation(
    inventarioViewModel: InventarioViewModel,
    ticketViewModel: TicketViewModel
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("catalogo") { CatalogoScreen(navController, inventarioViewModel) }
        composable("carrito") { CarritoScreen(navController, ticketViewModel) }
        composable("admin") { ConsolaAdminScreen(navController, ticketViewModel) }
    }
}