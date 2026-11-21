package com.example.gridmind.domain.engine

import com.example.gridmind.domain.model.GamePhase
import com.example.gridmind.domain.model.GameResult
import com.example.gridmind.domain.model.GameState

/** Pure game rules. All operations return new immutable states. */
class GameEngine(
    private val gridGenerator: GridGenerator = GridGenerator(),
    private val scoreCalculator: ScoreCalculator = ScoreCalculator(),
    private val levelManager: LevelManager = LevelManager()
) {

    fun startRound(level: Int): GameState {
        val config = levelManager.getLevel(level)
        return GameState(
            level = config.level,
            gridSize = config.gridSize,
            targetPositions = gridGenerator.generate(config.gridSize, config.highlightCount),
            totalTimeMillis = config.memorizeMillis,
            remainingTimeMillis = config.memorizeMillis,
            phase = GamePhase.SHOWING_PATTERN
        )
    }

    fun updateRemainingTime(state: GameState, remainingMillis: Long): GameState =
        if (state.phase != GamePhase.SHOWING_PATTERN) state
        else state.copy(remainingTimeMillis = remainingMillis.coerceIn(0L, state.totalTimeMillis))

    fun finishShowing(state: GameState): GameState =
        if (state.phase != GamePhase.SHOWING_PATTERN) state
        else state.copy(remainingTimeMillis = 0L, phase = GamePhase.WAITING_FOR_ANSWER)

    fun toggleCell(state: GameState, index: Int): GameState {
        if (state.phase != GamePhase.WAITING_FOR_ANSWER) return state
        if (index !in 0 until state.gridSize * state.gridSize) return state
        val selected = if (index in state.selectedPositions) {
            state.selectedPositions - index
        } else {
            state.selectedPositions + index
        }
        return state.copy(selectedPositions = selected)
    }

    fun submit(state: GameState): GameState {
        if (state.phase != GamePhase.WAITING_FOR_ANSWER) return state
        val result = evaluate(state.level, state.targetPositions, state.selectedPositions)
        return state.copy(
            phase = GamePhase.CHECKING_RESULT,
            score = result.score,
            result = result
        )
    }

    fun completeRound(state: GameState): GameState =
        if (state.phase != GamePhase.CHECKING_RESULT) state
        else state.copy(phase = GamePhase.ROUND_COMPLETED)

    fun evaluate(level: Int, target: Set<Int>, selected: Set<Int>): GameResult {
        val correct = target intersect selected
        val incorrect = selected - target
        val missed = target - selected
        val accuracy = if (target.isEmpty()) 0.0 else correct.size.toDouble() / target.size * 100.0
        return GameResult(
            level = level,
            correctPositions = correct,
            incorrectPositions = incorrect,
            missedPositions = missed,
            accuracy = accuracy,
            score = scoreCalculator.calculate(correct.size, incorrect.size),
            passed = levelManager.isPassed(accuracy)
        )
    }

    fun retry(state: GameState): GameState = startRound(state.level)

    /** Starts the following level; only allowed after a passed round. */
    fun nextLevel(state: GameState): GameState {
        check(state.result?.passed == true) { "Next level requires a passed round" }
        return startRound(state.level + 1)
    }
}
