package com.example.cockroach.data.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.cockroach.data.database.GameRecordEntity

data class GameRecordWithPlayer(
    @Embedded val record: GameRecordEntity,
    @Relation(
        parentColumn = "playerId",
        entityColumn = "id"
    )
    val player: Player
)