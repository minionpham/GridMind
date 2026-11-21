package com.example.gridmind.presentation.home

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.gridmind.domain.model.PlayerProgress
import com.example.gridmind.ui.components.GradientScreen
import com.example.gridmind.ui.components.PrimaryButton
import com.example.gridmind.ui.components.SecondaryButton

@Composable
fun HomeScreen(
    progress: PlayerProgress,
    onPlay: () -> Unit,
    onLevels: () -> Unit,
    onStatistics: () -> Unit,
    onSettings: () -> Unit
) {
    GradientScreen {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "GRID MIND",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Text(
                "Train your visual memory",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(32.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.widthIn(max = 400.dp)) {
                StatCard("Level", progress.highestUnlockedLevel.toString(), "home_level", Modifier.weight(1f))
                StatCard("Best score", progress.bestScore.toString(), "home_best", Modifier.weight(1f))
            }
            Spacer(Modifier.height(32.dp))
            Column(
                modifier = Modifier.widthIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PrimaryButton(
                    "Continue  Level ${progress.highestUnlockedLevel}",
                    onPlay,
                    Modifier.fillMaxWidth().testTag("play_button")
                )
                SecondaryButton("Select level", onLevels, Modifier.fillMaxWidth().testTag("levels_button"))
                SecondaryButton("Statistics", onStatistics, Modifier.fillMaxWidth().testTag("statistics_button"))
                SecondaryButton("Settings", onSettings, Modifier.fillMaxWidth().testTag("settings_button"))
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, tag: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.headlineMedium, modifier = Modifier.testTag(tag))
        }
    }
}

