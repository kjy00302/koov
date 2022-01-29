package com.kjy00302.koov.util

import java.math.BigInteger

object Base58 {
    private const val B58CHARS = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"
    private const val ZERO: Byte = 0
    private val FIFTYEIGHT: BigInteger = BigInteger.valueOf(58)

    fun b58Encode(data: ByteArray): String {
        var zeroCount = 0
        var out = ""

        for (i in data) {
            if (i == ZERO) zeroCount++
            else break
        }
        var n = BigInteger(1, data)
        while (n > BigInteger.ZERO) {
            val remainder = (n % FIFTYEIGHT).toInt()
            n /= FIFTYEIGHT
            out = B58CHARS[remainder] + out
        }
        return "1".repeat(zeroCount) + out
    }

    fun b58Decode(data: String): ByteArray {
        var zeroCount = 0
        var n = BigInteger.ZERO

        for (i in data) {
            if (i == '1') zeroCount++
            else break
        }
        for (i in data) {
            n *= FIFTYEIGHT
            n += (B58CHARS.indexOf(i).toBigInteger())
        }
        val bytes = n.toByteArray()
        val out = ByteArray(zeroCount)
        return if (bytes[0] == ZERO) {
            out + bytes.sliceArray(1 until bytes.size)
        } else {
            out + bytes
        }
    }
}