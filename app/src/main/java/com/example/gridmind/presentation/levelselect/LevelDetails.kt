package com.example.gridmind.presentation.levelselect

import com.example.gridmind.domain.engine.CubeLevelManager
import com.example.gridmind.domain.engine.LevelManager
import com.example.gridmind.domain.model.GameMode
import java.util.Locale

private val levelManager = LevelManager()
private val cubeLevelManager = CubeLevelManager()

fun levelDetail(mode: GameMode, level: Int): String = when (mode) {
    GameMode.REMEMBER_THE_POSITION -> levelManager.getLevel(level).let {
        String.format(Locale.US, "%dx%d \u00B7 %d \u00B7 %.1fs", it.gridSize, it.gridSize, it.highlightCount, it.memorizeMillis / 1000f)
    }
    GameMode.COUNT_CUBES -> cubeLevelManager.getLevel(level).let {
        String.format(Locale.US, "%dx%d \u00B7 %d cubes", it.gridSize, it.gridSize, it.cubeCount)
    }
}
