package com.example.mpazpanol.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = CianEscuela,
    secondary = AmarilloEscuela,
    background = FondoClaro,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = TextoOscuro,
    onBackground = TextoOscuro,
    onSurface = TextoOscuro,
    error = ErrorRojo,
)

private val DarkColorScheme = darkColorScheme(
    primary = CianEscuela,
    secondary = AmarilloEscuela,
    background = FondoOscuro,
    surface = SuperficieOscura,
    onPrimary = Color.Black,
    onSecondary = TextoClaro,
    onBackground = TextoClaro,
    onSurface = TextoClaro,
    error = ErrorRojo,
)

@Composable
fun MpazPanolTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}