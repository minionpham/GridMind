package com.example.gridmind.presentation.game

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gridmind.domain.model.GamePhase
import com.example.gridmind.domain.model.GameResult
import com.example.gridmind.domain.model.GameState
import com.example.gridmind.presentation.game.components.CellState
import com.example.gridmind.presentation.game.components.GameTimer
import com.example.gridmind.presentation.game.components.MemoryGrid
import com.example.gridmind.ui.components.GradientScreen
import com.example.gridmind.ui.components.PrimaryButton

private const val URGENT_MILLIS = 3000L

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onRoundFinished: (GameResult) -> Unit,
    onExit: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val feedback = remember(context) { FeedbackPlayer(context) }
    val settings by rememberUpdatedState(uiState.settings)
    val finished by rememberUpdatedState(onRoundFinished)

    DisposableEffect(feedback) { onDispose { feedback.release() } }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is GameEvent.RoundChecked -> feedback.play(event.result.passed, settings)
                is GameEvent.NavigateToResult -> finished(event.result)
            }
        }
    }

    BackHandler(onBack = onExit)

    GameContent(
        game = uiState.game,
        onCellClick = viewModel::onCellClick,
        onConfirm = viewModel::onConfirm
    )
}

@Composable
fun GameContent(
    game: GameState,
    onCellClick: (Int) -> Unit,
    onConfirm: () -> Unit
) {
    val answering = game.phase == GamePhase.WAITING_FOR_ANSWER
    val grid: @Composable (Modifier) -> Unit = { modifier ->
        MemoryGrid(
            gridSize = game.gridSize,
            cellStateOf = { game.cellState(it) },
            enabled = answering,
            onCellClick = onCellClick,
            modifier = modifier
        )
    }
    val header: @Composable () -> Unit = {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                "Level ${game.level}",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.testTag("level_text")
            )
            Text(
                "Score ${game.score}",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.testTag("score_text")
            )
        }
        GameTimer(
            remainingMillis = if (answering) game.answerRemainingMillis else game.remainingTimeMillis,
            totalMillis = if (answering) game.answerTimeMillis else game.totalTimeMillis,
            active = answering || game.phase == GamePhase.SHOWING_PATTERN,
            urgent = answering && game.answerRemainingMillis <= URGENT_MILLIS
        )
        Text(
            instructionFor(game),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.testTag("instruction_text")
        )
    }
    val confirm: @Composable () -> Unit = {
        Box(Modifier.heightIn(min = 56.dp).fillMaxWidth()) {
            if (answering) {
                PrimaryButton(
                    "Confirm",
                    onConfirm,
                    Modifier.fillMaxWidth().testTag("confirm_button"),
                    enabled = game.selectedPositions.isNotEmpty()
                )
            }
        }
    }

    GradientScreen {
        AdaptiveGameLayout(
            header = header,
            board = { grid(Modifier.fillMaxSize()) },
            footer = confirm
        )
    }
}
private fun instructionFor(game: GameState) = when (game.phase) {
    GamePhase.SHOWING_PATTERN -> "Memorize the highlighted cells"
    GamePhase.WAITING_FOR_ANSWER -> "Tap the cells you remember before time runs out"
    GamePhase.CHECKING_RESULT ->
        if (game.result?.passed == true) "Well done!" else "Not quite..."
    GamePhase.ROUND_COMPLETED -> "Round complete"
}

internal fun GameState.cellState(index: Int): CellState {
    val showResult = phase == GamePhase.CHECKING_RESULT || phase == GamePhase.ROUND_COMPLETED
    return when {
        phase == GamePhase.SHOWING_PATTERN ->
            if (index in targetPositions) CellState.HIGHLIGHTED else CellState.NORMAL
        showResult && index in targetPositions ->
            if (index in selectedPositions) CellState.CORRECT else CellState.MISSED
        showResult && index in selectedPositions -> CellState.INCORRECT
        index in selectedPositions -> CellState.SELECTED
        else -> CellState.NORMAL
    }
}


