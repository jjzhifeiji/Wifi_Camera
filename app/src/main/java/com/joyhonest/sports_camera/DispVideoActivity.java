package com.joyhonest.sports_camera;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;

import androidx.appcompat.app.AppCompatActivity;

import org.simple.eventbus.Subscriber;
public class DispVideoActivity extends AppCompatActivity {
    private static final String TAG = "DispVideo";
    private boolean bTouchSeekBar = false;
    private CustomVideoView video_play;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_disp_video);
        MyApp.F_makeFullScreen(this);
        this.video_play = (CustomVideoView) findViewById(R.id.video_play);
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApp.PlayBtnVoice();
                onBackPressed();
            }
        });
        MediaController mediaController = new MediaController(this);
        this.video_play.setVisibility(View.VISIBLE);
        this.video_play.setMediaController(mediaController);
        mediaController.setMediaPlayer(this.video_play);
        this.video_play.setVideoURI(Uri.parse(MyApp.dispList.get(0)));
        this.video_play.start();
        this.video_play.requestFocus();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        MyApp.F_makeFullScreen(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.video_play.stopPlayback();
    }

    @Subscriber(tag = "Go2Background")
    private void Go2Background(String str) {
        finish();
    }
}
