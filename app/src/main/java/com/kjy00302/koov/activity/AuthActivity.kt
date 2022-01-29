package com.kjy00302.koov.activity

import android.content.Context
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebStorage
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.kjy00302.koov.R
import com.kjy00302.koov.coovapi.COOVApi
import kotlinx.coroutines.*
import org.json.JSONObject
import kotlin.coroutines.CoroutineContext

class AuthActivity : AppCompatActivity(), CoroutineScope {
    lateinit var webView: WebView

    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() {
            return Dispatchers.Main + job
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webView = WebView(this)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.useWideViewPort = true
        webView.addJavascriptInterface(object {
            @JavascriptInterface
            fun close() {
                finish()
            }
        }, "self")

        webView.addJavascriptInterface(object {
            @JavascriptInterface
            fun postMessage(jsonString: String) {
                val authResponse = JSONObject(jsonString)
                if (authResponse.getString("type") == "success") {
                    val data = authResponse.getJSONObject("data")
                    val preferences = getSharedPreferences(
                        getString(R.string.preferences_file_key),
                        Context.MODE_PRIVATE
                    )
                    preferences.edit()
                        .putString("token", data.getString("token"))
                        .apply()
                }
                finish()
            }
        }, "ReactNativeWebView")

        webView.webViewClient = WebViewClient()
        setContentView(webView)
        job = Job()
        launch {
            val resp = COOVApi.retrofitService.getAuthPage()
            val body = withContext(Dispatchers.IO) {
                resp.body()!!.string()
            }
            webView.loadData(body, "text/html", "UTF-8")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        webView.clearCache(true)
        webView.destroy()
        WebStorage.getInstance().deleteAllData()
        job.cancel()
    }
}