package com.joyhonest.sports_camera;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.joyhonest.wifination.wifination;

import org.simple.eventbus.Subscriber;
public class OnlinePlayActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView DispImageVeiw;
    boolean bNeedClose = false;
    private Button btn_back;
    private Button btn_pause;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_online_play);
        MyApp.F_makeFullScreen(this);
        this.btn_back = (Button) findViewById(R.id.btn_back);
        this.btn_pause = (Button) findViewById(R.id.btn_pause);
        this.progressBar = (ProgressBar) findViewById(R.id.progressBar);
        this.DispImageVeiw = (ImageView) findViewById(R.id.DispImageVeiw);
        this.btn_pause.setVisibility(View.INVISIBLE);
        this.btn_back.setOnClickListener(this);
        this.btn_pause.setOnClickListener(this);
        this.progressBar.setVisibility(View.VISIBLE);
        wifination.naSetRevBmp(false);
        wifination.bRevBmp = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                MyNode myNode = MyApp.OnPlayNode;
                wifination.na4225StartPlay("", myNode.sUrl, myNode.nLength);
            }
        }, 200L);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() != R.id.btn_back) {
            return;
        }
        MyApp.PlayBtnVoice();
        onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
        MyApp.F_makeFullScreen(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        wifination.naDisConnectedTCP();
        SystemClock.sleep(100L);
        wifination.naSetRevBmp(true);
    }

    @Subscriber(tag = "OnPlayStatus")
    private void OnPlayStatus(int i) {
        if (i != 0) {
            this.bNeedClose = true;
            this.progressBar.setVisibility(View.INVISIBLE);
            return;
        }
        this.bNeedClose = false;
    }

    @Subscriber(tag = "ReviceBMP")
    private void ReviceBMP(Bitmap bitmap) {
        this.progressBar.setVisibility(View.INVISIBLE);
        this.DispImageVeiw.setImageBitmap(bitmap);
    }

    @Subscriber(tag = "Go2Background")
    private void Go2Background(String str) {
        finish();
    }
}
