package com.joyhonest.sports_camera;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import org.simple.eventbus.Subscriber;
public class SplashActivity extends AppCompatActivity implements View.OnClickListener {
    private ConstraintLayout NoDevice_AlertView;
    private Button btn_Start;
    private Button btn_cancel;
    private Button btn_ok;
    private TextView privacyPolicy;
    private final String TAG = "SplashActivity";
    private boolean bNeedExit = true;
    Handler handler_1 = new Handler();

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_splash);
        MyApp.F_makeFullScreen(this);
        this.btn_Start = (Button) findViewById(R.id.btn_Start);
        this.NoDevice_AlertView = (ConstraintLayout) findViewById(R.id.NoDevice_AlertView);
        this.btn_cancel = (Button) findViewById(R.id.btn_cancel);
        this.btn_ok = (Button) findViewById(R.id.btn_ok);
        this.privacyPolicy = (TextView) findViewById(R.id.privacyPolicy);
        this.NoDevice_AlertView.setVisibility(View.INVISIBLE);
        this.NoDevice_AlertView.setOnClickListener(this);
        this.btn_Start.setOnClickListener(this);
        this.btn_cancel.setOnClickListener(this);
        this.btn_ok.setOnClickListener(this);
        this.privacyPolicy.setOnClickListener(this);
        findViewById(R.id.btn_Help).setOnClickListener(this);
        findViewById(R.id.btn_Setting).setOnClickListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        MyApp.PlayBtnVoice();
        switch (view.getId()) {
            case R.id.btn_Help:
                this.bNeedExit = false;
                startActivity(new Intent(this, HelpActivity.class));
                return;
            case R.id.btn_Setting:
                if (MyApp.CheckConnectedDevice()) {
                    this.bNeedExit = false;
                    startActivity(new Intent(this, SettingActivity.class));
                    return;
                }
                this.NoDevice_AlertView.setVisibility(View.VISIBLE);
                return;
            case R.id.btn_Start:
                if (MyApp.CheckConnectedDevice()) {
                    this.bNeedExit = false;
                    startActivity(new Intent(this, PlayActivity.class));
                    return;
                }
                this.NoDevice_AlertView.setVisibility(View.VISIBLE);
                return;
            case R.id.btn_cancel:
                this.NoDevice_AlertView.setVisibility(View.GONE);
                return;
            case R.id.btn_ok:
                this.bNeedExit = false;
                MyApp.bGotsystemActivity = true;
                startActivity(new Intent("android.settings.WIFI_SETTINGS"));
                this.NoDevice_AlertView.setVisibility(View.GONE);
                return;
            case R.id.privacyPolicy:
                startActivity(new Intent(this, PrivacyPolicyActivity.class));
                return;
            default:
                return;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("SplashActivity", "onResume");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.handler_1.removeCallbacksAndMessages(null);
        Log.e("SplashActivity", "SplashActivity Destroy");
    }

    @Subscriber(tag = "GP4225_GetStatus")
    private void GP4225_GetStatus(String str) {
        Log.e("SplashActivity", "Get gp4225 status!");
    }

    @Subscriber(tag = "onUdpRevData")
    private void onUdpRevData(byte[] bArr) {
        Log.e("SplashActivity", "onUdpRevData!");
    }
}
