package com.example.cockroach.ui.game

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cockroach.data.database.AppDatabase
import com.example.cockroach.data.database.GameRecordEntity
import com.example.cockroach.data.model.Beetle
import com.example.cockroach.data.model.BeetleTypes
import com.example.cockroach.data.model.GameSettings
import com.example.cockroach.data.model.GameState
import com.example.cockroach.data.model.Player
import com.example.cockroach.data.repository.GameRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

const val deltaTime = 0.016f // 60 FPS

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: GameRepository = GameRepository(AppDatabase.getDatabase(application))

    // gameState с приватным сеттером (можно устанавливать только тут)
    var gameState by mutableStateOf(GameState())
        private set

    private var gameJob: Job? = null
    private var screenSize = IntSize(800, 600)

    // Текущий игрок
    private val _currentPlayer = MutableStateFlow<Player?>(null)

    // Установка текущего игрока
    fun setCurrentPlayer(player: Player?) {
        _currentPlayer.value = player
        // Сброс состояния игры при смене игрока
        resetGameState()
    }

    /**
     * Сбрасывает состояние игры к начальному
     */
    fun resetGameState() {
        gameJob?.cancel()
        gameState = GameState()
    }

    fun startGame(settings: GameSettings) {
        gameState = GameState(
            gameSettings = settings,
            timeRemaining = settings.roundDuration.toFloat(),
            isGameRunning = true
        )

        gameJob?.cancel()
        gameJob = viewModelScope.launch {
            while (isActive && gameState.isGameRunning && gameState.timeRemaining > 0) {
                if (gameState.isActive) {
                    updateGame()
                }
                delay(16)
            }
            if (gameState.timeRemaining <= 0) {
                endGame()
            }
        }
    }

    fun pauseGame() {
        if (gameState.isGameRunning) {
            gameState = gameState.copy(isPaused = true)
        }
    }

    fun resumeGame() {
        if (gameState.isGameRunning) {
            gameState = gameState.copy(isPaused = false)
        }
    }

    fun togglePause() {
        if (gameState.isGameRunning) {
            gameState = gameState.copy(isPaused = !gameState.isPaused)
        }
    }

    fun setScreenSize(size: IntSize) {
        screenSize = size
    }

    fun onScreenClick(position: Offset) {
        if (!gameState.isActive) return

        val hitBeetle = gameState.beetles.find { it.isClicked(position) && it.isAlive }

        gameState = if (hitBeetle != null) {
            gameState.copy(
                beetles = gameState.beetles.map {
                    if (it.id == hitBeetle.id) it.copy(isAlive = false) else it
                },
                score = gameState.score + hitBeetle.type.points,
                hitCount = gameState.hitCount + 1
            )
        } else {
            gameState.copy(
                missCount = gameState.missCount + 1,
                score = maxOf(0, gameState.score - 5)
            )
        }
    }

    private fun updateGame() {
        val currentTime = System.currentTimeMillis()

        // Обновление позиций жуков
        val updatedBeetles = gameState.beetles.map {
            it.updatePosition(deltaTime, screenSize, gameState.gameSettings.gameSpeed)
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
        saveGameRecord()
    }

    /**
     * Сохраняет результат игры в базу данных
     */
    private fun saveGameRecord() {
        val player = _currentPlayer.value
        if (player == null || player.id == 0L) {
            // Игрок не выбран или не сохранен в БД
            return
        }

        viewModelScope.launch {
            try {
                val record = GameRecordEntity(
                    playerId = player.id,
                    score = gameState.score,
                    accuracy = gameState.accuracy,
                    hitCount = gameState.hitCount,
                    missCount = gameState.missCount,
                    difficultyLevel = gameState.gameSettings.difficultyLevel,
                    gameSpeed = gameState.gameSettings.gameSpeed,
                    maxCockroaches = gameState.gameSettings.maxCockroaches,
                    roundDuration = gameState.gameSettings.roundDuration
                )
                repository.insertGameRecord(record)
            } catch (e: Exception) {
                // Логируем ошибку, но не прерываем игру
                e.printStackTrace()
            }
        }
    }
}