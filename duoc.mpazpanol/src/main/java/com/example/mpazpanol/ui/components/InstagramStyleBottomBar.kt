package com.example.mpazpanol.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun InstagramStyleBottomBar(
    currentTab: Int,
    cartCount: Int,
    isAdmin: Boolean,
    onTabSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = Color.White
    ) {
        NavigationBarItem(
            selected = currentTab == 0,
            onClick = { onTabSelected(0) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Catálogo") },
            label = { Text("Catálogo") }
        )
        NavigationBarItem(
            selected = currentTab == 1,
            onClick = { onTabSelected(1) },
            icon = {
                if (cartCount > 0) {
                    BadgedBox(
                        badge = {
                            Badge { Text(cartCount.toString()) }
                        }
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito")
                    }
                } else {
                    Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito")
                }
            },
            label = { Text("Carrito") }
        )
        if (isAdmin) {
            NavigationBarItem(
                selected = currentTab == 2,
                onClick = { onTabSelected(2) },
                icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin TI") },
                label = { Text("Admin TI") }
            )
        }
    }
}
