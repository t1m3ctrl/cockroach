package com.example.cockroach.data.model

import androidx.compose.ui.graphics.Color

data class BeetleType(
    val id: String,
    val name: String,
    val spriteResource: String,
    val points: Int,
    val speed: Float,
    val size: Float = 1.0f,
    val spawnChance: Float = 1.0f,
    val color: Color = Color.White
)

object BeetleTypes {
    val ALDER_LEAF = BeetleType(
        id = "beetle_alder_leaf",
        name = "Ольховый листоед",
        spriteResource = "beetle_alder_leaf",
        points = 10,
        speed = 1.0f,
        color = Color.Blue
    )


    val COMMON_SUN = BeetleType(
        id = "beetle_common_sun",
        name = "Солнечный жук",
        spriteResource = "beetle_common_sun",
        points = 15,
        speed = 1.2f,
        color = Color(0xFFFFD700)
    )

    val ALL_TYPES = listOf(ALDER_LEAF, COMMON_SUN)
}