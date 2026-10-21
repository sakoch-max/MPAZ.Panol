package com.example.mpazpanol.ui.screens.catalogo

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mpazpanol.ui.viewmodel.InventarioViewModel
import com.example.mpazpanol.ui.viewmodel.TicketViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    navController: NavController,
    inventarioViewModel: InventarioViewModel,
    ticketViewModel: TicketViewModel
) {
    val catalogo by inventarioViewModel.equipos.collectAsState()
    val carrito by ticketViewModel.carrito.collectAsState()
    var categoriaFiltro by remember { mutableStateOf("Todas") }

    val categorias = listOf("Todas") + catalogo.map { it.categoria }.distinct()
    val equiposFiltrados = if (categoriaFiltro == "Todas") {
        catalogo
    } else {
        catalogo.filter { it.categoria == categoriaFiltro }
    }

    val totalItemsCarrito = carrito.values.sum()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Catálogo de Equipos", fontWeight = FontWeight.Bold)
                        Text(
                            "Escuela Marcela Paz — Pañol TI",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate("carrito") },
                icon = {
                    BadgedBox(
                        badge = {
                            if (totalItemsCarrito > 0) {
                                Badge { Text(totalItemsCarrito.toString()) }
                            }
                        }
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null)
                    }
                },
                text = {
                    Text(
                        if (totalItemsCarrito > 0) "Ir al Carrito ($totalItemsCarrito)" else "Ver Reserva"
                    )
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Filtro por categorías
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categorias.forEach { cat ->
                    FilterChip(
                        selected = categoriaFiltro == cat,
                        onClick = { categoriaFiltro = cat },
                        label = { Text(cat) }
                    )
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(equiposFiltrados) { equipo ->
                    val cantidadEnCarrito = carrito[equipo] ?: 0

                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.Devices,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    equipo.nombre,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Category,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        equipo.categoria,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                val disponible = equipo.stockDisponible > 0
                                SuggestionChip(
                                    onClick = { },
                                    label = {
                                        Text(
                                            if (disponible) "Stock: ${equipo.stockDisponible}" else "Agotado",
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    },
                                    icon = {
                                        Icon(
                                            if (disponible) Icons.Default.CheckCircle else Icons.Default.Warning,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    },
                                    colors = SuggestionChipDefaults.suggestionChipColors(
                                        containerColor = if (disponible) {
                                            MaterialTheme.colorScheme.primaryContainer
                                        } else {
                                            MaterialTheme.colorScheme.errorContainer
                                        },
                                        labelColor = if (disponible) {
                                            MaterialTheme.colorScheme.onPrimaryContainer
                                        } else {
                                            MaterialTheme.colorScheme.onErrorContainer
                                        },
                                        iconContentColor = if (disponible) {
                                            MaterialTheme.colorScheme.onPrimaryContainer
                                        } else {
                                            MaterialTheme.colorScheme.onErrorContainer
                                        }
                                    )
                                )
                            }

                            // Botones de agregar al carrito
                            if (equipo.stockDisponible > 0) {
                                if (cantidadEnCarrito > 0) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        FilledIconButton(
                                            onClick = { ticketViewModel.decrementarDelCarrito(equipo) },
                                            modifier = Modifier.size(32.dp),
                                            colors = IconButtonDefaults.filledIconButtonColors(
                                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                                            )
                                        ) {
                                            Icon(
                                                Icons.Default.Remove,
                                                contentDescription = "Quitar uno",
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }

                                        Text(
                                            text = "$cantidadEnCarrito",
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp)
                                        )

                                        FilledIconButton(
                                            onClick = { ticketViewModel.agregarAlCarrito(equipo) },
                                            modifier = Modifier.size(32.dp),
                                            enabled = cantidadEnCarrito < equipo.stockDisponible
                                        ) {
                                            Icon(
                                                Icons.Default.Add,
                                                contentDescription = "Agregar otro",
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                } else {
                                    Button(
                                        onClick = { ticketViewModel.agregarAlCarrito(equipo) },
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Añadir")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
