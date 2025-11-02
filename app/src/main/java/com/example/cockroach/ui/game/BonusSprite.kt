package com.example.cockroach.ui.game

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Компонент для отображения бонуса на игровом поле
 */
@Composable
fun BonusSprite(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Пульсирующая анимация для привлечения внимания
    val infiniteTransition = rememberInfiniteTransition(label = "bonus_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bonus_scale"
    )

    Box(
        modifier = modifier
            .size(32.dp)
            .scale(scale)
            .background(
                color = Color(0xFFFFD700), // Золотой цвет
                shape = CircleShape
            )
            .border(
                width = 3.dp,
                color = Color(0xFFFFA500), // Оранжевая граница
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        // Иконка бонуса - звезда или подарок
        Text(
            text = "⭐",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
