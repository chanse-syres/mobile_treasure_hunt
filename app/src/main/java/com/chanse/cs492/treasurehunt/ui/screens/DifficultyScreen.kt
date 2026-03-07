package com.chanse.cs492.treasurehunt.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DifficultyScreen() {
    Scaffold { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Select Difficulty", style = MaterialTheme.typography.headlineSmall)
            Button(onClick = { /* TODO easy */ }, modifier = Modifier.fillMaxWidth()) { Text("Easy") }
            OutlinedButton(onClick = { /* TODO medium */ }, modifier = Modifier.fillMaxWidth()) { Text("Medium") }
            OutlinedButton(onClick = { /* TODO hard */ }, modifier = Modifier.fillMaxWidth()) { Text("Hard") }
        }
    }
}