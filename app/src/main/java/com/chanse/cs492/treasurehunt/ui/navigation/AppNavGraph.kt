package com.chanse.cs492.treasurehunt.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chanse.cs492.treasurehunt.ui.screens.DifficultyScreen
import com.chanse.cs492.treasurehunt.ui.screens.HomeScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                onPlay = {
                    // HomeScreen will only call this AFTER permissions, and if GPS is good
                    navController.navigate(Routes.DIFFICULTY)
                }
            )
        }
        composable(Routes.DIFFICULTY) {
            DifficultyScreen()
        }
    }
}