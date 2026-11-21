package com.example.gridmind.presentation.mode

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.gridmind.domain.model.GameMode
import com.example.gridmind.presentation.description
import com.example.gridmind.presentation.home.ModeArt
import com.example.gridmind.presentation.title
import com.example.gridmind.ui.components.GradientScreen
import com.example.gridmind.ui.components.PrimaryButton
import com.example.gridmind.ui.components.SecondaryButton

@Composable
fun ModeScreen(
    mode: GameMode,
    state: ModeUiState,
    onPlay: () -> Unit,
    onLevels: () -> Unit,
    onStatistics: () -> Unit,
    onBack: () -> Unit
) {
    GradientScreen {
        Column(Modifier.fillMaxSize()) {
            IconButton(onClick = onBack, modifier = Modifier.padding(8.dp)) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ModeArt(mode, size = 140.dp)
                Spacer(Modifier.height(16.dp))
                Text(mode.title, style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
                Text(
                    mode.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(24.dp))
                Card(
                    modifier = Modifier.widthIn(max = 400.dp).fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Stat("Level", state.highestUnlockedLevel.toString(), "home_level")
                        Stat("Best score", state.bestScore.toString(), "home_best")
                        Stat("Win streak", state.winStreak.toString(), "home_streak")
                    }
                }
                Spacer(Modifier.height(24.dp))
                Column(
                    modifier = Modifier.widthIn(max = 400.dp).fillMaxWidth().padding(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PrimaryButton(
                        "Continue \u00B7 Level ${state.highestUnlockedLevel}",
                        onPlay,
                        Modifier.fillMaxWidth().testTag("play_button")
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        SecondaryButton("Select level", onLevels, Modifier.weight(1f).testTag("levels_button"))
                        SecondaryButton("Statistics", onStatistics, Modifier.weight(1f).testTag("statistics_button"))
                    }
                }
            }
        }
    }
}

@Composable
private fun Stat(label: String, value: String, tag: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineMedium, modifier = Modifier.testTag(tag))
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
