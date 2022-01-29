package com.kjy00302.koov.coovapi.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VaccinationResult(
    @field:Json(name = "VCs")
    val vc: List<String>,
    @field:Json(name = "VCIdsToDelete")
    val vcIdsToDelete: List<String>
)