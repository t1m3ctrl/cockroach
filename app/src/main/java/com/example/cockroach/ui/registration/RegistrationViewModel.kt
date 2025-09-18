package com.example.cockroach.ui.registration

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import com.example.cockroach.data.model.DifficultyLevel
import com.example.cockroach.data.model.Gender
import com.example.cockroach.data.model.Player
import com.example.cockroach.domain.utils.ZodiacCalc
import java.util.Date

class RegistrationViewModel : ViewModel() {
    // Приватное изменяемое состояние
    private val _player = mutableStateOf(Player())
    // Публичное неизменяемое состояние для UI
    val playerData: State<Player> = _player

    // Состояние для отображения результата
    private val _showResult = mutableStateOf(false)
    val showResult: State<Boolean> = _showResult

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
     * Обновляет уровень сложности по позиции SeekBar (0-3)
     */
    fun updateDifficultyLevel(level: Float) {
        val difficultyLevel = when (level.toInt()) {
            0 -> DifficultyLevel.EASY
            1 -> DifficultyLevel.MEDIUM
            2 -> DifficultyLevel.HARD
            3 -> DifficultyLevel.EXPERT
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
     * Показывает результат регистрации
     */
    fun showPlayerData() {
        if (_player.value.isValid()) {
            _showResult.value = true
        }
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
}