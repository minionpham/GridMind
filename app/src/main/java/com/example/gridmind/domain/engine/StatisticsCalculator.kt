package com.example.gridmind.domain.engine

import com.example.gridmind.domain.model.GameHistory
import com.example.gridmind.domain.model.LevelStats
import com.example.gridmind.domain.model.PlayerStatistics
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class StatisticsCalculator(private val recentLimit: Int = 20) {

    /** [history] must be ordered newest first. */
    fun calculate(
        history: List<GameHistory>,
        bestScore: Int,
        highestUnlockedLevel: Int,
        now: Long = System.currentTimeMillis(),
        zone: ZoneId = ZoneId.systemDefault()
    ): PlayerStatistics {
        val days = history.map { Instant.ofEpochMilli(it.playedAt).atZone(zone).toLocalDate() }.toSortedSet()
        val today = Instant.ofEpochMilli(now).atZone(zone).toLocalDate()
        val recent = history.take(recentLimit)
        return PlayerStatistics(
            highestUnlockedLevel = highestUnlockedLevel,
            bestScore = bestScore,
            totalCompletedRounds = history.count { it.passed },
            totalGamesPlayed = history.size,
            averageAccuracy = if (history.isEmpty()) 0.0 else history.map { it.accuracy }.average(),
            currentWinStreak = history.takeWhile { it.passed }.size,
            bestWinStreak = bestRun(history.asReversed().map { it.passed }),
            currentDayStreak = currentDayStreak(days, today),
            bestDayStreak = bestDayStreak(days),
            levelStats = levelStats(history),
            recentGames = recent,
            trend = recent.asReversed()
        )
    }

    private fun levelStats(history: List<GameHistory>): List<LevelStats> =
        history.groupBy { it.level }.toSortedMap().map { (level, games) ->
            LevelStats(level, games.size, games.count { it.passed }, games.map { it.accuracy }.average())
        }

    private fun bestRun(flags: List<Boolean>): Int {
        var best = 0
        var run = 0
        for (f in flags) {
            run = if (f) run + 1 else 0
            best = maxOf(best, run)
        }
        return best
    }

    // A streak stays alive until the end of the day after the last played day.
    private fun currentDayStreak(days: Set<LocalDate>, today: LocalDate): Int {
        var day = when {
            today in days -> today
            today.minusDays(1) in days -> today.minusDays(1)
            else -> return 0
        }
        var count = 0
        while (day in days) {
            count++
            day = day.minusDays(1)
        }
        return count
    }

    private fun bestDayStreak(days: Set<LocalDate>): Int {
        var best = 0
        var run = 0
        var previous: LocalDate? = null
        for (d in days) {
            run = if (previous != null && previous.plusDays(1) == d) run + 1 else 1
            best = maxOf(best, run)
            previous = d
        }
        return best
    }
}
