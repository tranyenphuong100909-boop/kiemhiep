package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Wuxia Anime Fantasy Palette - Kiếm Mộng
val JadeCyan = Color(0xFF00E5FF)
val AzureBlue = Color(0xFF00A6FB)
val BlossomPink = Color(0xFFFF6994)
val DragonFire = Color(0xFFFF5722)
val FrostIce = Color(0xFF80D8FF)
val MysticGold = Color(0xFFFFD54F)
val NatureGreen = Color(0xFF69F0AE)
val DarkAbyss = Color(0xFF0B0F19)
val SurfaceDark = Color(0xFF141A29)
val CardDark = Color(0xFF1E2638)
val AccentBorder = Color(0xFF2C3954)

val DarkColorScheme = androidx.compose.material3.darkColorScheme(
    primary = JadeCyan,
    secondary = MysticGold,
    tertiary = BlossomPink,
    background = DarkAbyss,
    surface = SurfaceDark,
    surfaceVariant = CardDark,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color(0xFFE2E8F0),
    onSurface = Color(0xFFE2E8F0)
)
