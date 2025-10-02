package com.example.cockroach.ui.game

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cockroach.R
import com.example.cockroach.data.model.GameSettings
import com.example.cockroach.ui.theme.Green40

@SuppressLint("ReturnFromAwaitPointerEventScope")
@Composable
fun GameScreen(
    gameSettings: GameSettings = GameSettings(),
    viewModel: GameViewModel = viewModel()
) {
    val gameState = viewModel.gameState
    val density = LocalDensity.current

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Верхняя панель с информацией
            GameInfoPanel(
                score = gameState.score,
                timeRemaining = gameState.timeRemaining,
                accuracy = gameState.accuracy,
                isGameRunning = gameState.isGameRunning,
                isPaused = gameState.isPaused,
                onRestart = { viewModel.startGame(gameSettings) },
                onTogglePause = { viewModel.togglePause() }
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

                // Экран паузы
                if (gameState.isPaused) {
                    PauseScreen(onResume = { viewModel.resumeGame() })
                }

                // Экран завершения игры (показываем только если игра была запущена и закончилась)
                if (!gameState.isGameRunning && gameState.timeRemaining <= 0 && gameState.totalClicks > 0) {
                    GameOverScreen(
                        score = gameState.score,
                        accuracy = gameState.accuracy,
                        onRestart = { viewModel.startGame(gameSettings) }
                    )
                }
            }
        }
    }

}

@Composable
private fun GameInfoPanel(
    score: Int,
    timeRemaining: Float,
    accuracy: Float,
    isGameRunning: Boolean,
    isPaused: Boolean,
    onRestart: () -> Unit,
    onTogglePause: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Green40,
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

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (isGameRunning) {
                    Button(
                        onClick = onTogglePause,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isPaused) Color(0xFF4CAF50) else Color(0xFFFF9800)
                        )
                    ) {
//                        Text(if (isPaused) "Продолжить" else "Пауза")
                        Text(if (isPaused) "▶\uFE0F" else "⏸\uFE0F")
                    }
                }

                Button(
                    onClick = onRestart,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    )
                ) {
//                    Text(if (isGameRunning) "Рестарт" else "Начать игру")
                    Text(if (isGameRunning) "\uD83D\uDD04" else "\uD83C\uDFAE")
                }
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

@Composable
private fun PauseScreen(onResume: () -> Unit) {
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
                    text = "Игра на паузе",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Button(onClick = onResume) {
                    Text("Продолжить")
                }
            }
        }
    }
}