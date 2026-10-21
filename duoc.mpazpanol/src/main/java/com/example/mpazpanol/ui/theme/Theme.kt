package com.example.mpazpanol.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = CianEscuela,
    secondary = AmarilloEscuela,
    background = FondoClaro,
    surface = FondoClaro,
    onPrimary = Color.White,
    onSecondary = TextoOscuro,
    error = ErrorRojo,
)

@Composable
fun MpazPanolTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}