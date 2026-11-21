package com.example.gridmind.domain.engine

class ScoreCalculator(
    private val pointsPerCorrect: Int = 100,
    private val penaltyPerIncorrect: Int = 50
) {
    fun calculate(correctCount: Int, incorrectCount: Int): Int =
        (correctCount * pointsPerCorrect - incorrectCount * penaltyPerIncorrect).coerceAtLeast(0)
}
