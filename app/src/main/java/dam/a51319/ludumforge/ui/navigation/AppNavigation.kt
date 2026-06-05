package dam.a51319.ludumforge.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dam.a51319.ludumforge.ui.components.LudumForgeTopAppBar
import dam.a51319.ludumforge.ui.screens.*
import dam.a51319.ludumforge.viewmodels.AuthViewModel
import dam.a51319.ludumforge.viewmodels.PersonalDashboardViewModel

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val PERSONAL_DASHBOARD = "personal_dashboard"
    const val TEAM_WORKSPACE = "team_workspace"
    const val PUBLIC_JAMS = "public_jams"
    const val ROADMAP_GENERATOR = "roadmap_generator"
    const val OFFLINE_TERMINAL = "offline_terminal"
    const val SUBSCRIPTION = "subscription"
}

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = viewModel(),
    inviteJamId: String? = null
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val currentUser by authViewModel.currentUser.collectAsState()
    val startDest = if (currentUser != null) Routes.PERSONAL_DASHBOARD else Routes.LOGIN

    // Single hoisted VM shared by Dashboard, SubscriptionScreen, and TeamWorkspaceScreen
    val dashboardViewModel: PersonalDashboardViewModel = viewModel()

    // If the app was opened via a deep link, pass the jamId to the VM once
    LaunchedEffect(inviteJamId) {
        if (inviteJamId != null) {
            dashboardViewModel.setPendingInvite(inviteJamId)
        }
    }

    val mainTabs = listOf(
        Routes.PERSONAL_DASHBOARD,
        Routes.ROADMAP_GENERATOR,
        Routes.TEAM_WORKSPACE,
        Routes.PUBLIC_JAMS,
        Routes.OFFLINE_TERMINAL
    )

    Scaffold(
        topBar = {
            if (currentRoute != Routes.LOGIN && currentRoute != Routes.REGISTER) {
                LudumForgeTopAppBar(
                    currentUser = currentUser,
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    },
                    onNavigateToSubscription = {
                        navController.navigate(Routes.SUBSCRIPTION)
                    }
                )
            }
        },
        bottomBar = {
            if (currentRoute in mainTabs) {
                LudumForgeBottomBar(navController = navController, currentRoute = currentRoute)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDest,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.LOGIN) {
                LoginScreen(
                    viewModel = authViewModel,
                    onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
                    onLoginSuccess = {
                        navController.navigate(Routes.PERSONAL_DASHBOARD) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.REGISTER) {
                RegisterScreen(
                    viewModel = authViewModel,
                    onNavigateToLogin = { navController.popBackStack() },
                    onRegisterSuccess = {
                        navController.navigate(Routes.PERSONAL_DASHBOARD) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.PERSONAL_DASHBOARD) {
                PersonalDashboardScreen(
                    viewModel = dashboardViewModel,
                    authViewModel = authViewModel,
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    },
                    onNavigateToSubscription = {
                        navController.navigate(Routes.SUBSCRIPTION)
                    }
                )
            }
            composable(Routes.TEAM_WORKSPACE) {
                TeamWorkspaceScreen(dashboardViewModel = dashboardViewModel)
            }
            composable(Routes.PUBLIC_JAMS) {
                PublicJamsScreen(
                    dashboardViewModel = dashboardViewModel,
                    onNavigateToRoadmap = {
                        navController.navigate(Routes.ROADMAP_GENERATOR)
                    }
                )
            }
            composable(Routes.ROADMAP_GENERATOR) {
                RoadmapGeneratorScreen(authViewModel = authViewModel)
            }
            composable(Routes.OFFLINE_TERMINAL) { OfflineTerminalScreen() }
            composable(Routes.SUBSCRIPTION) {
                SubscriptionScreen(
                    dashboardViewModel = dashboardViewModel,
                    authViewModel = authViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun LudumForgeBottomBar(navController: NavHostController, currentRoute: String?) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryGray = MaterialTheme.colorScheme.secondary
    val surfaceContainerHigh = MaterialTheme.colorScheme.surfaceVariant

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.onPrimary,
        contentColor = secondaryGray,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Construction, contentDescription = "Planning") },
            label = { Text("Planning", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            selected = currentRoute == Routes.PERSONAL_DASHBOARD,
            onClick = { navigateToTab(navController, Routes.PERSONAL_DASHBOARD) },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = surfaceContainerHigh, selectedIconColor = primaryColor,
                selectedTextColor = primaryColor, unselectedIconColor = secondaryGray, unselectedTextColor = secondaryGray
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Groups, contentDescription = "Workspace") },
            label = { Text("Workspace", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            selected = currentRoute == Routes.TEAM_WORKSPACE,
            onClick = { navigateToTab(navController, Routes.TEAM_WORKSPACE) },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = surfaceContainerHigh, selectedIconColor = primaryColor,
                selectedTextColor = primaryColor, unselectedIconColor = secondaryGray, unselectedTextColor = secondaryGray
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "Roadmap") },
            label = { Text("AI Roadmap", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            selected = currentRoute == Routes.ROADMAP_GENERATOR,
            onClick = { navigateToTab(navController, Routes.ROADMAP_GENERATOR) },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = surfaceContainerHigh, selectedIconColor = primaryColor,
                selectedTextColor = primaryColor, unselectedIconColor = secondaryGray, unselectedTextColor = secondaryGray
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Explore, contentDescription = "Explore") },
            label = { Text("Explore", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            selected = currentRoute == Routes.PUBLIC_JAMS,
            onClick = { navigateToTab(navController, Routes.PUBLIC_JAMS) },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = surfaceContainerHigh, selectedIconColor = primaryColor,
                selectedTextColor = primaryColor, unselectedIconColor = secondaryGray, unselectedTextColor = secondaryGray
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Terminal, contentDescription = "Terminal") },
            label = { Text("Terminal", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            selected = currentRoute == Routes.OFFLINE_TERMINAL,
            onClick = { navigateToTab(navController, Routes.OFFLINE_TERMINAL) },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = surfaceContainerHigh, selectedIconColor = primaryColor,
                selectedTextColor = primaryColor, unselectedIconColor = secondaryGray, unselectedTextColor = secondaryGray
            )
        )
    }
}

private fun navigateToTab(navController: NavHostController, route: String) {
    navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
