package com.tilegame.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val SkyBlueLight = Color(0xFF87CEEB)
val SkyBlueDark = Color(0xFF4A90D9)
val GameGreen = Color(0xFF4CAF50)
val GameGold = Color(0xFFFFD700)
val GameBrown = Color(0xFF8D6E63)
val TileWhite = Color(0xFFFFFDF7)
val QueueSlotDark = Color(0xFF2C3E50)
val TopBarBg = Color(0xFF1A3A5C)
val BottomBarBg = Color(0xFF2C5F2D)
val BlockedOverlay = Color(0x60000000)

private val GameColorScheme = lightColorScheme(
    primary = SkyBlueDark,
    secondary = GameGreen,
    tertiary = GameGold,
    background = SkyBlueLight,
    surface = TileWhite,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
)

@Composable
fun TileGameTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = TopBarBg.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = GameColorScheme,
        content = content
    )
}
