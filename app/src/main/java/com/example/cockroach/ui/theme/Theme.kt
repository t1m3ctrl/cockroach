package com.example.cockroach.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Green80,
    onPrimary = Color(0xFF003909),
    primaryContainer = Color(0xFF005312),
    onPrimaryContainer = Color(0xFFD4F1C5),

    secondary = GreenGrey80,
    onSecondary = Color(0xFF2B3D2F),
    secondaryContainer = Color(0xFF3F5244),
    onSecondaryContainer = Color(0xFFDEEAE0),

    tertiary = LightGreen80,
    onTertiary = Color(0xFF1B3D1F),
    tertiaryContainer = Color(0xFF345835),
    onTertiaryContainer = Color(0xFFE8F5E9),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    background = Color(0xFF1A1C1A),
    onBackground = Color(0xFFE1E3E1),
    surface = Color(0xFF1A1C1A),
    onSurface = Color(0xFFE1E3E1),
    surfaceVariant = Color(0xFF414942),
    onSurfaceVariant = Color(0xFFC1C9C0),

    outline = Color(0xFF8B938A),
    outlineVariant = Color(0xFF414942),
    scrim = Color.Black,
    inverseSurface = Color(0xFFE1E3E1),
    inverseOnSurface = Color(0xFF2E312E),
    inversePrimary = Color(0xFF2E7D32)
)

private val LightColorScheme = lightColorScheme(
    primary = Green40,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB8F1B0),
    onPrimaryContainer = Color(0xFF002204),

    secondary = GreenGrey40,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDEEAE0),
    onSecondaryContainer = Color(0xFF1A2C1E),

    tertiary = LightGreen40,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE8F5E9),
    onTertiaryContainer = Color(0xFF0E2711),

    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    background = Color(0xFFF1F8F4),
    onBackground = Color(0xFF1A1C1A),
    surface = Color(0xFFF1F8F4),
    onSurface = Color(0xFF1A1C1A),
    surfaceVariant = Color(0xFFDDE5DB),
    onSurfaceVariant = Color(0xFF414942),

    outline = Color(0xFF717971),
    outlineVariant = Color(0xFFC1C9C0),
    scrim = Color.Black,
    inverseSurface = Color(0xFF2E312E),
    inverseOnSurface = Color(0xFFF0F1EC),
    inversePrimary = Color(0xFFB8F1B0)
)

@Composable
fun CockroachTheme(
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