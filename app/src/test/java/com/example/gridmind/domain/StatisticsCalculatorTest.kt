package com.example.gridmind.domain

import com.example.gridmind.domain.engine.StatisticsCalculator
import com.example.gridmind.domain.model.GameHistory
import java.time.LocalDate
import java.time.ZoneOffset
import org.junit.Assert.assertEquals
import org.junit.Test

class StatisticsCalculatorTest {
    private val calc = StatisticsCalculator(recentLimit = 3)
    private val zone = ZoneOffset.UTC
    private fun day(d: Int) = LocalDate.of(2026, 1, d).atStartOfDay(zone).toInstant().toEpochMilli() + 1000

    private fun game(id: Long, level: Int, passed: Boolean, acc: Double, day: Int) =
        GameHistory(id, level, 0, 0, 0, 0, acc, passed, day(day))

    // newest first
    private val history = listOf(
        game(5, 2, true, 100.0, 10),
        game(4, 2, true, 80.0, 10),
        game(3, 1, false, 40.0, 9),
        game(2, 1, true, 100.0, 7),
        game(1, 1, true, 100.0, 6)
    )

    @Test
    fun winStreaks() {
        val s = calc.calculate(history, 0, 1, day(10), zone)
        assertEquals(2, s.currentWinStreak)
        assertEquals(2, s.bestWinStreak)
    }

    @Test
    fun dayStreaksIncludeYesterdayButExpireAfterGap() {
        assertEquals(2, calc.calculate(history, 0, 1, day(10), zone).currentDayStreak)
        assertEquals(2, calc.calculate(history, 0, 1, day(11), zone).currentDayStreak)
        assertEquals(0, calc.calculate(history, 0, 1, day(12), zone).currentDayStreak)
        assertEquals(2, calc.calculate(history, 0, 1, day(12), zone).bestDayStreak)
    }

    @Test
    fun levelStatsAndTrend() {
        val s = calc.calculate(history, 0, 1, day(10), zone)
        assertEquals(2, s.levelStats.size)
        val l1 = s.levelStats.first()
        assertEquals(3, l1.played)
        assertEquals(2, l1.passed)
        assertEquals(80.0, l1.averageAccuracy, 0.001)
        assertEquals(listOf(3L, 4L, 5L), s.trend.map { it.id })
        assertEquals(5, s.totalGamesPlayed)
    }

    @Test
    fun emptyHistory() {
        val s = calc.calculate(emptyList(), 0, 1)
        assertEquals(0, s.currentWinStreak)
        assertEquals(0, s.currentDayStreak)
        assertEquals(0.0, s.averageAccuracy, 0.0)
    }
}
