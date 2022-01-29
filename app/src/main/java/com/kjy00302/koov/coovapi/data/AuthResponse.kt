package com.kjy00302.koov.coovapi.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class AuthResponse(
    @field:Json(name = "data")
    val data: Result,
    @field:Json(name = "type")
    val type: String
) {
    @JsonClass(generateAdapter = true)
    data class Result(
        @field:Json(name = "token")
        val token: String,
        @field:Json(name = "patnum")
        val patNum: String,
        @field:Json(name = "messageCode")
        val messageCode: String
    )
}


