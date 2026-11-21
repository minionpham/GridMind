package com.example.gridmind.domain

import com.example.gridmind.domain.engine.CubeEngine
import com.example.gridmind.domain.engine.CubeGenerator
import com.example.gridmind.domain.engine.CubeLevelManager
import com.example.gridmind.domain.model.GamePhase
import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CubeEngineTest {
    private val engine = CubeEngine(CubeGenerator(Random(3)))

    @Test
    fun heightsMatchRequestedCountAndLimits() {
        val generator = CubeGenerator(Random(1))
        for ((g, h, n) in listOf(Triple(2, 2, 4), Triple(3, 3, 9), Triple(5, 4, 40), Triple(4, 1, 16))) {
            val heights = generator.generateHeights(g, h, n)
            assertEquals(g * g, heights.size)
            assertEquals(n, heights.sum())
            assertTrue(heights.all { it in 0..h })
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsTooManyCubes() {
        CubeGenerator().generateHeights(2, 2, 9)
    }

    @Test
    fun optionsAreFourDistinctPositiveAndContainAnswer() {
        val generator = CubeGenerator(Random(9))
        for (correct in 1..60) {
            val options = generator.generateOptions(correct)
            assertEquals(4, options.toSet().size)
            assertTrue(correct in options)
            assertTrue(options.all { it >= 1 })
        }
    }

    @Test
    fun levelsAreValidAndNeverShrink() {
        val manager = CubeLevelManager()
        var previous = manager.getLevel(1).cubeCount
        for (n in 1..100) {
            val level = manager.getLevel(n)
            assertTrue(level.cubeCount <= level.gridSize * level.gridSize * level.maxHeight)
            assertTrue(level.cubeCount >= previous)
            previous = level.cubeCount
        }
    }

    @Test
    fun correctAnswerPasses() {
        var s = engine.startRound(2)
        s = engine.select(s, s.cubeCount)
        s = engine.submit(s)
        assertEquals(GamePhase.CHECKING_RESULT, s.phase)
        assertTrue(s.result!!.passed)
        assertEquals(100 + s.cubeCount * 10, s.result!!.score)
        assertEquals(GamePhase.ROUND_COMPLETED, engine.completeRound(s).phase)
    }

    @Test
    fun wrongOrMissingAnswerFails() {
        val start = engine.startRound(2)
        val wrong = start.options.first { it != start.cubeCount }
        val a = engine.submit(engine.select(start, wrong))
        assertTrue(!a.result!!.passed)
        assertEquals(0, a.result!!.score)
        val b = engine.submit(start)
        assertNull(b.result!!.chosen)
        assertTrue(!b.result!!.passed)
    }

    @Test
    fun selectTogglesAndIgnoresInvalidOptions() {
        val s = engine.startRound(1)
        val v = s.options.first()
        assertEquals(v, engine.select(s, v).selected)
        assertNull(engine.select(engine.select(s, v), v).selected)
        assertNull(engine.select(s, 999).selected)
        val done = engine.submit(s)
        assertEquals(done, engine.submit(done))
        assertNull(engine.select(done, v).selected)
    }
}
