package com.joyhonest.sports_camera;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.joyhonest.wifination.wifination;

import org.simple.eventbus.Subscriber;
public class BrowSelectActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "BrowSelectActivity";

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_brow_select);
        MyApp.F_makeFullScreen(this);
        findViewById(R.id.btn_Photo).setOnClickListener(this);
        findViewById(R.id.btn_Video).setOnClickListener(this);
        findViewById(R.id.btn_back).setOnClickListener(this);
        findViewById(R.id.btn_Photo).setClickable(false);
        findViewById(R.id.btn_Video).setClickable(false);
        wifination.naStartRead20000_20001();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (MyApp.bBROW_SD && (MyApp.nModel == 2 || MyApp.nModel == 3)) {
            wifination.na4225_SetMode((byte) 0);
        }
        super.onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
        MyApp.F_makeFullScreen(this);
        findViewById(R.id.btn_Photo).setClickable(false);
        findViewById(R.id.btn_Video).setClickable(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.btn_Photo).setClickable(true);
                findViewById(R.id.btn_Video).setClickable(true);
            }
        }, 500L);
    }

    @Override
    public void onClick(View view) {
        MyApp.PlayBtnVoice();
        switch (view.getId()) {
            case R.id.btn_Photo:
                MyApp.BROW_TYPE = 0;
                startActivity(new Intent(this, BrowGridActivity.class));
                return;
            case R.id.btn_Setting:
            case R.id.btn_Start:
            default:
                return;
            case R.id.btn_Video:
                MyApp.BROW_TYPE = 1;
                startActivity(new Intent(this, BrowGridActivity.class));
                return;
            case R.id.btn_back:
                onBackPressed();
                return;
        }
    }

    @Subscriber(tag = "onGetRtlMode")
    private void onGetRtlMode(Integer num) {
        Log.e("BrowSelectActivity", "Mode = " + num);
    }

    @Subscriber(tag = "Go2Background")
    private void Go2Background(String str) {
        finish();
    }
}
