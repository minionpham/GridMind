package com.example.gridmind.domain

import com.example.gridmind.domain.engine.ScoreCalculator
import org.junit.Assert.assertEquals
import org.junit.Test

class ScoreCalculatorTest {
    private val calculator = ScoreCalculator()

    @Test
    fun specExample() = assertEquals(350, calculator.calculate(4, 1))

    @Test
    fun emptySelectionScoresZero() = assertEquals(0, calculator.calculate(0, 0))

    @Test
    fun scoreNeverNegative() = assertEquals(0, calculator.calculate(0, 5))

    @Test
    fun customRules() = assertEquals(15, ScoreCalculator(10, 5).calculate(2, 1))
}
