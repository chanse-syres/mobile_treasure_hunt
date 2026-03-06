package com.chanse.cs492.treasurehunt.ui.screens

import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.RectF
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chanse.cs492.treasurehunt.R

@Composable
fun HomeScreen(onPlay: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {

        // Background image
        Image(
            painter = painterResource(id = R.drawable.home_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Curved title (top)
        CurvedTitle(
            text = "Treasure Hunt",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 95.dp)
                .fillMaxWidth()
                .height(130.dp)
        )

        // Play button
        Button(
            onClick = onPlay,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFD3A300), // color
                contentColor = Color(0xFF1B1B1B)
            ),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 105.dp) // vertical movement
                .fillMaxWidth(0.79f)
                .height(57.dp)
        ) {
            Text("Play", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

/**
 * Curved title using nativeCanvas.drawTextOnPath().
 * This version keeps the arc *inside* the Canvas so it doesn't disappear.
 */
@Composable
private fun CurvedTitle(text: String, modifier: Modifier = Modifier) {
    val density = LocalDensity.current
    val textSizePx = with(density) { 40.sp.toPx() }

    Canvas(modifier = modifier) {
        val path = Path()

        // Pushes arc DOWN inside this canvas
        val rect = RectF(
            0f,
            size.height * 0.15f,
            size.width,
            size.height * 2.35f
        )

        // Arches across the top
        path.addArc(rect, 200f, 140f)

        // Centers the text along the arc
        val measure = PathMeasure(path, false)
        val textWidth = Paint().apply { textSize = textSizePx }.measureText(text)
        val hOffset = (measure.length - textWidth) / 2f

        val gold = Color(0xFFD3A300).toArgb() //Same as button

        // For BLACK OUTLINE
        val strokePaint = Paint().apply {
            isAntiAlias = true
            color = android.graphics.Color.BLACK
            textSize = textSizePx
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            style = Paint.Style.STROKE
            strokeWidth = textSizePx * 0.10f  // outline thickness
            strokeJoin = Paint.Join.ROUND
        }

        // 2) GOLD FILL
        val fillPaint = Paint().apply {
            isAntiAlias = true
            color = gold
            textSize = textSizePx
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            style = Paint.Style.FILL
        }

        // Draw stroke first, then fill on top
        drawContext.canvas.nativeCanvas.drawTextOnPath(text, path, hOffset, 0f, strokePaint)
        drawContext.canvas.nativeCanvas.drawTextOnPath(text, path, hOffset, 0f, fillPaint)
    }
}