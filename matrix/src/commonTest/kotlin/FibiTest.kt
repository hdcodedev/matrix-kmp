package io.github.hdcodedev.matrix

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class FibiTest {
    @Test
    fun generateFibi_producesCorrectFibonacciSequence() {
        val sequence = generateFibi().take(10).toList()
        val expected = generateExpectedFibi(firstElement, secondElement, 10)
        assertEquals(expected, sequence)
    }

    @Test
    fun generateFibi_thirdElementIsSumOfFirstTwo() {
        val sequence = generateFibi().take(3).toList()
        assertEquals(firstElement + secondElement, sequence[2])
    }

    @Test
    fun generateFibi_containsAtLeastOnePositiveValue() {
        val sequence = generateFibi().take(5).toList()
        assertTrue(sequence.any { it > 0 })
    }

    @Test
    fun generateFibi_isInfinite() {
        val first100 = generateFibi().take(100).toList()
        assertNotEquals(0, first100.size)
        assertEquals(100, first100.size)
    }

    @Test
    fun generateFibi_firstFourElementsMatchExpectedSequence() {
        val sequence = generateFibi().take(4).toList()
        assertEquals(firstElement, sequence[0])
        assertEquals(secondElement, sequence[1])
        assertEquals(firstElement + secondElement, sequence[2])
        assertEquals(firstElement + 2 * secondElement, sequence[3])
    }

    @Test
    fun generateFibi_firstFiveElementsMatchExpectedSequence() {
        val sequence = generateFibi().take(5).toList()
        assertEquals(firstElement, sequence[0])
        assertEquals(secondElement, sequence[1])
        assertEquals(firstElement + secondElement, sequence[2])
        assertEquals(firstElement + 2 * secondElement, sequence[3])
        assertEquals(2 * firstElement + 3 * secondElement, sequence[4])
    }

    @Test
    fun generateFibi_followsFibonacciPattern() {
        val sequence = generateFibi().take(6).toList()
        for (i in 2..5) {
            assertEquals(sequence[i - 1] + sequence[i - 2], sequence[i])
        }
    }

    private fun generateExpectedFibi(
        first: Int,
        second: Int,
        count: Int,
    ): List<Int> {
        val result = mutableListOf(first, second)
        while (result.size < count) {
            result.add(result[result.size - 1] + result[result.size - 2])
        }
        return result
    }
}
