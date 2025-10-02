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
    val isAlive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun updatePosition(deltaTime: Float, screenSize: IntSize, gameSpeed: Float = 1.0f): Beetle {
        val newX = position.x + direction.x * type.speed * deltaTime * 100f * gameSpeed
        val newY = position.y + direction.y * type.speed * deltaTime * 100f * gameSpeed

        val spriteWidth = 64f
        val spriteHeight = 80f

        val isOutOfBounds = newX < -spriteWidth || newX > screenSize.width + spriteWidth ||
                           newY < -spriteHeight || newY > screenSize.height + spriteHeight

        return if (isOutOfBounds) {
            copy(isAlive = false)
        } else {
            copy(
                position = Offset(newX, newY),
                animationFrame = (animationFrame + 1) % 12
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