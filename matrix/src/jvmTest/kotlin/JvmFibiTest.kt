package io.github.hdcodedev.matrix

import kotlin.test.Test
import kotlin.test.assertEquals

class JvmFibiTest {
    @Test
    fun test_3rd_element() {
        assertEquals(5, generateFibi().take(3).last())
    }
}
