package com.chanse.cs492.treasurehunt.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.RectF
import android.graphics.Typeface
import android.location.LocationManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import com.chanse.cs492.treasurehunt.R

// Displays the app home screen and gates play behind location access.
@Composable
fun HomeScreen(onPlay: () -> Unit) {
    // Gets the current Android context.
    val ctx = LocalContext.current
    // Gets the system location manager for GPS checks.
    val locationManager = ctx.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    // Returns true when location permission has been granted.
    fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            ctx,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarse = ContextCompat.checkSelfPermission(
            ctx,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fine || coarse
    }

    // Returns true when location services are enabled on the device.
    fun isLocationEnabled(): Boolean = LocationManagerCompat.isLocationEnabled(locationManager)

    // Controls the GPS enable dialog state.
    var showEnableLocationDialog by remember { mutableStateOf(false) }
    // Controls the permission explanation dialog state.
    var showPermissionExplainer by remember { mutableStateOf(false) }

    // Requests location permission from the system.
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted =
            (result[Manifest.permission.ACCESS_FINE_LOCATION] == true) ||
                    (result[Manifest.permission.ACCESS_COARSE_LOCATION] == true)

        // Keeps the user on the home screen if permission is denied.
        if (!granted) return@rememberLauncherForActivityResult

        // Continues only when GPS is also enabled.
        if (isLocationEnabled()) {
            onPlay()
        } else {
            showEnableLocationDialog = true
        }
    }

    // Prompts the user to enable GPS in system settings.
    if (showEnableLocationDialog) {
        AlertDialog(
            onDismissRequest = { showEnableLocationDialog = false },
            title = { Text("Enable Location") },
            text = { Text("Please enable Location/GPS to continue.") },
            confirmButton = {
                TextButton(onClick = {
                    showEnableLocationDialog = false
                    ctx.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }) {
                    Text("Open Settings")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEnableLocationDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Explains why location permission is needed before requesting it.
    if (showPermissionExplainer) {
        AlertDialog(
            onDismissRequest = { showPermissionExplainer = false },
            title = { Text("Location Permission") },
            text = { Text("Treasure Hunt uses GPS to place and track treasure locations near you.") },
            confirmButton = {
                TextButton(onClick = {
                    showPermissionExplainer = false
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }) {
                    Text("Continue")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionExplainer = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Draws the home screen background, title, and play button.
    Box(modifier = Modifier.fillMaxSize()) {

        // Draws the home background image.
        Image(
            painter = painterResource(id = R.drawable.home_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Draws the curved title at the top of the screen.
        CurvedTitle(
            text = "Treasure Hunt",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 95.dp)
                .fillMaxWidth()
                .height(130.dp)
        )

        // Starts the permission and GPS validation flow.
        Button(
            onClick = {
                // Requests permission first when needed.
                // Prompts for GPS only after permission is available.
                // Navigates only when both permission and GPS are ready.
                if (!hasLocationPermission()) {
                    showPermissionExplainer = true
                } else {
                    if (isLocationEnabled()) {
                        onPlay()
                    } else {
                        showEnableLocationDialog = true
                    }
                }
            },
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFD3A300),
                contentColor = Color(0xFF1B1B1B)
            ),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 105.dp)
                .fillMaxWidth(0.79f)
                .height(57.dp)
        ) {
            Text("Play", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

/**
 * Draws a curved title using text placed along an arc path.
 */
@Composable
private fun CurvedTitle(text: String, modifier: Modifier = Modifier) {
    // Converts sp text size into pixels for canvas drawing.
    val density = LocalDensity.current
    val textSizePx = with(density) { 40.sp.toPx() }

    Canvas(modifier = modifier) {
        // Creates the path used to curve the title text.
        val path = Path()

        // Defines the arc bounds for the curved title.
        val rect = RectF(
            0f,
            size.height * 0.15f,
            size.width,
            size.height * 2.35f
        )

        path.addArc(rect, 200f, 140f)

        // Measures the path so the text can be centered on it.
        val measure = PathMeasure(path, false)
        val textWidth = Paint().apply { textSize = textSizePx }.measureText(text)
        val hOffset = (measure.length - textWidth) / 2f

        // Stores the gold text color in Android color format.
        val gold = Color(0xFFD3A300).toArgb()

        // Draws the black outline behind the title text.
        val strokePaint = Paint().apply {
            isAntiAlias = true
            color = android.graphics.Color.BLACK
            textSize = textSizePx
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            style = Paint.Style.STROKE
            strokeWidth = textSizePx * 0.10f
            strokeJoin = Paint.Join.ROUND
        }

        // Draws the gold fill on top of the outline.
        val fillPaint = Paint().apply {
            isAntiAlias = true
            color = gold
            textSize = textSizePx
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            style = Paint.Style.FILL
        }

        // Renders the title outline and fill along the same arc.
        drawContext.canvas.nativeCanvas.drawTextOnPath(text, path, hOffset, 0f, strokePaint)
        drawContext.canvas.nativeCanvas.drawTextOnPath(text, path, hOffset, 0f, fillPaint)
    }
}