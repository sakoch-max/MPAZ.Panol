package com.example.mpazpanol.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1E3A8A),      // Azul Institucional
    onPrimary = Color.White,
    secondary = Color(0xFF0D9488),    // Verde Teal
    onSecondary = Color.White,
    background = Color(0xFFF8FAFC),   // Gris claro de fondo
    surface = Color.White,
    onSurface = Color(0xFF1E293B),
    error = Color(0xFFDC2626)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF93C5FD),
    onPrimary = Color(0xFF1E3A8A),
    secondary = Color(0xFF5EEAD4),
    background = Color(0xFF0F172A),
    surface = Color(0xFF1E293B),
    onSurface = Color.White
)

@Composable
fun MPAZPanolTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}