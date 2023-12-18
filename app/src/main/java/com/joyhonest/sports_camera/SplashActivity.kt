package com.joyhonest.sports_camera

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import org.simple.eventbus.Subscriber

class SplashActivity : AppCompatActivity(), View.OnClickListener {


    private var NoDevice_AlertView: ConstraintLayout? = null
    private var btn_Start: Button? = null
    private var btn_cancel: Button? = null
    private var btn_ok: Button? = null
    private var privacyPolicy: TextView? = null
    private val TAG = "SplashActivity"
    private var bNeedExit = true
    var handler_1 = Handler()


    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_splash)
        MyApp.F_makeFullScreen(this)
        btn_Start = findViewById<View>(R.id.btn_Start) as Button
        NoDevice_AlertView = findViewById<View>(R.id.NoDevice_AlertView) as ConstraintLayout
        btn_cancel = findViewById<View>(R.id.btn_cancel) as Button
        btn_ok = findViewById<View>(R.id.btn_ok) as Button
        privacyPolicy = findViewById<View>(R.id.privacyPolicy) as TextView
        NoDevice_AlertView!!.visibility = View.INVISIBLE
        NoDevice_AlertView!!.setOnClickListener(this)
        btn_Start!!.setOnClickListener(this)
        btn_cancel!!.setOnClickListener(this)
        btn_ok!!.setOnClickListener(this)
        privacyPolicy!!.setOnClickListener(this)
        findViewById<View>(R.id.btn_Help).setOnClickListener(this)
        findViewById<View>(R.id.btn_Setting).setOnClickListener(this)
    }

    public override fun onPause() {
        super.onPause()
    }

    override fun onClick(view: View) {
        MyApp.PlayBtnVoice()
        when (view.id) {
            R.id.btn_Help -> {
                bNeedExit = false
                startActivity(Intent(this, HelpActivity::class.java))
                return
            }

            R.id.btn_Setting -> {
                if (MyApp.CheckConnectedDevice()) {
                    bNeedExit = false
                    startActivity(Intent(this, SettingActivity::class.java))
                    return
                }
                NoDevice_AlertView!!.visibility = View.VISIBLE
                return
            }

            R.id.btn_Start -> {
                if (MyApp.CheckConnectedDevice()) {
                    bNeedExit = false
                    startActivity(Intent(this, PlayActivity::class.java))
                    return
                }
                NoDevice_AlertView!!.visibility = View.VISIBLE
                return
            }

            R.id.btn_cancel -> {
                NoDevice_AlertView!!.visibility = View.GONE
                return
            }

            R.id.btn_ok -> {
                bNeedExit = false
                MyApp.bGotsystemActivity = true
                startActivity(Intent("android.settings.WIFI_SETTINGS"))
                NoDevice_AlertView!!.visibility = View.GONE
                return
            }

            R.id.privacyPolicy -> {
                startActivity(Intent(this, PrivacyPolicyActivity::class.java))
                return
            }

            else -> return
        }
    }

    public override fun onResume() {
        super.onResume()
        Log.e("SplashActivity", "onResume")
    }

    public override fun onDestroy() {
        super.onDestroy()
        handler_1.removeCallbacksAndMessages(null)
        Log.e("SplashActivity", "SplashActivity Destroy")
    }

    @Subscriber(tag = "GP4225_GetStatus")
    private fun GP4225_GetStatus(str: String) {
        Log.e("SplashActivity", "Get gp4225 status!")
    }

    @Subscriber(tag = "onUdpRevData")
    private fun onUdpRevData(bArr: ByteArray) {
        Log.e("SplashActivity", "onUdpRevData!")
    }
}