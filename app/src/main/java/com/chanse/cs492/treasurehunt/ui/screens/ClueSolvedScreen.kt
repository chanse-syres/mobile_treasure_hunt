package com.chanse.cs492.treasurehunt.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.chanse.cs492.treasurehunt.viewmodel.TreasureViewModel

// Displays the transition screen after a clue has been solved.
@Composable
fun ClueSolvedScreen(
    vm: TreasureViewModel,
    onContinue: () -> Unit,
    onSettings: () -> Unit
) {
    // Collects the latest UI state from the view model.
    val uiState by vm.uiState.collectAsState()
    // Gets the current clue to display solved information.
    val clue = uiState.currentClue

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Clue Solved!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Elapsed Time: ${formatElapsed(uiState.elapsedSeconds)}",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = clue?.solvedInfo ?: "You solved the clue.",
                style = MaterialTheme.typography.bodyLarge
            )

            // Moves the player to the next clue.
            Button(
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continue")
            }

            // Opens the settings action when implemented.
            OutlinedButton(
                onClick = onSettings,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Settings")
            }
        }
    }
}

// Formats elapsed time as mm:ss.
private fun formatElapsed(totalSeconds: Long): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}