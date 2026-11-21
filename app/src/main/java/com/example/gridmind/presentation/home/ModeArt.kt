package com.example.gridmind.presentation.home

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import com.example.gridmind.domain.model.GameMode
import com.example.gridmind.presentation.game.components.CubeStack

private val PreviewHeights = listOf(2, 1, 0, 1, 3, 1, 0, 1, 2)
private val LitCells = setOf(0, 4, 5, 7)
private val LitColor = Color(0xFF55A8FF)

/** Small illustration representing a game mode. */
@Composable
fun ModeArt(mode: GameMode, size: Dp, modifier: Modifier = Modifier) {
    when (mode) {
        GameMode.REMEMBER_THE_POSITION -> GridArt(modifier.size(size))
        GameMode.COUNT_CUBES -> CubeStack(3, PreviewHeights, modifier.size(size))
    }
}

@Composable
private fun GridArt(modifier: Modifier) {
    val idle = MaterialTheme.colorScheme.surfaceVariant
    Canvas(modifier) {
        val gap = size.width * 0.06f
        val cell = (size.width - gap * 2) / 3f
        for (i in 0 until 9) {
            drawRoundRect(
                color = if (i in LitCells) LitColor else idle,
                topLeft = Offset((i % 3) * (cell + gap), (i / 3) * (cell + gap)),
                size = Size(cell, cell),
                cornerRadius = CornerRadius(cell * 0.22f)
            )
        }
    }
}

/** Isometric cube cluster that floats gently; used as the app logo. */
@Composable
fun CubeLogo(size: Dp, modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "logo")
    val offset by transition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(tween(2200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "logoOffset"
    )
    CubeStack(
        gridSize = 3,
        heights = PreviewHeights,
        modifier = modifier.size(size).graphicsLayer { translationY = offset }
    )
}
