package com.example.cockroach.ui.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cockroach.data.model.Beetle
import com.example.cockroach.data.model.BeetleTypes
import com.example.cockroach.data.model.GameSettings
import com.example.cockroach.data.model.GameState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameViewModel : ViewModel() {
    var gameState by mutableStateOf(GameState())
        private set

    private var gameJob: Job? = null
    private var screenSize = IntSize(800, 600)

    fun startGame(settings: GameSettings) {
        gameState = GameState(
            gameSettings = settings,
            timeRemaining = settings.roundDuration.toFloat(),
            isGameRunning = true
        )

        gameJob?.cancel()
        gameJob = viewModelScope.launch {
            while (isActive && gameState.isGameRunning && gameState.timeRemaining > 0) {
                val deltaTime = 0.016f // 60 FPS
                updateGame(deltaTime)
                delay(16)
            }
            endGame()
        }
    }

    fun stopGame() {
        gameJob?.cancel()
        gameState = gameState.copy(isGameRunning = false)
    }

    fun setScreenSize(size: IntSize) {
        screenSize = size
    }

    fun onScreenClick(position: Offset) {
        if (!gameState.isGameRunning) return

        val hitBeetle = gameState.beetles.find { it.isClicked(position) && it.isAlive }

        if (hitBeetle != null) {
            gameState = gameState.copy(
                beetles = gameState.beetles.map {
                    if (it.id == hitBeetle.id) it.copy(isAlive = false) else it
                },
                score = gameState.score + hitBeetle.type.points,
                hitCount = gameState.hitCount + 1
            )
        } else {
            gameState = gameState.copy(missCount = gameState.missCount + 1)
        }
    }

    private fun updateGame(deltaTime: Float) {
        val currentTime = System.currentTimeMillis()

        // Обновление позиций жуков
        val updatedBeetles = gameState.beetles.map {
            it.updatePosition(deltaTime, screenSize)
        }.filter { it.isAlive }

        // Спавн новых жуков
        val needNewBeetle = updatedBeetles.size < gameState.gameSettings.maxCockroaches &&
                           currentTime - gameState.lastBeetleSpawn > 2000 / gameState.gameSettings.gameSpeed

        val beetles = if (needNewBeetle) {
            updatedBeetles + spawnBeetle()
        } else {
            updatedBeetles
        }

        gameState = gameState.copy(
            beetles = beetles,
            timeRemaining = gameState.timeRemaining - deltaTime,
            lastBeetleSpawn = if (needNewBeetle) currentTime else gameState.lastBeetleSpawn
        )
    }

    private fun spawnBeetle(): Beetle {
        val type = BeetleTypes.ALL_TYPES.random()
        val side = Random.nextInt(4)
        val position = when (side) {
            0 -> Offset(-64f, Random.nextFloat() * screenSize.height) // Слева
            1 -> Offset(screenSize.width.toFloat(), Random.nextFloat() * screenSize.height) // Справа
            2 -> Offset(Random.nextFloat() * screenSize.width, -80f) // Сверху
            else -> Offset(Random.nextFloat() * screenSize.width, screenSize.height.toFloat()) // Снизу
        }

        val direction = when (side) {
            0 -> Offset(1f, Random.nextFloat() * 2f - 1f) // Направо
            1 -> Offset(-1f, Random.nextFloat() * 2f - 1f) // Налево
            2 -> Offset(Random.nextFloat() * 2f - 1f, 1f) // Вниз
            else -> Offset(Random.nextFloat() * 2f - 1f, -1f) // Вверх
        }.let {
            val length = kotlin.math.sqrt(it.x * it.x + it.y * it.y)
            Offset(it.x / length, it.y / length)
        }

        return Beetle(
            type = type,
            position = position,
            direction = direction
        )
    }

    private fun endGame() {
        gameState = gameState.copy(isGameRunning = false)
    }
}