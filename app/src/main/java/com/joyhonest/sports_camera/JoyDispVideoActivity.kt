package com.joyhonest.sports_camera

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import cn.jzvd.Jzvd
import org.simple.eventbus.EventBus
import org.simple.eventbus.Subscriber

class JoyDispVideoActivity : AppCompatActivity() {
    private var my_jzvdStd: MyJzvdStd? = null
    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_joy_disp_video)
        val str: String = MyApp.sSDPath + "/tad.tmp"
        MyApp.F_GetFile4Gallery(Uri.parse(MyApp.dispList.get(0)), str)
        my_jzvdStd = findViewById<View>(R.id.jz_video) as MyJzvdStd
        findViewById<View>(R.id.btn_back).setOnClickListener {
            MyApp.PlayBtnVoice()
            onBackPressed()
        }
        my_jzvdStd!!.setUp(str, "", 0, JZMediaIjk::class.java)
        my_jzvdStd!!.startVideo()
    }

    @Subscriber(tag = "Go2Background")
    private fun Go2Background(str: String) {
        finish()
    }

    @Subscriber(tag = "PlayComplete")
    private fun PlayComplete(str: String) {
        onBackPressed()
    }

    public override fun onPause() {
        super.onPause()
    }

    public override fun onResume() {
        super.onResume()
        MyApp.F_makeFullScreen(this)
    }

    public override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    override fun onBackPressed() {
        Jzvd.releaseAllVideos()
        super.onBackPressed()
    }
}