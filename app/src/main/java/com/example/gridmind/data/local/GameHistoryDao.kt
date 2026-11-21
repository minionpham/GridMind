package com.example.gridmind.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameHistoryDao {
    @Insert
    suspend fun insert(entity: GameHistoryEntity)

    @Query("SELECT * FROM game_history WHERE mode = :mode ORDER BY playedAt DESC, id DESC")
    fun observeByMode(mode: String): Flow<List<GameHistoryEntity>>

    @Query("DELETE FROM game_history")
    suspend fun clear()
}

