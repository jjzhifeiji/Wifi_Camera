package com.joyhonest.sports_camera

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


class PermissionPageUtils(private val mContext: Context) {


    private val TAG = "PermissionPageManager"
    private val packageName = "com.example.lexinxingye.jhwifi"
    fun jumpPermissionPage() {
        val str = Build.MANUFACTURER
        Log.e("PermissionPageManager", "jumpPermissionPage --- name : $str")
        when (str) {
            "Coolpad" -> {
                goCoolpadMainager()
            }

            "Xiaomi" -> {
                goXiaoMiMainager()
            }

            "LG" -> {
                goLGMainager()
            }

            "OPPO" -> {
                goOppoMainager()
            }

            "Sony" -> {
                goSonyMainager()
            }

            "vivo" -> {
                goVivoMainager()
            }

            "Meizu" -> {
                goMeizuMainager()

            }

            "samsung" -> {
                goSangXinMainager()
            }

            "HUAWEI" -> {
                goHuaWeiMainager()
            }

            else -> {
                goIntentSetting()
                return
            }
        }


    }

    private fun goLGMainager() {
        try {
            val intent = Intent(packageName)
            intent.component = ComponentName("com.android.settings", "com.android.settings.Settings.AccessLockSummaryActivity")
            mContext.startActivity(intent)
        } catch (unused: Exception) {
            goIntentSetting()
        }
    }

    private fun goSonyMainager() {
        try {
            val intent = Intent(packageName)
            intent.component = ComponentName("com.sonymobile.cta", "com.sonymobile.cta.SomcCTAMainActivity")
            mContext.startActivity(intent)
        } catch (unused: Exception) {
            goIntentSetting()
        }
    }

    private fun goHuaWeiMainager() {
        try {
            val intent = Intent(packageName)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.component = ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity")
            mContext.startActivity(intent)
        } catch (unused: Exception) {
            goIntentSetting()
        }
    }

    private fun goXiaoMiMainager() {
        val miuiVersion: String? = miuiVersion
        Log.e("PermissionPageManager", "goMiaoMiMainager --- rom : $miuiVersion")
        val intent = Intent()
        if ("V6" == miuiVersion || "V7" == miuiVersion) {
            intent.action = "miui.intent.action.APP_PERM_EDITOR"
            intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity")
            intent.putExtra("extra_pkgname", packageName)
        } else if ("V8" == miuiVersion || "V9" == miuiVersion) {
            intent.action = "miui.intent.action.APP_PERM_EDITOR"
            intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity")
            intent.putExtra("extra_pkgname", packageName)
        } else {
            goIntentSetting()
        }
        mContext.startActivity(intent)
    }

    private fun goMeizuMainager() {
        try {
            val intent = Intent("com.meizu.safe.security.SHOW_APPSEC")
            intent.addCategory("android.intent.category.DEFAULT")
            intent.putExtra("packageName", packageName)
            mContext.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            goIntentSetting()
        }
    }

    private fun goSangXinMainager() {
        goIntentSetting()
    }

    private fun goIntentSetting() {
        val intent = Intent("android.settings.APPLICATION_DETAILS_SETTINGS")
        intent.data = Uri.fromParts("package", mContext.packageName, null)
        try {
            mContext.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun goOppoMainager() {
        doStartApplicationWithPackageName("com.coloros.safecenter")
    }

    private fun goCoolpadMainager() {
        doStartApplicationWithPackageName("com.yulong.android.security:remote")
    }

    private fun goVivoMainager() {
        doStartApplicationWithPackageName("com.bairenkeji.icaller")
    }

    private val appDetailSettingIntent: Intent
        private get() {
            val intent = Intent()
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (Build.VERSION.SDK_INT >= 9) {
                intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                intent.data = Uri.fromParts("package", mContext.packageName, null)
            } else if (Build.VERSION.SDK_INT <= 8) {
                intent.action = "android.intent.action.VIEW"
                intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails")
                intent.putExtra("com.android.settings.ApplicationPkgName", mContext.packageName)
            }
            return intent
        }

    private fun doStartApplicationWithPackageName(str: String) {
        val packageInfo: PackageInfo?
        packageInfo = try {
            mContext.packageManager.getPackageInfo(str, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }
        if (packageInfo == null) {
            return
        }
        val intent = Intent("android.intent.action.MAIN", null as Uri?)
        intent.addCategory("android.intent.category.LAUNCHER")
        intent.setPackage(packageInfo.packageName)
        val queryIntentActivities = mContext.packageManager.queryIntentActivities(intent, 0)
        Log.e("PermissionPageManager", "resolveinfoList" + queryIntentActivities.size)
        for (i in queryIntentActivities.indices) {
            Log.e("PermissionPageManager", queryIntentActivities[i].activityInfo.packageName + queryIntentActivities[i].activityInfo.name)
        }
        val next = queryIntentActivities.iterator().next()
        if (next != null) {
            val str2 = next.activityInfo.packageName
            val str3 = next.activityInfo.name
            val intent2 = Intent("android.intent.action.MAIN")
            intent2.addCategory("android.intent.category.LAUNCHER")
            intent2.component = ComponentName(str2, str3)
            try {
                mContext.startActivity(intent2)
            } catch (e2: Exception) {
                goIntentSetting()
                e2.printStackTrace()
            }
        }
    }

    companion object {
        private val miuiVersion: String?
            private get() {
                var bufferedReader: BufferedReader?
                var bufferedReader2: BufferedReader
                val bufferedReader3: BufferedReader? = null
                try {
                    return try {
                        bufferedReader = BufferedReader(InputStreamReader(Runtime.getRuntime().exec("getprop ro.miui.ui.version.name").inputStream), 1024)
                        try {
                            val readLine = bufferedReader.readLine()
                            bufferedReader.close()
                            try {
                                bufferedReader.close()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                            readLine
                        } catch (e2: IOException) {
                            e2.printStackTrace()
                            try {
                                bufferedReader.close()
                            } catch (e3: IOException) {
                                e3.printStackTrace()
                            }
                            null
                        }
                    } catch (th: Throwable) {
                        try {
                            bufferedReader3!!.close()
                        } catch (e4: IOException) {
                            e4.printStackTrace()
                        }
                        throw th
                    }
                } catch (e5: IOException) {
                    bufferedReader = null
                } catch (th2: Throwable) {
                }
                return null
            }
    }
}