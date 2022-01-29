package com.kjy00302.koov.coovapi.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetVC_V1Response(
    @field:Json(name = "messageCode")
    val messageCode: String,
    @field:Json(name = "result")
    val result: List<VC_V1>
) {
    @JsonClass(generateAdapter = true)
    data class VC_V1(
        @field:Json(name = "vc")
        val vc: String,
        @field:Json(name = "type")
        val type: String,
    )
}