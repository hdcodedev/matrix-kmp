package io.github.hdcodedev.matrix

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.sqrt

private const val GRID_SIZE = 5
private const val CENTER_INDEX = GRID_SIZE / 2
private const val CYCLE_MILLIS = 1_600L
private const val PULSE_TRAVEL = 1.1f
private const val PULSE_TRAIL = 0.28f

private val DefaultMatrixPulseColor = Color(0xFF5B8CFF)

/**
 * A compact 5x5 loading indicator with an original centre-to-edge pulse.
 *
 * The loader is drawn entirely in common Compose code, so the same geometry and animation
 * behavior is used by Android and iOS consumers.
 *
 * @param speed Multiplier for the animation speed. Non-positive and non-finite values use the
 * default speed.
 * @param animated Whether the pulse should animate. When false, the initial centre-bright frame
 * is rendered.
 * @param contentDescription Accessibility description for the loader, or null to omit it.
 */
@Composable
fun MatrixPulse(
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
    color: Color = DefaultMatrixPulseColor,
    speed: Float = 1f,
    animated: Boolean = true,
    contentDescription: String? = "Loading",
) {
    val safeSpeed = speed.takeIf { it > 0f && it.isFinite() } ?: 1f
    val safeSize = size.takeIf { it > 0.dp && it.value.isFinite() } ?: 1.dp
    val cycleNanos = (CYCLE_MILLIS * 1_000_000L / safeSpeed).toLong().coerceAtLeast(1L)
    var elapsedNanos by remember { mutableLongStateOf(0L) }

    LaunchedEffect(animated, cycleNanos) {
        elapsedNanos = 0L
        if (!animated) return@LaunchedEffect

        var startNanos: Long? = null
        while (true) {
            withFrameNanos { frameNanos ->
                val firstFrameNanos = startNanos
                if (firstFrameNanos == null) {
                    startNanos = frameNanos
                    elapsedNanos = 0L
                } else {
                    elapsedNanos = frameNanos - firstFrameNanos
                }
            }
        }
    }

    val phase =
        if (animated) {
            (elapsedNanos % cycleNanos).toFloat() / cycleNanos.toFloat()
        } else {
            0f
        }

    Box(
        modifier =
            modifier
                .size(safeSize)
                .then(
                    if (contentDescription == null) {
                        Modifier
                    } else {
                        Modifier.semantics {
                            this.contentDescription = contentDescription
                        }
                    },
                ),
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val dimension = minOf(this.size.width, this.size.height)
            val dotDiameter = dimension * 0.14f
            val dotRadius = dotDiameter / 2f
            val cellStep = dimension * 0.18f
            val gridSpan = cellStep * (GRID_SIZE - 1)
            val gridOrigin = (dimension - gridSpan) / 2f

            for (row in 0 until GRID_SIZE) {
                for (column in 0 until GRID_SIZE) {
                    val opacity = matrixPulseOpacity(row, column, phase)
                    drawCircle(
                        color = color,
                        radius = dotRadius,
                        center =
                            Offset(
                                x = gridOrigin + column * cellStep,
                                y = gridOrigin + row * cellStep,
                            ),
                        alpha = opacity,
                    )
                }
            }
        }
    }
}

internal fun matrixPulseOpacity(
    row: Int,
    column: Int,
    phase: Float,
): Float {
    require(row in 0 until GRID_SIZE)
    require(column in 0 until GRID_SIZE)

    val rowDistance = (row - CENTER_INDEX).toFloat()
    val columnDistance = (column - CENTER_INDEX).toFloat()
    val normalizedDistance =
        sqrt(rowDistance * rowDistance + columnDistance * columnDistance) / sqrt(8f)
    val progress = phase.coerceIn(0f, 1f) * PULSE_TRAVEL
    val behindHead = progress - normalizedDistance
    val pulse =
        if (behindHead in 0f..PULSE_TRAIL) {
            (1f - behindHead / PULSE_TRAIL).coerceIn(0f, 1f)
        } else {
            0f
        }
    val easedPulse = pulse * pulse * (3f - 2f * pulse)
    val baseOpacity = 0.12f + (1f - normalizedDistance) * 0.05f

    return (baseOpacity + easedPulse * 0.83f).coerceIn(0f, 1f)
}
