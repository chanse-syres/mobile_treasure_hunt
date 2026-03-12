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

private const val DEMO_FORCE_ADVANCE = true
// Displays the current clue and verifies whether the player reached the correct location.
@Composable
fun ClueScreen(
    vm: TreasureViewModel,
    onClueSolved: () -> Unit,
    onHuntCompleted: () -> Unit,
    onQuit: () -> Unit
) {
    // Observes the latest screen state from the view model.
    val uiState by vm.uiState.collectAsState()
    // Stores the currently active clue, if one exists.
    val clue = uiState.currentClue
    // Provides access to the current Android context.
    val context = LocalContext.current

    // Gets the fused location client used for location checks.
    val fusedLocationClient = remember(context) {
        LocationServices.getFusedLocationProviderClient(context)
    }

    // Gets the system location manager for provider status checks.
    val locationManager = remember(context) {
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    // Controls the dialog for disabled location services.
    val showEnableLocationDialog = remember { mutableStateOf(false) }
    // Controls the dialog for unavailable location results.
    val showLocationUnavailableDialog = remember { mutableStateOf(false) }
    // Tracks whether a location validation request is in progress.
    val checkingLocation = remember { mutableStateOf(false) }

    // Returns true when either fine or coarse location permission is granted.
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

    // Returns true when device location services are enabled.
    fun isLocationEnabled(): Boolean = LocationManagerCompat.isLocationEnabled(locationManager)

    // Checks the user's current position against the active clue target.
    fun validateCurrentLocation() {
        if (DEMO_FORCE_ADVANCE) {
            if (vm.isOnFinalClue()) {
                onHuntCompleted()
            } else {
                onClueSolved()
            }
            return
        }
        if (!hasLocationPermission()) {
            showLocationUnavailableDialog.value = true
            return
        }

        if (!isLocationEnabled()) {
            showEnableLocationDialog.value = true
            return
        }

        checkingLocation.value = true

        // Creates a cancellation token for the one-time location request.
        val cancellationTokenSource = CancellationTokenSource()

        fusedLocationClient
            .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token)
            .addOnSuccessListener { location ->
                checkingLocation.value = false

                if (location == null) {
                    showLocationUnavailableDialog.value = true
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

    // Shows a fallback screen when no clue has been loaded.
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

    // Shows the how-to-play dialog the first time it is needed.
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

    // Shows the clue hint dialog when requested by the user.
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

    // Shows feedback when the player is not close enough to the target location.
    if (uiState.wrongLocationVisible) {
        AlertDialog(
            onDismissRequest = { vm.dismissWrongLocation() },
            title = { Text("Not there yet") },
            text = {
                Text("You are not close enough to the correct location yet. Move closer and try again.")
            },
            confirmButton = {
                TextButton(onClick = { vm.dismissWrongLocation() }) {
                    Text("OK")
                }
            }
        )
    }

    // Prompts the user to enable location services when GPS is off.
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

    // Informs the user when the current location could not be retrieved.
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

    // Displays the main clue content and action buttons.
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

            // Opens the hint dialog for the current clue.
            Button(
                onClick = { vm.showHint(true) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Hint")
            }

            // Validates the player's location against the clue target.
            Button(
                onClick = { validateCurrentLocation() },
                enabled = !checkingLocation.value,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (checkingLocation.value) "Checking location..." else "Found It!")
            }

            // Reserves space for a future settings action.
            OutlinedButton(
                onClick = { /* scaffold only for now */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Settings")
            }

            // Exits the current hunt and returns to the previous flow.
            OutlinedButton(
                onClick = onQuit,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Quit")
            }
        }
    }
}

// Formats elapsed time as minutes and seconds.
private fun formatElapsed(totalSeconds: Long): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}