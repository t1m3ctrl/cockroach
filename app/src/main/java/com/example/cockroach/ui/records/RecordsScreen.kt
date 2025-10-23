package com.example.cockroach.ui.records

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RecordsScreen(
    modifier: Modifier = Modifier,
    viewModel: RecordsViewModel = viewModel()
) {
    val records by viewModel.topRecords.collectAsState()
    var selectedFilter by remember { mutableStateOf(RecordFilter.ALL) }

    LaunchedEffect(selectedFilter) {
        viewModel.loadRecords(selectedFilter)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Заголовок
        Text(
            text = "Таблица рекордов",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )

        // Фильтры
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RecordFilter.entries.forEach { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter.displayName) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Заголовок таблицы
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "№",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(0.5f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Игрок",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(2f)
                )
                Text(
                    text = "Очки",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Точность",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Сложность",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1.2f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Список рекордов
        if (records.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Нет записей",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(records.size) { index ->
                    val recordWithPlayer = records[index]
                    RecordItem(
                        position = index + 1,
                        playerName = recordWithPlayer.player.fullName,
                        score = recordWithPlayer.record.score,
                        accuracy = recordWithPlayer.record.accuracy,
                        difficulty = recordWithPlayer.record.difficultyLevel.displayName,
                        date = Date(recordWithPlayer.record.playedAt)
                    )
                }
            }
        }
    }
}

@Composable
fun RecordItem(
    position: Int,
    playerName: String,
    score: Int,
    accuracy: Float,
    difficulty: String,
    date: Date,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (position) {
                1 -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                2 -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                3 -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = when (position) {
                    1 -> "🥇"
                    2 -> "🥈"
                    3 -> "🥉"
                    else -> "$position"
                },
                modifier = Modifier.weight(0.5f),
                textAlign = TextAlign.Center,
                fontWeight = if (position <= 3) FontWeight.Bold else FontWeight.Normal
            )
            Column(modifier = Modifier.weight(2f)) {
                Text(
                    text = playerName,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(date),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "$score",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "${(accuracy * 100).toInt()}%",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Text(
                text = difficulty,
                modifier = Modifier.weight(1.2f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

enum class RecordFilter(val displayName: String) {
    ALL("Все"),
    TOP("Топ"),
    RECENT("Недавние")
}
