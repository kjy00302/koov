package com.kjy00302.koov.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.kjy00302.koov.R
import com.kjy00302.koov.coovapi.COOVApi
import com.kjy00302.koov.coovapi.data.DIDForm
import com.kjy00302.koov.util.EOSKey
import com.kjy00302.koov.vc.ClumsyVC
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.bouncycastle.jce.ECNamedCurveTable
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.json.JSONObject
import java.security.KeyPairGenerator
import java.security.Security
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import kotlin.coroutines.CoroutineContext


class MainActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var job: Job
    private lateinit var prefs: SharedPreferences

    override val coroutineContext: CoroutineContext
        get() {
            return Dispatchers.Main + job
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        Security.removeProvider("BC")
        Security.addProvider(BouncyCastleProvider())
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        job = Job()
        prefs = getSharedPreferences(getString(R.string.preferences_file_key), Context.MODE_PRIVATE)

        prefs.getString("pvtKey", null) ?: generateKeyPair()
        findViewById<TextView>(R.id.textView2).text = prefs.getString("pubKey", "No key")

        findViewById<Button>(R.id.button).setOnClickListener { view ->
            val intent = Intent(this, ResponderActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.button2).setOnClickListener { view ->
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.button3).setOnClickListener { view ->
            val token = prefs.getString("token", null) ?: return@setOnClickListener
            val pubKey = prefs.getString("pubKey", null) ?: return@setOnClickListener
            launch {
                val resp = COOVApi.retrofitService.checkToken(token)
                if (resp.isSuccessful && resp.body()!!.messageCode == "Success") {
                    val did = DIDForm("did:infra:01:$pubKey")
                    val resp = COOVApi.retrofitService.getVC_V1(token, "personal", did)
                    val prefsEditor = prefs.edit()
                    if (resp.isSuccessful) {
                        for (vc in resp.body()!!.result) {
                            prefsEditor.putString("vc_${vc.type}", vc.vc)
                        }
                    }
                    COOVApi.retrofitService.resetVCIssuance(token)
                    val vaccineResp = COOVApi.retrofitService.updateVCVaccination(token, did)
                    if (vaccineResp.isSuccessful) {
                        for (vc in vaccineResp.body()!!.result!!.vc) {
                            val claim = ClumsyVC.validateES256KJWT(
                                vc,
                                EOSKey.eosK1ToECKey(COOVApi.KDCA_EOSPUBKEY) as ECPublicKey
                            )
                            val jwt = JSONObject(claim)
                            val credSubject =
                                jwt.getJSONObject("vc").getJSONObject("credentialSubject")
                            prefsEditor.putString(
                                "vc_${credSubject.getString("vaccine")}_${
                                    credSubject.getInt(
                                        "doseNum"
                                    )
                                }", vc
                            )
                        }
                    }
                    prefsEditor.apply()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    fun generateKeyPair() {
        val keyPairGenerator = KeyPairGenerator.getInstance("ECDSA", "BC")
        keyPairGenerator.initialize(ECNamedCurveTable.getParameterSpec("secp256k1"))
        val keyPair = keyPairGenerator.genKeyPair()
        prefs.edit()
            .putString("pvtKey", EOSKey.ecKeyToEOSK1(keyPair.private as ECPrivateKey))
            .putString("pubKey", EOSKey.ecKeyToEOSK1(keyPair.public as ECPublicKey))
            .apply()
    }
}