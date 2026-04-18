package dam.a51319.ludumforge.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dam.a51319.ludumforge.ui.screens.LoginScreen
import dam.a51319.ludumforge.ui.screens.OfflineTerminalScreen
import dam.a51319.ludumforge.ui.screens.PersonalDashboardScreen
import dam.a51319.ludumforge.ui.screens.PublicJamsScreen
import dam.a51319.ludumforge.ui.screens.RegisterScreen
import dam.a51319.ludumforge.ui.screens.RoadmapGeneratorScreen
import dam.a51319.ludumforge.ui.screens.TeamWorkspaceScreen
import com.google.firebase.auth.FirebaseAuth

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val PERSONAL_DASHBOARD = "personal_dashboard"
    const val TEAM_WORKSPACE = "team_workspace"
    const val PUBLIC_JAMS = "public_jams"
    const val ROADMAP_GENERATOR = "roadmap_generator"
    const val OFFLINE_TERMINAL = "offline_terminal"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val currentUser = FirebaseAuth.getInstance().currentUser
    val startDest = if (currentUser != null) Routes.PERSONAL_DASHBOARD else Routes.LOGIN


    Scaffold(
        bottomBar = {
            val mainTabs = listOf(
                Routes.PERSONAL_DASHBOARD,
                Routes.TEAM_WORKSPACE,
                Routes.PUBLIC_JAMS
            )
            if (currentRoute in mainTabs) {
                LudumForgeBottomBar(navController = navController, currentRoute = currentRoute)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDest, // Use the dynamic variable here!
            modifier = Modifier.padding(innerPadding)
        ) {
            // AUTH ROUTES
            composable(Routes.LOGIN) {
                LoginScreen(
                    onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
                    onLoginSuccess = {
                        navController.navigate(Routes.PERSONAL_DASHBOARD) {
                            popUpTo(Routes.LOGIN) { inclusive = true } // Clear Login from stack
                        }
                    }
                )
            }
            composable(Routes.REGISTER) {
                RegisterScreen(
                    onNavigateToLogin = { navController.popBackStack() },
                    onRegisterSuccess = {
                        navController.navigate(Routes.PERSONAL_DASHBOARD) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                )
            }

            // APP ROUTES
            composable(Routes.PERSONAL_DASHBOARD) {
                PersonalDashboardScreen(
                    onLogout = {
                        navController.navigate(Routes.LOGIN) {
                            // Clear the entire navigation history so the user can't press 'Back' to return
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.TEAM_WORKSPACE) { TeamWorkspaceScreen() }
            composable(Routes.PUBLIC_JAMS) { PublicJamsScreen() }
            composable(Routes.ROADMAP_GENERATOR) { RoadmapGeneratorScreen() }
            composable(Routes.OFFLINE_TERMINAL) { OfflineTerminalScreen() }
        }
    }
}

@Composable
fun LudumForgeBottomBar(navController: NavHostController, currentRoute: String?) {
    // Theming mappings based on Architect's Vellum
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryGray = MaterialTheme.colorScheme.secondary
    val surfaceContainerHigh = MaterialTheme.colorScheme.surfaceVariant

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.onPrimary, // SurfaceContainerLowest
        contentColor = secondaryGray,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Architecture, contentDescription = "Planning") },
            label = { Text("Planning", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            selected = currentRoute == Routes.PERSONAL_DASHBOARD,
            onClick = {
                navController.navigate(Routes.PERSONAL_DASHBOARD) {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = surfaceContainerHigh,
                selectedIconColor = primaryColor,
                selectedTextColor = primaryColor,
                unselectedIconColor = secondaryGray,
                unselectedTextColor = secondaryGray
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Groups, contentDescription = "Workspace") },
            label = { Text("Workspace", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            selected = currentRoute == Routes.TEAM_WORKSPACE,
            onClick = {
                navController.navigate(Routes.TEAM_WORKSPACE) {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = surfaceContainerHigh,
                selectedIconColor = primaryColor,
                selectedTextColor = primaryColor,
                unselectedIconColor = secondaryGray,
                unselectedTextColor = secondaryGray
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Explore, contentDescription = "Explore") },
            label = { Text("Explore", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            selected = currentRoute == Routes.PUBLIC_JAMS,
            onClick = {
                navController.navigate(Routes.PUBLIC_JAMS) {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = surfaceContainerHigh,
                selectedIconColor = primaryColor,
                selectedTextColor = primaryColor,
                unselectedIconColor = secondaryGray,
                unselectedTextColor = secondaryGray
            )
        )
    }
}