package dam.a51319.ludumforge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import dam.a51319.ludumforge.ui.navigation.AppNavigation
import dam.a51319.ludumforge.ui.theme.LudumForgeTheme
import dam.a51319.ludumforge.viewmodels.PersonalDashboardViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Read deep link from the launching intent (if any)
        val inviteJamId: String? = intent?.data
            ?.takeIf { it.scheme == "ludumforge" && it.host == "join" }
            ?.getQueryParameter("jamId")

        setContent {
            LudumForgeTheme {
                AppNavigation(inviteJamId = inviteJamId)
            }
        }
    }
}
