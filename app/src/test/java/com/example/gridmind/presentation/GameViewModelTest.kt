package com.example.gridmind.presentation

import com.example.gridmind.domain.engine.GameEngine
import com.example.gridmind.domain.engine.GridGenerator
import com.example.gridmind.domain.model.GameHistory
import com.example.gridmind.domain.model.GamePhase
import com.example.gridmind.domain.model.GameResult
import com.example.gridmind.domain.model.GameSettings
import com.example.gridmind.domain.model.PlayerProgress
import com.example.gridmind.domain.model.PlayerStatistics
import com.example.gridmind.domain.model.ThemeMode
import com.example.gridmind.domain.repository.GameRepository
import com.example.gridmind.presentation.game.GameEvent
import com.example.gridmind.presentation.game.GameViewModel
import kotlin.random.Random
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

private class FakeRepository : GameRepository {
    val saved = mutableListOf<GameResult>()
    override val progress: Flow<PlayerProgress> = MutableStateFlow(PlayerProgress())
    override val settings: Flow<GameSettings> = MutableStateFlow(GameSettings())
    override val statistics: Flow<PlayerStatistics> = MutableStateFlow(PlayerStatistics())
    override suspend fun saveResult(result: GameResult, playedAt: Long) { saved += result }
    override suspend fun setSoundEnabled(enabled: Boolean) {}
    override suspend fun setVibrationEnabled(enabled: Boolean) {}
    override suspend fun setThemeMode(mode: ThemeMode) {}
    override suspend fun resetProgress() {}
}

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeRepository

    @Before fun setUp() { Dispatchers.setMain(dispatcher); repository = FakeRepository() }
    @After fun tearDown() { Dispatchers.resetMain() }

    private fun vm(level: Int = 1) =
        GameViewModel(level, GameEngine(GridGenerator(Random(5))), repository)

    @Test
    fun countdownMovesToAnswerPhase() = runTest(dispatcher) {
        val vm = vm()
        runCurrent()
        assertEquals(GamePhase.SHOWING_PATTERN, vm.uiState.value.game.phase)
        advanceTimeBy(2000)
        runCurrent()
        val mid = vm.uiState.value.game
        assertEquals(GamePhase.SHOWING_PATTERN, mid.phase)
        assertTrue(mid.remainingTimeMillis in 1..2100)
        advanceTimeBy(2100)
        runCurrent()
        assertEquals(GamePhase.WAITING_FOR_ANSWER, vm.uiState.value.game.phase)
        assertEquals(0L, vm.uiState.value.game.remainingTimeMillis)
    }

    @Test
    fun cellClicksIgnoredUntilAnswerPhaseThenToggle() = runTest(dispatcher) {
        val vm = vm()
        runCurrent()
        vm.onCellClick(0)
        assertTrue(vm.uiState.value.game.selectedPositions.isEmpty())
        advanceTimeBy(4100)
        runCurrent()
        vm.onCellClick(0)
        assertEquals(setOf(0), vm.uiState.value.game.selectedPositions)
        vm.onCellClick(0)
        assertTrue(vm.uiState.value.game.selectedPositions.isEmpty())
    }

    @Test
    fun confirmSavesResultAndEmitsNavigation() = runTest(dispatcher) {
        val vm = vm()
        val events = mutableListOf<GameEvent>()
        val job = backgroundScope.launch { vm.events.collect { e -> events += e } }
        advanceTimeBy(4100)
        runCurrent()
        vm.uiState.value.game.targetPositions.forEach { vm.onCellClick(it) }
        vm.onConfirm()
        vm.onConfirm()
        runCurrent()
        assertEquals(GamePhase.CHECKING_RESULT, vm.uiState.value.game.phase)
        advanceTimeBy(GameViewModel.FEEDBACK_MILLIS + 1)
        runCurrent()
        assertEquals(GamePhase.ROUND_COMPLETED, vm.uiState.value.game.phase)
        assertEquals(1, repository.saved.size)
        assertTrue(repository.saved.single().passed)
        assertTrue(events.last() is GameEvent.NavigateToResult)
        job.cancel()
    }

    @Test
    fun answerTimerCountsDownAndAutoSubmitsOnTimeout() = runTest(dispatcher) {
        val vm = vm()
        advanceTimeBy(4100)
        runCurrent()
        val game = vm.uiState.value.game
        assertEquals(GamePhase.WAITING_FOR_ANSWER, game.phase)
        assertEquals(8000L, game.answerTimeMillis)
        advanceTimeBy(3000)
        runCurrent()
        val mid = vm.uiState.value.game
        assertEquals(GamePhase.WAITING_FOR_ANSWER, mid.phase)
        assertTrue(mid.answerRemainingMillis in 1..5100)
        advanceTimeBy(5100)
        runCurrent()
        assertEquals(GamePhase.CHECKING_RESULT, vm.uiState.value.game.phase)
        assertTrue(!vm.uiState.value.game.result!!.passed)
        advanceTimeBy(GameViewModel.FEEDBACK_MILLIS + 1)
        runCurrent()
        assertEquals(1, repository.saved.size)
    }
}
