package com.example.cockroach.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.cockroach.data.model.Player
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {
    @Query("SELECT * FROM players ORDER BY createdAt DESC")
    fun getAllPlayers(): Flow<List<Player>>

    @Query("SELECT * FROM players WHERE id = :playerId")
    suspend fun getPlayerById(playerId: Long): Player?

    @Query("SELECT * FROM players WHERE fullName = :fullName LIMIT 1")
    suspend fun getPlayerByName(fullName: String): Player?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(player: Player): Long

    @Update
    suspend fun updatePlayer(player: Player)

    @Delete
    suspend fun deletePlayer(player: Player)

    @Query("DELETE FROM players")
    suspend fun deleteAllPlayers()
}