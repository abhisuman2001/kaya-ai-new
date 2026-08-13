package com.example.ui.theme

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
    primary = MetaBlue,
    secondary = MetaCyan,
    tertiary = StatusInfo,
    background = SiteBackgroundDark,
    surface = SiteSurfaceDark,
    surfaceVariant = SiteSurfaceVariantDark,
    onPrimary = Color.White,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = BorderDark,
    outlineVariant = BorderDark,
    error = StatusError
)

private val LightColorScheme = lightColorScheme(
    primary = MetaBlue,
    secondary = MetaCyan,
    tertiary = StatusInfo,
    background = SiteBackgroundLight,
    surface = SiteSurfaceLight,
    surfaceVariant = SiteSurfaceVariantLight,
    onPrimary = Color.White,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = Color(0xFFE5E7EB),
    outlineVariant = Color(0xFFE5E7EB),
    error = StatusError
)

@Composable
fun KayaTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false, // Set false to maintain exact Meta AI brand identity
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
