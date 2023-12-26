package com.joyhonest.sports_camera;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.joyhonest.wifination.wifination;

import org.simple.eventbus.Subscriber;

import java.util.Date;
public class PlayActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {
    private ImageView DisplayImageView;
    float PointX;
    float PointY;
    private TextView RecTime_Text;
    private AlertDialog alertDialog;
    private boolean bCheck;
    private Button btn_fillxy;
    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;
    private Button button5;
    private Button button6;
    private Button button7;
    private Button button8;
    private Button button_left0;
    private Button button_right0;
    private LinearLayout left_view;
    private PermissionAsker mAsker;
    private long mCurrentClickTime;
    private ClickPressedThread mPrevClickThread;
    private LinearLayout right_view;
    private WifiReceiver wifiReceiver;
    private ImageView wifi_signal;
    private final String TAG = "PlayActivity";
    int bNeedAction = -1;
    int nWidth = -1;
    int nHeight = -1;
    boolean bIREn = false;
    boolean bStatusLEDEn = false;
    byte nIR_Status = 0;
    boolean bLedStatus = false;
    boolean bFillxy = true;
    volatile byte nCMes = 0;
    int nTP1 = 0;
    Handler Handler_Read4225 = new Handler();
    Runnable Runnable_Read4225 = new Runnable() {
        @Override
        public void run() {
            byte b = nCMes;
            Log.e("PlayActivity", "Read4225 Status");
            byte b2 = (byte) (b + 1);
            if (b2 > 40) {
                nCMes = (byte) 40;
                b2 = 40;
            }
            nCMes = b2;
            if ((nTP1 & 3) == 0) {
                F_DispRssi();
            }
            nTP1++;
            wifination.na4225_ReadStatus();
            Handler_Read4225.postDelayed(this, 250L);
        }
    };
    private int[] wifiicon = {R.mipmap.jhd_wifi0, R.mipmap.jhd_wifi1, R.mipmap.jhd_wifi2, R.mipmap.jhd_wifi3, R.mipmap.jhd_wifi4};
    private PointF point2 = new PointF();
    private PointF point1 = new PointF();
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == 0) {
                point1.x = motionEvent.getX();
                point1.y = motionEvent.getY();
            }
            if (motionEvent.getAction() == 1) {
                point2.x = motionEvent.getX();
                point2.y = motionEvent.getY();
                float abs = Math.abs(point1.y - point2.y);
                Log.e("PlayActivity", "dy = " + abs);
                if (abs < 10.0d) {
                    MyApp.PlayBtnVoice();
                    Show_hid_toolbar();
                }
            }
            return true;
        }
    };
    boolean bt6 = true;
    boolean bt7 = true;
    boolean bTest = false;
    boolean bCanPlay = true;
    Handler Hand_DispRec = new Handler();
    Runnable Runnable_DispRec = new Runnable() {
        boolean bhalf = false;
        boolean bhalf1 = false;

        @Override
        public void run() {
            int naGetRecordTime = wifination.naGetRecordTime() / 1000;
            int i = naGetRecordTime / 60;
            int i2 = naGetRecordTime % 60;
            int i3 = i / 60;
            int i4 = i % 60;
            RecTime_Text.setText(i3 == 0 ? String.format("%02d:%02d", Integer.valueOf(i4), Integer.valueOf(i2)) : String.format("%02d:%02d:%02d", Integer.valueOf(i3), Integer.valueOf(i4), Integer.valueOf(i2)));
            boolean z = !this.bhalf;
            this.bhalf = z;
            if (z) {
                boolean z2 = !this.bhalf1;
                this.bhalf1 = z2;
                if (z2) {
                    button3.setBackgroundResource(R.mipmap.video_icon_recording);
                } else {
                    button3.setBackgroundResource(R.mipmap.video_icon);
                }
            }
            Hand_DispRec.postDelayed(this, 250L);
        }
    };
    Handler Hand_DispRec_SD = new Handler();
    Runnable Runnable_DispRec_SD = new Runnable() {
        boolean bhalf = false;
        boolean bhalf1 = false;

        @Override
        public void run() {
            boolean z = !this.bhalf;
            this.bhalf = z;
            if (z) {
                boolean z2 = !this.bhalf1;
                this.bhalf1 = z2;
                if (z2) {
                    button7.setBackgroundResource(R.mipmap.video_device_recording);
                } else {
                    button7.setBackgroundResource(R.mipmap.video_device);
                }
            }
            Hand_DispRec_SD.postDelayed(this, 250L);
        }
    };
    int nCheckConnected = 0;
    Handler CheckHander = new Handler();
    Runnable CheckRunnable = new Runnable() {
        @Override
        public void run() {
            if (nCheckConnected < 20) {
                nCheckConnected++;
            } else {
                MyApp.F_OpenCamera(false);
                SystemClock.sleep(250L);
                MyApp.F_OpenCamera(true);
                nCheckConnected = 0;
            }
            CheckHander.postDelayed(this, 100L);
        }
    };
    boolean bSdRecording = false;
    boolean bMultFing = false;
    PointF prePointF = new PointF(0.0f, 0.0f);
    private int mClickCount = 0;
    private Handler mBaseHandler = new Handler();
    boolean bAdjIng = false;

    private void DispIrIcon() {
    }

    private void DispPIrIcon() {
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return true;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_play);
        MyApp.F_makeFullScreen(this);
        this.wifi_signal = (ImageView) findViewById(R.id.wifi_signal);
        this.btn_fillxy = (Button) findViewById(R.id.btn_fillxy);
        this.DisplayImageView = (ImageView) findViewById(R.id.DisplayImageView);
        this.left_view = (LinearLayout) findViewById(R.id.left_view);
        this.right_view = (LinearLayout) findViewById(R.id.right_view);
        this.button1 = (Button) findViewById(R.id.button1);
        this.button2 = (Button) findViewById(R.id.button2);
        this.button3 = (Button) findViewById(R.id.button3);
        this.button4 = (Button) findViewById(R.id.button4);
        this.button5 = (Button) findViewById(R.id.button5);
        this.button6 = (Button) findViewById(R.id.button6);
        this.button7 = (Button) findViewById(R.id.button7);
        this.button8 = (Button) findViewById(R.id.button8);
        TextView textView = (TextView) findViewById(R.id.RecTime_Text);
        this.RecTime_Text = textView;
        textView.setVisibility(View.INVISIBLE);
        this.button6.setEnabled(false);
        this.button7.setEnabled(false);
        this.button_left0 = (Button) findViewById(R.id.button_left0);
        this.button_right0 = (Button) findViewById(R.id.button_right0);
        F_SetFitXY();
        this.DisplayImageView.setOnClickListener(this);
        this.button1.setOnClickListener(this);
        this.button2.setOnClickListener(this);
        this.button3.setOnClickListener(this);
        this.button4.setOnClickListener(this);
        this.button5.setOnClickListener(this);
        this.button6.setOnClickListener(this);
        this.button7.setOnClickListener(this);
        this.button8.setOnClickListener(this);
        this.left_view.setOnClickListener(this);
        this.btn_fillxy.setOnClickListener(this);
        this.button_left0.setOnClickListener(this);
        this.button_right0.setOnClickListener(this);
        String string = getResources().getString(R.string.warning);
        String string2 = getResources().getString(R.string.The_necessary_permission_denied);
        this.alertDialog = new AlertDialog.Builder(this).setTitle(string).setMessage(string2).setNegativeButton(getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new PermissionPageUtils(PlayActivity.this).jumpPermissionPage();
            }
        }).create();
        if (MyApp.isAndroidQ()) {
            MyApp.F_CreateLocalDir("");
        }
        this.mAsker = new PermissionAsker(10, new Runnable() {
            @Override
            public void run() {
                MyApp.F_CreateLocalDir("");
                DoAction();
            }
        }, new Runnable() {
            @Override
            public void run() {
                F_DispDialg();
            }
        });
        if (MyApp.nModel == 2 || MyApp.nModel == 3) {
            this.button6.setVisibility(View.VISIBLE);
            this.button7.setVisibility(View.VISIBLE);
        }
        this.wifiReceiver = new WifiReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.wifi.STATE_CHANGE");
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(this.wifiReceiver, intentFilter);
        F_AdjStatusLed_Button();
        F_AdjIR_Button();
    }

    private void F_SetFitXY() {
        if (this.bFillxy) {
            this.DisplayImageView.setScaleType(ImageView.ScaleType.FIT_XY);
            this.btn_fillxy.setBackgroundResource(R.mipmap.fitcenter);
            return;
        }
        this.DisplayImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        this.btn_fillxy.setBackgroundResource(R.mipmap.fillxy);
    }

    public void F_DispRssi() {
        int wifiRssi = getWifiRssi();
        if (wifiRssi > 4) {
            wifiRssi = 4;
        }
        if (wifiRssi < 0) {
            wifiRssi = 0;
        }
        this.wifi_signal.setBackgroundResource(this.wifiicon[wifiRssi]);
    }

    public void F_DispDialg() {
        this.alertDialog.show();
    }

    private void F_Checked() {
        if (MyApp.isAndroidQ()) {
            DoAction();
        } else {
            this.mAsker.askPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE");
        }
    }

    public void DoAction() {
        if (this.bNeedAction == 0) {
            TakePhoto();
            this.bNeedAction = -1;
        }
        if (this.bNeedAction == 1) {
            StartRecordVideo();
            this.bNeedAction = -1;
        }
        if (this.bNeedAction == 2) {
            this.bNeedAction = -1;
            startActivity(new Intent(this, BrowSelectActivity.class));
        }
    }

    public void Show_hid_toolbar() {
        if (MyApp.bIsConnect) {
            int width = this.left_view.getWidth() + 2;
            int width2 = this.right_view.getWidth() + 2;
            if (this.left_view.getTranslationX() < 0.0f) {
                ObjectAnimator.ofFloat(this.left_view, "TranslationX", 0.0f).setDuration(300L).start();
                ObjectAnimator.ofFloat(this.right_view, "TranslationX", 0.0f).setDuration(300L).start();
                return;
            }
            ObjectAnimator.ofFloat(this.left_view, "TranslationX", -width).setDuration(300L).start();
            ObjectAnimator.ofFloat(this.right_view, "TranslationX", width2).setDuration(300L).start();
        } else if (this.left_view.getTranslationX() < 0.0f) {
            ObjectAnimator.ofFloat(this.left_view, "TranslationX", 0.0f).setDuration(300L).start();
            ObjectAnimator.ofFloat(this.right_view, "TranslationX", 0.0f).setDuration(300L).start();
        }
    }

    private void StartRecordVideo() {
        if (MyApp.bisRecording) {
            wifination.naStopRecord(0);
        } else {
            wifination.naStartRecord(MyApp.getFileNameFromDate(true, true), 0);
        }
    }

    private void TakePhoto() {
        MyApp.PlayPhotoMusic();
        wifination.naSnapPhoto(MyApp.getFileNameFromDate(false, true), 0);
        this.button2.setClickable(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                button2.setClickable(true);
            }
        }, 200L);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() != R.id.button2 && view.getId() != R.id.button6 && view.getId() != R.id.button7) {
            MyApp.PlayBtnVoice();
        }
        switch (view.getId()) {
            case R.id.DisplayImageView:
                Log.e("PlayActivity", "clicked!");
                Show_hid_toolbar(true);
                return;
            case R.id.btn_fillxy:
                this.bFillxy = !this.bFillxy;
                F_SetFitXY();
                return;
            case R.id.button1:
                onBackPressed();
                return;
            case R.id.button2:
                MyApp.bBROW_SD = false;
                if (MyApp.bIsConnect) {
                    this.bNeedAction = 0;
                    F_Checked();
                    return;
                }
                MyApp.PlayBtnVoice();
                return;
            case R.id.button3:
                MyApp.bBROW_SD = false;
                if (MyApp.bIsConnect) {
                    this.bNeedAction = 1;
                    F_Checked();
                    return;
                }
                return;
            case R.id.button4:
                MyApp.bBROW_SD = false;
                this.bNeedAction = 2;
                F_Checked();
                return;
            case R.id.button6:
                MyApp.bBROW_SD = true;
                if (MyApp.bIsConnect && wifination.gp4225_Device.bSD && !this.bSdRecording && this.bt6) {
                    MyApp.PlayPhotoMusic();
                    wifination.naSnapPhoto("", 1);
                    this.bt6 = false;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            bt6 = true;
                        }
                    }, 500L);
                    return;
                }
                return;
            case R.id.button7:
                MyApp.bBROW_SD = true;
                if (MyApp.bIsConnect && wifination.gp4225_Device.bSD && this.bt7) {
                    this.bt7 = false;
                    MyApp.PlayBtnVoice();
                    if (this.bSdRecording) {
                        wifination.naStopRecord(1);
                    } else {
                        wifination.naStartRecord("", 1);
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            bt7 = true;
                        }
                    }, 2000L);
                    return;
                }
                return;
            case R.id.button8:
                if (MyApp.bisRecording) {
                    wifination.naStopRecord(0);
                }
                MyApp.bBROW_SD = true;
                this.bNeedAction = 2;
                F_Checked();
                return;
            case R.id.button_left0:
                byte b = this.nIR_Status;
                if (b == 0) {
                    wifination.naSetIR(1);
                    return;
                } else if (b == 1) {
                    wifination.naSetIR(2);
                    return;
                } else {
                    wifination.naSetIR(0);
                    return;
                }
            case R.id.button_right0:
                if (this.bLedStatus) {
                    wifination.naSetStatusLedDisp(false);
                    return;
                } else {
                    wifination.naSetStatusLedDisp(true);
                    return;
                }
            default:
                return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        this.mAsker.onRequestPermissionsResult(iArr);
    }

    @Override
    public void onPause() {
        super.onPause();
        this.bCanPlay = false;
    }

    @Override
    public void onResume() {
        this.bCanPlay = true;
        super.onResume();
        if (!MyApp.CheckConnectedDevice()) {
            onBackPressed();
            return;
        }
        MyApp.F_makeFullScreen(this);
        if (!MyApp.bIsOpen) {
            this.nWidth = -1;
            this.nHeight = -1;
            MyApp.F_OpenCamera(true);
            this.nCheckConnected = 0;
        }
        this.Handler_Read4225.removeCallbacksAndMessages(null);
        this.Handler_Read4225.post(this.Runnable_Read4225);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        this.Handler_Read4225.removeCallbacksAndMessages(null);
        this.bCheck = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.Hand_DispRec.removeCallbacksAndMessages(null);
        this.Handler_Read4225.removeCallbacksAndMessages(null);
        this.Hand_DispRec_SD.removeCallbacksAndMessages(null);
        MyApp.F_OpenCamera(false);
        unregisterReceiver(this.wifiReceiver);
        Log.e("PlayActivity", "PlayActivity Destroy");
    }

    private void F_DispRecordTime(boolean z) {
        if (z) {
            this.RecTime_Text.setVisibility(View.VISIBLE);
            this.Hand_DispRec.removeCallbacksAndMessages(null);
            this.Hand_DispRec.post(this.Runnable_DispRec);
            return;
        }
        this.RecTime_Text.setVisibility(View.INVISIBLE);
        this.Hand_DispRec.removeCallbacksAndMessages(null);
        this.button3.setBackgroundResource(R.mipmap.video_icon);
    }

    private void F_DispRecordTime_SD(boolean z) {
        if (z) {
            this.Hand_DispRec_SD.removeCallbacksAndMessages(null);
            this.Hand_DispRec_SD.post(this.Runnable_DispRec_SD);
            return;
        }
        this.Hand_DispRec_SD.removeCallbacksAndMessages(null);
        this.button7.setBackgroundResource(R.mipmap.video_device);
    }

    private void F_AdjIR_Button() {
        if (!this.bIREn) {
            this.button_left0.setVisibility(View.GONE);
            View findViewById = findViewById(R.id.space_1);
            if (findViewById != null) {
                findViewById.setVisibility(View.GONE);
                return;
            }
            return;
        }
        this.button_left0.setVisibility(View.VISIBLE);
        View findViewById2 = findViewById(R.id.space_1);
        if (findViewById2 != null) {
            findViewById2.setVisibility(View.VISIBLE);
        }
    }

    private void F_AdjStatusLed_Button() {
        if (!this.bStatusLEDEn) {
            this.button_right0.setVisibility(View.GONE);
            View findViewById = findViewById(R.id.space_2);
            if (findViewById != null) {
                findViewById.setVisibility(View.GONE);
                return;
            }
            return;
        }
        this.button_right0.setVisibility(View.VISIBLE);
        View findViewById2 = findViewById(R.id.space_2);
        if (findViewById2 != null) {
            findViewById2.setVisibility(View.VISIBLE);
        }
    }

    @Subscriber(tag = "SavePhotoOK")
    private void SavePhotoOK(String str) {
        if (str.length() < 5) {
            return;
        }
        String substring = str.substring(0, 2);
        String substring2 = str.substring(2, str.length());
        if (Integer.parseInt(substring) == 0) {
            MyApp.F_Save2ToGallery(substring2, true);
        } else {
            MyApp.F_Save2ToGallery(substring2, false);
        }
    }

    @Subscriber(tag = "SDStatus_Changed")
    private void _OnStatusChanged(int i) {
        Log.d("   Status", "" + i);
        if ((i & 2) != 0) {
            if (MyApp.bisRecording) {
                return;
            }
            MyApp.bisRecording = true;
            F_DispRecordTime(true);
        } else if (MyApp.bisRecording) {
            MyApp.bisRecording = false;
            F_DispRecordTime(false);
        }
    }

    @Subscriber(tag = "ReceiveBMP")
    private void ReviceBMP(Bitmap bitmap) {
        if (this.bCanPlay) {
            this.nCMes = (byte) 0;
            MyApp.bIsConnect = true;
            this.nCheckConnected = 0;
            if (this.nWidth < 0) {
                this.nWidth = bitmap.getWidth();
                this.nHeight = bitmap.getHeight();
            }
            this.DisplayImageView.setImageBitmap(bitmap);
        }
    }

    private void Show_hid_toolbar(boolean z) {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.left_view);
        LinearLayout linearLayout2 = (LinearLayout) findViewById(R.id.right_view);
        if (linearLayout == null || linearLayout2 == null) {
            return;
        }
        int width = linearLayout2.getWidth();
        int translationX = (int) linearLayout2.getTranslationX();
        if (!z) {
            if (translationX != 0) {
                ObjectAnimator.ofFloat(linearLayout2, "TranslationX", 0.0f).setDuration(2L).start();
                ObjectAnimator.ofFloat(linearLayout, "TranslationX", 0.0f).setDuration(200L).start();
            }
        } else if (translationX == 0) {
            ObjectAnimator.ofFloat(linearLayout2, "TranslationX", width).setDuration(200L).start();
            ObjectAnimator.ofFloat(linearLayout, "TranslationX", -width).setDuration(200L).start();
        } else {
            ObjectAnimator.ofFloat(linearLayout2, "TranslationX", 0.0f).setDuration(200L).start();
            ObjectAnimator.ofFloat(linearLayout, "TranslationX", 0.0f).setDuration(200L).start();
        }
    }

    @Subscriber(tag = "GP4225_GetStatus")
    private void GP4225_GetStatus(String str) {
        if (wifination.gp4225_Device.bSD) {
            if (!this.button6.isEnabled()) {
                this.button6.setEnabled(true);
                this.button7.setEnabled(true);
                this.button8.setEnabled(true);
                this.button6.setBackgroundResource(R.mipmap.photo_device);
                this.button7.setBackgroundResource(R.mipmap.video_device);
                this.button8.setBackgroundResource(R.mipmap.gallery_device);
            }
        } else if (this.button6.isEnabled()) {
            this.button6.setEnabled(false);
            this.button7.setEnabled(false);
            this.button8.setEnabled(false);
            this.button6.setBackgroundResource(R.mipmap.photo_device_dis);
            this.button7.setBackgroundResource(R.mipmap.video_device_dis);
            this.button8.setBackgroundResource(R.mipmap.gallery_device_dis);
        }
        if (wifination.gp4225_Device.bSDRecording) {
            if (!this.bSdRecording) {
                this.bSdRecording = true;
                F_DispRecordTime_SD(true);
            }
        } else if (this.bSdRecording) {
            this.bSdRecording = false;
            F_DispRecordTime_SD(false);
        }
        int[] iArr = {R.mipmap.battery_0, R.mipmap.battery_1, R.mipmap.battery_2, R.mipmap.battery_3, R.mipmap.battery_4};
        int i = wifination.gp4225_Device.nBattery;
        if (i > 4) {
            i = 4;
        }
        if (i < 0) {
            i = 3;
        }
        this.button5.setBackgroundResource(iArr[i]);
    }

    private long getSystem() {
        return Long.valueOf(new Date().getTime()).longValue();
    }

    private class ClickPressedThread implements Runnable {
        private ClickPressedThread() {
        }

        @Override
        public void run() {
            mClickCount = 0;
        }
    }

    @Subscriber(tag = "onAdjFocus")
    private void onAdjFocus(int i) {
        this.bAdjIng = false;
    }

    private void F_DispIrStatus() {
        byte b = this.nIR_Status;
        if (b == 1) {
            this.button_left0.setBackgroundResource(R.mipmap.ir_on);
        } else if (b == 2) {
            this.button_left0.setBackgroundResource(R.mipmap.ir_auto);
        } else {
            this.button_left0.setBackgroundResource(R.mipmap.ir_off);
        }
    }

    private void F_DispLedStatus() {
        if (this.bLedStatus) {
            this.button_right0.setBackgroundResource(R.mipmap.led_status_on);
        } else {
            this.button_right0.setBackgroundResource(R.mipmap.led_status_off);
        }
    }

    @Subscriber(tag = "GP4225_GetIR_Status")
    private void GP4225_GetIR_Status(Integer num) {
        this.nIR_Status = (byte) num.intValue();
        F_DispIrStatus();
        Log.e("GetStatus", "IR = " + num);
    }

    @Subscriber(tag = "GP4225_GetPIR_Status")
    private void GP4225_GetPIR_Status(Integer num) {
        Log.e("GetStatus", "PIR = " + num);
    }

    @Subscriber(tag = "GP4225_GetLed_Status")
    private void GP4225_GetLed_Status(Integer num) {
        this.bLedStatus = num.intValue() != 0;
        F_DispLedStatus();
        Log.e("GetStatus", "LEDStatus = " + num);
    }

    @Subscriber(tag = "Go2Background")
    private void Go2Background(String str) {
        finish();
    }

    @Subscriber(tag = "GP4225_GetDeviceInfo")
    private void GP4225_GetDeviceInfo(byte[] bArr) {
        if (bArr != null) {
            if (bArr.length >= 56) {
                int i = ((bArr[55] & -1) * 16777216) | (bArr[52] & -1) | ((bArr[53] & -1) * 256) | ((bArr[54] & -1) * 65536);
                this.bIREn = (i & 8) != 0;
                this.bStatusLEDEn = (i & 16) != 0;
                F_AdjIR_Button();
                F_AdjStatusLed_Button();
                if (this.bIREn) {
                    wifination.naReadIR();
                }
                if (this.bStatusLEDEn) {
                    wifination.naReadStatusLedDisp();
                    return;
                }
                return;
            }
            this.bIREn = false;
            this.bStatusLEDEn = false;
            F_AdjIR_Button();
            F_AdjStatusLed_Button();
            return;
        }
        this.bIREn = false;
        this.bStatusLEDEn = false;
        F_AdjIR_Button();
        F_AdjStatusLed_Button();
    }

    private int getWifiRssi() {
        WifiInfo connectionInfo = ((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();
        return WifiManager.calculateSignalLevel(connectionInfo != null ? connectionInfo.getRssi() : -200, 5);
    }
}
