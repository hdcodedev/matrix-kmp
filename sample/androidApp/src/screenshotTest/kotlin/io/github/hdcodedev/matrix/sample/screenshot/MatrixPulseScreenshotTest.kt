package io.github.hdcodedev.matrix.sample.screenshot

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.android.tools.screenshot.PreviewTest
import io.github.hdcodedev.matrix.MatrixPulse
import io.github.hdcodedev.matrix.sample.screenshot.shared.ScreenshotPreview
import io.github.hdcodedev.matrix.sample.screenshot.shared.ScreenshotSurface

@PreviewTest
@ScreenshotPreview
@Composable
fun MatrixPulseStaticPreview() {
    ScreenshotSurface {
        MatrixPulse(
            size = 96.dp,
            animated = false,
        )
    }
}

@PreviewTest
@ScreenshotPreview
@Composable
fun MatrixPulseCustomAppearancePreview() {
    ScreenshotSurface {
        MatrixPulse(
            size = 48.dp,
            color = Color.Magenta,
            animated = false,
        )
    }
}
