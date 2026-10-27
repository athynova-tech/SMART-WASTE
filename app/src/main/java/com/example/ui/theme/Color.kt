package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Theme Mode Selection
enum class ThemeMode(val title: String) {
    LIGHT("Light Mode"),
    DARK("Dark Mode"),
    SYSTEM("System Default")
}

// App Theme Color Palette Accents
enum class AppThemePalette(
    val title: String,
    val primary: Color,
    val primaryVariant: Color,
    val containerDark: Color,
    val onContainerDark: Color,
    val containerLight: Color,
    val onContainerLight: Color
) {
    ECO_EMERALD(
        title = "Eco Emerald",
        primary = Color(0xFF10B981),
        primaryVariant = Color(0xFF059669),
        containerDark = Color(0xFF064E3B),
        onContainerDark = Color(0xFFD1FAE5),
        containerLight = Color(0xFFD1FAE5),
        onContainerLight = Color(0xFF064E3B)
    ),
    MUNICIPAL_OCEAN(
        title = "Municipal Ocean",
        primary = Color(0xFF0284C7),
        primaryVariant = Color(0xFF0369A1),
        containerDark = Color(0xFF0C4A6E),
        onContainerDark = Color(0xFFBAE6FD),
        containerLight = Color(0xFFE0F2FE),
        onContainerLight = Color(0xFF0369A1)
    ),
    SOLAR_AMBER(
        title = "Solar Amber",
        primary = Color(0xFFF59E0B),
        primaryVariant = Color(0xFFD97706),
        containerDark = Color(0xFF78350F),
        onContainerDark = Color(0xFFFEF3C7),
        containerLight = Color(0xFFFEF3C7),
        onContainerLight = Color(0xFF78350F)
    ),
    ROYAL_AMETHYST(
        title = "Royal Amethyst",
        primary = Color(0xFFA855F7),
        primaryVariant = Color(0xFF9333EA),
        containerDark = Color(0xFF581C87),
        onContainerDark = Color(0xFFF3E8FF),
        containerLight = Color(0xFFF3E8FF),
        onContainerLight = Color(0xFF581C87)
    )
}

// Font Size Scaling for Enhanced Accessibility & Readability
enum class FontSizeScale(val title: String, val scale: Float, val description: String) {
    STANDARD("Standard (Enhanced 120%)", 1.20f, "Comfortable, enlarged base sizing"),
    LARGE("Large (135%)", 1.35f, "High visibility & relaxed reading"),
    EXTRA_LARGE("Extra Large (155%)", 1.55f, "Maximum bold clarity & legibility")
}

// Sophisticated Dark Foundation Colors (High-Contrast & Eye-Safe)
val ObsidianDarkBg = Color(0xFF0F172A)
val CharcoalSurface = Color(0xFF1E293B)
val CharcoalSurfaceVariant = Color(0xFF334155)
val CharcoalContainerHigh = Color(0xFF475569)
val CharcoalOutline = Color(0xFF475569)
val CharcoalOutlineVariant = Color(0xFF334155)

// Crisp Light Foundation Colors (High-Contrast & Clean)
val LightBg = Color(0xFFF8FAFC)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFF1F5F9)
val LightContainerHigh = Color(0xFFE2E8F0)
val LightOutline = Color(0xFFCBD5E1)
val LightOutlineVariant = Color(0xFFE2E8F0)

// Vibrant Eco Green Accents
val EcoGreenPrimary = Color(0xFF10B981)
val EcoGreenAccent = Color(0xFF34D399)
val EcoGreenDark = Color(0xFF064E3B)
val EcoGreenLight = Color(0xFFD1FAE5)

// Municipal Sky / Cyan Accents
val MunicipalBlue = Color(0xFF0284C7)
val MunicipalBlueLight = Color(0xFFBAE6FD)
val MunicipalBlueDark = Color(0xFF0C4A6E)

// Amber / Warning Accents
val AmberWarning = Color(0xFFF59E0B)
val AmberLight = Color(0xFFFEF3C7)
val AmberDark = Color(0xFF78350F)

// Crimson / Error Accents
val ErrorRed = Color(0xFFEF4444)
val ErrorRedLight = Color(0xFFFEE2E2)
val ErrorRedDark = Color(0xFF7F1D1D)

// Semantic Dark / Neutrals (Preserved for backwards-compatibility & high contrast)
val Slate900 = Color(0xFF0F172A) // Deepest text / surface
val Slate800 = Color(0xFF1E293B) // Elevated card surface
val Slate700 = Color(0xFF334155) // High container surface
val Slate600 = Color(0xFF64748B) // Secondary / subtitle text
val Slate500 = Color(0xFF94A3B8) // Medium muted text / icons
val Slate400 = Color(0xFFCBD5E1) // Subdued label text
val Slate300 = Color(0xFFE2E8F0) // Muted border & icon accent
val Slate200 = Color(0xFFF1F5F9) // Borders & dividers
val Slate100 = Color(0xFFF8FAFC) // Pill / Tag backgrounds
val Slate50 = Color(0xFFFFFFFF)  // High-contrast primary text

// Status & Dashboard Accents
val AlertRed = Color(0xFFEF4444)
val SuccessGreen = Color(0xFF10B981)
val PendingAmber = Color(0xFFF59E0B)
val RecycleGold = Color(0xFFEAB308)
val DarkSlate900 = Color(0xFF0F172A)
