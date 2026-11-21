package com.example.gridmind.domain.model

enum class GamePhase {
    SHOWING_PATTERN,
    WAITING_FOR_ANSWER,
    CHECKING_RESULT,
    ROUND_COMPLETED
}

data class GameState(
    val level: Int = 1,
    val gridSize: Int = 3,
    val targetPositions: Set<Int> = emptySet(),
    val selectedPositions: Set<Int> = emptySet(),
    val totalTimeMillis: Long = 4000L,
    val remainingTimeMillis: Long = 4000L,
    val answerTimeMillis: Long = 8000L,
    val answerRemainingMillis: Long = 8000L,
    val score: Int = 0,
    val phase: GamePhase = GamePhase.SHOWING_PATTERN,
    val result: GameResult? = null
)

