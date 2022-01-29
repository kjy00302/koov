package com.kjy00302.koov.coovapi.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class KIPassResponse(
    @field:Json(name = "qrCode")
    val qrCode: String
)