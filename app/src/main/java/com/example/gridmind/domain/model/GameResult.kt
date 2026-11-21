package com.example.gridmind.domain.model

data class GameResult(
    val level: Int,
    val correctPositions: Set<Int>,
    val incorrectPositions: Set<Int>,
    val missedPositions: Set<Int>,
    val accuracy: Double,
    val score: Int,
    val passed: Boolean
) {
    val correctCount: Int get() = correctPositions.size
    val incorrectCount: Int get() = incorrectPositions.size
    val missedCount: Int get() = missedPositions.size
}
