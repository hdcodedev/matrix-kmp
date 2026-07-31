package io.github.hdcodedev.matrix

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun FibiChart(
    numbers: List<Int>,
    modifier: Modifier = Modifier,
    barColor: Color = MaterialTheme.colorScheme.primary,
    barWidth: Dp = 32.dp,
    barSpacing: Dp = 8.dp,
    chartHeight: Dp = 200.dp,
) {
    if (numbers.isEmpty()) return

    val maxValue = numbers.max()

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(chartHeight),
    ) {
        Canvas(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 4.dp),
        ) {
            val barWidthPx = barWidth.toPx()
            val spacingPx = barSpacing.toPx()
            val totalWidth = barWidthPx * numbers.size + spacingPx * (numbers.size - 1)
            var startX = (size.width - totalWidth) / 2

            numbers.forEach { value ->
                val barHeight = (value.toFloat() / maxValue) * size.height
                drawRect(
                    color = barColor,
                    topLeft = Offset(startX, size.height - barHeight),
                    size = Size(barWidthPx, barHeight),
                )
                startX += barWidthPx + spacingPx
            }
        }
    }
}
