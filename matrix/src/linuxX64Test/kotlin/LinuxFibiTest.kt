package io.github.hdcodedev.matrix

import kotlin.test.Test
import kotlin.test.assertEquals

class LinuxFibiTest {
    @Test
    fun test_3rd_element() {
        assertEquals(8, generateFibi().take(3).last())
    }
}
