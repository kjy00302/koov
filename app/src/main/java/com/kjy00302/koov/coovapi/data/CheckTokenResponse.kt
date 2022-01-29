package com.kjy00302.koov.coovapi.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CheckTokenResponse(
    @field:Json(name = "messageCode")
    val messageCode: String,
    @field:Json(name = "userExist")
    val userExist: Boolean
)