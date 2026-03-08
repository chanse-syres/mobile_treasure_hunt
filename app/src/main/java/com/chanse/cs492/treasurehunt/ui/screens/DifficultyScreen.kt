package com.chanse.cs492.treasurehunt.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chanse.cs492.treasurehunt.R
import com.chanse.cs492.treasurehunt.data.TreasureHuntContentLoader

@Composable
fun DifficultyScreen(onEasySelected: () -> Unit) {
    val ctx = LocalContext.current
    val content = remember(ctx) { TreasureHuntContentLoader.load(ctx) }
    Scaffold(contentWindowInsets = WindowInsets(0, 0, 0, 0)) { padding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Image(
                painter = painterResource(id = R.drawable.difficulty_bg),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )

            Button(
                onClick = { /* TODO hard */ },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = maxHeight * 0.23f)
                    .fillMaxWidth(0.76f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD3D3D3),
                    contentColor = Color.Black
                )
            ) { Text(content.difficulty.hardLabel) }

            Button(
                onClick = { /* TODO medium */ },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = maxHeight * 0.46f)
                    .fillMaxWidth(0.76f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD3D3D3),
                    contentColor = Color.Black
                )
            ) { Text(content.difficulty.mediumLabel) }

            Button(
                onClick = onEasySelected,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = maxHeight * 0.66f)
                    .fillMaxWidth(0.76f)
            ) { Text(content.difficulty.easyLabel) }

            Text(
                text = content.difficulty.prompt,
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFFFD700),
                style = TextStyle(drawStyle = Stroke(width = 10f)),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 38.dp)
            )

            Text(
                text = content.difficulty.prompt,
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 34.dp)
            )
        }
    }
}