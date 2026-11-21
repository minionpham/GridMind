package com.example.gridmind.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_history")
data class GameHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val level: Int,
    val score: Int,
    val correctCount: Int,
    val incorrectCount: Int,
    val missedCount: Int,
    val accuracy: Double,
    val passed: Boolean,
    val playedAt: Long
)
