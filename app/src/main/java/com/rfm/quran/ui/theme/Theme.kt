package com.rfm.quran.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = Color(0xFF39D353),
    onPrimary = Color(0xFF0D1117),
    primaryContainer = Color(0xFF1C4429),
    onPrimaryContainer = Color(0xFFB7F7C4),
    secondary = Color(0xFFE3B341),
    onSecondary = Color(0xFF0D1117),
    tertiary = Color(0xFF58A6FF),
    background = Color(0xFF0D1117),
    onBackground = Color(0xFFE6EDF3),
    surface = Color(0xFF161B22),
    onSurface = Color(0xFFE6EDF3),
    surfaceVariant = Color(0xFF21262D),
    onSurfaceVariant = Color(0xFF8B949E),
)

private val LightColors = lightColorScheme(
    primary = Color(0xFF1A7F37),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFB7F7C4),
    onPrimaryContainer = Color(0xFF0D2815),
    secondary = Color(0xFF9E7B1E),
    onSecondary = Color(0xFFFFFFFF),
    background = Color(0xFFF6F8FA),
    onBackground = Color(0xFF1F2328),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1F2328),
    surfaceVariant = Color(0xFFEAEEF2),
    onSurfaceVariant = Color(0xFF59636E),
)

@Composable
fun QuranTheme(content: @Composable () -> Unit) {
    val dark = isSystemInDarkTheme()
    MaterialTheme(
        colorScheme = if (dark) DarkColors else LightColors,
        content = content
    )
}
