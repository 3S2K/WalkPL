package org.example.project.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val HotPink = Color(0xFFFF69B4)
private val DarkBackground = Color(0xFF030303)
private val DarkSurface = Color(0xFF1D1D1D)
private val DarkGray = Color(0xFF282828)

private val DarkColorScheme = darkColorScheme(
    primary = HotPink,
    secondary = HotPink,
    tertiary = HotPink,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkGray,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
} 