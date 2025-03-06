package com.example.stringassistant

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class CircularBufferTest {
    @Test
    fun readTest() {
        val testBuffer = CircularBuffer(8)
        assertEquals(0.toByte(), testBuffer.read())

    }
    @Test
    fun writeTest() {
        val testBuffer = CircularBuffer(8)
        testBuffer.write(0b1010101010.toByte()) //0b1000000
        assertEquals(0b10101010.toByte(), testBuffer.read())
    }
    @Test
    fun readBlockTest() {
        val testBuffer = CircularBuffer(2)
        val blah = byteArrayOf(0b1111111, 0b1000000, 0b100000)
        testBuffer.writeBlock(blah)
        assertEquals(0b100000.toByte(), testBuffer.read())
        assertEquals(0b1000000.toByte(), testBuffer.read())

    }

    @Test
    fun testAll() {

        this.readTest()
        this.writeTest()
        this.readBlockTest()
    }

}