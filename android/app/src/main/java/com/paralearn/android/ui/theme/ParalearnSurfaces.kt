package com.paralearn.android.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class ParalearnSurfaceColors(
    val containerLowest: Color,
    val containerLow: Color,
    val container: Color,
    val containerHigh: Color,
    val containerHighest: Color,
    val containerBright: Color,
    val topBar: Color
)

val LocalParalearnSurfaces = staticCompositionLocalOf { darkParalearnSurfaces() }

fun darkParalearnSurfaces() = ParalearnSurfaceColors(
    containerLowest = SurfaceContainerLowest,
    containerLow = SurfaceContainerLow,
    container = SurfaceContainer,
    containerHigh = SurfaceContainerHigh,
    containerHighest = SurfaceContainerHighest,
    containerBright = SurfaceBright,
    topBar = DarkBackground
)

fun lightParalearnSurfaces() = ParalearnSurfaceColors(
    containerLowest = Color(0xFFFFFFFF),
    containerLow = Color(0xFFF8FAFC),
    container = Color(0xFFF1F5F9),
    containerHigh = Color(0xFFE2E8F0),
    containerHighest = Color(0xFFCBD5E1),
    containerBright = Color(0xFFE2E8F0),
    topBar = LightBackground
)

@Composable
fun paralearnSurfaces(): ParalearnSurfaceColors = LocalParalearnSurfaces.current
