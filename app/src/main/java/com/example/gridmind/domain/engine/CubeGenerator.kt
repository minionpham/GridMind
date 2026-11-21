package com.example.gridmind.domain.engine

import kotlin.random.Random

class CubeGenerator(private val random: Random = Random.Default) {

    /** Places cubes one by one on random columns, so every cube rests on the floor or another cube. */
    fun generateHeights(gridSize: Int, maxHeight: Int, cubeCount: Int): List<Int> {
        val columns = gridSize * gridSize
        require(cubeCount in 1..(columns * maxHeight)) { "cubeCount does not fit the grid" }
        val heights = IntArray(columns)
        repeat(cubeCount) {
            val open = (0 until columns).filter { heights[it] < maxHeight }
            heights[open.random(random)]++
        }
        return heights.toList()
    }

    /** Four distinct positive choices including [correct], shuffled. */
    fun generateOptions(correct: Int): List<Int> {
        require(correct >= 1) { "correct must be >= 1" }
        val distractors = ((correct - 3)..(correct + 3))
            .filter { it >= 1 && it != correct }
            .shuffled(random)
            .take(3)
        return (distractors + correct).shuffled(random)
    }
}
