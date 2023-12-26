package com.joyhonest.sports_camera;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import cn.jzvd.Jzvd;
public class JoyDispVideoActivity extends AppCompatActivity {
    private MyJzvdStd my_jzvdStd;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_joy_disp_video);
        String str = MyApp.sSDPath + "/tad.tmp";
        MyApp.F_GetFile4Gallery(Uri.parse(MyApp.dispList.get(0)), str);
        this.my_jzvdStd = (MyJzvdStd) findViewById(R.id.jz_video);
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApp.PlayBtnVoice();
                onBackPressed();
            }
        });
        this.my_jzvdStd.setUp(str, "", 0, JZMediaIjk.class);
        this.my_jzvdStd.startVideo();
    }

    @Subscriber(tag = "Go2Background")
    private void Go2Background(String str) {
        finish();
    }

    @Subscriber(tag = "PlayComplete")
    private void PlayComplete(String str) {
        onBackPressed();
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
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Jzvd.releaseAllVideos();
        super.onBackPressed();
    }
}
