package com.kjy00302.koov

import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import com.kjy00302.koov.vc.ClumsyVC
import com.kjy00302.koov.util.EOSKey

class JWTUnitTest {
    private lateinit var testPrivateKey: ECPrivateKey
    private lateinit var testPublicKey: ECPublicKey

    @Before
    fun insertBCAndKey(){
        Security.addProvider(BouncyCastleProvider())
        testPrivateKey = EOSKey.eosK1ToECKey(
            "PVT_K1_27cG8Cx11H7TiJFJM2QLpaRgTGSoW5skuhXxAvA6154DT9fkYT") as ECPrivateKey
        testPublicKey = EOSKey.eosK1ToECKey(
            "PUB_K1_5otXky6P3f3j8YRbydy5gNed5Ds7fpVT5QEss39qnqaNUZGD3n") as ECPublicKey

    }

    @Test
    fun jwtHeaderValidate_isCorrect(){
        val claims = ClumsyVC.validateES256KJWT(
        "eyJhbGciOiJFUzI1NksiLCJ0eXAiOiJKV1QifQ"
            +".eyJ0ZXN0IjoiVGVzdCJ9.SSb3-5pYXo_eToH"
            +"NBWY0WtJY5WbrU3yJOruA-ZOJPFmyEA3Mwyop"
            +"zh95S0RRPM-jleoVNRgAFnNw4dNxrUXQUQ",
            testPublicKey)
        assertEquals("{\"test\":\"Test\"}", claims)
    }

    @Test
    fun makeJWT_isCorrect(){
        val jwt = ClumsyVC.makeES256KJWT("{\"test\":\"Test\"}", testPrivateKey)
        assertEquals("{\"test\":\"Test\"}", ClumsyVC.validateES256KJWT(jwt, testPublicKey))
    }
}