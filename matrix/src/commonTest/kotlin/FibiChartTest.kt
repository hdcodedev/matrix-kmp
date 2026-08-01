package io.github.hdcodedev.matrix

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test

class FibiChartTest {
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun fibiChart_rendersFibonacciData() =
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
    fun fibiChart_acceptsCustomColor() =
        runComposeUiTest {
            setContent {
                FibiChart(numbers = listOf(1, 2, 3), barColor = Color.Red)
            }
            onRoot().assertIsDisplayed()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun fibiChart_handlesEmptyData() =
        runComposeUiTest {
            setContent { FibiChart(numbers = emptyList()) }
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun fibiChart_handlesNonPositiveData() =
        runComposeUiTest {
            setContent { FibiChart(numbers = listOf(-2, 0)) }
        }
}
