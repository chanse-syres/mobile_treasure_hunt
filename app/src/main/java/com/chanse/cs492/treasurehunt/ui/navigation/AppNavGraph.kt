package com.chanse.cs492.treasurehunt.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chanse.cs492.treasurehunt.ui.screens.ClueScreen
import com.chanse.cs492.treasurehunt.ui.screens.ClueSolvedScreen
import com.chanse.cs492.treasurehunt.ui.screens.CompletedScreen
import com.chanse.cs492.treasurehunt.ui.screens.DifficultyScreen
import com.chanse.cs492.treasurehunt.ui.screens.HomeScreen
import com.chanse.cs492.treasurehunt.viewmodel.TreasureViewModel

// Defines the app navigation graph and connects each route to its screen.
@Composable
fun AppNavGraph() {
    // Creates the navigation controller used to move between screens.
    val navController = rememberNavController()
    // Shares one view model instance across the navigation flow.
    val treasureViewModel: TreasureViewModel = viewModel()

    // Starts navigation at the home screen.
    NavHost(navController = navController, startDestination = Routes.HOME) {
        // Displays the home screen and routes the player to difficulty selection.
        composable(Routes.HOME) {
            HomeScreen(
                onPlay = {
                    navController.navigate(Routes.DIFFICULTY)
                }
            )
        }

        // Displays hunt choices and starts the selected hunt when ready.
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

        // Shows the active clue screen and handles solve, complete, and quit actions.
        composable(Routes.CLUE) {
            ClueScreen(
                vm = treasureViewModel,
                onClueSolved = {
                    treasureViewModel.pauseTimer()
                    navController.navigate(Routes.CLUE_SOLVED)
                },
                onHuntCompleted = {
                    treasureViewModel.completeHunt()
                    navController.navigate(Routes.COMPLETED)
                },
                onQuit = {
                    treasureViewModel.resetHunt()
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }

        // Shows clue completion feedback before moving to the next clue.
        composable(Routes.CLUE_SOLVED) {
            ClueSolvedScreen(
                vm = treasureViewModel,
                onContinue = {
                    treasureViewModel.goToNextClue()
                    treasureViewModel.startTimer()
                    navController.navigate(Routes.CLUE) {
                        popUpTo(Routes.CLUE) { inclusive = true }
                    }
                },
                onSettings = {
                    // Placeholder for future settings navigation.
                    // scaffold placeholder for now
                }
            )
        }

        // Shows the final completion screen and supports returning to the flow.
        composable(Routes.COMPLETED) {
            CompletedScreen(
                vm = treasureViewModel,
                onHome = {
                    treasureViewModel.resetHunt()
                    navController.navigate(Routes.DIFFICULTY) {
                        popUpTo(Routes.DIFFICULTY) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onStats = {
                    // Placeholder for future statistics navigation.
                    // scaffold placeholder for now
                },
                onLeaderboard = {
                    // Placeholder for future leaderboard navigation.
                    // scaffold placeholder for now
                }
            )
        }
    }
}