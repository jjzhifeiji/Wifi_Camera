package com.joyhonest.sports_camera

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.joyhonest.wifination.wifination
import org.simple.eventbus.Subscriber

class BrowSelectActivity : AppCompatActivity(), View.OnClickListener {
    private val TAG = "BrowSelectActivity"
    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_brow_select)
        MyApp.F_makeFullScreen(this)
        findViewById<View>(R.id.btn_Photo).setOnClickListener(this)
        findViewById<View>(R.id.btn_Video).setOnClickListener(this)
        findViewById<View>(R.id.btn_back).setOnClickListener(this)
        findViewById<View>(R.id.btn_Photo).isClickable = false
        findViewById<View>(R.id.btn_Video).isClickable = false
        wifination.naStartRead20000_20001()
    }

    public override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (MyApp.bBROW_SD && (MyApp.nModel == 2 || MyApp.nModel == 3)) {
            wifination.na4225_SetMode(0.toByte())
        }
        super.onBackPressed()
    }

    public override fun onResume() {
        super.onResume()
        MyApp.F_makeFullScreen(this)
        findViewById<View>(R.id.btn_Photo).isClickable = false
        findViewById<View>(R.id.btn_Video).isClickable = false
        Handler().postDelayed({
            findViewById<View>(R.id.btn_Photo).isClickable = true
            findViewById<View>(R.id.btn_Video).isClickable = true
        }, 500L)
    }

    override fun onClick(view: View) {
        MyApp.PlayBtnVoice()
        when (view.id) {
            R.id.btn_Photo -> {
                MyApp.BROW_TYPE = 0
                startActivity(Intent(this, BrowGridActivity::class.java))
                return
            }

            R.id.btn_Setting, R.id.btn_Start -> return
            R.id.btn_Video -> {
                MyApp.BROW_TYPE = 1
                startActivity(Intent(this, BrowGridActivity::class.java))
                return
            }

            R.id.btn_back -> {
                onBackPressed()
                return
            }

            else -> return
        }
    }

    @Subscriber(tag = "onGetRtlMode")
    private fun onGetRtlMode(num: Int) {
        Log.e("BrowSelectActivity", "Mode = $num")
    }

    @Subscriber(tag = "Go2Background")
    private fun Go2Background(str: String) {
        finish()
    }
}