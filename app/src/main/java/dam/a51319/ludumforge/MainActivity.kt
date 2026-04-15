package dam.a51319.ludumforge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import dam.a51319.ludumforge.ui.navigation.AppNavigation
import dam.a51319.ludumforge.ui.theme.LudumForgeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Optional: Tells Android to draw the UI behind the system bars (edge-to-edge)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            LudumForgeTheme {
                AppNavigation()
            }
        }
    }
}