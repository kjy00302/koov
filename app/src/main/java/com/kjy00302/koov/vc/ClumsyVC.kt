package com.kjy00302.koov.vc

import android.util.Base64
import com.kjy00302.koov.util.EOSKey
import com.kjy00302.koov.vc.data.JWTHeader
import com.kjy00302.koov.vc.data.VCJWTClaims
import com.kjy00302.koov.vc.data.VerifiablePresentation
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import org.bouncycastle.asn1.ASN1InputStream
import org.bouncycastle.asn1.ASN1Integer
import org.bouncycastle.asn1.ASN1Sequence
import org.bouncycastle.asn1.DERSequenceGenerator
import java.io.ByteArrayOutputStream
import java.math.BigInteger
import java.security.KeyPair
import java.security.Signature
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.util.*

// Clumsy VC implementation (with JWT)

object ClumsyVC {
    private val moshi: Moshi = Moshi.Builder().build()
    private val headerAdapter: JsonAdapter<JWTHeader> = moshi.adapter(JWTHeader::class.java)

    private const val B64ENCODEFLAG = Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP

    fun validateES256KJWT(jwt: String, key: ECPublicKey): String {
        val jwtParts = jwt.split('.')
        val jwtHeader =
            headerAdapter.fromJson(Base64.decode(jwtParts[0], Base64.URL_SAFE).decodeToString())
        if (!(jwtHeader != null && jwtHeader.algorithm contentEquals "ES256K" && jwtHeader.type contentEquals "JWT")) {
            throw Exception()
        }

        val sign = Base64.decode(jwtParts[2], Base64.URL_SAFE)
        val signature = Signature.getInstance("SHA256withECDSA", "BC")
        signature.initVerify(key)
        signature.update((jwtParts[0] + '.' + jwtParts[1]).toByteArray())
        if (!signature.verify(jwsES256SignToDER(sign))) {
            throw Exception()
        }
        return Base64.decode(jwtParts[1], Base64.URL_SAFE).decodeToString()
    }

    fun makeES256KJWT(rawClaims: String, key: ECPrivateKey): String {
        val header = Base64.encodeToString(
            headerAdapter.toJson(JWTHeader("ES256K", "JWT")).toByteArray(),
            B64ENCODEFLAG
        )
        val claims = Base64.encodeToString(rawClaims.toByteArray(), B64ENCODEFLAG)

        val headerAndClaims = "${header}.${claims}"

        val signature = Signature.getInstance("SHA256withECDSA", "BC")
        signature.initSign(key)
        signature.update(headerAndClaims.toByteArray())
        val sign = Base64.encodeToString(derToJWSES256Sign(signature.sign()), B64ENCODEFLAG)
        return "${headerAndClaims}.${sign}"
    }

    fun jwsES256SignToDER(sign: ByteArray): ByteArray {
        val derByteStream = ByteArrayOutputStream()
        val der = DERSequenceGenerator(derByteStream)
        der.addObject(ASN1Integer(BigInteger(1, sign.sliceArray(0..31))))
        der.addObject(ASN1Integer(BigInteger(1, sign.sliceArray(32..63))))
        der.close()
        return derByteStream.toByteArray()
    }

    fun derToJWSES256Sign(sign: ByteArray): ByteArray {
        val array = ByteArray(64)
        val der = ASN1InputStream(sign).readObject() as ASN1Sequence
        var offset = 0
        val r = (der.getObjectAt(0) as ASN1Integer).positiveValue.toByteArray()
        offset = if (r.size == 33) 1 else 0
        r.copyInto(array, 0, offset)
        val s = (der.getObjectAt(1) as ASN1Integer).positiveValue.toByteArray()
        offset = if (s.size == 33) 1 else 0
        s.copyInto(array, 32, offset)
        return array
    }

    fun makeVP(
        key: KeyPair,
        vcs: List<String>,
        nonce: String,
        audience: List<String>? = null,
        expire: Long? = null
    ): String {

        val now = Calendar.getInstance().timeInMillis / 1000

        val vp = VerifiablePresentation(
            context = listOf("https://www.w3.org/2018/credentials/v1"),
            type = listOf("VerifiablePresentation"),
            verifiableCredential = vcs
        )
        val claims = VCJWTClaims(
            notBefore = now,
            issuer = "did:infra:01:${EOSKey.ecKeyToEOSK1(key.public as ECPublicKey)}",
            nonce = nonce,
            audience = audience,
            expiration = if (expire != null) now + expire else null,
            vp = vp
        )

        val claimAdapter = moshi.adapter(VCJWTClaims::class.java)
        return makeES256KJWT(claimAdapter.toJson(claims), key.private as ECPrivateKey)
    }
}