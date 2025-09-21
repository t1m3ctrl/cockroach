package com.example.cockroach.data.model

data class GameSettings(
    val gameSpeed: Float = 1.0f, // 0.5 - 3.0
    val maxCockroaches: Int = 10, // 5 - 20
    val bonusInterval: Int = 15, // 5 - 30 секунд
    val roundDuration: Int = 60 // 30 - 180 секунд
)