package com.joyhonest.sports_camera;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.internal.view.SupportMenu;
import androidx.core.view.ViewCompat;

import com.joyhonest.wifination.wifination;

import org.simple.eventbus.Subscriber;
public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    ConstraintLayout AlertView;
    TextView Ver_View;
    Button btn_1min;
    Button btn_3min;
    Button btn_5min;
    Button btn_osd;
    Button btn_rev;
    Button format_b;
    TextView format_v;
    Button rest_b;
    TextView rest_v;
    TextView sLine1;
    TextView sLine2;
    private final String TAG = "SettingActivity";
    boolean bRev = false;
    boolean bOsd = false;
    int nRecT = 0;
    boolean bReadOK = false;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_setting);
        MyApp.F_makeFullScreen(this);
        ((TextView) findViewById(R.id.APP_Ver_View)).setText("1.6-24");
        ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.AlertView);
        this.AlertView = constraintLayout;
        constraintLayout.setVisibility(View.INVISIBLE);
        TextView textView = (TextView) findViewById(R.id.sLine2);
        this.sLine2 = textView;
        textView.setTextColor(SupportMenu.CATEGORY_MASK);
        this.sLine1 = (TextView) findViewById(R.id.sLine1);
        this.btn_rev = (Button) findViewById(R.id.btn_rev);
        this.btn_osd = (Button) findViewById(R.id.btn_osd);
        this.btn_1min = (Button) findViewById(R.id.btn_1min);
        this.btn_3min = (Button) findViewById(R.id.btn_3min);
        this.btn_5min = (Button) findViewById(R.id.btn_5min);
        this.Ver_View = (TextView) findViewById(R.id.Ver_View);
        this.format_v = (TextView) findViewById(R.id.format_v);
        this.format_b = (Button) findViewById(R.id.format_b);
        this.rest_v = (TextView) findViewById(R.id.rest_v);
        this.rest_b = (Button) findViewById(R.id.rest_b);
        this.btn_rev.setOnClickListener(this);
        this.btn_osd.setOnClickListener(this);
        this.btn_1min.setOnClickListener(this);
        this.btn_3min.setOnClickListener(this);
        this.btn_5min.setOnClickListener(this);
        this.format_v.setOnClickListener(this);
        this.format_b.setOnClickListener(this);
        this.rest_v.setOnClickListener(this);
        this.rest_b.setOnClickListener(this);
        this.AlertView.setOnClickListener(this);
        findViewById(R.id.btn_ok).setOnClickListener(this);
        findViewById(R.id.btn_ok_a).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApp.PlayBtnVoice();
                onBackPressed();
            }
        });
        MyApp.forceSendRequestByWifiData(true, new RequestWifi_Interface() {
            @Override
            public void onResult() {
                MyApp.F_Init4225();
                wifination.na4225_ReadReversal();
                wifination.na4225_ReadOsd();
                wifination.na4225_ReadRecordTime();
                wifination.na4225_ReadFireWareVer();
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            }
        }, 100L);
    }

    @Override
    public void onClick(View view) {
        MyApp.PlayBtnVoice();
        switch (view.getId()) {
            case R.id.btn_1min:
                this.nRecT = 0;
                F_DispStatus();
                wifination.na4225_SetRecordTime(this.nRecT);
                return;
            case R.id.btn_3min:
                this.nRecT = 1;
                F_DispStatus();
                wifination.na4225_SetRecordTime(this.nRecT);
                return;
            case R.id.btn_5min:
                this.nRecT = 2;
                F_DispStatus();
                wifination.na4225_SetRecordTime(this.nRecT);
                return;
            case R.id.btn_cancel:
                DispAlertView(false, false);
                return;
            case R.id.btn_ok:
                DispAlertView(false, false);
                wifination.na4225_FormatSD();
                this.AlertView.setVisibility(View.GONE);
                return;
            case R.id.btn_ok_a:
                DispAlertView(false, false);
                return;
            case R.id.btn_osd:
                this.bOsd = !this.bOsd;
                F_DispStatus();
                wifination.na4225_SetOsd(this.bOsd);
                return;
            case R.id.btn_rev:
                this.bRev = !this.bRev;
                F_DispStatus();
                wifination.na4225_SetReversal(this.bRev);
                DispAlertView(false, true);
                return;
            case R.id.format_b:
            case R.id.format_v:
                DispAlertView(true, true);
                return;
            case R.id.rest_b:
            case R.id.rest_v:
                wifination.na4225_ResetDevice();
                DispAlertView(false, true);
                return;
            default:
                return;
        }
    }

    private void DispAlertView(boolean z, boolean z2) {
        if (z2) {
            if (z) {
                this.sLine1.setText(R.string.Warring);
                this.sLine2.setText(R.string.All_Data_will_be_deleted);
                this.sLine2.setTextColor(SupportMenu.CATEGORY_MASK);
                findViewById(R.id.btn_ok).setVisibility(View.VISIBLE);
                findViewById(R.id.btn_cancel).setVisibility(View.VISIBLE);
                findViewById(R.id.btn_ok_a).setVisibility(View.GONE);
            } else {
                this.sLine1.setText("");
                this.sLine2.setText(R.string.Please_reset_device);
                this.sLine2.setTextColor(ViewCompat.MEASURED_STATE_MASK);
                findViewById(R.id.btn_ok).setVisibility(View.INVISIBLE);
                findViewById(R.id.btn_cancel).setVisibility(View.INVISIBLE);
                findViewById(R.id.btn_ok_a).setVisibility(View.VISIBLE);
            }
            this.AlertView.setVisibility(View.VISIBLE);
            return;
        }
        this.AlertView.setVisibility(View.GONE);
    }

    private void F_DispStatus() {
        if (this.bOsd) {
            this.btn_osd.setBackgroundResource(R.mipmap.on);
        } else {
            this.btn_osd.setBackgroundResource(R.mipmap.off);
        }
        if (this.bRev) {
            this.btn_rev.setBackgroundResource(R.mipmap.on);
        } else {
            this.btn_rev.setBackgroundResource(R.mipmap.off);
        }
        int i = this.nRecT;
        if (i == 0) {
            this.btn_1min.setBackgroundResource(R.mipmap.selected_button);
            this.btn_3min.setBackgroundResource(R.mipmap.no_selected_button);
            this.btn_5min.setBackgroundResource(R.mipmap.no_selected_button);
        } else if (i == 1) {
            this.btn_1min.setBackgroundResource(R.mipmap.no_selected_button);
            this.btn_3min.setBackgroundResource(R.mipmap.selected_button);
            this.btn_5min.setBackgroundResource(R.mipmap.no_selected_button);
        } else {
            this.btn_1min.setBackgroundResource(R.mipmap.no_selected_button);
            this.btn_3min.setBackgroundResource(R.mipmap.no_selected_button);
            this.btn_5min.setBackgroundResource(R.mipmap.selected_button);
        }
    }

    @Subscriber(tag = "GP4225_GetDeviceOsdStatus")
    private void GP4225_GetDeviceOsdStatus(int i) {
        this.bReadOK = true;
        Log.e("SettingActivity", "Osd = " + i);
        if (i == 0) {
            this.bOsd = false;
        } else {
            this.bOsd = true;
        }
        F_DispStatus();
    }

    @Subscriber(tag = "GP4225_GetDeviceReversaltatus")
    private void GP4225_GetDeviceReversaltatus(int i) {
        this.bReadOK = true;
        Log.e("SettingActivity", "Reversal = " + i);
        if (i == 0) {
            this.bRev = false;
        } else {
            this.bRev = true;
        }
        F_DispStatus();
    }

    @Subscriber(tag = "GP4225_GetDeviceRecordTime")
    private void GP4225_GetDeviceRecordTime(int i) {
        this.bReadOK = true;
        Log.e("SettingActivity", "RecordTime = " + i);
        if (i == 0) {
            this.nRecT = 0;
        } else if (i == 1) {
            this.nRecT = 1;
        } else {
            this.nRecT = 2;
        }
        F_DispStatus();
    }

    @Subscriber(tag = "GP4225_GetFireWareVersion")
    private void GP4225_GetFireWareVersion(String str) {
        this.bReadOK = true;
        this.Ver_View.setText(str);
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
