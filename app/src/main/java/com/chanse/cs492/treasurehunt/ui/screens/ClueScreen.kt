package com.chanse.cs492.treasurehunt.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.chanse.cs492.treasurehunt.viewmodel.TreasureViewModel

@Composable
fun ClueScreen(
    vm: TreasureViewModel,
    onQuit: () -> Unit
) {
    val uiState by vm.uiState.collectAsState()
    val clue = uiState.currentClue

    if (clue == null) {
        Scaffold { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text("No clue loaded.")
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = onQuit) {
                    Text("Back Home")
                }
            }
        }
        return
    }

    if (uiState.howToPlayVisible) {
        AlertDialog(
            onDismissRequest = { /* force user to use Okay */ },
            title = {
                Text("How to play:")
            },
            text = {
                Text("Read the clue, use Hint if needed, then go to the location and press Found It when you think you have solved it.")
            },
            confirmButton = {
                TextButton(onClick = { vm.acknowledgeHowToPlay() }) {
                    Text("Okay")
                }
            }
        )
    }

    if (uiState.hintVisible) {
        AlertDialog(
            onDismissRequest = { vm.showHint(false) },
            title = {
                Text("Hint")
            },
            text = {
                Text(clue.hintText)
            },
            confirmButton = {
                TextButton(onClick = { vm.showHint(false) }) {
                    Text("Close")
                }
            }
        )
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = clue.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = clue.clueText,
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = "Elapsed Time: ${formatElapsed(uiState.elapsedSeconds)}",
                style = MaterialTheme.typography.titleMedium
            )

            Button(
                onClick = { vm.showHint(true) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Hint")
            }

            Button(
                onClick = { /* next step: location and Found It validation */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Found It!")
            }

            OutlinedButton(
                onClick = onQuit,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Quit")
            }
        }
    }
}

private fun formatElapsed(totalSeconds: Long): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}