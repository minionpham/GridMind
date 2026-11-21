package com.example.gridmind.presentation.levelselect

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.example.gridmind.ui.components.GradientScreen

private const val PREVIEW_LOCKED_LEVELS = 3

@Composable
fun LevelSelectScreen(
    state: LevelSelectUiState,
    onLevelSelected: (Int) -> Unit,
    onBack: () -> Unit,
    detailFor: (Int) -> String
) {
    val levels = (1..state.highestUnlockedLevel + PREVIEW_LOCKED_LEVELS).toList()
    GradientScreen {
        Column(Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text("Select level", style = MaterialTheme.typography.headlineMedium)
            }
            LazyVerticalGrid(
                columns = GridCells.Adaptive(104.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(top = 8.dp).testTag("level_grid")
            ) {
                items(levels, key = { it }) { level ->
                    val unlocked = level <= state.highestUnlockedLevel
                    LevelCard(
                        level = level,
                        unlocked = unlocked,
                        current = level == state.highestUnlockedLevel,
                        bestScore = state.bestScores[level],
                        detail = detailFor(level),
                        onClick = { onLevelSelected(level) }
                    )
                }
            }
        }
    }
}

@Composable
private fun LevelCard(
    level: Int,
    unlocked: Boolean,
    current: Boolean,
    bestScore: Int?,
    detail: String,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp).testTag("level_$level")
    val cardColors = CardDefaults.cardColors(
        containerColor = colors.surface,
        disabledContainerColor = colors.surface.copy(alpha = 0.4f)
    )
    val border = if (current) BorderStroke(2.dp, colors.primary) else null
    val content: @Composable () -> Unit = {
        Column(
            Modifier.fillMaxWidth().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                if (unlocked) level.toString() else " $level",
                style = MaterialTheme.typography.headlineMedium,
                color = if (unlocked) colors.onSurface else colors.onSurfaceVariant
            )
            Text(
                detail,
                style = MaterialTheme.typography.bodySmall,
                color = colors.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            if (unlocked) {
                Text(
                    if (bestScore != null) "Best $bestScore" else "New",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (bestScore != null) colors.primary else colors.onSurfaceVariant
                )
            }
        }
    }
    if (unlocked) {
        Card(onClick = onClick, modifier = modifier, colors = cardColors, border = border, content = { content() })
    } else {
        Card(onClick = {}, enabled = false, modifier = modifier, colors = cardColors, content = { content() })
    }
}

