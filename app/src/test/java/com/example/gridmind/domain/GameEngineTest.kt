package com.example.gridmind.domain

import com.example.gridmind.domain.engine.GameEngine
import com.example.gridmind.domain.engine.GridGenerator
import com.example.gridmind.domain.model.GamePhase
import com.example.gridmind.domain.model.GameState
import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GameEngineTest {
    private val engine = GameEngine(GridGenerator(Random(1)))

    private fun waiting(level: Int = 1): GameState = engine.finishShowing(engine.startRound(level))

    @Test
    fun startRoundBuildsLevelConfig() {
        val s = engine.startRound(3)
        assertEquals(GamePhase.SHOWING_PATTERN, s.phase)
        assertEquals(4, s.gridSize)
        assertEquals(5, s.targetPositions.size)
        assertEquals(3000L, s.remainingTimeMillis)
    }

    @Test
    fun selectionIgnoredWhileShowing() {
        val s = engine.toggleCell(engine.startRound(1), 0)
        assertTrue(s.selectedPositions.isEmpty())
    }

    @Test
    fun togglesSelectionAndRejectsInvalidIndex() {
        var s = engine.toggleCell(waiting(), 2)
        assertEquals(setOf(2), s.selectedPositions)
        s = engine.toggleCell(s, 2)
        assertTrue(s.selectedPositions.isEmpty())
        assertTrue(engine.toggleCell(s, 99).selectedPositions.isEmpty())
        assertTrue(engine.toggleCell(s, -1).selectedPositions.isEmpty())
    }

    @Test
    fun phaseTransitions() {
        var s = engine.startRound(1)
        s = engine.finishShowing(s)
        assertEquals(GamePhase.WAITING_FOR_ANSWER, s.phase)
        s = engine.submit(s)
        assertEquals(GamePhase.CHECKING_RESULT, s.phase)
        assertNotNull(s.result)
        s = engine.completeRound(s)
        assertEquals(GamePhase.ROUND_COMPLETED, s.phase)
    }

    @Test
    fun submitOutsideAnswerPhaseIsIgnored() {
        val s = engine.startRound(1)
        assertEquals(s, engine.submit(s))
    }

    @Test
    fun evaluatesSpecExample() {
        val r = engine.evaluate(3, setOf(1, 3, 6, 10, 12), setOf(1, 3, 6, 10, 0))
        assertEquals(4, r.correctCount)
        assertEquals(1, r.incorrectCount)
        assertEquals(1, r.missedCount)
        assertEquals(80.0, r.accuracy, 0.001)
        assertEquals(350, r.score)
        assertTrue(r.passed)
    }

    @Test
    fun emptySelectionFails() {
        val r = engine.evaluate(1, setOf(1, 2, 3), emptySet())
        assertEquals(0.0, r.accuracy, 0.0)
        assertEquals(0, r.score)
        assertEquals(3, r.missedCount)
        assertTrue(!r.passed)
    }

    @Test
    fun belowThresholdFails() {
        assertTrue(!engine.evaluate(1, setOf(1, 2, 3, 4, 5), setOf(1, 2, 3)).passed)
    }

    @Test
    fun retryRestartsSameLevel() {
        val s = engine.submit(waiting(2))
        val retried = engine.retry(s)
        assertEquals(2, retried.level)
        assertEquals(GamePhase.SHOWING_PATTERN, retried.phase)
        assertTrue(retried.selectedPositions.isEmpty())
        assertEquals(null, retried.result)
    }

    @Test
    fun nextLevelAfterPass() {
        var s = waiting(1)
        s.targetPositions.forEach { s = engine.toggleCell(s, it) }
        s = engine.submit(s)
        assertTrue(s.result!!.passed)
        assertEquals(2, engine.nextLevel(s).level)
    }

    @Test(expected = IllegalStateException::class)
    fun nextLevelAfterFailRejected() {
        engine.nextLevel(engine.submit(waiting(1)))
    }
}
