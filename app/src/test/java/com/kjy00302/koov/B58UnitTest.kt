package com.kjy00302.koov

import org.junit.Test
import org.junit.Assert.*
import kotlin.random.Random
import com.kjy00302.koov.util.Base58

class B58UnitTest {
    private val dataset_array = arrayOf(
        byteArrayOf(0x61, 0x62),
        byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0),
        byteArrayOf(1, 2, 3, 4, 5, 6, 7 ,8),
        byteArrayOf(0, 0, 1, 1, 0, 0),
        byteArrayOf(-1, -1, -1, -1)
    )
    private val dataset_string = arrayOf(
        "8Qq",
        "11111111",
        "An6UebxCZd",
        "112VKmH",
        "7YXq9G"
    )
    @Test
    fun b58Encode_isCorrect(){
        for (i in 0..4)
            assertEquals(dataset_string[i], Base58.b58Encode(dataset_array[i]))
    }

    @Test
    fun b58Decode_isCorrect(){
        for (i in 0..4) {
            assertArrayEquals(dataset_array[i], Base58.b58Decode(dataset_string[i]))
        }
    }

    @Test
    fun b58Test(){
        val array = ByteArray(128)
        for (i in 0..100){
            Random.nextBytes(array)
            assertArrayEquals(array, Base58.b58Decode(Base58.b58Encode(array)))
        }
    }
}