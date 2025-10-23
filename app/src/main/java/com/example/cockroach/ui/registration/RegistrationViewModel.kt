package com.example.cockroach.ui.registration

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cockroach.data.database.AppDatabase
import com.example.cockroach.data.model.DifficultyLevel
import com.example.cockroach.data.model.Gender
import com.example.cockroach.data.model.Player
import com.example.cockroach.data.repository.GameRepository
import com.example.cockroach.domain.utils.ZodiacCalc
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class RegistrationViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: GameRepository = GameRepository(AppDatabase.getDatabase(application))

    // Приватное изменяемое состояние
    private val _player = mutableStateOf(Player())
    // Публичное неизменяемое состояние для UI
    val playerData: State<Player> = _player

    // Состояние для отображения результата
    private val _showResult = mutableStateOf(false)
    val showResult: State<Boolean> = _showResult

    // Список всех игроков
    private val _allPlayers = MutableStateFlow<List<Player>>(emptyList())
    val allPlayers: StateFlow<List<Player>> = _allPlayers.asStateFlow()

    // Текущий выбранный игрок
    private val _currentPlayer = MutableStateFlow<Player?>(null)
    val currentPlayer: StateFlow<Player?> = _currentPlayer.asStateFlow()

    init {
        loadPlayers()
    }

    private fun loadPlayers() {
        viewModelScope.launch {
            repository.getAllPlayers().collect { players ->
                _allPlayers.value = players
            }
        }
    }

    /**
     * Обновляет ФИО игрока
     */
    fun updateFullName(fullName: String) {
        _player.value = _player.value.copy(fullName = fullName)
    }

    /**
     * Обновляет пол игрока
     */
    fun updateGender(gender: Gender) {
        _player.value = _player.value.copy(gender = gender)
    }

    /**
     * Обновляет курс игрока (1-6)
     */
    fun updateCourse(courseIndex: Int) {
        _player.value = _player.value.copy(course = courseIndex + 1) // +1 потому что индекс начинается с 0
    }

    /**
     * Обновляет уровень сложности по позиции SeekBar (0-4)
     */
    fun updateDifficultyLevel(level: Float) {
        val difficultyLevel = when (level.toInt()) {
            0 -> DifficultyLevel.EASY
            1 -> DifficultyLevel.MEDIUM
            2 -> DifficultyLevel.HARD
            3 -> DifficultyLevel.EXPERT
            4 -> DifficultyLevel.CUSTOM
            else -> DifficultyLevel.EASY
        }
        _player.value = _player.value.copy(difficultyLevel = difficultyLevel)
    }

    /**
     * Обновляет дату рождения и автоматически вычисляет знак зодиака
     */
    fun updateBirthDate(birthDate: Date) {
        val zodiacSign = ZodiacCalc.calculateZodiacSign(birthDate)
        _player.value = _player.value.copy(
            birthDate = birthDate,
            zodiacSign = zodiacSign
        )
    }

    /**
     * Скрывает результат регистрации
     */
    fun hidePlayerData() {
        _showResult.value = false
    }

    /**
     * Очищает все данные формы
     */
    fun clearData() {
        _player.value = Player()
        _showResult.value = false
    }

    /**
     * Возвращает текст для отображения уровня сложности
     */
    fun getDifficultyText(): String {
        return _player.value.difficultyLevel.displayName
    }

    /**
     * Проверяет, можно ли отправить форму
     */
    fun canSubmit(): Boolean {
        return _player.value.isValid()
    }

    /**
     * Сохраняет игрока в базу данных
     * Если игрок уже существует (id != 0), обновляет его, иначе создает нового
     */
    fun savePlayer() {
        viewModelScope.launch {
            if (_player.value.isValid()) {
                if (_player.value.id != 0L) {
                    // Обновляем существующего игрока
                    repository.updatePlayer(_player.value)
                    _currentPlayer.value = _player.value
                } else {
                    // Создаем нового игрока
                    val playerId = repository.insertPlayer(_player.value)
                    _currentPlayer.value = _player.value.copy(id = playerId)
                }
                _showResult.value = true
            }
        }
    }

    /**
     * Выбирает существующего игрока
     */
    fun selectPlayer(player: Player) {
        _player.value = player
        _currentPlayer.value = player
    }

    /**
     * Создание нового игрока (очистка формы)
     */
    fun createNewPlayer() {
        _player.value = Player()
        _currentPlayer.value = null
    }
}