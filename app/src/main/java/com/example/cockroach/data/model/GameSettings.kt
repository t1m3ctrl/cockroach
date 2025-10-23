package com.example.cockroach.data.model

data class GameSettings(
    val gameSpeed: Float = 1.0f, // 0.5 - 3.0
    val maxCockroaches: Int = 10, // 5 - 20
    val bonusInterval: Int = 15, // 5 - 30 секунд
    val roundDuration: Int = 60, // 30 - 180 секунд
    val difficultyLevel: DifficultyLevel = DifficultyLevel.EASY
) {

    /**
     * Определяет уровень сложности по текущим настройкам
     * Если настройки соответствуют одному из стандартных уровней, возвращает его
     * Иначе возвращает CUSTOM
     */
    fun detectDifficultyLevel(): DifficultyLevel {
        // Проверяем только основные параметры, влияющие на сложность
        return when {
            gameSpeed == 1.0f && maxCockroaches == 5 && roundDuration == 60 -> DifficultyLevel.EASY
            gameSpeed == 1.5f && maxCockroaches == 10 && roundDuration == 60 -> DifficultyLevel.MEDIUM
            gameSpeed == 2.0f && maxCockroaches == 15 && roundDuration == 60 -> DifficultyLevel.HARD
            gameSpeed == 2.5f && maxCockroaches == 20 && roundDuration == 60 -> DifficultyLevel.EXPERT
            else -> DifficultyLevel.CUSTOM
        }
    }

    /**
     * Возвращает копию настроек с автоматически установленным уровнем сложности
     * Если настройки соответствуют стандартному уровню, устанавливает его
     * Если настройки были изменены и не соответствуют ни одному стандартному уровню, устанавливает CUSTOM
     */
    fun withAutoCustomDifficulty(): GameSettings {
        val detectedLevel = detectDifficultyLevel()
        return if (detectedLevel != difficultyLevel) {
            copy(difficultyLevel = detectedLevel)
        } else {
            this
        }
    }
}