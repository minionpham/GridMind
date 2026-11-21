package com.example.gridmind.presentation.game.components

import androidx.compose.foundation.Canvas
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.testTag
import com.example.gridmind.ui.theme.Background
import com.example.gridmind.ui.theme.Highlight
import com.example.gridmind.ui.theme.Primary

private val RightFace = Color(0xFF2563C9)

/**
 * Draws columns of cubes in isometric projection. [heights] is row-major
 * (index = y * gridSize + x); columns are drawn back to front so front cubes hide rear ones.
 */
@Composable
fun CubeStack(gridSize: Int, heights: List<Int>, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.testTag("cube_stack")) {
        val maxHeight = (heights.maxOrNull() ?: 0).coerceAtLeast(1)
        val w = minOf(size.width / gridSize, size.height / ((gridSize + maxHeight) / 2f))
        val e = w / 2f
        val totalHeight = (gridSize + maxHeight) * w / 2f
        val originX = size.width / 2f
        val originY = (size.height - totalHeight) / 2f + (maxHeight - 1) * e

        for (sum in 0..2 * (gridSize - 1)) {
            for (x in 0 until gridSize) {
                val y = sum - x
                if (y !in 0 until gridSize) continue
                val stack = heights.getOrElse(y * gridSize + x) { 0 }
                for (z in 0 until stack) {
                    drawCube(
                        cx = originX + (x - y) * w / 2f,
                        ty = originY + (x + y) * w / 4f - z * e,
                        w = w,
                        e = e
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawCube(cx: Float, ty: Float, w: Float, e: Float) {
    val hw = w / 2f
    val q = w / 4f
    val top = Path().apply {
        moveTo(cx, ty); lineTo(cx + hw, ty + q); lineTo(cx, ty + 2 * q); lineTo(cx - hw, ty + q); close()
    }
    val left = Path().apply {
        moveTo(cx - hw, ty + q); lineTo(cx, ty + 2 * q); lineTo(cx, ty + 2 * q + e); lineTo(cx - hw, ty + q + e); close()
    }
    val right = Path().apply {
        moveTo(cx, ty + 2 * q); lineTo(cx + hw, ty + q); lineTo(cx + hw, ty + q + e); lineTo(cx, ty + 2 * q + e); close()
    }
    drawPath(top, Highlight)
    drawPath(left, Primary)
    drawPath(right, RightFace)
    val edge = Stroke(width = maxOf(1f, w * 0.03f), join = StrokeJoin.Round)
    listOf(top, left, right).forEach { drawPath(it, Background, style = edge) }
}
