package com.chanse.cs492.treasurehunt.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chanse.cs492.treasurehunt.ui.screens.ClueScreen
import com.chanse.cs492.treasurehunt.ui.screens.DifficultyScreen
import com.chanse.cs492.treasurehunt.ui.screens.HomeScreen
import com.chanse.cs492.treasurehunt.viewmodel.TreasureViewModel

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val treasureViewModel: TreasureViewModel = viewModel()

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                onPlay = {
                    navController.navigate(Routes.DIFFICULTY)
                }
            )
        }

        composable(Routes.DIFFICULTY) {
            DifficultyScreen(
                vm = treasureViewModel,
                onSelectDifficulty = { huntId ->
                    val ready = treasureViewModel.selectHunt(huntId)
                    if (ready) {
                        navController.navigate(Routes.CLUE)
                    }
                }
            )
        }

        composable(Routes.CLUE) {
            ClueScreen(
                vm = treasureViewModel,
                onQuit = {
                    treasureViewModel.resetHunt()
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}