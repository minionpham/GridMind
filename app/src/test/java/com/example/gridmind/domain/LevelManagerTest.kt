package com.example.gridmind.domain

import com.example.gridmind.domain.engine.LevelManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LevelManagerTest {
    private val manager = LevelManager()

    @Test
    fun initialLevelsMatchSpec() {
        val l1 = manager.getLevel(1)
        assertEquals(3, l1.gridSize); assertEquals(3, l1.highlightCount); assertEquals(4000L, l1.memorizeMillis)
        val l5 = manager.getLevel(5)
        assertEquals(5, l5.gridSize); assertEquals(9, l5.highlightCount); assertEquals(2000L, l5.memorizeMillis)
    }

    @Test
    fun levelsBeyondTableStayValidAndNeverEasier() {
        var previous = manager.getLevel(5)
        for (n in 6..200) {
            val level = manager.getLevel(n)
            assertTrue(level.highlightCount <= level.cellCount)
            assertTrue(level.highlightCount >= previous.highlightCount)
            assertTrue(level.memorizeMillis <= previous.memorizeMillis)
            assertTrue(level.memorizeMillis >= LevelManager.MIN_MEMORIZE_MILLIS)
            previous = level
        }
    }

    @Test
    fun passThresholdIsInclusive() {
        assertTrue(manager.isPassed(80.0))
        assertFalse(manager.isPassed(79.9))
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsInvalidLevel() {
        manager.getLevel(0)
    }
}
