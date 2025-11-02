package com.example.cockroach.data.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import kotlin.random.Random

/**
 * Представляет бонус, который появляется на экране каждые 15 секунд
 */
data class Bonus(
    val id: String = Random.nextLong().toString(),
    val position: Offset,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
) {
    /**
     * Проверяет, был ли бонус нажат
     */
    fun isClicked(clickPosition: Offset): Boolean {
        return clickPosition.x >= position.x &&
               clickPosition.x <= position.x + BONUS_SIZE &&
               clickPosition.y >= position.y &&
               clickPosition.y <= position.y + BONUS_SIZE
    }

    /**
     * Проверяет, истек ли срок действия бонуса
     */
    fun isExpired(): Boolean {
        return System.currentTimeMillis() - createdAt > BONUS_DURATION
    }

    companion object {
        const val BONUS_SIZE = 32f
        const val BONUS_DURATION = 5000L // 5 секунд до исчезновения бонуса

        /**
         * Создает новый бонус в случайной позиции на экране
         */
        fun spawn(screenSize: IntSize): Bonus {
            val x = Random.nextFloat() * (screenSize.width - BONUS_SIZE)
            val y = Random.nextFloat() * (screenSize.height - BONUS_SIZE)
            return Bonus(position = Offset(x, y))
        }
    }
}
