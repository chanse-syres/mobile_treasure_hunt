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

@Composable
fun CompletedScreen(
    vm: TreasureViewModel,
    onHome: () -> Unit,
    onStats: () -> Unit,
    onLeaderboard: () -> Unit
) {
    val uiState by vm.uiState.collectAsState()
    val hunt = uiState.selectedHunt

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Treasure Hunt Complete!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = hunt?.finalMessage ?: "Congratulations!",
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = "Final Time: ${formatElapsed(uiState.elapsedSeconds)}",
                style = MaterialTheme.typography.titleMedium
            )

            Button(
                onClick = onHome,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Home")
            }

            OutlinedButton(
                onClick = onStats,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Stats")
            }

            OutlinedButton(
                onClick = onLeaderboard,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Leaderboard")
            }
        }
    }
}

private fun formatElapsed(totalSeconds: Long): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}