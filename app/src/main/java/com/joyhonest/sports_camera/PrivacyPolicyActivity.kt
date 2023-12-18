package com.joyhonest.sports_camera

import android.app.Activity
import android.content.Intent
import android.net.MailTo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView

class PrivacyPolicyActivity : Activity() {
    private var backPrivacy: ImageView? = null
    private var btnReload: TextView? = null
    private var errorLayout: RelativeLayout? = null
    private var loadProgressBar: ProgressBar? = null
    private var loadSuccess = true
    private var webView: WebView? = null
    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_privacy_policy)
        initView()
    }

    private fun initView() {
        webView = findViewById<View>(R.id.webView) as WebView
        loadProgressBar = findViewById<View>(R.id.loadProgressBar) as ProgressBar
        errorLayout = findViewById<View>(R.id.error_layout) as RelativeLayout
        btnReload = findViewById<View>(R.id.btn_reload) as TextView
        backPrivacy = findViewById<View>(R.id.backPrivacy) as ImageView
        webView!!.settings.javaScriptEnabled = true
        loadWebpage()
        webView!!.webViewClient = object : WebViewClient() {
            override fun onReceivedError(webView: WebView, i: Int, str: String, str2: String) {
                super.onReceivedError(webView, i, str, str2)
                if (Build.VERSION.SDK_INT >= 23) {
                    return
                }
                loadSuccess = false
                handleLoadSuccess(false)
            }

            override fun onReceivedError(webView: WebView, webResourceRequest: WebResourceRequest, webResourceError: WebResourceError) {
                super.onReceivedError(webView, webResourceRequest, webResourceError)
                if (webResourceRequest.isForMainFrame) {
                    loadSuccess = false
                    handleLoadSuccess(false)
                }
            }

            override fun onPageFinished(webView: WebView, str: String) {
                val privacyPolicyActivity: PrivacyPolicyActivity = this@PrivacyPolicyActivity
                privacyPolicyActivity.handleLoadSuccess(privacyPolicyActivity.loadSuccess)
            }

            override fun shouldOverrideUrlLoading(webView: WebView, str: String): Boolean {
                if (str.startsWith(MailTo.MAILTO_SCHEME)) {
                    sendMail(str)
                    return true
                }
                return super.shouldOverrideUrlLoading(webView, str)
            }
        }
        btnReload!!.setOnClickListener { loadWebpage() }
        backPrivacy!!.setOnClickListener { onBackPressed() }
    }

    fun sendMail(str: String) {
        val intent = Intent("android.intent.action.SENDTO")
        intent.data = Uri.parse(str)
        intent.putExtra("android.intent.extra.EMAIL", arrayOf(str.substring(7)))
        startActivity(Intent.createChooser(intent, "Send Email"))
    }

    fun loadWebpage() {
        loadSuccess = true
        webView!!.loadUrl("http://www.joyhonest.com/Privacy_policy_Sports_Camera.html")
        loadProgressBar!!.visibility = View.VISIBLE
        webView!!.visibility = View.GONE
        errorLayout!!.visibility = View.GONE
    }

    fun handleLoadSuccess(z: Boolean) {
        loadProgressBar!!.visibility = View.GONE
        if (z) {
            webView!!.visibility = View.VISIBLE
            errorLayout!!.visibility = View.GONE
        } else {
            webView!!.visibility = View.GONE
            errorLayout!!.visibility = View.VISIBLE
        }
        if (z) {
            if (resources.configuration.uiMode and 48 == 32) {
                webView!!.evaluateJavascript("javascript:setMode('dark')") { }
            } else {
                webView!!.evaluateJavascript("javascript:setMode('light')") { }
            }
        }
    }
}