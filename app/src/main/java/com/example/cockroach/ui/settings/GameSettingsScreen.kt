package com.example.cockroach.ui.settings

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cockroach.R
import com.example.cockroach.data.model.GameSettings


//@SuppressLint("DefaultLocale")
@Composable
fun GameSettingsScreen(
    modifier: Modifier = Modifier,
    onSettingsChanged: (GameSettings) -> Unit = {}
) {
    var settings by remember { mutableStateOf(GameSettings()) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.game_settings_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Скорость игры
        item {
            SettingSlider(
                title = stringResource(R.string.speed_game),
                value = settings.gameSpeed,
                valueRange = 0.5f..3.0f,
                onValueChange = { settings = settings.copy(gameSpeed = it) },
                valueFormatter = { "${String.format("%.1f", it)}x" },
                steps = 4
            )
        }

        // Максимальное количество тараканов
        item {
            SettingSlider(
                title = stringResource(R.string.max_cockroaches),
                value = settings.maxCockroaches.toFloat(),
                valueRange = 5f..20f,
                onValueChange = { settings = settings.copy(maxCockroaches = it.toInt()) },
                valueFormatter = { "${it.toInt()} шт." },
                steps = 14
            )
        }

        // Интервал появления бонусов
        item {
            SettingSlider(
                title = stringResource(R.string.bonus_interval),
                value = settings.bonusInterval.toFloat(),
                valueRange = 5f..30f,
                onValueChange = { settings = settings.copy(bonusInterval = it.toInt()) },
                valueFormatter = { "${it.toInt()} сек." },
                steps = 24
            )
        }

        // Длительность раунда
        item {
            SettingSlider(
                title = stringResource(R.string.round_duration),
                value = settings.roundDuration.toFloat(),
                valueRange = 30f..180f,
                onValueChange = { settings = settings.copy(roundDuration = it.toInt()) },
                valueFormatter = { "${it.toInt()} сек." },
                steps = 29
            )
        }

        // Кнопки управления
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = { settings = GameSettings() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.reset))
                }

                Button(
                    onClick = {
                        onSettingsChanged(settings)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Сохранить")
                }
            }
        }
    }
}

@Composable
fun SettingSlider(
    title: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    valueFormatter: (Float) -> String,
    steps: Int = 0,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = valueFormatter(value),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }

            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = valueRange,
                steps = steps,
                modifier = Modifier.fillMaxWidth()
            )

            // Подписи минимум и максимум
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = valueFormatter(valueRange.start),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = valueFormatter(valueRange.endInclusive),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}