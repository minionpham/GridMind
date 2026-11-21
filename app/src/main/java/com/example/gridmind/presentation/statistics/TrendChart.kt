package com.example.gridmind.presentation.statistics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.gridmind.domain.model.GameHistory
import com.example.gridmind.ui.theme.Error
import com.example.gridmind.ui.theme.Success

enum class TrendMetric { ACCURACY, SCORE }

/** Line chart of [games] (oldest to newest). Accuracy is plotted on a fixed 0-100 axis. */
@Composable
fun TrendChart(games: List<GameHistory>, metric: TrendMetric, modifier: Modifier = Modifier) {
    val lineColor = MaterialTheme.colorScheme.primary
    val gridColor = MaterialTheme.colorScheme.surfaceVariant
    val values = games.map { if (metric == TrendMetric.ACCURACY) it.accuracy else it.score.toDouble() }
    val maxValue = if (metric == TrendMetric.ACCURACY) 100.0 else maxOf(values.maxOrNull() ?: 0.0, 1.0)

    Canvas(modifier = modifier.fillMaxWidth().height(160.dp).padding(8.dp).testTag("trend_chart")) {
        fun y(v: Double) = size.height - (v / maxValue * size.height).toFloat()
        fun x(i: Int) = if (values.size == 1) size.width / 2 else size.width * i / (values.size - 1)

        listOf(0.0, 0.5, 1.0).forEach {
            drawLine(gridColor, Offset(0f, y(maxValue * it)), Offset(size.width, y(maxValue * it)), 2f)
        }
        if (metric == TrendMetric.ACCURACY) {
            drawLine(
                Success.copy(alpha = 0.5f), Offset(0f, y(80.0)), Offset(size.width, y(80.0)), 2f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f))
            )
        }
        if (values.size > 1) {
            val path = Path().apply {
                values.forEachIndexed { i, v -> if (i == 0) moveTo(x(i), y(v)) else lineTo(x(i), y(v)) }
            }
            drawPath(path, lineColor, style = Stroke(width = 5f))
        }
        values.forEachIndexed { i, v ->
            drawCircle(if (games[i].passed) Success else Error, radius = 8f, center = Offset(x(i), y(v)))
        }
    }
}
