package com.joyhonest.sports_camera;

import android.graphics.Bitmap;
public class MyNode {
    public static final int Status_downloaded = 2;
    public static final int Status_downloading = 1;
    public static final int Status_waitdownload = 3;
    public static final int Stauts_normal = 0;
    public static int TYPE_Local = 0;
    public static int TYPE_SD = 1;
    public Bitmap bitmap;
    public int nGetType;
    public int nLength;
    public int nOp;
    public long nPre;
    public int nStatus;
    public int nType;
    public String sPath;
    public String sSDMp4name;
    public String sText;
    public String sUrl;

    public MyNode() {
        this.nOp = 0;
        this.nStatus = 0;
        this.nType = TYPE_Local;
        this.bitmap = null;
        this.sPath = "";
        this.sUrl = "";
        this.sText = "";
        this.sSDMp4name = "";
        this.nOp = 0;
        this.nLength = 0;
    }

    public MyNode(int i) {
        this.nOp = 0;
        this.nStatus = 0;
        this.nType = i;
        this.bitmap = null;
        this.sPath = "";
        this.sUrl = "";
        this.sText = "";
        this.sSDMp4name = "";
        this.nOp = 0;
        this.nLength = 0;
    }
}
