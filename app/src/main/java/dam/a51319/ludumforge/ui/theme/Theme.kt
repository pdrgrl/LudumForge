package dam.a51319.ludumforge.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Strict Light Palette enforcing "The Architect's Vellum"
private val StudioLightColorScheme = lightColorScheme(
    primary = PrimaryBlack,
    onPrimary = SurfaceContainerLowest,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = SurfaceContainerLowest,

    secondary = SecondaryGray,
    onSecondary = SurfaceContainerLowest,

    background = SurfaceBase,
    onBackground = PrimaryBlack,

    surface = SurfaceBase,
    onSurface = PrimaryBlack,
    surfaceVariant = SurfaceContainerHigh,
    onSurfaceVariant = OnSurfaceVariant,

    error = ErrorRed,
    onError = SurfaceContainerLowest
)

@Composable
fun LudumForgeTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = StudioLightColorScheme, // Enforces strict light mode
        typography = Typography,
        content = content
    )
}