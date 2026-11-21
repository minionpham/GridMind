package com.example.gridmind.domain.engine

import com.example.gridmind.domain.model.CubeResult
import com.example.gridmind.domain.model.CubeState
import com.example.gridmind.domain.model.GamePhase

class CubeEngine(
    private val generator: CubeGenerator = CubeGenerator(),
    private val levelManager: CubeLevelManager = CubeLevelManager()
) {
    fun startRound(level: Int): CubeState {
        val config = levelManager.getLevel(level)
        val heights = generator.generateHeights(config.gridSize, config.maxHeight, config.cubeCount)
        return CubeState(
            level = config.level,
            gridSize = config.gridSize,
            heights = heights,
            options = generator.generateOptions(heights.sum()),
            answerTimeMillis = config.answerMillis,
            answerRemainingMillis = config.answerMillis
        )
    }

    fun updateTime(state: CubeState, remainingMillis: Long): CubeState =
        if (state.phase != GamePhase.WAITING_FOR_ANSWER) state
        else state.copy(answerRemainingMillis = remainingMillis.coerceIn(0L, state.answerTimeMillis))

    /** Tapping the selected option again clears it. */
    fun select(state: CubeState, value: Int): CubeState {
        if (state.phase != GamePhase.WAITING_FOR_ANSWER || value !in state.options) return state
        return state.copy(selected = if (state.selected == value) null else value)
    }

    fun submit(state: CubeState): CubeState {
        if (state.phase != GamePhase.WAITING_FOR_ANSWER) return state
        val correct = state.cubeCount
        val passed = state.selected == correct
        val result = CubeResult(
            level = state.level,
            chosen = state.selected,
            correctAnswer = correct,
            score = if (passed) BASE_SCORE + correct * SCORE_PER_CUBE else 0,
            passed = passed
        )
        return state.copy(phase = GamePhase.CHECKING_RESULT, result = result)
    }

    fun completeRound(state: CubeState): CubeState =
        if (state.phase != GamePhase.CHECKING_RESULT) state
        else state.copy(phase = GamePhase.ROUND_COMPLETED)

    companion object {
        const val BASE_SCORE = 100
        const val SCORE_PER_CUBE = 10
    }
}
