package com.mocare.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mocare.app.ui.screen.addmotor.AddMotorScreen
import com.mocare.app.ui.screen.fuelinput.FuelInputScreen
import com.mocare.app.ui.screen.home.HomeScreen
import com.mocare.app.ui.screen.summary.SummaryScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onAddMotorClick = { navController.navigate("addMotor") },
                onFuelInputClick = { motorId -> navController.navigate("fuelInput/$motorId") },
                onSummaryClick = { motorId -> navController.navigate("summary/$motorId") }
            )
        }
        composable("addMotor") {
            AddMotorScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("fuelInput/{motorId}") { backStackEntry ->
            val motorId = backStackEntry.arguments?.getString("motorId")?.toLongOrNull() ?: return@composable
            FuelInputScreen(
                motorId = motorId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("summary/{motorId}") { backStackEntry ->
            val motorId = backStackEntry.arguments?.getString("motorId")?.toLongOrNull() ?: return@composable
            SummaryScreen(
                motorId = motorId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
