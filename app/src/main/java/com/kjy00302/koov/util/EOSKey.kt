package com.kjy00302.koov.util

import com.kjy00302.koov.util.Base58.b58Decode
import com.kjy00302.koov.util.Base58.b58Encode
import org.bouncycastle.crypto.digests.RIPEMD160Digest
import org.bouncycastle.jce.ECNamedCurveTable
import org.bouncycastle.jce.ECPointUtil
import org.bouncycastle.jce.spec.ECNamedCurveSpec
import java.math.BigInteger
import java.security.KeyFactory
import java.security.interfaces.ECKey
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.security.spec.ECPrivateKeySpec
import java.security.spec.ECPublicKeySpec

object EOSKey {
    fun ecKeyToEOSK1(key: ECPublicKey): String {
        val encodedKey = (key as org.bouncycastle.jce.interfaces.ECPublicKey).q.getEncoded(true)
        val digest = RIPEMD160Digest()
        digest.update(encodedKey + "K1".toByteArray(), 0, encodedKey.size + 2)
        val checksum = ByteArray(digest.digestSize)
        digest.doFinal(checksum, 0)
        return "PUB_K1_" + b58Encode(encodedKey + checksum.sliceArray(0..3))
    }

    fun ecKeyToEOSK1(key: ECPrivateKey): String {
        var encodedKey = (key as org.bouncycastle.jce.interfaces.ECPrivateKey).d.toByteArray()
        val digest = RIPEMD160Digest()
        if (encodedKey.size == 33)
            encodedKey = encodedKey.sliceArray(1 until 32)
        digest.update(encodedKey + "K1".toByteArray(), 0, encodedKey.size + 2)
        val checksum = ByteArray(digest.digestSize)
        digest.doFinal(checksum, 0)
        return "PVT_K1_" + b58Encode(encodedKey + checksum.sliceArray(0..3))
    }

    fun eosK1ToECKey(EosKey: String): ECKey {
        val keyElements = EosKey.split('_')
        val decoded = b58Decode(keyElements[2])
        val keyData = decoded.sliceArray(0 until decoded.size - 4)
        val checksum = decoded.sliceArray(decoded.size - 4 until decoded.size)

        val digest = RIPEMD160Digest()
        digest.update(keyData + "K1".toByteArray(), 0, keyData.size + 2)
        val actualChecksum = ByteArray(digest.digestSize)
        digest.doFinal(actualChecksum, 0)
        if (!(checksum contentEquals actualChecksum.sliceArray(0..3))) {
            throw Exception()
        }

        val spec = ECNamedCurveTable.getParameterSpec("secp256k1")
        val kf = KeyFactory.getInstance("ECDSA")
        val params = ECNamedCurveSpec(spec.name, spec.curve, spec.g, spec.n)

        return when (keyElements[0]) {
            "PUB" -> {
                val point = ECPointUtil.decodePoint(
                    params.curve,
                    keyData
                )
                val pubKeySpec = ECPublicKeySpec(point, params)
                kf.generatePublic(pubKeySpec) as ECPublicKey
            }
            "PVT" -> {
                val pvtKeySpec = ECPrivateKeySpec(BigInteger(1, keyData), params)
                kf.generatePrivate(pvtKeySpec) as ECPrivateKey
            }
            else -> throw Exception()
        }
    }
}