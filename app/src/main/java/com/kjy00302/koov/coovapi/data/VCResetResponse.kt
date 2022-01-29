package com.kjy00302.koov.coovapi.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VCResetResponse(
    @field:Json(name = "resCode")
    val resultCode: String
)