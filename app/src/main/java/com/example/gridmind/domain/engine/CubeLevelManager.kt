package com.example.gridmind.domain.engine

import com.example.gridmind.domain.model.CubeLevel
import kotlin.math.min

class CubeLevelManager {
    fun getLevel(level: Int): CubeLevel {
        require(level >= 1) { "level must be >= 1" }
        val gridSize = min(2 + level / 2, MAX_GRID_SIZE)
        val maxHeight = min(2 + level / 3, MAX_HEIGHT)
        val cap = (gridSize * gridSize * maxHeight / 2).coerceAtLeast(2)
        val count = min(3 + 2 * level, cap).coerceAtMost(MAX_CUBES)
        val answerMillis = (8_000L + count * 1_000L).coerceAtMost(MAX_ANSWER_MILLIS)
        return CubeLevel(level, gridSize, maxHeight, count, answerMillis)
    }

    companion object {
        const val MAX_GRID_SIZE = 5
        const val MAX_HEIGHT = 4
        const val MAX_CUBES = 40
        const val MAX_ANSWER_MILLIS = 30_000L
    }
}
