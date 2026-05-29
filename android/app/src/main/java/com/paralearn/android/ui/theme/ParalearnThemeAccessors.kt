package com.paralearn.android.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun appBackgroundColor(): Color = MaterialTheme.colorScheme.background

@Composable
fun appTextMainColor(): Color = MaterialTheme.colorScheme.onSurface

@Composable
fun appTextSecondaryColor(): Color = MaterialTheme.colorScheme.onSurfaceVariant

@Composable
fun appSurfaceLowest(): Color = paralearnSurfaces().containerLowest

@Composable
fun appSurfaceLow(): Color = paralearnSurfaces().containerLow

@Composable
fun appSurfaceContainer(): Color = paralearnSurfaces().container

@Composable
fun appSurfaceHigh(): Color = paralearnSurfaces().containerHigh
