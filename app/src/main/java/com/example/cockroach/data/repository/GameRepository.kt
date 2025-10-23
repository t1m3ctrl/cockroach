package com.example.cockroach.data.repository

import com.example.cockroach.data.database.AppDatabase
import com.example.cockroach.data.database.GameRecordEntity
import com.example.cockroach.data.model.GameRecordWithPlayer
import com.example.cockroach.data.model.Player
import kotlinx.coroutines.flow.Flow

class GameRepository(database: AppDatabase) {
    private val playerDao = database.playerDao()
    private val gameRecordDao = database.gameRecordDao()

    // Player operations
    fun getAllPlayers(): Flow<List<Player>> = playerDao.getAllPlayers()

    suspend fun insertPlayer(player: Player): Long = playerDao.insertPlayer(player)

    suspend fun updatePlayer(player: Player) = playerDao.updatePlayer(player)

    // Game record operations
    suspend fun insertGameRecord(record: GameRecordEntity): Long =
        gameRecordDao.insertGameRecord(record)

    fun getTopRecordsWithPlayers(limit: Int = 10): Flow<List<GameRecordWithPlayer>> =
        gameRecordDao.getTopRecordsWithPlayers(limit)

    fun getRecentRecordsWithPlayers(limit: Int = 20): Flow<List<GameRecordWithPlayer>> =
        gameRecordDao.getRecentRecordsWithPlayers(limit)
}