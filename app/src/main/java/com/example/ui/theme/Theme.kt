package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

@Composable
fun SmartWasteTheme(
    themeMode: ThemeMode = ThemeMode.DARK,
    palette: AppThemePalette = AppThemePalette.ECO_EMERALD,
    fontSizeScale: FontSizeScale = FontSizeScale.STANDARD,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDark -> {
            darkColorScheme(
                primary = palette.primary,
                onPrimary = Color(0xFF003822),
                primaryContainer = palette.containerDark,
                onPrimaryContainer = palette.onContainerDark,
                secondary = MunicipalBlue,
                onSecondary = Color(0xFF003258),
                secondaryContainer = MunicipalBlueDark,
                onSecondaryContainer = MunicipalBlueLight,
                tertiary = AmberWarning,
                onTertiary = AmberDark,
                tertiaryContainer = AmberDark,
                onTertiaryContainer = AmberLight,
                background = ObsidianDarkBg,
                surface = CharcoalSurface,
                surfaceVariant = CharcoalSurfaceVariant,
                onBackground = Color(0xFFF8FAFC),
                onSurface = Color(0xFFF8FAFC),
                onSurfaceVariant = Color(0xFFCBD5E1),
                outline = CharcoalOutline,
                outlineVariant = CharcoalOutlineVariant,
                error = ErrorRed,
                errorContainer = ErrorRedDark,
                onError = Color.Black,
                onErrorContainer = ErrorRedLight
            )
        }
        else -> {
            lightColorScheme(
                primary = palette.primaryVariant,
                onPrimary = Color.White,
                primaryContainer = palette.containerLight,
                onPrimaryContainer = palette.onContainerLight,
                secondary = MunicipalBlue,
                onSecondary = Color.White,
                secondaryContainer = Color(0xFFE0F2FE),
                onSecondaryContainer = Color(0xFF0369A1),
                tertiary = AmberWarning,
                onTertiary = Color.White,
                tertiaryContainer = AmberLight,
                onTertiaryContainer = AmberDark,
                background = LightBg,
                surface = LightSurface,
                surfaceVariant = LightSurfaceVariant,
                onBackground = Color(0xFF0F172A),
                onSurface = Color(0xFF0F172A),
                onSurfaceVariant = Color(0xFF334155),
                outline = LightOutline,
                outlineVariant = LightOutlineVariant,
                error = ErrorRed,
                errorContainer = ErrorRedLight,
                onError = Color.White,
                onErrorContainer = ErrorRedDark
            )
        }
    }

    val currentDensity = LocalDensity.current
    val scaledDensity = remember(currentDensity, fontSizeScale) {
        Density(
            density = currentDensity.density,
            fontScale = currentDensity.fontScale * fontSizeScale.scale
        )
    }

    CompositionLocalProvider(LocalDensity provides scaledDensity) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) = SmartWasteTheme(
    themeMode = if (darkTheme) ThemeMode.DARK else ThemeMode.LIGHT,
    dynamicColor = dynamicColor,
    content = content
)
