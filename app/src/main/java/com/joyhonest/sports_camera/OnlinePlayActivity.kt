package com.joyhonest.sports_camera

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.joyhonest.wifination.wifination
import org.simple.eventbus.Subscriber

class OnlinePlayActivity : AppCompatActivity(), View.OnClickListener {


    private var DispImageVeiw: ImageView? = null
    var bNeedClose = false
    private var btn_back: Button? = null
    private var btn_pause: Button? = null
    private var progressBar: ProgressBar? = null


    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_online_play)
        MyApp.F_makeFullScreen(this)
        btn_back = findViewById<View>(R.id.btn_back) as Button
        btn_pause = findViewById<View>(R.id.btn_pause) as Button
        progressBar = findViewById<View>(R.id.progressBar) as ProgressBar
        DispImageVeiw = findViewById<View>(R.id.DispImageVeiw) as ImageView
        btn_pause!!.visibility = View.INVISIBLE
        btn_back!!.setOnClickListener(this)
        btn_pause!!.setOnClickListener(this)
        progressBar!!.visibility = View.VISIBLE
        wifination.naSetRevBmp(false)
        wifination.bRevBmp = true
        Handler().postDelayed({
            val myNode: MyNode? = MyApp.OnPlayNode
            wifination.na4225StartPlay("", myNode?.sUrl, myNode!!.nLength)
        }, 200L)
    }

    override fun onClick(view: View) {
        if (view.id != R.id.btn_back) {
            return
        }
        MyApp.PlayBtnVoice()
        onBackPressed()
    }

    public override fun onResume() {
        super.onResume()
        MyApp.F_makeFullScreen(this)
    }

    public override fun onDestroy() {
        super.onDestroy()
        wifination.naDisConnectedTCP()
        SystemClock.sleep(100L)
        wifination.naSetRevBmp(true)
    }

    @Subscriber(tag = "OnPlayStatus")
    private fun OnPlayStatus(i: Int) {
        if (i != 0) {
            bNeedClose = true
            progressBar!!.visibility = View.INVISIBLE
            return
        }
        bNeedClose = false
    }

    @Subscriber(tag = "ReviceBMP")
    private fun ReviceBMP(bitmap: Bitmap) {
        progressBar!!.visibility = View.INVISIBLE
        DispImageVeiw!!.setImageBitmap(bitmap)
    }

    @Subscriber(tag = "Go2Background")
    private fun Go2Background(str: String) {
        finish()
    }
}