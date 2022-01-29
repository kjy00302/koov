package com.kjy00302.koov.coovapi

import com.kjy00302.koov.coovapi.data.DIDForm
import com.kjy00302.koov.coovapi.data.ENPForm
import com.kjy00302.koov.coovapi.data.VaccinationResult
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object COOVApi {
    const val BASEURL = "https://app.coov.io/api/"
    const val AUTH_SECRET = "EC1CYhJoAXwyJkvmV7ZrrtZYRuVEJm3b8j4xhZt8Cw8AgvE82"
    const val KDCA_EOSPUBKEY = "PUB_K1_5LnLPSFL1ioJyATiTQ7jUzQJe1PqzEbBWyE8efzyn88TBz4w8N"

    val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create())
        .baseUrl(BASEURL)
        .build()

    val retrofitService: COOVApiService by lazy {
        retrofit.create(COOVApiService::class.java)
    }

    suspend fun checkToken(token: String): Boolean? {
        val resp = retrofitService.checkToken(token)
        if (resp.isSuccessful) {
            return resp.body()!!.messageCode == "Success"
        }
        return null
    }

    suspend fun getAuthPage(): String? {
        val resp = retrofitService.getAuthPage()
        if (resp.isSuccessful) {
            return resp.body()!!.string()
        }
        return null
    }

    suspend fun getVCVer1(token: String, type: String, did: String): Map<String, String>? {
        val resp = retrofitService.getVC_V1(token, type, DIDForm(did))
        if (resp.isSuccessful) {
            return resp.body()!!.result.associate { it.type to it.vc }
        }
        return null
    }

    suspend fun updateVCVaccination(token: String, did: String): VaccinationResult? {
        val resp = retrofitService.updateVCVaccination(token, DIDForm(did))
        if (resp.isSuccessful) {
            val body = resp.body()!!
            if (body.resultCode == "Success") {
                return body.result!!
            }
        }
        return null
    }

    suspend fun resetVCIssuance(token: String): Boolean? {
        val resp = retrofitService.resetVCIssuance(token)
        if (resp.isSuccessful) {
            return resp.body()!!.resultCode == "Success"
        }
        return null
    }

    suspend fun getKIPass(token: String, endPoint: String): String? {
        val resp = retrofitService.getKIPass("Bearer $token", ENPForm(endPoint))
        if (resp.isSuccessful) {
            return resp.body()!!.qrCode
        }
        return null
    }
}
