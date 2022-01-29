package com.kjy00302.koov

import com.kjy00302.koov.coovapi.COOVApi
import kotlinx.coroutines.runBlocking
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import java.security.Security
import java.security.interfaces.ECPublicKey
import com.kjy00302.koov.util.EOSKey
import com.kjy00302.koov.vc.ClumsyVC
import org.json.JSONObject


class COOVApiUnitTest {
    private lateinit var kdcaPubKey: ECPublicKey
    // TODO: INSERT AUTHORIZATION TOKEN HERE
    private val token = "INSERT TOKEN HERE"
    private val testDID = "did:infra:01:PUB_K1_7yaRqCjbgVzZqkQLYFf7c4ci1g78CgxZX4Fv3XugQ6jZv24jy2"

    @Before
    fun insertBCAndKey(){
        Security.addProvider(BouncyCastleProvider())
        kdcaPubKey = EOSKey.eosK1ToECKey(COOVApi.KDCA_EOSPUBKEY) as ECPublicKey
    }

    @Test
    fun coovGetVC_V1_isCorrect(){
        runBlocking{
            val resp = COOVApi.getVCVer1(token, "personal", testDID)
            val isAdult_jwt = resp!!["isAdult"]!!

            val jwt = JSONObject(ClumsyVC.validateES256KJWT(isAdult_jwt, kdcaPubKey))
            assertNotEquals(null, jwt.getJSONObject("vc").getJSONObject("credentialSubject").getBoolean("isAdult"))
        }
    }

    @Test
    fun coovCheckToken_isCorrect(){
        runBlocking{
            val resp = COOVApi.checkToken(token)
            assertEquals(true, resp)
        }
    }

    @Test
    fun coovResetVC_isCorrect(){
        runBlocking {
            val resp = COOVApi.resetVCIssuance(token)
            assertEquals(true, resp!!)
        }
    }

    @Test
    fun coovVaccinationVC_isCorrect(){
        runBlocking {
            COOVApi.resetVCIssuance(token)
            val result = COOVApi.updateVCVaccination(token, testDID)
            val jwt = JSONObject(ClumsyVC.validateES256KJWT(result!!.vc[0], kdcaPubKey))
            assertNotEquals(null, jwt.getJSONObject("vc").getJSONObject("credentialSubject").getString("vaccine"))

        }
    }

    @Test
    fun coovAuthPage_isCorrect(){
        runBlocking {
            val resp = COOVApi.getAuthPage()
            assertNotEquals(null, resp)
        }
    }

    @Test
    fun coovKIPass_isCorrect(){
        runBlocking {
            val qrCode = COOVApi.getKIPass(token, "")
            assertEquals(true, qrCode!!.startsWith("003|"))
        }
    }
}