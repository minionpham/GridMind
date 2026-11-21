package com.example.gridmind.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gridmind.domain.model.PlayerProgress
import com.example.gridmind.domain.repository.GameRepository
import com.example.gridmind.ui.appContainer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(repository: GameRepository) : ViewModel() {
    val progress: StateFlow<PlayerProgress> = repository.progress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PlayerProgress())

    companion object {
        val Factory = viewModelFactory { initializer { HomeViewModel(appContainer().repository) } }
    }
}
