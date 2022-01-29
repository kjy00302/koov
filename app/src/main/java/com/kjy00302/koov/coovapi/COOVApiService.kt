package com.kjy00302.koov.coovapi

import com.kjy00302.koov.coovapi.data.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface COOVApiService {
    @POST("v1/issue/coov/vc")
    @Headers("Content-Type: application/json")
    suspend fun getVC_V1(
        @Header("authorization") token: String,
        @Query("type") type: String,
        @Body did: DIDForm
    ): Response<GetVC_V1Response>

    @GET("v1/auth/token/check")
    suspend fun checkToken(@Header("authorization") token: String): Response<CheckTokenResponse>

    @POST("v2/issue/coov/vc/vaccination")
    @Headers("Content-Type: application/json")
    suspend fun updateVCVaccination(
        @Header("authorization") token: String,
        @Body did: DIDForm
    ): Response<VaccinationResponse>

    @POST("v2/issue/coov/reset")
    suspend fun resetVCIssuance(@Header("authorization") token: String): Response<VCResetResponse>

    @GET("v1/auth")
    @Headers("secret-key: ${COOVApi.AUTH_SECRET}")
    suspend fun getAuthPage(): Response<ResponseBody>

    @POST("https://kipass.coov.io/v1/kipass/issueCheckInQRCode")
    @Headers("Content-Type: application/json")
    suspend fun getKIPass(
        @Header("authorization") bearer: String,
        @Body enp: ENPForm
    ): Response<KIPassResponse>
}