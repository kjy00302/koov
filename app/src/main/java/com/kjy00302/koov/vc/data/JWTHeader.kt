package com.kjy00302.koov.vc.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class JWTHeader(
    @field:Json(name = "alg")
    val algorithm: String,
    @field:Json(name = "typ")
    val type: String
)