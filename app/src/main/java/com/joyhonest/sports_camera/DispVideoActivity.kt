package com.joyhonest.sports_camera

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import org.simple.eventbus.Subscriber

class DispVideoActivity : AppCompatActivity() {
    private val bTouchSeekBar = false
    private var video_play: CustomVideoView? = null
    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_disp_video)
        MyApp.F_makeFullScreen(this)
        video_play = findViewById<View>(R.id.video_play) as CustomVideoView
        findViewById<View>(R.id.btn_back).setOnClickListener {
            MyApp.PlayBtnVoice()
            onBackPressed()
        }
        val mediaController = MediaController(this)
        video_play!!.visibility = View.VISIBLE
        video_play!!.setMediaController(mediaController)
        mediaController.setMediaPlayer(video_play)
        video_play!!.setVideoURI(Uri.parse(MyApp.Companion.dispList.get(0)))
        video_play!!.start()
        video_play!!.requestFocus()
    }

    public override fun onPause() {
        super.onPause()
    }

    public override fun onResume() {
        super.onResume()
        MyApp.F_makeFullScreen(this)
    }

    public override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        video_play!!.stopPlayback()
    }

    @Subscriber(tag = "Go2Background")
    private fun Go2Background(str: String) {
        finish()
    }

    companion object {
        private const val TAG = "DispVideo"
    }
}