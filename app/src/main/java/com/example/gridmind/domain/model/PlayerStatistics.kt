package com.example.gridmind.domain.model

data class LevelStats(
    val level: Int,
    val played: Int,
    val passed: Int,
    val averageAccuracy: Double,
    val bestScore: Int = 0
) {
    val winRate: Double get() = if (played == 0) 0.0 else passed.toDouble() / played * 100.0
}

data class PlayerStatistics(
    val highestUnlockedLevel: Int = 1,
    val bestScore: Int = 0,
    val totalCompletedRounds: Int = 0,
    val totalGamesPlayed: Int = 0,
    val averageAccuracy: Double = 0.0,
    val currentWinStreak: Int = 0,
    val bestWinStreak: Int = 0,
    val currentDayStreak: Int = 0,
    val bestDayStreak: Int = 0,
    val levelStats: List<LevelStats> = emptyList(),
    val recentGames: List<GameHistory> = emptyList(),
    /** Oldest to newest, for charting. */
    val trend: List<GameHistory> = emptyList()
)

