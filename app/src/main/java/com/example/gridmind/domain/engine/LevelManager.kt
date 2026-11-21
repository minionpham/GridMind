package com.example.gridmind.domain.engine

import com.example.gridmind.domain.model.GameLevel
import kotlin.math.min

class LevelManager(
    private val levels: List<GameLevel> = DEFAULT_LEVELS,
    val passAccuracy: Double = 80.0
) {
    init {
        require(levels.isNotEmpty()) { "levels must not be empty" }
        require(levels.mapIndexed { i, l -> l.level == i + 1 }.all { it }) {
            "levels must be numbered consecutively from 1"
        }
    }

    fun getLevel(level: Int): GameLevel {
        require(level >= 1) { "level must be >= 1" }
        if (level <= levels.size) return levels[level - 1]
        // Beyond the table: grow on the largest grid, capped at 60% of cells.
        val last = levels.last()
        val extra = level - last.level
        val gridSize = min(last.gridSize, MAX_GRID_SIZE)
        val cap = gridSize * gridSize * 3 / 5
        val highlight = min(cap, last.highlightCount + (extra + 1) / 2).coerceAtLeast(1)
        val time = (last.memorizeMillis - extra * 100L).coerceAtLeast(MIN_MEMORIZE_MILLIS)
        // Answer time follows the highlight count but shrinks slowly, never below the minimum.
        val answer = (highlight * 2000L - extra * 200L).coerceAtLeast(GameLevel.MIN_ANSWER_MILLIS)
        return GameLevel(level, gridSize, highlight, time, answer)
    }

    fun isPassed(accuracy: Double): Boolean = accuracy >= passAccuracy

    companion object {
        const val MAX_GRID_SIZE = 5
        const val MIN_MEMORIZE_MILLIS = 1000L

        val DEFAULT_LEVELS = listOf(
            GameLevel(1, 3, 3, 4000),
            GameLevel(2, 3, 4, 3000),
            GameLevel(3, 4, 5, 3000),
            GameLevel(4, 4, 7, 2500),
            GameLevel(5, 5, 9, 2000)
        )
    }
}

