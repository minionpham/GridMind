package com.example.gridmind.domain.model

data class GameHistory(
    val id: Long,
    val level: Int,
    val score: Int,
    val correctCount: Int,
    val incorrectCount: Int,
    val missedCount: Int,
    val accuracy: Double,
    val passed: Boolean,
    val playedAt: Long
)
