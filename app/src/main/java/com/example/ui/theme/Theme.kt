package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val MeghlaDarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = DeepMidnight,
    primaryContainer = SurfaceElevated,
    onPrimaryContainer = NeonCyan,
    secondary = NeonMagenta,
    onSecondary = DeepMidnight,
    secondaryContainer = SurfaceCard,
    onSecondaryContainer = NeonMagenta,
    tertiary = GoldenSpark,
    onTertiary = DeepMidnight,
    background = DeepMidnight,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceCard,
    onSurfaceVariant = TextSecondary,
    outline = SurfaceCardBorder,
    error = ErrorRed,
    onError = TextPrimary
)

private val MeghlaLightColorScheme = MeghlaDarkColorScheme // Keep premium dark aesthetic for Meghla

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force cyberpunk dark vibe
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = MeghlaDarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = DeepMidnight.toArgb()
                window.navigationBarColor = DeepMidnight.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
