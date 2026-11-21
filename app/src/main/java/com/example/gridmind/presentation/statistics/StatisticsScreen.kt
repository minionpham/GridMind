package com.example.gridmind.presentation.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.gridmind.domain.model.GameHistory
import com.example.gridmind.domain.model.LevelStats
import com.example.gridmind.domain.model.PlayerStatistics
import com.example.gridmind.ui.components.GradientScreen
import com.example.gridmind.ui.theme.Error
import com.example.gridmind.ui.theme.Success
import java.text.DateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StatisticsScreen(stats: PlayerStatistics, onBack: () -> Unit) {
    GradientScreen {
        Column(Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text("Statistics", style = MaterialTheme.typography.headlineMedium)
            }
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Line("Highest unlocked level", stats.highestUnlockedLevel.toString())
                            Line("Best score", stats.bestScore.toString())
                            Line("Completed rounds", stats.totalCompletedRounds.toString())
                            Line("Games played", stats.totalGamesPlayed.toString())
                            Line("Average accuracy", String.format(Locale.US, "%.0f%%", stats.averageAccuracy))
                        }
                    }
                }
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Line("Win streak", "${stats.currentWinStreak} (best ${stats.bestWinStreak})", "win_streak")
                            Line("Daily streak", "${stats.currentDayStreak} (best ${stats.bestDayStreak})", "day_streak")
                        }
                    }
                }
                item { TrendCard(stats) }
                item { LevelStatsCard(stats.levelStats) }
                item { Text("Recent games", style = MaterialTheme.typography.titleLarge) }
                if (stats.recentGames.isEmpty()) {
                    item {
                        Text(
                            "No games played yet.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.testTag("empty_history")
                        )
                    }
                }
                items(stats.recentGames, key = { it.id }) { HistoryRow(it) }
            }
        }
    }
}

@Composable
private fun Line(label: String, value: String, tag: String? = null) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            value,
            style = MaterialTheme.typography.titleLarge,
            modifier = if (tag != null) Modifier.testTag(tag) else Modifier
        )
    }
}

@Composable
private fun TrendCard(stats: PlayerStatistics) {
    var metric by rememberSaveable { mutableStateOf(TrendMetric.ACCURACY) }
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Progress", style = MaterialTheme.typography.titleLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TrendMetric.entries.forEach {
                    FilterChip(
                        selected = metric == it,
                        onClick = { metric = it },
                        label = { Text(if (it == TrendMetric.ACCURACY) "Accuracy" else "Score") }
                    )
                }
            }
            if (stats.trend.isEmpty()) {
                Text("Play a round to see your progress.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                TrendChart(stats.trend, metric)
                Text(
                    "Last ${stats.trend.size} games",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun LevelStatsCard(levels: List<LevelStats>) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("By level", style = MaterialTheme.typography.titleLarge)
            if (levels.isEmpty()) {
                Text("No data yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                Row(Modifier.fillMaxWidth()) {
                    HeaderCell("Level", Modifier.weight(1f))
                    HeaderCell("Played", Modifier.weight(1f))
                    HeaderCell("Win rate", Modifier.weight(1f))
                    HeaderCell("Accuracy", Modifier.weight(1f))
                }
                levels.forEach {
                    Row(Modifier.fillMaxWidth().testTag("level_row_${it.level}")) {
                        Text(it.level.toString(), Modifier.weight(1f))
                        Text(it.played.toString(), Modifier.weight(1f))
                        Text(String.format(Locale.US, "%.0f%%", it.winRate), Modifier.weight(1f))
                        Text(String.format(Locale.US, "%.0f%%", it.averageAccuracy), Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderCell(text: String, modifier: Modifier) {
    Text(
        text,
        modifier = modifier,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun HistoryRow(game: GameHistory) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Level ${game.level}  ${game.score} pts", style = MaterialTheme.typography.bodyLarge)
                Text(
                    DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(Date(game.playedAt)),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                if (game.passed) "PASS" else "FAILED",
                color = if (game.passed) Success else Error,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

