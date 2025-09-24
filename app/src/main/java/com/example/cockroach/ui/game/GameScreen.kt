package com.example.cockroach.ui.game

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cockroach.data.model.GameSettings

@SuppressLint("ReturnFromAwaitPointerEventScope")
@Composable
fun GameScreen(
    gameSettings: GameSettings = GameSettings(),
    viewModel: GameViewModel = viewModel()
) {
    val gameState = viewModel.gameState
    val density = LocalDensity.current

    LaunchedEffect(gameSettings) {
        if (!gameState.isGameRunning && gameState.timeRemaining == 0f) {
            viewModel.startGame(gameSettings)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2E7D32))
    ) {
        // Верхняя панель с информацией
        GameInfoPanel(
            score = gameState.score,
            timeRemaining = gameState.timeRemaining,
            accuracy = gameState.accuracy,
            onRestart = { viewModel.startGame(gameSettings) }
        )

        // Игровое поле
        Box(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { size ->
                    viewModel.setScreenSize(size)
                }
                .pointerInput(Unit) {
                    while (true) {
                        val offset = awaitPointerEventScope {
                            awaitFirstDown().position
                        }
                        viewModel.onScreenClick(offset)
                    }
                }
        ) {
            // Отрисовка жуков
            gameState.beetles.forEach { beetle ->
                if (beetle.isAlive) {
                    Box(
                        modifier = Modifier
                            .offset(
                                x = with(density) { beetle.position.x.toDp() },
                                y = with(density) { beetle.position.y.toDp() }
                            )
                    ) {
                        BeetleSprite(
                            beetle = beetle,
                            onClick = {
                                viewModel.onScreenClick(
                                    Offset(
                                        beetle.position.x + 32f,
                                        beetle.position.y + 40f
                                    )
                                )
                            }
                        )
                    }
                }
            }

            // Экран завершения игры
            if (!gameState.isGameRunning && gameState.timeRemaining <= 0) {
                GameOverScreen(
                    score = gameState.score,
                    accuracy = gameState.accuracy,
                    onRestart = { viewModel.startGame(gameSettings) }
                )
            }
        }
    }
}

@Composable
private fun GameInfoPanel(
    score: Int,
    timeRemaining: Float,
    accuracy: Float,
    onRestart: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF1B5E20),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                InfoItem("Счет", score.toString())
                InfoItem("Время", "${timeRemaining.toInt()}с")
                InfoItem("Точность", "${(accuracy * 100).toInt()}%")
            }

            Button(
                onClick = onRestart,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                )
            ) {
                Text("Начать игру")
            }
        }
    }
}

@Composable
private fun InfoItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 12.sp
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun GameOverScreen(
    score: Int,
    accuracy: Float,
    onRestart: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
            .clickable { /* Prevent clicks through */ },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(32.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Игра окончена!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Финальный счет: $score")
                    Text("Точность: ${(accuracy * 100).toInt()}%")
                }

                Button(onClick = onRestart) {
                    Text("Играть снова")
                }
            }
        }
    }
}