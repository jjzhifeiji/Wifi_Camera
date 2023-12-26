package com.joyhonest.sports_camera;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class boot_Activity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_boot_);
        getWindow().getDecorView().setBackgroundResource(R.mipmap.boot_interface);
        MyApp.F_makeFullScreen(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!AppPrefs.getInstance(getApplicationContext()).getPrivacyPolicyAgreement()) {
                    requestPrivacyPolicyAgreement();
                    return;
                }
                startActivity(new Intent(boot_Activity.this, SplashActivity.class));
                finish();
                overridePendingTransition(0, 0);
            }
        }, 1500L);
    }

    @Override
    public void onResume() {
        super.onResume();
        MyApp.F_makeFullScreen(this);
    }

    public void requestPrivacyPolicyAgreement() {
        PrivacyPolicyAgreementDialog newInstance = PrivacyPolicyAgreementDialog.newInstance(this, getResources().getString(R.string.privacy_introduce_title), getResources().getString(R.string.privacy_introduce_content), getResources().getString(R.string.privacy_agree), getResources().getString(R.string.privacy_disagree), new PrivacyPolicyAgreementDialog.Listener() {
            @Override
            public void onYes() {
                AppPrefs.getInstance(getApplicationContext()).setPrivacyPolicyAgreement(true);
                startActivity(new Intent(boot_Activity.this, SplashActivity.class));
                finish();
                overridePendingTransition(0, 0);
            }

            @Override
            public void onNo() {
                finish();
            }
        });
        newInstance.setCancelable(false);
        newInstance.show(getSupportFragmentManager(), "Privacy Policy Agreement");
    }
}
