package com.example.gridmind.domain.model

enum class ThemeMode { SYSTEM, DARK, LIGHT }

data class GameSettings(
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val themeMode: ThemeMode = ThemeMode.DARK
)

data class PlayerProgress(
    val bestScore: Int = 0,
    val highestUnlockedLevel: Int = 1
)
