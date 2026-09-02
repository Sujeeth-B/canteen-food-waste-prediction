package com.example.ui.theme

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

// ─── Dark Color Scheme — Charcoal + Orange ────────────────────────────────────
private val DarkColorScheme = darkColorScheme(
    primary                = OrangePrimary,
    onPrimary              = Color.White,
    primaryContainer       = OrangePrimaryContainer,
    onPrimaryContainer     = OrangePrimaryContainerOn,

    secondary              = AmberSecondary,
    onSecondary            = Color.White,
    secondaryContainer     = AmberSecondaryContainer,
    onSecondaryContainer   = Color(0xFFFFD9B3),

    tertiary               = ChartAmber,
    onTertiary             = Color(0xFF1A0A00),

    background             = DarkBackground,
    onBackground           = DarkOnSurface,

    surface                = DarkSurface,
    onSurface              = DarkOnSurface,
    surfaceVariant         = DarkSurfaceVariant,
    onSurfaceVariant       = DarkOnSurfaceVariant,

    outline                = DarkOutline,
    error                  = RiskHigh,
    onError                = Color.White
)

// ─── Light Color Scheme — Clean White + Orange ────────────────────────────────
private val LightColorScheme = lightColorScheme(
    primary                = OrangePrimaryLight,
    onPrimary              = Color.White,
    primaryContainer       = OrangePrimaryContainerLight,
    onPrimaryContainer     = OrangePrimaryContainerOnLight,

    secondary              = AmberSecondaryLight,
    onSecondary            = Color.White,
    secondaryContainer     = AmberSecondaryContainerLight,
    onSecondaryContainer   = Color(0xFF7C2D12),

    tertiary               = RiskMedium,
    onTertiary             = Color.White,

    background             = LightBackground,
    onBackground           = LightOnSurface,

    surface                = LightSurface,
    onSurface              = LightOnSurface,
    surfaceVariant         = LightSurfaceVariant,
    onSurfaceVariant       = LightOnSurfaceVariant,

    outline                = LightOutline,
    error                  = RiskHigh,
    onError                = Color.White
)

@Composable
fun WasteWiseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use brand colors for cohesive identity
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else      -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content
    )
}
