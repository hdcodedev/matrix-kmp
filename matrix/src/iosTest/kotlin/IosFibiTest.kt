package io.github.hdcodedev.matrix

import kotlin.test.Test
import kotlin.test.assertEquals

class IosFibiTest {
    @Test
    fun test_3rd_element() {
        assertEquals(7, generateFibi().take(3).last())
    }
}
