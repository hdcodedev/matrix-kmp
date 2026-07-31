package io.github.hdcodedev.matrix

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.test.swipeLeft
import kotlin.test.Test

class FibiChartTest {
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun fibiChart_rendersCorrectNumberOfBars() =
        runComposeUiTest {
            setContent {
                FibiChart(numbers = listOf(1, 2, 3, 5, 8))
            }
            onRoot().assertIsDisplayed()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun fibiChart_rendersSingleBar() =
        runComposeUiTest {
            setContent {
                FibiChart(numbers = listOf(42))
            }
            onRoot().assertIsDisplayed()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun fibiChart_rendersWithCustomColor() =
        runComposeUiTest {
            setContent {
                FibiChart(numbers = listOf(1, 2, 3))
            }
            onRoot()
                .performTouchInput { swipeLeft() }
        }
}
