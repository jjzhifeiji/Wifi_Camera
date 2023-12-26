package com.joyhonest.sports_camera;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import tv.danmaku.ijk.media.player.IjkMediaMeta;
public class Storage {
    public static final String VENDOR = "JH-WIFI-FPV";

    public static String getPhoneCardPath() {
        return Environment.getDataDirectory().getPath();
    }

    public static String getNormalSDCardPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    public static String getSDCardPathEx() {
        String str = new String();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("mount").getInputStream()));
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    break;
                } else if (!readLine.contains("secure") && !readLine.contains("asec") && (readLine.contains("fat") || readLine.contains("fuse"))) {
                    String[] split = readLine.split(" ");
                    if (split != null && split.length > 1 && getTotalSize(split[1]) > IjkMediaMeta.AV_CH_STEREO_RIGHT && !getNormalSDCardPath().equals(split[1])) {
                        str = str.concat(split[1] + ";");
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        return str;
    }

    public static long getTotalSize(String str) {
        try {
            StatFs statFs = new StatFs(new File(str).getPath());
            return statFs.getBlockSizeLong() * statFs.getBlockCountLong();
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    public static long getAvailableSize(String str) {
        try {
            StatFs statFs = new StatFs(new File(str).getPath());
            long blockSizeLong = statFs.getBlockSizeLong() * statFs.getAvailableBlocksLong();
            statFs.getBlockSizeLong();
            statFs.getBlockCountLong();
            return blockSizeLong;
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    public static int dip2px(Context context, float f) {
        return (int) ((f * context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    public static float dip2pxF(Context context, float f) {
        return f * context.getResources().getDisplayMetrics().density;
    }

    public static int px2dip(Context context, float f) {
        return (int) ((f / context.getResources().getDisplayMetrics().density) + 0.5f);
    }
}
