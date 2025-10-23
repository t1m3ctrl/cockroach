package com.example.cockroach.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.cockroach.data.database.Converters
import java.util.Date

@Entity(tableName = "players")
@TypeConverters(Converters::class)
data class Player(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fullName: String = "",
    val gender: Gender? = null,
    val course: Int = 1,
    val difficultyLevel: DifficultyLevel = DifficultyLevel.EASY,
    val birthDate: Date? = null,
    val zodiacSign: ZodiacSign? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    /**
     * Проверяет, заполнены ли все обязательные поля
     */
    fun isValid(): Boolean {
        return fullName.isNotBlank() &&
                gender != null &&
                birthDate != null
    }

}

enum class Gender(val displayName: String) {
    MALE("Мужской"),
    FEMALE("Женский")
}

enum class ZodiacSign(val displayName: String) {
    ARIES("Овен"),
    TAURUS("Телец"),
    GEMINI("Близнецы"),
    CANCER("Рак"),
    LEO("Лев"),
    VIRGO("Дева"),
    LIBRA("Весы"),
    SCORPIO("Скорпион"),
    SAGITTARIUS("Стрелец"),
    CAPRICORN("Козерог"),
    AQUARIUS("Водолей"),
    PISCES("Рыбы")
}

enum class DifficultyLevel(val displayName: String, val value: Int) {
    EASY("Легкий", 1),
    MEDIUM("Средний", 2),
    HARD("Сложный", 3),
    EXPERT("Эксперт", 4),
    CUSTOM("Пользовательский", 5);

    /**
     * Возвращает настройки игры для выбранного уровня сложности
     */
    fun toGameSettings(): GameSettings {
        return when (this) {
            EASY -> GameSettings(gameSpeed = 1.0f, maxCockroaches = 5, roundDuration = 60)
            MEDIUM -> GameSettings(gameSpeed = 1.5f, maxCockroaches = 10, roundDuration = 60)
            HARD -> GameSettings(gameSpeed = 2.0f, maxCockroaches = 15, roundDuration = 60)
            EXPERT -> GameSettings(gameSpeed = 2.5f, maxCockroaches = 20, roundDuration = 60)
            CUSTOM -> GameSettings() // Стандартные настройки для пользовательского режима
        }
    }
}

