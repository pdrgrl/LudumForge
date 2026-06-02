package dam.a51319.ludumforge.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Strict Light Palette enforcing "The Cyber-Forge" (Light Mode)
private val StudioLightColorScheme = lightColorScheme(
    primary = LightPrimaryBlack,
    onPrimary = LightSurfaceContainerLowest,
    primaryContainer = LightPrimaryContainerDark,
    onPrimaryContainer = LightPrimaryBlack,

    secondary = LightSecondaryGray,
    onSecondary = LightSurfaceContainerLowest,

    background = LightSurfaceBase,
    onBackground = LightPrimaryBlack,

    surface = LightSurfaceBase,
    onSurface = LightPrimaryBlack,
    surfaceVariant = LightSurfaceContainerHigh,
    onSurfaceVariant = LightOnSurfaceVariant,

    error = ErrorRed,
    onError = LightSurfaceContainerLowest
)

// Strict Dark Palette enforcing "The Cyber-Forge" (Dark Mode)
private val StudioDarkColorScheme = darkColorScheme(
    primary = DarkPrimaryBlack,
    onPrimary = DarkSurfaceContainerLowest,
    primaryContainer = DarkPrimaryContainerDark,
    onPrimaryContainer = DarkPrimaryBlack,

    secondary = DarkSecondaryGray,
    onSecondary = DarkSurfaceBase,

    background = DarkSurfaceBase,
    onBackground = DarkPrimaryBlack,

    surface = DarkSurfaceBase,
    onSurface = DarkPrimaryBlack,
    surfaceVariant = DarkSurfaceContainerHigh,
    onSurfaceVariant = DarkOnSurfaceVariant,

    error = ErrorRed,
    onError = DarkSurfaceBase
)

@Composable
fun LudumForgeTheme(
    darkTheme: Boolean = true, // Exposes preference toggle
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) StudioDarkColorScheme else StudioLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}