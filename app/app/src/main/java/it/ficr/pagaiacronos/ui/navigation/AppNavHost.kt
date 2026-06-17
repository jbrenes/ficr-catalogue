package it.ficr.pagaiacronos.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import it.ficr.pagaiacronos.R
import it.ficr.pagaiacronos.ui.about.AboutScreen
import it.ficr.pagaiacronos.ui.athlete.AthleteProfileScreen
import it.ficr.pagaiacronos.ui.results.ResultsScreen
import it.ficr.pagaiacronos.ui.settings.SettingsScreen
import it.ficr.pagaiacronos.ui.sync.SyncScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDest = navBackStackEntry?.destination

    val tabs = listOf(
        Triple(Screen.Results, Icons.Default.List, R.string.tab_results),
        Triple(Screen.Sync, Icons.Default.Sync, R.string.tab_sync),
        Triple(Screen.Settings, Icons.Default.Settings, R.string.tab_settings),
        Triple(Screen.About, Icons.Default.Info, R.string.tab_about)
    )

    val showBottomBar = tabs.any { (screen, _, _) ->
        currentDest?.hierarchy?.any { it.route == screen.route } == true
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    tabs.forEach { (screen, icon, labelRes) ->
                        val selected = currentDest?.hierarchy
                            ?.any { it.route == screen.route } == true
                        NavigationBarItem(
                            icon = { Icon(icon, contentDescription = null) },
                            label = { Text(stringResource(labelRes)) },
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Results.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Results.route) {
                ResultsScreen(onAthleteClick = { id ->
                    navController.navigate(Screen.AthleteProfile.withId(id))
                })
            }
            composable(Screen.Sync.route) { SyncScreen() }
            composable(Screen.Settings.route) { SettingsScreen() }
            composable(Screen.About.route) { AboutScreen() }
            composable(
                route = Screen.AthleteProfile.route,
                arguments = listOf(navArgument("athleteId") { type = NavType.LongType })
            ) { backStack ->
                val id = backStack.arguments?.getLong("athleteId") ?: return@composable
                AthleteProfileScreen(
                    athleteId = id,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
