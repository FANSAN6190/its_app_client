package com.example.civiceye.ui.home


import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.example.civiceye.R
import com.example.civiceye.databinding.ActivityMapBinding

class MapActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val webView: WebView = binding.mapWebView
        webView.settings.javaScriptEnabled = true
        webView.loadUrl("file:///android_asset/map1.html")

        webView.addJavascriptInterface(object : Any() {
            @JavascriptInterface
            fun onLocationSelected(longitude: Double, latitude: Double) {
                val result = Intent().apply {
                    putExtra("longitude", longitude)
                    putExtra("latitude", latitude)
                }
                setResult(Activity.RESULT_OK, result)
                finish()
            }
        }, "Android")
    }
}