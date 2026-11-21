package com.example.gridmind.presentation.mode

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

data class ModeUiState(
    val highestUnlockedLevel: Int = 1,
    val bestScore: Int = 0,
    val winStreak: Int = 0
)

class ModeViewModel(mode: GameMode, repository: GameRepository) : ViewModel() {
    val uiState: StateFlow<ModeUiState> = repository.statistics(mode)
        .map { ModeUiState(it.highestUnlockedLevel, it.bestScore, it.currentWinStreak) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ModeUiState())

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val mode = GameMode.valueOf(
                    createSavedStateHandle().get<String>("mode") ?: GameMode.REMEMBER_THE_POSITION.name
                )
                ModeViewModel(mode, appContainer().repository)
            }
        }
    }
}
