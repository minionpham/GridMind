package com.example.gridmind.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gridmind.domain.engine.GameEngine
import com.example.gridmind.domain.model.GamePhase
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

class GameViewModel(
    level: Int,
    private val engine: GameEngine,
    private val repository: GameRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState(game = engine.startRound(level)))
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val _events = Channel<GameEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            repository.settings.collect { settings -> _uiState.update { it.copy(settings = settings) } }
        }
        startCountdown()
    }

    fun onCellClick(index: Int) {
        _uiState.update { it.copy(game = engine.toggleCell(it.game, index)) }
    }

    fun onConfirm() {
        if (_uiState.value.game.phase != GamePhase.WAITING_FOR_ANSWER) return
        _uiState.update { it.copy(game = engine.submit(it.game)) }
        val result = _uiState.value.game.result ?: return
        viewModelScope.launch {
            _events.send(GameEvent.RoundChecked(result))
            // Persistence failure must not trap the player on this screen.
            runCatching { repository.saveResult(result) }
            delay(FEEDBACK_MILLIS)
            _uiState.update { it.copy(game = engine.completeRound(it.game)) }
            _events.send(GameEvent.NavigateToResult(result))
        }
    }

    private fun startCountdown() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var remaining = _uiState.value.game.totalTimeMillis
            while (remaining > 0) {
                delay(TICK_MILLIS)
                remaining -= TICK_MILLIS
                _uiState.update { it.copy(game = engine.updateRemainingTime(it.game, remaining)) }
            }
            _uiState.update { it.copy(game = engine.finishShowing(it.game)) }
        }
    }

    companion object {
        const val TICK_MILLIS = 50L
        const val FEEDBACK_MILLIS = 1500L

        val Factory = viewModelFactory {
            initializer {
                val level = createSavedStateHandle().get<Int>("level") ?: 1
                val container = appContainer()
                GameViewModel(level, container.createGameEngine(), container.repository)
            }
        }
    }
}
