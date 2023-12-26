package com.joyhonest.sports_camera;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class PermissionPageUtils {
    private Context mContext;
    private final String TAG = "PermissionPageManager";
    private String packageName = "com.example.lexinxingye.jhwifi";

    public PermissionPageUtils(Context context) {
        this.mContext = context;
    }

    public void jumpPermissionPage() {
        String str = Build.MANUFACTURER;
        Log.e("PermissionPageManager", "jumpPermissionPage --- name : " + str);
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -1678088054:
                if (str.equals("Coolpad")) {
                    c = 0;
                    break;
                }
                break;
            case -1675632421:
                if (str.equals("Xiaomi")) {
                    c = 1;
                    break;
                }
                break;
            case 2427:
                if (str.equals("LG")) {
                    c = 2;
                    break;
                }
                break;
            case 2432928:
                if (str.equals("OPPO")) {
                    c = 3;
                    break;
                }
                break;
            case 2582855:
                if (str.equals("Sony")) {
                    c = 4;
                    break;
                }
                break;
            case 3620012:
                if (str.equals("vivo")) {
                    c = 5;
                    break;
                }
                break;
            case 74224812:
                if (str.equals("Meizu")) {
                    c = 6;
                    break;
                }
                break;
            case 1864941562:
                if (str.equals("samsung")) {
                    c = 7;
                    break;
                }
                break;
            case 2141820391:
                if (str.equals("HUAWEI")) {
                    c = '\b';
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                goCoolpadMainager();
                return;
            case 1:
                goXiaoMiMainager();
                return;
            case 2:
                goLGMainager();
                return;
            case 3:
                goOppoMainager();
                return;
            case 4:
                goSonyMainager();
                return;
            case 5:
                goVivoMainager();
                return;
            case 6:
                goMeizuMainager();
                return;
            case 7:
                goSangXinMainager();
                return;
            case '\b':
                goHuaWeiMainager();
                return;
            default:
                goIntentSetting();
                return;
        }
    }

    private void goLGMainager() {
        try {
            Intent intent = new Intent(this.packageName);
            intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$AccessLockSummaryActivity"));
            this.mContext.startActivity(intent);
        } catch (Exception unused) {
            goIntentSetting();
        }
    }

    private void goSonyMainager() {
        try {
            Intent intent = new Intent(this.packageName);
            intent.setComponent(new ComponentName("com.sonymobile.cta", "com.sonymobile.cta.SomcCTAMainActivity"));
            this.mContext.startActivity(intent);
        } catch (Exception unused) {
            goIntentSetting();
        }
    }

    private void goHuaWeiMainager() {
        try {
            Intent intent = new Intent(this.packageName);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity"));
            this.mContext.startActivity(intent);
        } catch (Exception unused) {
            goIntentSetting();
        }
    }

    private static String getMiuiVersion() {
        BufferedReader bufferedReader;
        BufferedReader bufferedReader2;
        BufferedReader bufferedReader3 = null;
        try {
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("getprop ro.miui.ui.version.name").getInputStream()), 1024);
                try {
                    String readLine = bufferedReader.readLine();
                    bufferedReader.close();
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return readLine;
                } catch (IOException e2) {
                    e2.printStackTrace();
                    try {
                        bufferedReader.close();
                    } catch (IOException e3) {
                        e3.printStackTrace();
                    }
                    return null;
                }
            } catch (Throwable th) {
                try {
                    bufferedReader3.close();
                } catch (IOException e4) {
                    e4.printStackTrace();
                }
                throw th;
            }
        } catch (IOException e5) {
            bufferedReader = null;
        } catch (Throwable th2) {
        }
        return null;
    }

    private void goXiaoMiMainager() {
        String miuiVersion = getMiuiVersion();
        Log.e("PermissionPageManager", "goMiaoMiMainager --- rom : " + miuiVersion);
        Intent intent = new Intent();
        if ("V6".equals(miuiVersion) || "V7".equals(miuiVersion)) {
            intent.setAction("miui.intent.action.APP_PERM_EDITOR");
            intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
            intent.putExtra("extra_pkgname", this.packageName);
        } else if ("V8".equals(miuiVersion) || "V9".equals(miuiVersion)) {
            intent.setAction("miui.intent.action.APP_PERM_EDITOR");
            intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
            intent.putExtra("extra_pkgname", this.packageName);
        } else {
            goIntentSetting();
        }
        this.mContext.startActivity(intent);
    }

    private void goMeizuMainager() {
        try {
            Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.putExtra("packageName", this.packageName);
            this.mContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            goIntentSetting();
        }
    }

    private void goSangXinMainager() {
        goIntentSetting();
    }

    private void goIntentSetting() {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", this.mContext.getPackageName(), null));
        try {
            this.mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void goOppoMainager() {
        doStartApplicationWithPackageName("com.coloros.safecenter");
    }

    private void goCoolpadMainager() {
        doStartApplicationWithPackageName("com.yulong.android.security:remote");
    }

    private void goVivoMainager() {
        doStartApplicationWithPackageName("com.bairenkeji.icaller");
    }

    private Intent getAppDetailSettingIntent() {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", this.mContext.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            intent.setAction("android.intent.action.VIEW");
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            intent.putExtra("com.android.settings.ApplicationPkgName", this.mContext.getPackageName());
        }
        return intent;
    }

    private void doStartApplicationWithPackageName(String str) {
        PackageInfo packageInfo;
        try {
            packageInfo = this.mContext.getPackageManager().getPackageInfo(str, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            packageInfo = null;
        }
        if (packageInfo == null) {
            return;
        }
        Intent intent = new Intent("android.intent.action.MAIN", (Uri) null);
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.setPackage(packageInfo.packageName);
        List<ResolveInfo> queryIntentActivities = this.mContext.getPackageManager().queryIntentActivities(intent, 0);
        Log.e("PermissionPageManager", "resolveinfoList" + queryIntentActivities.size());
        for (int i = 0; i < queryIntentActivities.size(); i++) {
            Log.e("PermissionPageManager", queryIntentActivities.get(i).activityInfo.packageName + queryIntentActivities.get(i).activityInfo.name);
        }
        ResolveInfo next = queryIntentActivities.iterator().next();
        if (next != null) {
            String str2 = next.activityInfo.packageName;
            String str3 = next.activityInfo.name;
            Intent intent2 = new Intent("android.intent.action.MAIN");
            intent2.addCategory("android.intent.category.LAUNCHER");
            intent2.setComponent(new ComponentName(str2, str3));
            try {
                this.mContext.startActivity(intent2);
            } catch (Exception e2) {
                goIntentSetting();
                e2.printStackTrace();
            }
        }
    }
}
