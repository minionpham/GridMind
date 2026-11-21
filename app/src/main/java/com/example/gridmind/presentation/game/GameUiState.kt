package com.example.gridmind.presentation.game

import com.example.gridmind.domain.model.GameResult
import com.example.gridmind.domain.model.GameSettings
import com.example.gridmind.domain.model.GameState

data class GameUiState(
    val game: GameState = GameState(),
    val settings: GameSettings = GameSettings()
)

sealed interface GameEvent {
    data class RoundChecked(val result: GameResult) : GameEvent
    data class NavigateToResult(val result: GameResult) : GameEvent
}
