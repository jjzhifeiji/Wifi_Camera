package com.joyhonest.sports_camera;

import android.app.Activity;

import androidx.core.app.ActivityCompat;
public class PermissionAsker {
    private static final Runnable RUN = new Runnable() {
        @Override
        public void run() {
        }
    };
    private boolean bChecked;
    private Runnable mDeniRun;
    private Runnable mOkRun;
    private int mReqCode;

    public PermissionAsker() {
        Runnable runnable = RUN;
        this.mOkRun = runnable;
        this.mDeniRun = runnable;
        this.mReqCode = 1;
        this.bChecked = false;
    }

    public PermissionAsker(int i, Runnable runnable, Runnable runnable2) {
        Runnable runnable3 = RUN;
        this.mOkRun = runnable3;
        this.mDeniRun = runnable3;
        this.mReqCode = 1;
        this.bChecked = false;
        this.mReqCode = i;
        this.mOkRun = runnable;
        this.mDeniRun = runnable2;
    }

    public void setReqCode(int i) {
        this.mReqCode = i;
    }

    public void setSuccedCallback(Runnable runnable) {
        this.mOkRun = runnable;
    }

    public void setFailedCallback(Runnable runnable) {
        this.mDeniRun = runnable;
    }

    public PermissionAsker askPermission(Activity activity, String... strArr) {
        int i = 0;
        for (String str : strArr) {
            i += ActivityCompat.checkSelfPermission(activity, str);
        }
        if (i == 0) {
            this.mOkRun.run();
        } else {
            ActivityCompat.requestPermissions(activity, strArr, this.mReqCode);
        }
        return this;
    }

    public void onRequestPermissionsResult(int[] iArr) {
        boolean z = true;
        for (int i : iArr) {
            z &= i == 0;
        }
        if (iArr.length > 0 && z) {
            this.mOkRun.run();
        } else {
            this.mDeniRun.run();
        }
    }
}
