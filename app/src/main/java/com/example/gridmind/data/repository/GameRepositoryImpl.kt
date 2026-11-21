package com.example.gridmind.data.repository

import com.example.gridmind.data.local.GameHistoryDao
import com.example.gridmind.data.local.GameHistoryEntity
import com.example.gridmind.data.local.PreferencesDataSource
import com.example.gridmind.domain.engine.StatisticsCalculator
import com.example.gridmind.domain.model.GameHistory
import com.example.gridmind.domain.model.GameMode
import com.example.gridmind.domain.model.GameResult
import com.example.gridmind.domain.model.GameSettings
import com.example.gridmind.domain.model.PlayerProgress
import com.example.gridmind.domain.model.PlayerStatistics
import com.example.gridmind.domain.model.ThemeMode
import com.example.gridmind.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GameRepositoryImpl(
    private val dao: GameHistoryDao,
    private val preferences: PreferencesDataSource
) : GameRepository {

    private val calculator = StatisticsCalculator()

    override val settings: Flow<GameSettings> = preferences.settings

    override fun progress(mode: GameMode): Flow<PlayerProgress> = preferences.progress(mode)

    override fun statistics(mode: GameMode): Flow<PlayerStatistics> = combine(
        preferences.progress(mode),
        dao.observeByMode(mode.name)
    ) { progress, rows ->
        calculator.calculate(
            history = rows.map { it.toDomain() },
            bestScore = progress.bestScore,
            highestUnlockedLevel = progress.highestUnlockedLevel
        )
    }

    override suspend fun saveResult(result: GameResult, mode: GameMode, playedAt: Long) {
        dao.insert(
            GameHistoryEntity(
                level = result.level,
                score = result.score,
                correctCount = result.correctCount,
                incorrectCount = result.incorrectCount,
                missedCount = result.missedCount,
                accuracy = result.accuracy,
                passed = result.passed,
                mode = mode.name,
                playedAt = playedAt
            )
        )
        preferences.updateProgress(
            mode = mode,
            score = result.score,
            unlockedLevel = if (result.passed) result.level + 1 else 1
        )
    }

    override suspend fun setSoundEnabled(enabled: Boolean) = preferences.setSound(enabled)
    override suspend fun setVibrationEnabled(enabled: Boolean) = preferences.setVibration(enabled)
    override suspend fun setThemeMode(mode: ThemeMode) = preferences.setTheme(mode)

    override suspend fun resetProgress() {
        dao.clear()
        preferences.resetProgress()
    }

    private fun GameHistoryEntity.toDomain() = GameHistory(
        id = id,
        level = level,
        score = score,
        correctCount = correctCount,
        incorrectCount = incorrectCount,
        missedCount = missedCount,
        accuracy = accuracy,
        passed = passed,
        playedAt = playedAt
    )

}



