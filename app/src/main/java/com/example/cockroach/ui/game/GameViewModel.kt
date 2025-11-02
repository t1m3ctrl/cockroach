package com.example.cockroach.ui.game

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cockroach.R
import com.example.cockroach.data.database.AppDatabase
import com.example.cockroach.data.database.GameRecordEntity
import com.example.cockroach.data.model.Beetle
import com.example.cockroach.data.model.BeetleTypes
import com.example.cockroach.data.model.Bonus
import com.example.cockroach.data.model.GameSettings
import com.example.cockroach.data.model.GameState
import com.example.cockroach.data.model.Player
import com.example.cockroach.data.repository.GameRepository
import com.example.cockroach.domain.utils.SoundManager
import com.example.cockroach.domain.utils.TiltSensor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

const val deltaTime = 0.016f // 60 FPS
const val BONUS_SPAWN_INTERVAL = 15000L // 15 секунд между появлением бонусов
const val TILT_MODE_DURATION = 5000L // 5 секунд действия режима наклона

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: GameRepository = GameRepository(AppDatabase.getDatabase(application))

    // gameState с приватным сеттером (можно устанавливать только тут)
    var gameState by mutableStateOf(GameState())
        private set

    private var gameJob: Job? = null
    private var screenSize = IntSize(800, 600)

    // Сенсор наклона и звуковой менеджер
    private val tiltSensor = TiltSensor(application)
    private val soundManager = SoundManager(application)
    private var currentTiltAcceleration = Offset.Zero

    private var tiltRemainingTime: Long? = null

    // Текущий игрок
    private val _currentPlayer = MutableStateFlow<Player?>(null)

    // Установка текущего игрока
    fun setCurrentPlayer(player: Player?) {
        _currentPlayer.value = player
        // Сброс состояния игры при смене игрока
        resetGameState()
    }

    init {
        // Подписываемся на изменения акселерометра
        viewModelScope.launch {
            tiltSensor.tiltAcceleration.collect { acceleration ->
                if (gameState.isTiltModeActive) {
                    currentTiltAcceleration = acceleration
                }
            }
        }
    }

    /**
     * Сбрасывает состояние игры к начальному
     */
    fun resetGameState() {
        gameJob?.cancel()
        tiltSensor.stopListening()
        soundManager.stopAllSounds()
        currentTiltAcceleration = Offset.Zero
        gameState = GameState()
    }

    override fun onCleared() {
        super.onCleared()
        tiltSensor.release()
        soundManager.release()
    }

    fun startGame(settings: GameSettings) {
        val currentTime = System.currentTimeMillis()
        gameState = GameState(
            gameSettings = settings,
            timeRemaining = settings.roundDuration.toFloat(),
            isGameRunning = true,
            lastBonusSpawn = currentTime // Устанавливаем время начала, чтобы первый бонус появился через 15 секунд
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
        if (gameState.isGameRunning && !gameState.isPaused) {
            if (gameState.isTiltModeActive) {
                tiltRemainingTime = gameState.tiltModeEndTime - System.currentTimeMillis()
                tiltSensor.stopListening()
            }

            soundManager.stopAllSounds()

            gameState = gameState.copy(isPaused = true)
        }
    }

    fun resumeGame() {
        if (gameState.isGameRunning && gameState.isPaused) {
            if (gameState.isTiltModeActive && tiltRemainingTime != null) {
                gameState = gameState.copy(
                    tiltModeEndTime = System.currentTimeMillis() + tiltRemainingTime!!
                )
                tiltSensor.startListening()
                tiltRemainingTime = null
            }

            gameState = gameState.copy(isPaused = false)
        }
    }

//    fun pauseGame() {
//        if (gameState.isGameRunning) {
//            gameState = gameState.copy(isPaused = true)
//        }
//    }
//
//    fun resumeGame() {
//        if (gameState.isGameRunning) {
//            gameState = gameState.copy(isPaused = false)
//        }
//    }

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

        // Проверяем, был ли нажат бонус
        gameState.bonus?.let { bonus ->
            if (bonus.isActive && bonus.isClicked(position)) {
                activateTiltMode()
                return
            }
            null
        }

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

    /**
     * Активирует режим наклона на 5 секунд
     */
    private fun activateTiltMode() {
        val currentTime = System.currentTimeMillis()
        gameState = gameState.copy(
            bonus = null,
            isTiltModeActive = true,
            tiltModeEndTime = currentTime + TILT_MODE_DURATION
        )

        // Начинаем отслеживать наклон и воспроизводим "крик" жуков
        viewModelScope.launch(Dispatchers.Default) {
            tiltSensor.startListening()
            soundManager.playBeetleScream(R.raw.beetle_scream)
        }

    }

    private fun updateGame() {
        val currentTime = System.currentTimeMillis()

        // Проверяем, нужно ли деактивировать режим наклона
        val shouldDeactivateTilt = gameState.isTiltModeActive &&
                                    currentTime >= gameState.tiltModeEndTime
        if (shouldDeactivateTilt) {
            tiltSensor.stopListening()
            currentTiltAcceleration = Offset.Zero
        }

        // Обновление позиций жуков с учетом наклона
        val tiltEffect = if (gameState.isTiltModeActive) currentTiltAcceleration else Offset.Zero
        val updatedBeetles = gameState.beetles.map {
            it.updatePosition(
                deltaTime = deltaTime,
                screenSize = screenSize,
                gameSpeed = gameState.gameSettings.gameSpeed,
                tiltAcceleration = tiltEffect
            )
        }.filter { it.isAlive }

        // Спавн новых жуков
        val needNewBeetle = updatedBeetles.size < gameState.gameSettings.maxCockroaches &&
                           currentTime - gameState.lastBeetleSpawn > 2000 / gameState.gameSettings.gameSpeed

        val beetles = if (needNewBeetle) {
            updatedBeetles + spawnBeetle()
        } else {
            updatedBeetles
        }

        // Спавн или обновление бонуса
        val needNewBonus = gameState.bonus == null &&
                          currentTime - gameState.lastBonusSpawn >= BONUS_SPAWN_INTERVAL

        val bonus = when {
            needNewBonus -> Bonus.spawn(screenSize)
            gameState.bonus?.isExpired() == true -> null
            else -> gameState.bonus
        }

        gameState = gameState.copy(
            beetles = beetles,
            timeRemaining = gameState.timeRemaining - deltaTime,
            lastBeetleSpawn = if (needNewBeetle) currentTime else gameState.lastBeetleSpawn,
            bonus = bonus,
            lastBonusSpawn = if (needNewBonus) currentTime else gameState.lastBonusSpawn,
            isTiltModeActive = !shouldDeactivateTilt && gameState.isTiltModeActive
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