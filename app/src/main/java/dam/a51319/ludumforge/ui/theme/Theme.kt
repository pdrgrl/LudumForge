package dam.a51319.ludumforge.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// Strict Dark Palette ignoring system light/dark mode
private val StrictDarkColorScheme = darkColorScheme(
    primary = NeonPurple,
    onPrimary = BackgroundDark,
    primaryContainer = NeonPurpleDark,
    onPrimaryContainer = TextPrimary,

    secondary = NeonGreen,
    onSecondary = BackgroundDark,
    secondaryContainer = NeonGreenDark,
    onSecondaryContainer = TextPrimary,

    background = BackgroundDark,
    onBackground = TextPrimary,

    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondary,

    error = ErrorRed,
    onError = BackgroundDark
)

@Composable
fun LudumForgeTheme(
    // Removed dynamicColor and darkTheme booleans to enforce our strict aesthetic
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = StrictDarkColorScheme, // Enforces the strict dark aesthetic
        typography = Typography,
        content = content
    )
}