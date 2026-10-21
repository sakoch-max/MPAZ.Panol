package com.example.mpazpanol.ui.screens.catalogo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material. icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mpazpanol.model.Equipo
import com.example.mpazpanol.ui.viewmodel.InventarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(navController: NavController, viewModel: InventarioViewModel) {
    val catalogo by viewModel.equipos.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Catálogo de Equipos") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("carrito") }) {
                Icon(Icons.Default.ShoppingCart, contentDescription = "Reservar")
            }
        }
    ) { padding ->
        LazyColumn(contentPadding = padding, modifier = Modifier.padding(16.dp)) {
            items(catalogo) { equipo ->
                Card(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(equipo.nombre, style = MaterialTheme.typography.titleMedium)
                        Text("Categoría: ${equipo.categoria}")
                        Text("Disponible: ${equipo.stockDisponible}", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}