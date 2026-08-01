package io.github.hdcodedev.matrix.sample.screenshot

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import io.github.hdcodedev.matrix.FibiChart
import io.github.hdcodedev.matrix.sample.screenshot.shared.ScreenshotPreview
import io.github.hdcodedev.matrix.sample.screenshot.shared.ScreenshotSurface

@PreviewTest
@ScreenshotPreview
@Composable
fun FibiChartDefaultPreview() {
    ScreenshotSurface {
        FibiChart(numbers = listOf(1, 2, 3, 5, 8))
    }
}

@PreviewTest
@ScreenshotPreview
@Composable
fun FibiChartSingleBarPreview() {
    ScreenshotSurface {
        FibiChart(numbers = listOf(42))
    }
}

@PreviewTest
@ScreenshotPreview
@Composable
fun FibiChartWithLargerDatasetPreview() {
    ScreenshotSurface {
        FibiChart(numbers = listOf(1, 2, 3, 5, 8, 13, 21, 34))
    }
}
