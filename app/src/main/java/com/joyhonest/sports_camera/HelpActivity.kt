package com.joyhonest.sports_camera

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import org.simple.eventbus.Subscriber

class HelpActivity : AppCompatActivity() {
    var ImageView_01: ImageView? = null
    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_help)
        MyApp.F_makeFullScreen(this)
        ImageView_01 = findViewById<View>(R.id.ImageView_01) as ImageView
        if (MyApp.isZH) {
            ImageView_01!!.setImageResource(R.mipmap.help01_cn)
        } else {
            ImageView_01!!.setImageResource(R.mipmap.help01_en)
        }
        findViewById<View>(R.id.btn_back).setOnClickListener {
            MyApp.PlayBtnVoice()
            onBackPressed()
        }
    }

    public override fun onResume() {
        super.onResume()
        MyApp.F_makeFullScreen(this)
    }

    public override fun onDestroy() {
        super.onDestroy()
    }

    @Subscriber(tag = "Go2Background")
    private fun Go2Background(str: String) {
        finish()
    }
}