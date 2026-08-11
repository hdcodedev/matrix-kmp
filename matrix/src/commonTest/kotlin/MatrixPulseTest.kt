package io.github.hdcodedev.matrix

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class MatrixPulseTest {
    @Test
    fun matrixPulse_rendersWithAccessibilityDescription() =
        runComposeUiTest {
            setContent {
                MatrixPulse(
                    contentDescription = "Matrix pulse",
                    animated = false,
                )
            }

            onNodeWithContentDescription("Matrix pulse").assertIsDisplayed()
        }

    @Test
    fun matrixPulse_acceptsCustomAppearance() =
        runComposeUiTest {
            setContent {
                MatrixPulse(
                    size = 48.dp,
                    color = Color.Magenta,
                    speed = 1.5f,
                    animated = false,
                )
            }

            onNodeWithContentDescription("Loading")
                .assertIsDisplayed()
                .assertWidthIsEqualTo(48.dp)
        }

    @Test
    fun matrixPulse_staticFrame_isDeterministic() {
        val first = matrixPulseOpacity(row = 2, column = 2, phase = 0f)
        val second = matrixPulseOpacity(row = 2, column = 2, phase = 0f)

        assertEquals(first, second)
        assertTrue(first > matrixPulseOpacity(row = 0, column = 0, phase = 0f))
    }

    @Test
    fun matrixPulse_movesFromCenterTowardTheEdge() {
        val centerAtStart = matrixPulseOpacity(row = 2, column = 2, phase = 0f)
        val cornerAtStart = matrixPulseOpacity(row = 0, column = 0, phase = 0f)
        val centerAtMidpoint = matrixPulseOpacity(row = 2, column = 2, phase = 0.5f)
        val adjacentAtMidpoint = matrixPulseOpacity(row = 2, column = 1, phase = 0.5f)
        val centerNearEdge = matrixPulseOpacity(row = 2, column = 2, phase = 0.91f)
        val cornerNearEdge = matrixPulseOpacity(row = 0, column = 0, phase = 0.91f)

        assertTrue(centerAtStart > cornerAtStart)
        assertTrue(adjacentAtMidpoint > centerAtMidpoint)
        assertTrue(cornerNearEdge > centerNearEdge)
    }

    @Test
    fun matrixPulse_nonPositiveSpeed_doesNotCrash() =
        runComposeUiTest {
            setContent {
                MatrixPulse(
                    speed = 0f,
                    animated = false,
                )
            }

            onNodeWithContentDescription("Loading").assertIsDisplayed()
        }

    @Test
    fun matrixPulse_invalidSize_doesNotCrash() =
        runComposeUiTest {
            setContent {
                MatrixPulse(
                    size = Dp(Float.POSITIVE_INFINITY),
                    animated = false,
                )
            }

            onNodeWithContentDescription("Loading").assertIsDisplayed()
        }

    @Test
    fun matrixPulse_canOmitAccessibilityDescription() =
        runComposeUiTest {
            setContent {
                MatrixPulse(
                    contentDescription = null,
                    animated = false,
                )
            }

            onNodeWithContentDescription("Loading").assertDoesNotExist()
        }

    @Test
    fun matrixPulse_animatedMode_remainsDisplayedAcrossFrames() =
        runComposeUiTest {
            mainClock.autoAdvance = false
            setContent { MatrixPulse() }

            onNodeWithContentDescription("Loading").assertIsDisplayed()
            mainClock.advanceTimeBy(800)
            onNodeWithContentDescription("Loading").assertIsDisplayed()
        }
}
