package com.example.gridmind.presentation.levelselect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gridmind.domain.model.GameMode
import com.example.gridmind.domain.repository.GameRepository
import com.example.gridmind.ui.appContainer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class LevelSelectUiState(
    val highestUnlockedLevel: Int = 1,
    val bestScores: Map<Int, Int> = emptyMap()
)

class LevelSelectViewModel(mode: GameMode, repository: GameRepository) : ViewModel() {
    val uiState: StateFlow<LevelSelectUiState> = repository.statistics(mode)
        .map { stats ->
            LevelSelectUiState(
                highestUnlockedLevel = stats.highestUnlockedLevel,
                bestScores = stats.levelStats.associate { it.level to it.bestScore }
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), LevelSelectUiState())

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val mode = GameMode.valueOf(createSavedStateHandle().get<String>("mode") ?: GameMode.REMEMBER_THE_POSITION.name)
                LevelSelectViewModel(mode, appContainer().repository)
            }
        }
    }
}
