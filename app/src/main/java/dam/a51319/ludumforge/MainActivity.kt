package dam.a51319.ludumforge

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import dam.a51319.ludumforge.data.SessionManager
import dam.a51319.ludumforge.ui.navigation.AppNavigation
import dam.a51319.ludumforge.ui.theme.LudumForgeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Read deep link from the launching intent (if any)
        val inviteJamId: String? = intent?.data
            ?.takeIf { it.scheme == "ludumforge" && it.host == "join" }
            ?.getQueryParameter("jamId")

        // Load persisted dark mode preference
        val sharedPrefs = getSharedPreferences("LudumForgePrefs", Context.MODE_PRIVATE)
        val isDark = sharedPrefs.getBoolean("is_dark_theme", true)
        SessionManager.setDarkTheme(isDark)

        setContent {
            val isDarkThemeState by SessionManager.isDarkTheme.collectAsState()
            LudumForgeTheme(darkTheme = isDarkThemeState) {
                AppNavigation(inviteJamId = inviteJamId)
            }
        }
    }
}
