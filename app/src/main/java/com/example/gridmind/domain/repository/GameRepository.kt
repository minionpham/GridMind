package com.example.gridmind.domain.repository

import com.example.gridmind.domain.model.GameHistory
import com.example.gridmind.domain.model.GameResult
import com.example.gridmind.domain.model.GameSettings
import com.example.gridmind.domain.model.PlayerProgress
import com.example.gridmind.domain.model.PlayerStatistics
import com.example.gridmind.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface GameRepository {
    val progress: Flow<PlayerProgress>
    val settings: Flow<GameSettings>
    val statistics: Flow<PlayerStatistics>

    suspend fun saveResult(result: GameResult, playedAt: Long = System.currentTimeMillis())
    suspend fun setSoundEnabled(enabled: Boolean)
    suspend fun setVibrationEnabled(enabled: Boolean)
    suspend fun setThemeMode(mode: ThemeMode)
    suspend fun resetProgress()
}
