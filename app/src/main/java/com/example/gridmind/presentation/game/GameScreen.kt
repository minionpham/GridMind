package com.example.gridmind.presentation.game

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
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
    GradientScreen {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 520.dp)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
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
                remainingMillis = game.remainingTimeMillis,
                totalMillis = game.totalTimeMillis,
                active = game.phase == GamePhase.SHOWING_PATTERN
            )
            Text(
                instructionFor(game),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("instruction_text")
            )
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                MemoryGrid(
                    gridSize = game.gridSize,
                    cellStateOf = { game.cellState(it) },
                    enabled = game.phase == GamePhase.WAITING_FOR_ANSWER,
                    onCellClick = onCellClick,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Box(Modifier.heightIn(min = 56.dp).fillMaxWidth()) {
                if (game.phase == GamePhase.WAITING_FOR_ANSWER) {
                    PrimaryButton(
                        "Confirm",
                        onConfirm,
                        Modifier.fillMaxWidth().testTag("confirm_button"),
                        enabled = game.selectedPositions.isNotEmpty()
                    )
                }
            }
        }
    }
}

private fun instructionFor(game: GameState) = when (game.phase) {
    GamePhase.SHOWING_PATTERN -> "Memorize the highlighted cells"
    GamePhase.WAITING_FOR_ANSWER -> "Tap the cells you remember"
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

