package com.kjy00302.koov.vc.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VCJWTClaims(
    @field:Json(name = "iss")
    val issuer: String? = null,
    @field:Json(name = "sub")
    val subject: String? = null,
    @field:Json(name = "aud")
    val audience: List<String>? = null,
    @field:Json(name = "exp")
    val expiration: Long? = null,
    @field:Json(name = "nbf")
    val notBefore: Long? = null,
    @field:Json(name = "nonce")
    val nonce: String? = null,
    @field:Json(name = "vp")
    val vp: VerifiablePresentation? = null,
    @field:Json(name = "vc")
    val vc: VerifiableCredential? = null
)