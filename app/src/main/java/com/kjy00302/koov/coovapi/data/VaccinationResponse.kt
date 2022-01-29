package com.kjy00302.koov.coovapi.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VaccinationResponse(
    @field:Json(name = "resCode")
    val resultCode: String,
    @field:Json(name = "result")
    val result: VaccinationResult?
)