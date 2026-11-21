package com.example.gridmind.domain.model

data class GameLevel(
    val level: Int,
    val gridSize: Int,
    val highlightCount: Int,
    val memorizeMillis: Long,
    val answerMillis: Long = defaultAnswerMillis(highlightCount)
) {
    init {
        require(level >= 1) { "level must be >= 1" }
        require(gridSize >= 2) { "gridSize must be >= 2" }
        require(highlightCount in 1..(gridSize * gridSize)) {
            "highlightCount must be within 1..${gridSize * gridSize}"
        }
        require(memorizeMillis > 0) { "memorizeMillis must be positive" }
        require(answerMillis > 0) { "answerMillis must be positive" }
    }

    val cellCount: Int get() = gridSize * gridSize

    companion object {
        const val MIN_ANSWER_MILLIS = 8000L

        fun defaultAnswerMillis(highlightCount: Int): Long =
            maxOf(MIN_ANSWER_MILLIS, highlightCount * 2000L)
    }
}

