package com.kjy00302.koov.vc.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CredentialSubject(
    @field:Json(name = "name")
    val name: String?,
    @field:Json(name = "sex")
    val sex: String?,
    @field:Json(name = "dob")
    val dob: String?,
    @field:Json(name = "isAdult")
    val isAdult: Boolean?,
    @field:Json(name = "id")
    val id: String?,
    @field:Json(name = "vaccine")
    val vaccine: String?,
    @field:Json(name = "brand")
    val brand: Map<String, String>?,
    @field:Json(name = "lotNum")
    val lotNumber: String?,
    @field:Json(name = "doseNum")
    val doseNumber: Int?,
    @field:Json(name = "date")
    val date: String?,
    @field:Json(name = "country")
    val country: String?,
    @field:Json(name = "adminCentre")
    val adminCentre: AdminCentre?,
    @field:Json(name = "wallet")
    val wallet: String?,
    @field:Json(name = "issuer")
    val issuer: String?,
) {
    @JsonClass(generateAdapter = true)
    data class AdminCentre(
        @field:Json(name = "id")
        val id: String,
        @field:Json(name = "name")
        val name: Map<String, String>,
    )
}