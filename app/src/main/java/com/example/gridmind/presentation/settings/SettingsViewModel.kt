package com.example.gridmind.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gridmind.domain.model.GameSettings
import com.example.gridmind.domain.model.ThemeMode
import com.example.gridmind.domain.repository.GameRepository
import com.example.gridmind.ui.appContainer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: GameRepository) : ViewModel() {
    val settings: StateFlow<GameSettings> = repository.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), GameSettings())

    fun setSound(enabled: Boolean) = viewModelScope.launch { repository.setSoundEnabled(enabled) }
    fun setVibration(enabled: Boolean) = viewModelScope.launch { repository.setVibrationEnabled(enabled) }
    fun setTheme(mode: ThemeMode) = viewModelScope.launch { repository.setThemeMode(mode) }
    fun resetProgress() = viewModelScope.launch { repository.resetProgress() }

    companion object {
        val Factory = viewModelFactory { initializer { SettingsViewModel(appContainer().repository) } }
    }
}
