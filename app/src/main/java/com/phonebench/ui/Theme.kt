package com.phonebench.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Colors = darkColorScheme(
    primary = Color(0xFF4ADE80),
    onPrimary = Color(0xFF0A0E27),
    secondary = Color(0xFF4FACFE),
    background = Color(0xFF0A0E27),
    surface = Color(0xFF131A3A),
    onBackground = Color(0xFFE6EDF3),
    onSurface = Color(0xFFE6EDF3),
    error = Color(0xFFEF4444)
)

@Composable
fun PhoneBenchTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = Colors,
        content = content
    )
}
