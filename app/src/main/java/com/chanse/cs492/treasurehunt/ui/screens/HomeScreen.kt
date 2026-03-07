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

@Composable
fun HomeScreen(onPlay: () -> Unit) {
    val ctx = LocalContext.current
    val locationManager = ctx.getSystemService(Context.LOCATION_SERVICE) as LocationManager

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

    fun isLocationEnabled(): Boolean = LocationManagerCompat.isLocationEnabled(locationManager)

    var showEnableLocationDialog by remember { mutableStateOf(false) }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted =
            (result[Manifest.permission.ACCESS_FINE_LOCATION] == true) ||
                    (result[Manifest.permission.ACCESS_COARSE_LOCATION] == true)

        // If user denies, it stays on the home screen.
        if (!granted) return@rememberLauncherForActivityResult

        // If user accepts, navigation to the difficulty screen happens.
        if (isLocationEnabled()) {
            onPlay()
        } else {
            showEnableLocationDialog = true
        }
    }

    // Dialog for enabling GPS/Location in device settings
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

    Box(modifier = Modifier.fillMaxSize()) {

        // Background images
        Image(
            painter = painterResource(id = R.drawable.home_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Curved titles for the top of the app
        CurvedTitle(
            text = "Treasure Hunt",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 95.dp)
                .fillMaxWidth()
                .height(130.dp)
        )

        // Play button that is gated by permissions and GPS enabled
        Button(
            onClick = {
                // Step E flow:
                // 1. If no permission, request it
                // 2. If permission but GPS off, prompts to enable
                // 3. If both good are true, navigate
                if (!hasLocationPermission()) {
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
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
 * Curved title using nativeCanvas.drawTextOnPath().
 * Includes black outline and a gold fill (same as button).
 */
@Composable
private fun CurvedTitle(text: String, modifier: Modifier = Modifier) {
    val density = LocalDensity.current
    val textSizePx = with(density) { 40.sp.toPx() }

    Canvas(modifier = modifier) {
        val path = Path()

        val rect = RectF(
            0f,
            size.height * 0.15f,
            size.width,
            size.height * 2.35f
        )

        path.addArc(rect, 200f, 140f)

        val measure = PathMeasure(path, false)
        val textWidth = Paint().apply { textSize = textSizePx }.measureText(text)
        val hOffset = (measure.length - textWidth) / 2f

        val gold = Color(0xFFD3A300).toArgb()

        val strokePaint = Paint().apply {
            isAntiAlias = true
            color = android.graphics.Color.BLACK
            textSize = textSizePx
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            style = Paint.Style.STROKE
            strokeWidth = textSizePx * 0.10f
            strokeJoin = Paint.Join.ROUND
        }

        val fillPaint = Paint().apply {
            isAntiAlias = true
            color = gold
            textSize = textSizePx
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            style = Paint.Style.FILL
        }

        drawContext.canvas.nativeCanvas.drawTextOnPath(text, path, hOffset, 0f, strokePaint)
        drawContext.canvas.nativeCanvas.drawTextOnPath(text, path, hOffset, 0f, fillPaint)
    }
}