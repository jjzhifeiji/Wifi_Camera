package com.joyhonest.sports_camera;

import android.app.Activity;
import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Window;

import com.joyhonest.wifination.wifination;

import org.simple.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class MyApp extends Application implements Application.ActivityLifecycleCallbacks {
    public static final int Brow_Photo = 0;
    public static final int Brow_Video = 1;
    public static final int TYPE_GP = 0;
    public static final int TYPE_GP4225 = 2;
    public static final int TYPE_GP4225_33 = 3;
    public static final int TYPE_RTL = 1;
    public static int nModel = 2;
    private static MyApp singleton;
    private int activityAount = 0;
    public static List<String> dispList = new ArrayList();
    public static int dispListInx = 0;
    public static MyNode OnPlayNode = null;
    public static int BROW_TYPE = 0;
    public static boolean bBROW_SD = false;
    public static boolean bG_Save2SD = false;
    public static String sLocalPath = "";
    public static String sSDPath = "";
    private static SoundPool soundPool = null;
    private static int music_photo = -1;
    private static int music_btn = -1;
    public static boolean bIsConnect = false;
    public static boolean bIsOpen = false;
    public static boolean bisRecording = false;
    public static boolean bPlayLocalVideo = false;
    private static ConnectivityManager connectivityManager = null;
    public static String s_SD_ = "Sports_Camera_SD_test";
    public static String s_Local_ = "Sports_Camera_test";
    public static boolean bGotsystemActivity = false;

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        singleton = this;
        if (Build.VERSION.SDK_INT >= 21) {
            SoundPool.Builder builder = new SoundPool.Builder();
            builder.setMaxStreams(2);
            AudioAttributes.Builder builder2 = new AudioAttributes.Builder();
            builder2.setLegacyStreamType(3);
            builder.setAudioAttributes(builder2.build());
            soundPool = builder.build();
        } else {
            soundPool = new SoundPool(2, 3, 0);
        }
        music_btn = soundPool.load(this, R.raw.button46, 1);
        music_photo = soundPool.load(this, R.raw.photo_m, 1);
    }

    public static MyApp getInstance() {
        return singleton;
    }

    public static void PlayBtnVoice() {
        int i = music_btn;
        if (i != -1) {
            soundPool.play(i, 1.0f, 1.0f, 0, 0, 1.0f);
        }
    }

    public static void PlayPhotoMusic() {
        int i = music_photo;
        if (i != -1) {
            soundPool.play(i, 1.0f, 1.0f, 0, 0, 1.0f);
        }
    }

    public static void F_CreateLocalDir(String str) {
        if (isAndroidQ()) {
            ClearTempDir();
            File externalFilesDir = singleton.getExternalFilesDir(s_Local_);
            if (externalFilesDir != null) {
                sLocalPath = externalFilesDir.getAbsolutePath();
            }
            File externalFilesDir2 = singleton.getExternalFilesDir(s_SD_);
            if (externalFilesDir2 != null) {
                sSDPath = externalFilesDir2.getAbsolutePath();
                return;
            }
            return;
        }
        F_CreateLocalDir();
    }

    public static void F_CreateLocalDir() {
        String str = s_Local_;
        try {
            String normalSDCardPath = Storage.getNormalSDCardPath();
            String format = String.format("%s/Sports-Camera/%s", normalSDCardPath, str);
            File file = new File(format);
            if (!file.exists()) {
                file.mkdirs();
            }
            sLocalPath = format;
            String format2 = String.format("%s/Sports-Camera/%s", normalSDCardPath, s_SD_);
            File file2 = new File(format2);
            if (!file2.exists()) {
                file2.mkdirs();
            }
            sSDPath = format2;
        } catch (Exception unused) {
        }
    }

    public static String getFileNameFromDate(boolean z, boolean z2) {
        String format = new SimpleDateFormat("yyyy-MM-dd-HHmmssSSS", Locale.getDefault()).format(new Date());
        String str = sSDPath;
        if (z2) {
            str = sLocalPath;
        }
        String str2 = !z ? "jpg" : "mp4";
        File file = new File(str);
        if (!file.exists()) {
            file.mkdirs();
        }
        return str + "/" + format + "." + str2;
    }

    public static boolean isAndroidQ() {
        return Build.VERSION.SDK_INT >= 29;
    }

    public static void ClearTempDir() {
        File externalFilesDir = singleton.getExternalFilesDir("temp");
        if (externalFilesDir == null || !externalFilesDir.exists()) {
            return;
        }
        externalFilesDir.delete();
    }

    public static String F_GetMediaFile(Uri uri, boolean z) {
        Uri uri2;
        List<String> pathSegments = uri.getPathSegments();
        if (pathSegments == null) {
            return null;
        }
        String str = pathSegments.size() >= 4 ? pathSegments.get(3) : "1";
        ContentResolver contentResolver = singleton.getApplicationContext().getContentResolver();
        if (z) {
            uri2 = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        } else {
            uri2 = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        }
        Cursor query = contentResolver.query(uri2, new String[]{"_data"}, "_id=?", new String[]{str}, null);
        if (query != null) {
            query.moveToNext();
            String string = query.getString(0);
            String substring = string.substring(string.lastIndexOf("."));
            String str2 = singleton.getExternalFilesDir("temp").getPath() + "/dispfile" + substring;
            try {
                InputStream openInputStream = contentResolver.openInputStream(uri);
                File file = new File(str2);
                if (file.exists()) {
                    file.delete();
                }
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                byte[] bArr = new byte[512000];
                while (true) {
                    int read = openInputStream.read(bArr);
                    if (read == -1) {
                        break;
                    }
                    fileOutputStream.write(bArr, 0, read);
                }
                openInputStream.close();
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return str2;
        }
        return null;
    }

    public static List<Uri> F_GetAllLocalFiles(boolean z) {
        Uri uri;
        Cursor query;
        String str = s_Local_;
        String str2 = sLocalPath;
        if (bBROW_SD) {
            str = s_SD_;
            str2 = sSDPath;
        }
        ArrayList arrayList = new ArrayList();
        ContentResolver contentResolver = singleton.getApplicationContext().getContentResolver();
        if (!str.endsWith("/")) {
            str = str + "/";
        }
        if (z) {
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            if (isAndroidQ()) {
                query = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{"_id"}, "relative_path=?", new String[]{Environment.DIRECTORY_DCIM + File.separator + str}, null);
            } else {
                query = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{"_id"}, "_data like ?", new String[]{str2 + "/%"}, null);
            }
        } else {
            uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            if (isAndroidQ()) {
                query = contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{"_id"}, "relative_path=?", new String[]{Environment.DIRECTORY_DCIM + File.separator + str}, null);
            } else {
                query = contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{"_id"}, "_data like ?", new String[]{str2 + "/%"}, null);
            }
        }
        if (query != null) {
            while (query.moveToNext()) {
                arrayList.add(ContentUris.withAppendedId(uri, query.getLong(0)));
            }
        }
        return arrayList;
    }

    public static Uri F_CheckIsExit(String str) {
        boolean z = true;
        String substring = str.substring(str.lastIndexOf(".") + 1);
        z = (substring.equalsIgnoreCase("mp4") || substring.equalsIgnoreCase("avi") || substring.equalsIgnoreCase("mov")) ? false : false;
        String str2 = s_Local_;
        if (bBROW_SD) {
            str2 = s_SD_;
        }
        return F_CheckIsExit(Environment.DIRECTORY_DCIM + File.separator + str2, str, z);
    }

    private static Uri F_CheckIsExit(String str, String str2, boolean z) {
        Cursor query;
        String str3 = s_Local_;
        if (bBROW_SD) {
            str3 = s_SD_;
        }
        Context applicationContext = singleton.getApplicationContext();
        if (applicationContext == null) {
            return null;
        }
        ContentResolver contentResolver = applicationContext.getContentResolver();
        if (!str.endsWith("/")) {
            str = str + "/";
        }
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        if (!z) {
            uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        }
        Uri uri2 = uri;
        if (z) {
            if (isAndroidQ()) {
                query = contentResolver.query(uri2, new String[]{"_id"}, "relative_path=? and _display_name=?", new String[]{str, str2}, null);
            } else {
                query = contentResolver.query(uri2, new String[]{"_id"}, "_data like ?", new String[]{"%" + str3 + "/" + str2 + "%"}, null);
            }
        } else if (isAndroidQ()) {
            query = contentResolver.query(uri2, new String[]{"_id"}, "relative_path=? and _display_name=?", new String[]{str, str2}, null);
        } else {
            query = contentResolver.query(uri2, new String[]{"_id"}, "_data like ?", new String[]{"%" + str3 + "/" + str2 + "%"}, null);
        }
        if (query != null) {
            if (query.moveToNext()) {
                Uri withAppendedId = ContentUris.withAppendedId(uri2, query.getLong(0));
                query.close();
                return withAppendedId;
            }
            query.close();
        }
        return null;
    }

    public static void F_Save2ToGallery(String str, boolean z) {
        F_Save2ToGallery_A(str, z, false);
    }

    public static boolean F_GetFile4Gallery(Uri uri, String str) {
        MyApp myApp = singleton;
        if (myApp == null) {
            return false;
        }
        ContentResolver contentResolver = myApp.getContentResolver();
        if (uri == null) {
            return false;
        }
        try {
            InputStream openInputStream = contentResolver.openInputStream(uri);
            if (openInputStream == null) {
                return false;
            }
            File file = new File(str);
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(new File(str));
            byte[] bArr = new byte[512000];
            while (true) {
                int read = openInputStream.read(bArr);
                if (read != -1) {
                    fileOutputStream.write(bArr, 0, read);
                } else {
                    openInputStream.close();
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void F_Save2ToGallery_A(String str, boolean z, boolean z2) {
        String str2;
        String str3;
        Uri insert;
        if (singleton == null) {
            return;
        }
        String substring = str.substring(str.lastIndexOf("/") + 1);
        String substring2 = str.substring(str.lastIndexOf(".") + 1);
        if (z2) {
            str2 = Environment.DIRECTORY_DCIM + File.separator + s_SD_;
        } else {
            str2 = Environment.DIRECTORY_DCIM + File.separator + s_Local_;
        }
        if (isAndroidQ()) {
            if (!new File(str).exists() || (str3 = s_Local_) == null || str3.length() == 0) {
                return;
            }
            ContentResolver contentResolver = singleton.getContentResolver();
            ContentValues contentValues = new ContentValues();
            if (F_CheckIsExit(str2, substring, z) != null) {
                return;
            }
            if (z) {
                contentValues.put("_display_name", substring);
                if (substring2.equalsIgnoreCase("png")) {
                    contentValues.put("mime_type", "image/png");
                } else {
                    contentValues.put("mime_type", "image/jpeg");
                }
                contentValues.put("relative_path", str2);
                insert = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            } else {
                contentValues.put("_display_name", substring);
                if (substring2.equalsIgnoreCase("mov")) {
                    contentValues.put("mime_type", "video/mov");
                } else if (substring2.equalsIgnoreCase("avi")) {
                    contentValues.put("mime_type", "video/avi");
                } else {
                    contentValues.put("mime_type", "video/mp4");
                }
                contentValues.put("relative_path", str2);
                insert = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);
            }
            if (insert != null) {
                try {
                    OutputStream openOutputStream = contentResolver.openOutputStream(insert);
                    FileInputStream fileInputStream = new FileInputStream(new File(str));
                    byte[] bArr = new byte[512000];
                    while (true) {
                        int read = fileInputStream.read(bArr);
                        if (read == -1) {
                            break;
                        }
                        openOutputStream.write(bArr, 0, read);
                    }
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                File file = new File(str);
                if (file.isFile() && file.exists()) {
                    file.delete();
                    return;
                }
                return;
            }
            return;
        }
        try {
            singleton.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.parse("file://" + str)));
        } catch (NullPointerException e2) {
            e2.printStackTrace();
        }
    }

    public static void DeleteImage(String str) {
        try {
            singleton.getApplicationContext().getContentResolver().delete(Uri.parse(str), null, null);
        } catch (Exception unused) {
        }
    }

    public static void forceSendRequestByWifiData(final boolean z, final RequestWifi_Interface requestWifi_Interface) {
        if (Build.VERSION.SDK_INT >= 21) {
            NetworkRequest.Builder builder = new NetworkRequest.Builder();
            builder.addTransportType(1);
            NetworkRequest build = builder.build();
            ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onUnavailable() {
                    super.onUnavailable();
                    RequestWifi_Interface requestWifi_Interface2 = requestWifi_Interface;
                    if (requestWifi_Interface2 != null) {
                        requestWifi_Interface2.onResult();
                    }
                }

                @Override
                public void onAvailable(Network network) {
                    super.onAvailable(network);
                    if (z) {
                        Log.i("test", "已根据功能和传输类型找到合适的网络");
                        if (Build.VERSION.SDK_INT >= 23) {
                            MyApp.connectivityManager.bindProcessToNetwork(network);
                        } else {
                            ConnectivityManager.setProcessDefaultNetwork(network);
                        }
                    } else if (Build.VERSION.SDK_INT >= 23) {
                        MyApp.connectivityManager.bindProcessToNetwork(null);
                    } else {
                        ConnectivityManager.setProcessDefaultNetwork(null);
                    }
                    MyApp.connectivityManager.unregisterNetworkCallback(this);
                    RequestWifi_Interface requestWifi_Interface2 = requestWifi_Interface;
                    if (requestWifi_Interface2 != null) {
                        requestWifi_Interface2.onResult();
                    }
                }
            };
            if (Build.VERSION.SDK_INT >= 26) {
                connectivityManager.requestNetwork(build, networkCallback, 500);
            } else {
                connectivityManager.requestNetwork(build, networkCallback);
            }
        }
    }

    public static void forceSendRequestByWifiData() {
        if (Build.VERSION.SDK_INT >= 21) {
            final ConnectivityManager connectivityManager2 = (ConnectivityManager) singleton.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkRequest.Builder builder = new NetworkRequest.Builder();
            builder.addTransportType(1);
            NetworkRequest build = builder.build();
            ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onUnavailable() {
                    super.onUnavailable();
                }

                @Override
                public void onAvailable(Network network) {
                    super.onAvailable(network);
                    Log.i("test", "已根据功能和传输类型找到合适的网络");
                    if (Build.VERSION.SDK_INT >= 23) {
                        connectivityManager2.bindProcessToNetwork(network);
                    } else {
                        ConnectivityManager.setProcessDefaultNetwork(network);
                    }
                    connectivityManager2.unregisterNetworkCallback(this);
                }
            };
            if (Build.VERSION.SDK_INT >= 26) {
                connectivityManager2.requestNetwork(build, networkCallback, 500);
            } else {
                connectivityManager2.requestNetwork(build, networkCallback);
            }
        }
    }

    public static void F_makeFullScreen(Context context) {
        Activity activity = (Activity) context;
        Window window = activity.getWindow();
        window.setFlags(67108864, 67108864);
        window.setFlags(134217728, 134217728);
        window.setFlags(128, 128);
        Resources resources = context.getResources();
        int identifier = resources.getIdentifier("config_showNavigationBar", "bool", "android");
        boolean z = false;
        boolean z2 = identifier > 0 ? resources.getBoolean(identifier) : false;
        try {
            Class<?> cls = Class.forName("android.os.SystemProperties");
            String str = (String) cls.getMethod("get", String.class).invoke(cls, "qemu.hw.mainkeys");
            if (!"1".equals(str)) {
                z = "0".equals(str) ? true : z2;
            }
            z2 = z;
        } catch (Exception unused) {
        }
        if (z2) {
            activity.getWindow().getDecorView().setSystemUiVisibility(5894);
        }
    }

    private static String intToIp(int i) {
        return (i & 255) + "." + ((i >> 8) & 255) + "." + ((i >> 16) & 255) + "." + ((i >> 24) & 255);
    }

    public static boolean CheckConnectedDevice() {
        String intToIp = intToIp(((WifiManager) singleton.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getIpAddress());
        String substring = intToIp.substring(0, intToIp.lastIndexOf("."));
        if (substring.equalsIgnoreCase("192.168.33") || substring.equalsIgnoreCase("192.168.34")) {
            if (substring.equalsIgnoreCase("192.168.33")) {
                nModel = 3;
            } else {
                nModel = 2;
            }
            return true;
        } else if (substring.equalsIgnoreCase("192.168.32")) {
            nModel = 1;
            return true;
        } else {
            return false;
        }
    }

    public static void F_Init4225() {
        int i = nModel;
        if (i == 2 || i == 3) {
            wifination.na4225_SetMode((byte) 0);
            wifination.naStartReadUdp(IjkMediaPlayer.FFP_PROP_INT64_SELECTED_VIDEO_STREAM);
            wifination.naStartRead20000_20001();
        }
    }

    public static void F_OpenCamera(boolean z) {
        if (z) {
            if (bIsOpen) {
                return;
            }
            bIsOpen = true;
            forceSendRequestByWifiData(false, null);
            SystemClock.sleep(150L);
            forceSendRequestByWifiData(true, null);
            SystemClock.sleep(220L);
            wifination.naSetRevBmp(true);
            wifination.naSet3DDenoiser(false);
            wifination.naInit("");
            F_Init4225();
            int i = nModel;
            if (i == 2 || i == 3) {
                wifination.na4225_SyncTime(F_GetDataTime(), 7);
                wifination.na4225_ReadDeviceInfo();
                wifination.na4225_ReadDeviceInfo();
                wifination.na4225_ReadDeviceInfo();
            }
            Log.e("MyApp", "Open Camera");
        } else if (bIsOpen) {
            wifination.naStop();
            bIsOpen = false;
            bIsConnect = false;
            Log.e("MyApp", "Close Camera");
        }
    }

    public static byte[] F_GetDataTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(14, -(calendar.get(15) + calendar.get(16)));
        return new byte[]{(byte) (calendar.get(1) - 2000), (byte) (calendar.get(2) + 1), (byte) calendar.get(5), (byte) calendar.get(11), (byte) calendar.get(12), (byte) calendar.get(13), (byte) (TimeZone.getDefault().getOffset(System.currentTimeMillis()) / 3600000)};
    }

    public static boolean isZH() {
        return singleton.getResources().getConfiguration().locale.getLanguage().toUpperCase().contains("ZH");
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        EventBus.getDefault().register(activity);
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        EventBus.getDefault().unregister(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        this.activityAount++;
        bGotsystemActivity = false;
    }

    @Override
    public void onActivityStopped(Activity activity) {
        int i = this.activityAount - 1;
        this.activityAount = i;
        if (i != 0 || bGotsystemActivity) {
            return;
        }
        EventBus.getDefault().post("", "Go2Background");
    }
}
