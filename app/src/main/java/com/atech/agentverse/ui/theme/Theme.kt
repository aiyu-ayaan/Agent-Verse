/*
 *
 *  Copyright (c) 2026 Ayaan.
 *  Licensed under the MIT License.
 *
 *  Created: 2026
 *  Modified: 3/18/26, 9:46 PM
 *
 *  AgentVerse
 *  Integrates multiple AI models with a modular clean architecture.
 *
 *
 */

package com.atech.agentverse.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AvDarkPrimary,
    onPrimary = AvInk,
    secondary = AvDarkSecondary,
    onSecondary = AvInk,
    background = AvDarkBackground,
    onBackground = Color(0xFFEAF1FF),
    surface = AvDarkSurface,
    onSurface = Color(0xFFEAF1FF),
    surfaceVariant = Color(0xFF1A2534),
    onSurfaceVariant = Color(0xFFC7D4E8),
    outline = Color(0xFF4E5D75),
    error = Color(0xFFFFB4AB),
)

private val LightColorScheme = lightColorScheme(
    primary = AvBlue,
    onPrimary = Color.White,
    secondary = AvMint,
    onSecondary = Color.White,
    background = AvMist,
    onBackground = AvInk,
    surface = Color.White,
    onSurface = AvInk,
    surfaceVariant = Color(0xFFEAF0FA),
    onSurfaceVariant = Color(0xFF4B5B73),
    outline = Color(0xFFC3CEDF),
    error = Color(0xFFB3261E),
    primaryContainer = AvBlueLight,
    onPrimaryContainer = Color(0xFF00275D),
)

@Composable
fun AgentVerseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
