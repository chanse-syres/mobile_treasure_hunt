package com.chanse.cs492.treasurehunt.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import com.chanse.cs492.treasurehunt.viewmodel.TreasureViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

@Composable
fun ClueScreen(
    vm: TreasureViewModel,
    onClueSolved: () -> Unit,
    onHuntCompleted: () -> Unit,
    onQuit: () -> Unit
) {
    val uiState by vm.uiState.collectAsState()
    val clue = uiState.currentClue
    val context = LocalContext.current

    val fusedLocationClient = remember(context) {
        LocationServices.getFusedLocationProviderClient(context)
    }

    val locationManager = remember(context) {
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    val showEnableLocationDialog = remember { mutableStateOf(false) }
    val showLocationUnavailableDialog = remember { mutableStateOf(false) }
    val checkingLocation = remember { mutableStateOf(false) }

    fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarse = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fine || coarse
    }

    fun isLocationEnabled(): Boolean = LocationManagerCompat.isLocationEnabled(locationManager)

    fun validateCurrentLocation() {
        if (!hasLocationPermission()) {
            showLocationUnavailableDialog.value = true
            return
        }

        if (!isLocationEnabled()) {
            showEnableLocationDialog.value = true
            return
        }

        checkingLocation.value = true

        val cancellationTokenSource = CancellationTokenSource()

        fusedLocationClient
            .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token)
            .addOnSuccessListener { location ->
                checkingLocation.value = false

                if (location == null) {
                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { lastLocation ->
                            if (lastLocation == null) {
                                showLocationUnavailableDialog.value = true
                                return@addOnSuccessListener
                            }

                            val matched = vm.verifyLocation(lastLocation.latitude, lastLocation.longitude)

                            if (matched) {
                                if (vm.isOnFinalClue()) {
                                    onHuntCompleted()
                                } else {
                                    onClueSolved()
                                }
                            }
                        }
                        .addOnFailureListener {
                            showLocationUnavailableDialog.value = true
                        }
                    return@addOnSuccessListener
                }

                val matched = vm.verifyLocation(location.latitude, location.longitude)

                if (matched) {
                    if (vm.isOnFinalClue()) {
                        onHuntCompleted()
                    } else {
                        onClueSolved()
                    }
                }
            }
            .addOnFailureListener {
                checkingLocation.value = false
                showLocationUnavailableDialog.value = true
            }
    }

    if (clue == null) {
        Scaffold { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text("No clue loaded..")
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
            onDismissRequest = { },
            title = { Text("How to play:") },
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
            title = { Text("Hint") },
            text = { Text(clue.hintText) },
            confirmButton = {
                TextButton(onClick = { vm.showHint(false) }) {
                    Text("Close")
                }
            }
        )
    }

    if (uiState.wrongLocationVisible) {
        AlertDialog(
            onDismissRequest = { vm.dismissWrongLocation() },
            title = { Text("Not there yet") },
            text = {
                Text(
                    uiState.lastDistanceMeters
                        ?.let { "You are about ${it.toInt()} meters away. Move closer and try again." }
                        ?: "You are not close enough to the correct location yet. Move closer and try again."
                )
            },
            confirmButton = {
                TextButton(onClick = { vm.dismissWrongLocation() }) {
                    Text("OK")
                }
            }
        )
    }

    if (showEnableLocationDialog.value) {
        AlertDialog(
            onDismissRequest = { showEnableLocationDialog.value = false },
            title = { Text("Enable Location") },
            text = { Text("Please enable Location/GPS to verify the clue.") },
            confirmButton = {
                TextButton(onClick = {
                    showEnableLocationDialog.value = false
                    context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }) {
                    Text("Open Settings")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEnableLocationDialog.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showLocationUnavailableDialog.value) {
        AlertDialog(
            onDismissRequest = { showLocationUnavailableDialog.value = false },
            title = { Text("Location unavailable") },
            text = {
                Text("Your current location could not be retrieved. Make sure GPS is enabled and try again.")
            },
            confirmButton = {
                TextButton(onClick = { showLocationUnavailableDialog.value = false }) {
                    Text("OK")
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
                onClick = { validateCurrentLocation() },
                enabled = !checkingLocation.value,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (checkingLocation.value) "Checking location..." else "Found It!")
            }

            OutlinedButton(
                onClick = { /* scaffold only for now */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Settings")
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