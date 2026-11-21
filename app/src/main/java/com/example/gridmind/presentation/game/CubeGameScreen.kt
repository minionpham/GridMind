package com.example.gridmind.presentation.game

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gridmind.domain.model.CubeResult
import com.example.gridmind.domain.model.CubeState
import com.example.gridmind.domain.model.GamePhase
import com.example.gridmind.presentation.game.components.CubeStack
import com.example.gridmind.presentation.game.components.GameTimer
import com.example.gridmind.ui.components.GradientScreen
import com.example.gridmind.ui.components.PrimaryButton
import com.example.gridmind.ui.theme.Error
import com.example.gridmind.ui.theme.Success

private const val URGENT_MILLIS = 3000L

@Composable
fun CubeGameScreen(
    viewModel: CubeGameViewModel,
    onRoundFinished: (CubeResult) -> Unit,
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
                is CubeGameEvent.RoundChecked -> feedback.play(event.result.passed, settings)
                is CubeGameEvent.NavigateToResult -> finished(event.result)
            }
        }
    }

    BackHandler(onBack = onExit)

    CubeGameContent(
        game = uiState.game,
        onOptionClick = viewModel::onOptionClick,
        onConfirm = viewModel::onConfirm
    )
}

@Composable
fun CubeGameContent(
    game: CubeState,
    onOptionClick: (Int) -> Unit,
    onConfirm: () -> Unit
) {
    val answering = game.phase == GamePhase.WAITING_FOR_ANSWER
    val header: @Composable () -> Unit = {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Level ${game.level}", style = MaterialTheme.typography.titleLarge, modifier = Modifier.testTag("level_text"))
            Text("Count the cubes", style = MaterialTheme.typography.titleLarge)
        }
        GameTimer(
            remainingMillis = game.answerRemainingMillis,
            totalMillis = game.answerTimeMillis,
            active = answering,
            urgent = answering && game.answerRemainingMillis <= URGENT_MILLIS
        )
        Text(
            if (answering) "How many cubes are there? Hidden ones count too!"
            else if (game.result?.passed == true) "Correct!" else "Not quite...",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.testTag("instruction_text")
        )
    }
    val footer: @Composable () -> Unit = {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            game.options.forEach { value ->
                OptionButton(
                    value = value,
                    game = game,
                    onClick = { onOptionClick(value) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Box(Modifier.heightIn(min = 56.dp).fillMaxWidth()) {
            if (answering) {
                PrimaryButton(
                    "Confirm",
                    onConfirm,
                    Modifier.fillMaxWidth().testTag("confirm_button"),
                    enabled = game.selected != null
                )
            }
        }
    }
    GradientScreen {
        AdaptiveGameLayout(
            header = header,
            board = { CubeStack(game.gridSize, game.heights, Modifier.fillMaxSize()) },
            footer = footer
        )
    }
}
@Composable
private fun OptionButton(value: Int, game: CubeState, onClick: () -> Unit, modifier: Modifier) {
    val checked = game.phase != GamePhase.WAITING_FOR_ANSWER
    val selected = game.selected == value
    val container = when {
        checked && value == game.cubeCount -> Success
        checked && selected -> Error
        selected -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    Button(
        onClick = onClick,
        enabled = !checked,
        modifier = modifier.heightIn(min = 64.dp).testTag("option_$value"),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = container,
            disabledContainerColor = container,
            contentColor = Color.White,
            disabledContentColor = Color.White
        )
    ) {
        Text(value.toString(), style = MaterialTheme.typography.titleLarge)
    }
}
