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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chanse.cs492.treasurehunt.R
import com.chanse.cs492.treasurehunt.data.model.Hunt
import com.chanse.cs492.treasurehunt.viewmodel.TreasureViewModel

@Composable
fun DifficultyScreen(
    vm: TreasureViewModel,
    onSelectDifficulty: (String) -> Unit
) {
    val uiState by vm.uiState.collectAsState()

    val hard = uiState.hunts.firstOrNull { it.id == "hard" }
    val medium = uiState.hunts.firstOrNull { it.id == "medium" }
    val easy = uiState.hunts.firstOrNull { it.id == "easy" }

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

            DifficultyButton(
                hunt = hard,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = maxHeight * 0.23f)
                    .fillMaxWidth(0.76f),
                onSelectDifficulty = onSelectDifficulty
            )

            DifficultyButton(
                hunt = medium,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = maxHeight * 0.46f)
                    .fillMaxWidth(0.76f),
                onSelectDifficulty = onSelectDifficulty
            )

            DifficultyButton(
                hunt = easy,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = maxHeight * 0.66f)
                    .fillMaxWidth(0.76f),
                onSelectDifficulty = onSelectDifficulty
            )

            Text(
                text = "Select difficulty above",
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFFFD700),
                style = TextStyle(drawStyle = Stroke(width = 10f)),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 38.dp)
            )

            Text(
                text = "Select difficulty above",
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

@Composable
private fun DifficultyButton(
    hunt: Hunt?,
    modifier: Modifier,
    onSelectDifficulty: (String) -> Unit
) {
    val enabled = hunt?.enabled == true

    Button(
        onClick = {
            if (hunt != null && enabled) {
                onSelectDifficulty(hunt.id)
            }
        },
        modifier = modifier,
        enabled = hunt != null && enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (enabled) Color(0xFF3B82F6) else Color(0xFFD3D3D3),
            contentColor = Color.Black
        )
    ) {
        Text(hunt?.title ?: "Loading...")
    }
}