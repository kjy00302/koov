package com.kjy00302.koov.vc.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VerifiablePresentation(
    @field:Json(name = "@context")
    val context: List<String>,
    @field:Json(name = "type")
    val type: List<String>,
    @field:Json(name = "verifiableCredential")
    val verifiableCredential: List<String>
)