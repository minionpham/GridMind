package com.example.gridmind.domain

import com.example.gridmind.domain.engine.GridGenerator
import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GridGeneratorTest {
    @Test
    fun generatesRequestedCountWithValidUniqueIndexes() {
        val generator = GridGenerator(Random(42))
        for ((size, count) in listOf(3 to 3, 3 to 9, 4 to 7, 5 to 15, 5 to 25, 2 to 0)) {
            val result = generator.generate(size, count)
            assertEquals(count, result.size)
            assertTrue(result.all { it in 0 until size * size })
        }
    }

    @Test
    fun isDeterministicWithSameSeed() {
        assertEquals(
            GridGenerator(Random(7)).generate(4, 5),
            GridGenerator(Random(7)).generate(4, 5)
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsMoreHighlightsThanCells() {
        GridGenerator().generate(3, 10)
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsInvalidGridSize() {
        GridGenerator().generate(0, 0)
    }
}
