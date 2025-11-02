package com.example.cockroach.data.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import kotlin.random.Random

data class Beetle(
    val id: String = Random.nextLong().toString(),
    val type: BeetleType,
    val position: Offset,
    val direction: Offset,
    val animationFrame: Int = 0,
    val animationProgress: Float = 0f,
    val isAlive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun updatePosition(
        deltaTime: Float,
        screenSize: IntSize,
        gameSpeed: Float = 1.0f,
        tiltAcceleration: Offset = Offset.Zero
    ): Beetle {
        // Проверяем, активен ли режим наклона
        val isTiltActive = tiltAcceleration != Offset.Zero

        // Если наклон активен, гравитация доминирует над обычным движением
        val movementX: Float
        val movementY: Float

        if (isTiltActive) {
            // Режим наклона: гравитация полностью управляет движением
            val tiltInfluence = 500f // Сильная гравитация
            movementX = tiltAcceleration.x * tiltInfluence * deltaTime
            movementY = tiltAcceleration.y * tiltInfluence * deltaTime
        } else {
            // Обычное движение
            movementX = direction.x * type.speed * deltaTime * 100f * gameSpeed
            movementY = direction.y * type.speed * deltaTime * 100f * gameSpeed
        }

        val newX = position.x + movementX
        val newY = position.y + movementY

        val spriteWidth = 64f
        val spriteHeight = 80f

        val isOutOfBounds = newX < -spriteWidth || newX > screenSize.width + spriteWidth ||
                           newY < -spriteHeight || newY > screenSize.height + spriteHeight

        return if (isOutOfBounds) {
            copy(isAlive = false)
        } else {
            val newProgress = animationProgress + 0.5f
            val newFrame = newProgress.toInt() % 12

            copy(
                position = Offset(newX, newY),
                animationFrame = newFrame,
                animationProgress = newProgress
            )
        }
    }

    fun isClicked(clickPosition: Offset): Boolean {
        val spriteWidth = 64f
        val spriteHeight = 80f
        return clickPosition.x >= position.x &&
               clickPosition.x <= position.x + spriteWidth &&
               clickPosition.y >= position.y &&
               clickPosition.y <= position.y + spriteHeight
    }
}