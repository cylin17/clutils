package com.cylin.clutilsx

import com.cylin.clutils.text.toDigit
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun string_convert() {
        var num: Int = "123".toDigit()
        assertEquals(true, 123 == num)

        var floatNum: Float = "123.4".toDigit()
        assertEquals(true, 123.4f == floatNum)

        var doubleNum: Double = "123.23".toDigit()
        assertEquals(true, 123.23 == doubleNum)

        var longNum: Long = "123".toDigit()
        assertEquals(true, 123L == longNum)
    }
}
