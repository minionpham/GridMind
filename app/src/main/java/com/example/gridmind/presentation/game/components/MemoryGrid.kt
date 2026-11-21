package com.example.gridmind.presentation.game.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MemoryGrid(
    gridSize: Int,
    cellStateOf: (Int) -> CellState,
    enabled: Boolean,
    onCellClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = if (gridSize >= 5) 6.dp else 8.dp
    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        val side = minOf(maxWidth, maxHeight)
        val cellSize = (side - spacing * (gridSize - 1)) / gridSize
        Column(verticalArrangement = Arrangement.spacedBy(spacing)) {
            repeat(gridSize) { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(spacing)) {
                    repeat(gridSize) { col ->
                        val index = row * gridSize + col
                        GridCell(
                            index = index,
                            state = cellStateOf(index),
                            enabled = enabled,
                            size = cellSize,
                            onClick = { onCellClick(index) }
                        )
                    }
                }
            }
        }
    }
}
