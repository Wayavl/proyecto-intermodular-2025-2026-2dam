package com.paralearn.android.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.unit.sp
import com.paralearn.android.R

// Set up Google Fonts provider
val provider = GoogleFont.Provider(
    "com.google.android.gms.fonts",
    "com.google.android.gms",
    R.array.com_google_android_gms_fonts_certs
)

val SpaceGroteskFont = GoogleFont("Space Grotesk")

val SpaceGrotesk = FontFamily(
    Font(googleFont = SpaceGroteskFont, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = SpaceGroteskFont, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = SpaceGroteskFont, fontProvider = provider, weight = FontWeight.Bold)
)

val Typography = androidx.compose.material3.Typography(
    displayLarge = TextStyle(
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        letterSpacing = (-0.5).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),
    titleLarge = TextStyle(
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default, // Standard Inter/Roboto for body
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    labelMedium = TextStyle(
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        letterSpacing = 1.sp
    )
)