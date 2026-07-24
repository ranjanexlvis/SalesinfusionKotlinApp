package com.example.kmmexlvistech.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SalesInfusionLightColors = lightColorScheme(
    primary = PrimaryColor,
    onPrimary = Color.White,
    primaryContainer = drawerSelectedColor,
    onPrimaryContainer = PrimaryVariantColor,

    secondary = AccentColor,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFAD6E7),
    onSecondaryContainer = Color(0xFF5A0F2E),

    background = BackgroundContent,
    onBackground = Grey,

    surface = lightSurface,
    onSurface = Grey,
    surfaceVariant = TextFieldColor,
    onSurfaceVariant = Grey,

    error = Color(0xFFD32F2F),
    onError = Color.White,

    outline = BorderColor,
    outlineVariant = PagerDotColor,
)

private val SalesInfusionDarkColors = darkColorScheme(
    primary = PrimaryColorDark,
    onPrimary = Color(0xFF00304F),
    primaryContainer = PrimaryVariantColor,
    onPrimaryContainer = drawerSelectedColor,

    secondary = Color(0xFFE888B4),
    onSecondary = Color(0xFF450022),
    secondaryContainer = Color(0xFF5A0F2E),
    onSecondaryContainer = Color(0xFFFAD6E7),

    background = BackgroundDark,
    onBackground = OnBackgroundDark,

    surface = SurfaceDark,
    onSurface = OnBackgroundDark,
    surfaceVariant = Color(0xFF2A2D31),
    onSurfaceVariant = Color(0xFFC7C9CC),

    error = Color(0xFFFF6B6B),
    onError = Color(0xFF690005),

    outline = Color(0xFF8E9099),
    outlineVariant = Color(0xFF44474F),
)

/**
 * App-wide theme for Sales Infusion. This is intentionally a separate theme from
 * `AppTheme` (SalesinfusionColors/AyurvedicDashboardScreen) so the two brand looks
 * don't collide — use this one for the Sales Infusion CRM screens (My Work / My Leads /
 * My Calls / My Task etc.).
 */
@Composable
fun SalesInfusionTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) SalesInfusionDarkColors else SalesInfusionLightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = SalesinfusionTypography,
        content = content
    )
}