package com.example.gridmind.domain.engine

import kotlin.random.Random

class GridGenerator(private val random: Random = Random.Default) {

    fun generate(gridSize: Int, highlightCount: Int): Set<Int> {
        require(gridSize >= 1) { "gridSize must be >= 1" }
        val total = gridSize * gridSize
        require(highlightCount in 0..total) { "highlightCount must be within 0..$total" }
        return (0 until total).shuffled(random).take(highlightCount).toSet()
    }
}
