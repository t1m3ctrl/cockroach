package com.example.cockroach.data.model

data class GameState(
    val beetles: List<Beetle> = emptyList(),
    val score: Int = 0,
    val hitCount: Int = 0,
    val missCount: Int = 0,
    val isGameRunning: Boolean = false,
    val timeRemaining: Float = 0f,
    val lastBeetleSpawn: Long = 0L,
    val gameSettings: GameSettings = GameSettings()
) {
    val totalClicks: Int get() = hitCount + missCount
    val accuracy: Float get() = if (totalClicks > 0) hitCount.toFloat() / totalClicks else 0f
}