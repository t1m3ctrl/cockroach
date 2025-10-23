package com.example.cockroach.data.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.cockroach.data.model.DifficultyLevel
import com.example.cockroach.data.model.Player

@Entity(
    tableName = "game_records",
    foreignKeys = [
        ForeignKey(
            entity = Player::class,
            parentColumns = ["id"],
            childColumns = ["playerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("playerId")]
)
@TypeConverters(Converters::class)
data class GameRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val playerId: Long,
    val score: Int,
    val accuracy: Float,
    val hitCount: Int,
    val missCount: Int,
    val difficultyLevel: DifficultyLevel,
    val gameSpeed: Float,
    val maxCockroaches: Int,
    val roundDuration: Int,
    val playedAt: Long = System.currentTimeMillis()
)