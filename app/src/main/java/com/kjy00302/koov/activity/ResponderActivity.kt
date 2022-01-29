package com.kjy00302.koov.activity

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.kjy00302.koov.R
import com.kjy00302.koov.coovapi.COOVApi
import com.kjy00302.koov.util.EOSKey
import com.kjy00302.koov.vc.ClumsyVC
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import io.socket.engineio.client.transports.WebSocket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.security.KeyPair
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import kotlin.coroutines.CoroutineContext

class ResponderActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var socket: Socket
    private lateinit var prefs: SharedPreferences
    private lateinit var keyPair: KeyPair

    private val qrCodeWriter = QRCodeWriter()
    private lateinit var qrImageView: ImageView
    private lateinit var qrCodeBitmap: Bitmap
    private lateinit var refreshTimer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_responder)
        job = Job()

        prefs = getSharedPreferences(getString(R.string.preferences_file_key), Context.MODE_PRIVATE)

        val privateKey = EOSKey.eosK1ToECKey(prefs.getString("pvtKey", null)!!) as ECPrivateKey
        val publicKey = EOSKey.eosK1ToECKey(prefs.getString("pubKey", null)!!) as ECPublicKey
        keyPair = KeyPair(publicKey, privateKey)

        qrImageView = findViewById(R.id.imageView)
        val size = (resources.displayMetrics.density * 160).toInt()
        qrCodeBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)

        refreshTimer = object : CountDownTimer(15000, 1000) {
            override fun onTick(timesLeft: Long) {
                findViewById<TextView>(R.id.textView).text = (timesLeft / 1000).toString()
            }

            override fun onFinish() {
                qrImageView.imageAlpha = 128
                socket.disconnect().connect()
            }
        }
        setupSocket()
        startVerification()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        refreshTimer.cancel()
        socket.close()
    }

    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() {
            return Dispatchers.Main + job
        }

    private fun setupSocket() {
        socket = IO.socket(
            "https://wss.coov.io",
            IO.Options.builder().setTransports(arrayOf(WebSocket.NAME)).build()
        )
        socket.on(Socket.EVENT_CONNECT, onConnect)
        socket.on("challenge", onChallenge)
        socket.on("result", onResult)
    }

    private fun startVerification() {
        socket.connect()
    }

    private fun updateQRCode(data: String) {
        val size = qrCodeBitmap.width
        val qrCode = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, size, size)
        qrCodeBitmap.apply {
            for (x in 0 until size)
                for (y in 0 until size)
                    setPixel(x, y, if (qrCode[x, y]) Color.BLACK else Color.WHITE)
        }
        qrImageView.setImageBitmap(qrCodeBitmap)
        qrImageView.imageAlpha = 255
    }

    private val onConnect = Emitter.Listener { args ->
        Log.d("Responder", socket.id())
        launch {
            val qrCodeString = COOVApi.getKIPass(
                prefs.getString("token", "")!!,
                "https://wss.coov.io/${socket.id()}"
            )
            if (qrCodeString != null) {
                updateQRCode(qrCodeString)
            }
        }
        refreshTimer.start()
    }

    private val onChallenge = Emitter.Listener { args ->
        val challenge = args[0] as JSONObject
        val from = challenge.getJSONObject("data").getString("from")

        val vp = ClumsyVC.makeVP(
            keyPair, listOf(
                prefs.getString("vc_COVID19_2", null)!!,
                prefs.getString("vc_COVID19_1", null)!!,
                prefs.getString("vc_name", null)!!
            ), challenge.getJSONObject("data").getString("challenge")
        )

        val dataObj = JSONObject()
        dataObj.put("from", socket.id())
        dataObj.put("vp", vp)

        val respObj = JSONObject()
        respObj.put("to", from)
        respObj.put("data", dataObj)
        socket.emit("response", respObj)
    }

    private val onResult = Emitter.Listener { args ->
        finish()
    }
}