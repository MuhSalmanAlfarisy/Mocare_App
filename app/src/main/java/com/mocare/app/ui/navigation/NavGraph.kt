package com.mocare.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mocare.app.ui.screen.home.HomeScreen
import com.mocare.app.ui.screen.profile.ProfileScreen

import com.mocare.app.ui.screen.history.HistoryScreen

object Routes {
    const val HOME = "home"
    const val PROFILE = "profile"
    const val HISTORY = "history"
    const val STATS = "stats"
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onProfileClick = { navController.navigate(Routes.PROFILE) },
                onHistoryClick = { navController.navigate(Routes.HISTORY) {
                    popUpTo(Routes.HOME) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }},
                onStatsClick = { navController.navigate(Routes.STATS) {
                    popUpTo(Routes.HOME) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }}
            )
        }
        composable(Routes.HISTORY) {
            HistoryScreen(
                onNavigateHome = { navController.navigate(Routes.HOME) {
                    popUpTo(Routes.HOME) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }},
                onNavigateStats = { navController.navigate(Routes.STATS) {
                    popUpTo(Routes.HOME) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }}
            )
        }
        composable(Routes.STATS) {
            com.mocare.app.ui.screen.StatsScreen(
                onNavigateHome = { navController.navigate(Routes.HOME) {
                    popUpTo(Routes.HOME) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }},
                onNavigateHistory = { navController.navigate(Routes.HISTORY) {
                    popUpTo(Routes.HOME) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }}
            )
        }
        composable(Routes.PROFILE) {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
