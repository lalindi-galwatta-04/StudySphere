
package com.example.studysphere.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// --------------------------------------------------
// THEME OPTIONS
// --------------------------------------------------

enum class AppThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}


// --------------------------------------------------
// LIGHT COLOUR SCHEME
// --------------------------------------------------

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    secondary = LightSecondary,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnBackground,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnBackground,
    error = ErrorRed
)


// --------------------------------------------------
// DARK COLOUR SCHEME
// --------------------------------------------------

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    secondary = DarkSecondary,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnBackground,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnBackground,
    error = ErrorRed
)


// --------------------------------------------------
// MAIN STUDYSPHERE THEME
// --------------------------------------------------

@Composable
fun StudySphereTheme(
    darkTheme: Boolean? = null,
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {

    val useDarkTheme = darkTheme ?: when (themeMode) {

        AppThemeMode.SYSTEM -> isSystemInDarkTheme()

        AppThemeMode.LIGHT -> false

        AppThemeMode.DARK -> true
    }

    val colorScheme = if (useDarkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = StudySphereTypography,
        content = content
    )
}
