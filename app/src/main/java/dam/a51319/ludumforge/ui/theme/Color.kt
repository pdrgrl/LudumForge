package dam.a51319.ludumforge.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme

// --- Raw Theme Palette Colors ---

// Cyber-Forge Dark Palette values
val DarkSurfaceBase = Color(0xFF0A090B)
val DarkSurfaceContainerHigh = Color(0xFF1B191E)
val DarkSurfaceContainerLowest = Color(0xFF121114)
val DarkPrimaryBlack = Color(0xFFEBEAED)
val DarkPrimaryContainerDark = Color(0xFF28262C)
val DarkSecondaryGray = Color(0xFF96949C)
val DarkOnSurfaceVariant = Color(0xFFB1AEB6)
val DarkGhostBorder = Color(0xFF34323A).copy(alpha = 0.4f)

// Cyber-Forge Light Palette values
val LightSurfaceBase = Color(0xFFF9F9FA)
val LightSurfaceContainerHigh = Color(0xFFEBEAEF)
val LightSurfaceContainerLowest = Color(0xFFFFFFFF)
val LightPrimaryBlack = Color(0xFF16151A)
val LightPrimaryContainerDark = Color(0xFFDCDAE0)
val LightSecondaryGray = Color(0xFF75737B)
val LightOnSurfaceVariant = Color(0xFF5F5E64)
val LightGhostBorder = Color(0xFFC6C6C6).copy(alpha = 0.3f)

// Theme accents representing raw forge heat and code/graphics precision
val MoltenOrange = Color(0xFFFC5A1B)
val MoltenOrangeEnd = Color(0xFFD43F00)
val CyberCyan = Color(0xFF00F5D4)
val ErrorRed = Color(0xFFFF5252)

// --- Dynamic Theme Getters ---

val SurfaceBase: Color
    @Composable
    get() = MaterialTheme.colorScheme.background

val SurfaceContainerHigh: Color
    @Composable
    get() = MaterialTheme.colorScheme.surfaceVariant

val SurfaceContainerLowest: Color
    @Composable
    get() = MaterialTheme.colorScheme.surface

val PrimaryBlack: Color
    @Composable
    get() = MaterialTheme.colorScheme.primary

val PrimaryContainerDark: Color
    @Composable
    get() = MaterialTheme.colorScheme.primaryContainer

val SecondaryGray: Color
    @Composable
    get() = MaterialTheme.colorScheme.secondary

val OnSurfaceVariant: Color
    @Composable
    get() = MaterialTheme.colorScheme.onSurfaceVariant

val GhostBorder: Color
    @Composable
    get() = if (MaterialTheme.colorScheme.background == LightSurfaceBase) LightGhostBorder else DarkGhostBorder