package com.example.mpazpanol


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen() {
    val categories = listOf("Todos", "Notebooks", "Proyectores", "Robótica", "Redes")
    var selectedCategory by remember { mutableStateOf("Todos") }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MPAZ Pañol - Catálogo", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
        ) {
            // Buscador
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Buscar notebook, kit LEGO, proyector...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp)
            )


            // Categorías (Filter Chips)
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category) }
                    )
                }
            }


            Spacer(modifier = Modifier.height(12.dp))


            // Lista de Tarjetas de Equipos
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(sampleResources) { item ->
                    ResourceCard(item = item)
                }
            }
        }
    }
}

@Composable
fun ResourceCard(item: ResourceItem) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Ubicación: ${item.location}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(6.dp))


                // Badge de disponibilidad
                Surface(
                    color = if (item.availableStock > 0) Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (item.availableStock > 0) "Disponible: ${item.availableStock} de ${item.totalStock}" else "Agotado",
                        color = if (item.availableStock > 0) Color(0xFF15803D) else Color(0xFFB91C1C),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }


            IconButton(
                onClick = { /* Agregar al Carrito */ },
                enabled = item.availableStock > 0
            ) {
                Icon(
                    imageVector = Icons.Default.AddShoppingCart,
                    contentDescription = "Agregar a la solicitud",
                    tint = if (item.availableStock > 0) MaterialTheme.colorScheme.primary else Color.Gray
                )
            }
        }
    }
}


data class ResourceItem(val name: String, val location: String, val availableStock: Int, val totalStock: Int)


val sampleResources = listOf(
    ResourceItem("Kit LEGO WeDo 2.0", "Pañol Central - Estante A1", 8, 10),
    ResourceItem("Proyector Epson PowerLite", "Pañol Central - Repisa 3", 2, 5),
    ResourceItem("Notebook HP ProBook G8", "Carro Carga 1", 15, 20),
    ResourceItem("Placa ESP32 + Kit Sensores", "Pañol Central - Caja Robótica", 0, 6)
)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CatalogScreenPreview() {
    MaterialTheme {
        CatalogScreen()
    }
}

