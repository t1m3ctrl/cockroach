package com.example.cockroach.data.model

import java.util.Date

data class Player(
    val fullName: String = "",
    val gender: Gender? = null,
    val course: Int = 1,
    val difficultyLevel: DifficultyLevel = DifficultyLevel.EASY,
    val birthDate: Date? = null,
    val zodiacSign: ZodiacSign? = null
) {
    /**
     * Проверяет, заполнены ли все обязательные поля
     */
    fun isValid(): Boolean {
        return fullName.isNotBlank() &&
                gender != null &&
                birthDate != null
    }

    /**
     * Возвращает строковое представление данных игрока
     */
    fun getDisplayText(): String {
        return buildString {
            appendLine("ФИО: $fullName")
            appendLine("Пол: ${gender?.displayName ?: "Не указан"}")
            appendLine("Курс: $course")
            appendLine("Сложность: ${difficultyLevel.displayName}")
            appendLine("Дата рождения: ${birthDate?.let {
                java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault()).format(it)
            } ?: "Не указана"}")
            appendLine("Знак зодиака: ${zodiacSign?.displayName ?: "Не определен"}")
        }
    }
}

enum class Gender(val displayName: String) {
    MALE("Мужской"),
    FEMALE("Женский")
}

enum class ZodiacSign(val displayName: String, val iconName: String) {
    ARIES("Овен", "aries"),
    TAURUS("Телец", "taurus"),
    GEMINI("Близнецы", "gemini"),
    CANCER("Рак", "cancer"),
    LEO("Лев", "leo"),
    VIRGO("Дева", "virgo"),
    LIBRA("Весы", "libra"),
    SCORPIO("Скорпион", "scorpio"),
    SAGITTARIUS("Стрелец", "sagittarius"),
    CAPRICORN("Козерог", "capricorn"),
    AQUARIUS("Водолей", "aquarius"),
    PISCES("Рыбы", "pisces")
}

enum class DifficultyLevel(val displayName: String, val value: Int) {
    EASY("Легкий", 1),
    MEDIUM("Средний", 2),
    HARD("Сложный", 3),
    EXPERT("Эксперт", 4)
}

