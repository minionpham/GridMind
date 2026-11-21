package com.example.gridmind.domain.model

data class CubeLevel(
    val level: Int,
    val gridSize: Int,
    val maxHeight: Int,
    val cubeCount: Int,
    val answerMillis: Long
) {
    init {
        require(level >= 1) { "level must be >= 1" }
        require(gridSize >= 1) { "gridSize must be >= 1" }
        require(maxHeight >= 1) { "maxHeight must be >= 1" }
        require(cubeCount in 1..(gridSize * gridSize * maxHeight)) { "cubeCount does not fit the grid" }
        require(answerMillis > 0) { "answerMillis must be positive" }
    }
}

data class CubeResult(
    val level: Int,
    val chosen: Int?,
    val correctAnswer: Int,
    val score: Int,
    val passed: Boolean
) {
    /** Maps this result onto the shared history record: one answer, either right or wrong. */
    fun toGameResult() = GameResult(
        level = level,
        correctPositions = if (passed) setOf(0) else emptySet(),
        incorrectPositions = if (passed) emptySet() else setOf(0),
        missedPositions = emptySet(),
        accuracy = if (passed) 100.0 else 0.0,
        score = score,
        passed = passed
    )
}

/** [heights] is row-major (index = y * gridSize + x); each value is the stack height of that column. */
data class CubeState(
    val level: Int = 1,
    val gridSize: Int = 2,
    val heights: List<Int> = emptyList(),
    val options: List<Int> = emptyList(),
    val selected: Int? = null,
    val answerTimeMillis: Long = 10_000L,
    val answerRemainingMillis: Long = 10_000L,
    val phase: GamePhase = GamePhase.WAITING_FOR_ANSWER,
    val result: CubeResult? = null
) {
    val cubeCount: Int get() = heights.sum()
}
