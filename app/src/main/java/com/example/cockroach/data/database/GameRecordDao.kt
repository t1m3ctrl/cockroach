package com.example.cockroach.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.cockroach.data.model.GameRecordWithPlayer
import kotlinx.coroutines.flow.Flow

@Dao
interface GameRecordDao {
    @Insert
    suspend fun insertGameRecord(record: GameRecordEntity): Long

    @Query("SELECT * FROM game_records ORDER BY playedAt DESC")
    fun getAllRecords(): Flow<List<GameRecordEntity>>

    @Query("SELECT * FROM game_records WHERE playerId = :playerId ORDER BY playedAt DESC")
    fun getRecordsByPlayer(playerId: Long): Flow<List<GameRecordEntity>>

    @Query("SELECT * FROM game_records ORDER BY score DESC LIMIT :limit")
    fun getTopRecords(limit: Int = 10): Flow<List<GameRecordEntity>>

    @Query("""
        SELECT * FROM game_records
        WHERE difficultyLevel = :difficulty
        ORDER BY score DESC
        LIMIT :limit
    """)
    fun getTopRecordsByDifficulty(difficulty: String, limit: Int = 10): Flow<List<GameRecordEntity>>

    @Query("SELECT MAX(score) FROM game_records WHERE playerId = :playerId")
    suspend fun getPlayerBestScore(playerId: Long): Int?

    @Query("SELECT COUNT(*) FROM game_records WHERE playerId = :playerId")
    suspend fun getPlayerGamesCount(playerId: Long): Int

    @Query("DELETE FROM game_records")
    suspend fun deleteAllRecords()

    // Запросы с JOIN для получения рекордов с информацией об игроках
    @Transaction
    @Query("""
        SELECT gr.* FROM game_records gr
        INNER JOIN (
            SELECT playerId, MAX(score) as max_score
            FROM game_records
            GROUP BY playerId
        ) best ON gr.playerId = best.playerId AND gr.score = best.max_score
        GROUP BY gr.playerId
        ORDER BY gr.score DESC
        LIMIT :limit
    """)
    fun getTopRecordsWithPlayers(limit: Int = 10): Flow<List<GameRecordWithPlayer>>

    @Transaction
    @Query("""
        SELECT * FROM game_records
        WHERE difficultyLevel = :difficulty
        ORDER BY score DESC
        LIMIT :limit
    """)
    fun getTopRecordsWithPlayersByDifficulty(difficulty: String, limit: Int = 10): Flow<List<GameRecordWithPlayer>>

    @Transaction
    @Query("SELECT * FROM game_records ORDER BY playedAt DESC LIMIT :limit")
    fun getRecentRecordsWithPlayers(limit: Int = 20): Flow<List<GameRecordWithPlayer>>
}