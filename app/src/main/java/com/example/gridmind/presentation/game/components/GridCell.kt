package com.example.gridmind.presentation.game.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.gridmind.ui.theme.Error
import com.example.gridmind.ui.theme.Highlight
import com.example.gridmind.ui.theme.Primary
import com.example.gridmind.ui.theme.Success

enum class CellState { NORMAL, HIGHLIGHTED, SELECTED, CORRECT, INCORRECT, MISSED }

@Composable
fun GridCell(
    index: Int,
    state: CellState,
    enabled: Boolean,
    size: Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val target = when (state) {
        CellState.NORMAL -> MaterialTheme.colorScheme.surfaceVariant
        CellState.HIGHLIGHTED -> Highlight
        CellState.SELECTED -> Primary
        CellState.CORRECT -> Success
        CellState.INCORRECT -> Error
        CellState.MISSED -> Success.copy(alpha = 0.25f)
    }
    val color by animateColorAsState(target, animationSpec = tween(200), label = "cellColor")
    val shape = RoundedCornerShape(12.dp)
    val glow = state != CellState.NORMAL && state != CellState.MISSED

    Box(
        modifier = modifier
            .size(size)
            .shadow(if (glow) 6.dp else 0.dp, shape, ambientColor = color, spotColor = color)
            .clip(shape)
            .let { if (state == CellState.MISSED) it.border(BorderStroke(2.dp, Success), shape) else it }
            .let { base -> if (state == CellState.SELECTED) base.border(2.dp, Color.White.copy(alpha = 0.7f), shape) else base }
            .background(color)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .testTag("cell_$index")
            .semantics {
                contentDescription = "Cell $index"
                stateDescription = state.name
            }
    )
}

