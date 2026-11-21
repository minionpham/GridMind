package com.example.gridmind.presentation.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gridmind.domain.model.PlayerStatistics
import com.example.gridmind.domain.repository.GameRepository
import com.example.gridmind.ui.appContainer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class StatisticsViewModel(repository: GameRepository) : ViewModel() {
    val statistics: StateFlow<PlayerStatistics> = repository.statistics
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PlayerStatistics())

    companion object {
        val Factory = viewModelFactory { initializer { StatisticsViewModel(appContainer().repository) } }
    }
}
