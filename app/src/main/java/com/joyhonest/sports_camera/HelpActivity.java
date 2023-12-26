package com.joyhonest.sports_camera;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import org.simple.eventbus.Subscriber;
public class HelpActivity extends AppCompatActivity {
    ImageView ImageView_01;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_help);
        MyApp.F_makeFullScreen(this);
        this.ImageView_01 = (ImageView) findViewById(R.id.ImageView_01);
        if (MyApp.isZH()) {
            this.ImageView_01.setImageResource(R.mipmap.help01_cn);
        } else {
            this.ImageView_01.setImageResource(R.mipmap.help01_en);
        }
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApp.PlayBtnVoice();
                onBackPressed();
            }
        });
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

    @Subscriber(tag = "Go2Background")
    private void Go2Background(String str) {
        finish();
    }
}
