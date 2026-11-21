package com.example.gridmind.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gridmind.domain.engine.CubeEngine
import com.example.gridmind.domain.model.CubeResult
import com.example.gridmind.domain.model.CubeState
import com.example.gridmind.domain.model.GameMode
import com.example.gridmind.domain.model.GamePhase
import com.example.gridmind.domain.model.GameSettings
import com.example.gridmind.domain.repository.GameRepository
import com.example.gridmind.ui.appContainer
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CubeUiState(
    val game: CubeState = CubeState(),
    val settings: GameSettings = GameSettings()
)

sealed interface CubeGameEvent {
    data class RoundChecked(val result: CubeResult) : CubeGameEvent
    data class NavigateToResult(val result: CubeResult) : CubeGameEvent
}

class CubeGameViewModel(
    level: Int,
    private val engine: CubeEngine,
    private val repository: GameRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CubeUiState(game = engine.startRound(level)))
    val uiState: StateFlow<CubeUiState> = _uiState.asStateFlow()

    private val _events = Channel<CubeGameEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            repository.settings.collect { settings -> _uiState.update { it.copy(settings = settings) } }
        }
        startCountdown()
    }

    fun onOptionClick(value: Int) {
        _uiState.update { it.copy(game = engine.select(it.game, value)) }
    }

    fun onConfirm() {
        if (_uiState.value.game.phase != GamePhase.WAITING_FOR_ANSWER) return
        timerJob?.cancel()
        _uiState.update { it.copy(game = engine.submit(it.game)) }
        val result = _uiState.value.game.result ?: return
        viewModelScope.launch {
            _events.send(CubeGameEvent.RoundChecked(result))
            runCatching { repository.saveResult(result.toGameResult(), GameMode.COUNT_CUBES) }
            delay(FEEDBACK_MILLIS)
            _uiState.update { it.copy(game = engine.completeRound(it.game)) }
            _events.send(CubeGameEvent.NavigateToResult(result))
        }
    }

    private fun startCountdown() {
        timerJob = viewModelScope.launch {
            var remaining = _uiState.value.game.answerTimeMillis
            while (remaining > 0) {
                delay(TICK_MILLIS)
                remaining -= TICK_MILLIS
                _uiState.update { it.copy(game = engine.updateTime(it.game, remaining)) }
            }
            // Time is up: submit the current choice (or none).
            onConfirm()
        }
    }

    companion object {
        const val TICK_MILLIS = 50L
        const val FEEDBACK_MILLIS = 1500L

        val Factory = viewModelFactory {
            initializer {
                val level = createSavedStateHandle().get<Int>("level") ?: 1
                CubeGameViewModel(level, CubeEngine(), appContainer().repository)
            }
        }
    }
}
