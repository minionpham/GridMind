package com.example.gridmind.presentation.game.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import java.util.Locale

@Composable
fun GameTimer(
    remainingMillis: Long,
    totalMillis: Long,
    active: Boolean,
    modifier: Modifier = Modifier
) {
    val progress = if (totalMillis > 0) remainingMillis.toFloat() / totalMillis else 0f
    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = if (active) String.format(Locale.US, "%.1fs", remainingMillis / 1000f) else "--",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.testTag("game_timer")
        )
        LinearProgressIndicator(
            progress = { if (active) progress else 0f },
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
                .height(6.dp)
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(3.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            drawStopIndicator = {}
        )
    }
}
