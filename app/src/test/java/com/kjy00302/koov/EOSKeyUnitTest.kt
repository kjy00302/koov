package com.kjy00302.koov

import org.bouncycastle.jce.ECNamedCurveTable
import org.bouncycastle.jce.ECPointUtil
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.jce.spec.ECNamedCurveSpec
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import java.math.BigInteger
import java.security.KeyFactory
import java.security.Security
import java.security.spec.ECParameterSpec
import java.security.spec.ECPrivateKeySpec
import java.security.spec.ECPublicKeySpec
import java.util.Base64
import com.kjy00302.koov.util.EOSKey
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey


class EOSKeyUnitTest {

    private lateinit var keyFactory: KeyFactory
    private lateinit var keyParams: ECParameterSpec

    @Before
    fun insertBCAndPrepareKeyFactory(){
        Security.addProvider(BouncyCastleProvider())
        keyFactory = KeyFactory.getInstance("ECDSA")
        val spec = ECNamedCurveTable.getParameterSpec("secp256k1")
        keyParams = ECNamedCurveSpec(spec.name, spec.curve, spec.g, spec.n)
    }

    @Test
    fun ecToEosPub_isCorrect(){
        val point = ECPointUtil.decodePoint(
            keyParams.curve,
            Base64.getDecoder().decode("A+ple8WEFFpdhxbnt5j/Jn09WXp0b45WaRBx8qpaIQIb"))
        val pubKeySpec = ECPublicKeySpec(point, keyParams)
        val eoskey = EOSKey.ecKeyToEOSK1(keyFactory.generatePublic(pubKeySpec) as ECPublicKey)
        assertEquals("PUB_K1_8cTsXg1sMrgY16VYV57geGm1QbzSY43T1EPRhCF2ZvwzXPrr9R", eoskey)
    }

    @Test
    fun ecToEosPvt_isCorrect(){
        val pvtKeySpec = ECPrivateKeySpec(
            BigInteger(1, Base64.getDecoder().decode("SDt9D84ZMDdYlNdSSSApWq2tsFLYE3/7+u2Q3hEjagE=")),
            keyParams)
        val eoskey = EOSKey.ecKeyToEOSK1(keyFactory.generatePrivate(pvtKeySpec) as ECPrivateKey)
        assertEquals("PVT_K1_Yp5bBKHT3joWWKVFrPwXu2ScSM1tauowDVLNBwtjh9418BmJu", eoskey)
    }

    @Test
    fun EosToECPub_isCorrect(){
        val pubKey = EOSKey.eosK1ToECKey(
            "PUB_K1_8cTsXg1sMrgY16VYV57geGm1QbzSY43T1EPRhCF2ZvwzXPrr9R")
                as org.bouncycastle.jce.interfaces.ECPublicKey
        assertEquals(
            "A+ple8WEFFpdhxbnt5j/Jn09WXp0b45WaRBx8qpaIQIb",
            Base64.getEncoder().encodeToString(pubKey.q.getEncoded(true)))
    }

    @Test
    fun EosToECPvt_isCorrect(){
        val pvtKey = EOSKey.eosK1ToECKey(
            "PVT_K1_Yp5bBKHT3joWWKVFrPwXu2ScSM1tauowDVLNBwtjh9418BmJu")
                as org.bouncycastle.jce.interfaces.ECPrivateKey
        assertEquals(
            "SDt9D84ZMDdYlNdSSSApWq2tsFLYE3/7+u2Q3hEjagE=",
            Base64.getEncoder().encodeToString(pvtKey.d.toByteArray()))
    }
}