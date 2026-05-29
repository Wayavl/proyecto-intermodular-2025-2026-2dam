package com.paralearn.android.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryCyan,
    onPrimary = DarkBackground,
    primaryContainer = PrimaryBlue,
    onPrimaryContainer = Color.White,
    secondary = AccentElectric,
    onSecondary = Color.White,
    background = DarkBackground,
    onBackground = TextMain,
    surface = DarkSurface,
    onSurface = TextMain,
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = TextSecondary,
    error = ErrorRed,
    outline = Color(0xFF334155)
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0E7FF),
    onPrimaryContainer = PrimaryBlue,
    secondary = AccentElectric,
    onSecondary = Color.White,
    background = LightBackground,
    onBackground = LightTextMain,
    surface = LightSurface,
    onSurface = LightTextMain,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = LightTextSecondary,
    error = ErrorRed,
    outline = Color(0xFFCBD5E1)
)

@Composable
fun ParalearnTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val surfaces = if (darkTheme) darkParalearnSurfaces() else lightParalearnSurfaces()

    CompositionLocalProvider(LocalParalearnSurfaces provides surfaces) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}