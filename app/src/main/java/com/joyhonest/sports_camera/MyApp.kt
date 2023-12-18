package com.joyhonest.sports_camera

import android.app.Activity
import android.app.Application
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.media.AudioAttributes
import android.media.SoundPool
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.provider.MediaStore
import android.util.Log
import com.joyhonest.wifination.wifination
import org.simple.eventbus.EventBus
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class MyApp : Application(), Application.ActivityLifecycleCallbacks {
    private var activityAount = 0
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {}
    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(this)
        connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        instance = this
        if (Build.VERSION.SDK_INT >= 21) {
            val builder = SoundPool.Builder()
            builder.setMaxStreams(2)
            val builder2 = AudioAttributes.Builder()
            builder2.setLegacyStreamType(3)
            builder.setAudioAttributes(builder2.build())
            soundPool = builder.build()
        } else {
            soundPool = SoundPool(2, 3, 0)
        }
        music_btn = soundPool!!.load(this, R.raw.button46, 1)
        music_photo = soundPool!!.load(this, R.raw.photo_m, 1)
    }

    override fun onActivityCreated(activity: Activity, bundle: Bundle) {
        EventBus.getDefault().register(activity)
    }

    override fun onActivityDestroyed(activity: Activity) {
        EventBus.getDefault().unregister(activity)
    }

    override fun onActivityStarted(activity: Activity) {
        activityAount++
        bGotsystemActivity = false
    }

    override fun onActivityStopped(activity: Activity) {
        val i = activityAount - 1
        activityAount = i
        if (i != 0 || bGotsystemActivity) {
            return
        }
        EventBus.getDefault().post("", "Go2Background")
    }

    companion object {
        const val Brow_Photo = 0
        const val Brow_Video = 1
        const val TYPE_GP = 0
        const val TYPE_GP4225 = 2
        const val TYPE_GP4225_33 = 3
        const val TYPE_RTL = 1
        var nModel = 2
        var instance: MyApp? = null
            private set
        var dispList: MutableList<String> = mutableListOf()
        var dispListInx = 0
        var OnPlayNode: MyNode? = null
        var BROW_TYPE = 0
        var bBROW_SD = false
        var bG_Save2SD = false
        var sLocalPath = ""
        var sSDPath = ""
        private var soundPool: SoundPool? = null
        private var music_photo = -1
        private var music_btn = -1
        var bIsConnect = false
        var bIsOpen = false
        var bisRecording = false
        var bPlayLocalVideo = false
        private var connectivityManager: ConnectivityManager? = null
        var s_SD_ = "Sports_Camera_SD_test"
        var s_Local_ = "Sports_Camera_test"
        var bGotsystemActivity = false
        fun PlayBtnVoice() {
            val i = music_btn
            if (i != -1) {
                soundPool!!.play(i, 1.0f, 1.0f, 0, 0, 1.0f)
            }
        }

        fun PlayPhotoMusic() {
            val i = music_photo
            if (i != -1) {
                soundPool!!.play(i, 1.0f, 1.0f, 0, 0, 1.0f)
            }
        }

        fun F_CreateLocalDir(str: String?) {
            if (isAndroidQ) {
                ClearTempDir()
                val externalFilesDir = instance!!.getExternalFilesDir(s_Local_)
                if (externalFilesDir != null) {
                    sLocalPath = externalFilesDir.absolutePath
                }
                val externalFilesDir2 = instance!!.getExternalFilesDir(s_SD_)
                if (externalFilesDir2 != null) {
                    sSDPath = externalFilesDir2.absolutePath
                    return
                }
                return
            }
            F_CreateLocalDir()
        }

        fun F_CreateLocalDir() {
            val str = s_Local_
            try {
                val normalSDCardPath = Storage.normalSDCardPath
                val format = String.format("%s/Sports-Camera/%s", normalSDCardPath, str)
                val file = File(format)
                if (!file.exists()) {
                    file.mkdirs()
                }
                sLocalPath = format
                val format2 = String.format("%s/Sports-Camera/%s", normalSDCardPath, s_SD_)
                val file2 = File(format2)
                if (!file2.exists()) {
                    file2.mkdirs()
                }
                sSDPath = format2
            } catch (unused: Exception) {
            }
        }

        fun getFileNameFromDate(z: Boolean, z2: Boolean): String {
            val format = SimpleDateFormat("yyyy-MM-dd-HHmmssSSS", Locale.getDefault()).format(Date())
            var str = sSDPath
            if (z2) {
                str = sLocalPath
            }
            val str2 = if (!z) "jpg" else "mp4"
            val file = File(str)
            if (!file.exists()) {
                file.mkdirs()
            }
            return "$str/$format.$str2"
        }

        val isAndroidQ: Boolean
            get() = Build.VERSION.SDK_INT >= 29

        fun ClearTempDir() {
            val externalFilesDir = instance!!.getExternalFilesDir("temp")
            if (externalFilesDir == null || !externalFilesDir.exists()) {
                return
            }
            externalFilesDir.delete()
        }

        fun F_GetMediaFile(uri: Uri, z: Boolean): String? {
            val uri2: Uri
            val pathSegments = uri.pathSegments ?: return null
            val str = if (pathSegments.size >= 4) pathSegments[3] else "1"
            val contentResolver = instance!!.applicationContext.contentResolver
            uri2 = if (z) {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            } else {
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            }
            val query = contentResolver.query(uri2, arrayOf("_data"), "_id=?", arrayOf(str), null)
            if (query != null) {
                query.moveToNext()
                val string = query.getString(0)
                val substring = string.substring(string.lastIndexOf("."))
                val str2 = instance!!.getExternalFilesDir("temp")!!.path + "/dispfile" + substring
                try {
                    val openInputStream = contentResolver.openInputStream(uri)
                    val file = File(str2)
                    if (file.exists()) {
                        file.delete()
                    }
                    val fileOutputStream = FileOutputStream(file)
                    val bArr = ByteArray(512000)
                    while (true) {
                        val read = openInputStream!!.read(bArr)
                        if (read == -1) {
                            break
                        }
                        fileOutputStream.write(bArr, 0, read)
                    }
                    openInputStream.close()
                    fileOutputStream.flush()
                    fileOutputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                return str2
            }
            return null
        }

        fun F_GetAllLocalFiles(z: Boolean): List<Uri> {
            val uri: Uri
            val query: Cursor
            var str = s_Local_
            var str2 = sLocalPath
            if (bBROW_SD) {
                str = s_SD_
                str2 = sSDPath
            }
            val arrayList: ArrayList<Uri> = ArrayList<Uri>()
            val contentResolver = instance!!.applicationContext.contentResolver
            if (!str.endsWith("/")) {
                str = "$str/"
            }
            if (z) {
                uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                if (isAndroidQ) {
                    query = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, arrayOf("_id"), "relative_path=?", arrayOf(Environment.DIRECTORY_DCIM + File.separator + str), null)!!
                } else {
                    query = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, arrayOf("_id"), "_data like ?", arrayOf("$str2/%"), null)!!
                }
            } else {
                uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                if (isAndroidQ) {
                    query = contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, arrayOf("_id"), "relative_path=?", arrayOf(Environment.DIRECTORY_DCIM + File.separator + str), null)!!
                } else {
                    query = contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, arrayOf("_id"), "_data like ?", arrayOf("$str2/%"), null)!!
                }
            }
            if (query != null) {
                while (query.moveToNext()) {
                    arrayList.add(ContentUris.withAppendedId(uri, query.getLong(0)) as Nothing)
                }
            }
            return arrayList
        }

        fun F_CheckIsExit(str: String): Uri? {
            var z = true
            val substring = str.substring(str.lastIndexOf(".") + 1)
            z = if (substring.equals("mp4", ignoreCase = true) || substring.equals("avi", ignoreCase = true) || substring.equals("mov", ignoreCase = true)) false else false
            var str2 = s_Local_
            if (bBROW_SD) {
                str2 = s_SD_
            }
            return F_CheckIsExit(Environment.DIRECTORY_DCIM + File.separator + str2, str, z)
        }

        private fun F_CheckIsExit(str: String, str2: String, z: Boolean): Uri? {
            var str = str
            val query: Cursor
            var str3 = s_Local_
            if (bBROW_SD) {
                str3 = s_SD_
            }
            val applicationContext = instance!!.applicationContext ?: return null
            val contentResolver = applicationContext.contentResolver
            if (!str.endsWith("/")) {
                str = "$str/"
            }
            var uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            if (!z) {
                uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            }
            val uri2 = uri
            if (z) {
                if (isAndroidQ) {
                    query = contentResolver.query(uri2, arrayOf("_id"), "relative_path=? and _display_name=?", arrayOf(str, str2), null)!!
                } else {
                    query = contentResolver.query(uri2, arrayOf("_id"), "_data like ?", arrayOf("%$str3/$str2%"), null)!!
                }
            } else if (isAndroidQ) {
                query = contentResolver.query(uri2, arrayOf("_id"), "relative_path=? and _display_name=?", arrayOf(str, str2), null)!!
            } else {
                query = contentResolver.query(uri2, arrayOf("_id"), "_data like ?", arrayOf("%$str3/$str2%"), null)!!
            }
            if (query != null) {
                if (query.moveToNext()) {
                    val withAppendedId = ContentUris.withAppendedId(uri2, query.getLong(0))
                    query.close()
                    return withAppendedId
                }
                query.close()
            }
            return null
        }

        fun F_Save2ToGallery(str: String, z: Boolean) {
            F_Save2ToGallery_A(str, z, false)
        }

        fun F_GetFile4Gallery(uri: Uri?, str: String?): Boolean {
            val myApp = instance ?: return false
            val contentResolver = myApp.contentResolver
            if (uri == null) {
                return false
            }
            try {
                val openInputStream = contentResolver.openInputStream(uri) ?: return false
                val file = File(str)
                if (file.exists()) {
                    file.delete()
                }
                val fileOutputStream = FileOutputStream(File(str))
                val bArr = ByteArray(512000)
                while (true) {
                    val read = openInputStream.read(bArr)
                    if (read != -1) {
                        fileOutputStream.write(bArr, 0, read)
                    } else {
                        openInputStream.close()
                        fileOutputStream.flush()
                        fileOutputStream.close()
                        return true
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                return false
            }
        }

        fun F_Save2ToGallery_A(str: String, z: Boolean, z2: Boolean) {
            var str2: String = ""
            var str3: String = ""
            val insert: Uri
            if (instance == null) {
                return
            }
            val substring = str.substring(str.lastIndexOf("/") + 1)
            val substring2 = str.substring(str.lastIndexOf(".") + 1)
            str2 = if (z2) {
                Environment.DIRECTORY_DCIM + File.separator + s_SD_
            } else {
                Environment.DIRECTORY_DCIM + File.separator + s_Local_
            }
            if (isAndroidQ) {
                if (!File(str).exists() || s_Local_.also { str3 = it } == null || str3?.isEmpty() == true) {
                    return
                }
                val contentResolver = instance!!.contentResolver
                val contentValues = ContentValues()
                if (F_CheckIsExit(str2, substring, z) != null) {
                    return
                }
                if (z) {
                    contentValues.put("_display_name", substring)
                    if (substring2.equals("png", ignoreCase = true)) {
                        contentValues.put("mime_type", "image/png")
                    } else {
                        contentValues.put("mime_type", "image/jpeg")
                    }
                    contentValues.put("relative_path", str2)
                    insert = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)!!
                } else {
                    contentValues.put("_display_name", substring)
                    if (substring2.equals("mov", ignoreCase = true)) {
                        contentValues.put("mime_type", "video/mov")
                    } else if (substring2.equals("avi", ignoreCase = true)) {
                        contentValues.put("mime_type", "video/avi")
                    } else {
                        contentValues.put("mime_type", "video/mp4")
                    }
                    contentValues.put("relative_path", str2)
                    insert = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)!!
                }
                if (insert != null) {
                    try {
                        val openOutputStream = contentResolver.openOutputStream(insert)
                        val fileInputStream = FileInputStream(File(str))
                        val bArr = ByteArray(512000)
                        while (true) {
                            val read = fileInputStream.read(bArr)
                            if (read == -1) {
                                break
                            }
                            openOutputStream!!.write(bArr, 0, read)
                        }
                        fileInputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    val file = File(str)
                    if (file.isFile && file.exists()) {
                        file.delete()
                        return
                    }
                    return
                }
                return
            }
            try {
                instance!!.sendBroadcast(Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.parse("file://$str")))
            } catch (e2: NullPointerException) {
                e2.printStackTrace()
            }
        }

        fun DeleteImage(str: String?) {
            try {
                instance!!.applicationContext.contentResolver.delete(Uri.parse(str), null, null)
            } catch (unused: Exception) {
            }
        }

        fun forceSendRequestByWifiData(z: Boolean, requestWifi_Interface: RequestWifi_Interface?) {
            if (Build.VERSION.SDK_INT >= 21) {
                val builder = NetworkRequest.Builder()
                builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                val build = builder.build()
                val networkCallback: ConnectivityManager.NetworkCallback = object : ConnectivityManager.NetworkCallback() {
                    override fun onUnavailable() {
                        super.onUnavailable()
                        requestWifi_Interface?.onResult()
                    }

                    override fun onAvailable(network: Network) {
                        super.onAvailable(network)
                        if (z) {
                            Log.i("test", "已根据功能和传输类型找到合适的网络")
                            if (Build.VERSION.SDK_INT >= 23) {
                                connectivityManager!!.bindProcessToNetwork(network)
                            } else {
                                ConnectivityManager.setProcessDefaultNetwork(network)
                            }
                        } else if (Build.VERSION.SDK_INT >= 23) {
                            connectivityManager!!.bindProcessToNetwork(null)
                        } else {
                            ConnectivityManager.setProcessDefaultNetwork(null)
                        }
                        connectivityManager!!.unregisterNetworkCallback(this)
                        requestWifi_Interface?.onResult()
                    }
                }
                if (Build.VERSION.SDK_INT >= 26) {
                    connectivityManager!!.requestNetwork(build, networkCallback, 500)
                } else {
                    connectivityManager!!.requestNetwork(build, networkCallback)
                }
            }
        }

        fun forceSendRequestByWifiData() {
            if (Build.VERSION.SDK_INT >= 21) {
                val connectivityManager2 = instance!!.applicationContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
                val builder = NetworkRequest.Builder()
                builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                val build = builder.build()
                val networkCallback: ConnectivityManager.NetworkCallback = object : ConnectivityManager.NetworkCallback() {
                    override fun onUnavailable() {
                        super.onUnavailable()
                    }

                    override fun onAvailable(network: Network) {
                        super.onAvailable(network)
                        Log.i("test", "已根据功能和传输类型找到合适的网络")
                        if (Build.VERSION.SDK_INT >= 23) {
                            connectivityManager2.bindProcessToNetwork(network)
                        } else {
                            ConnectivityManager.setProcessDefaultNetwork(network)
                        }
                        connectivityManager2.unregisterNetworkCallback(this)
                    }
                }
                if (Build.VERSION.SDK_INT >= 26) {
                    connectivityManager2.requestNetwork(build, networkCallback, 500)
                } else {
                    connectivityManager2.requestNetwork(build, networkCallback)
                }
            }
        }

        fun F_makeFullScreen(context: Context) {
            val activity = context as Activity
            val window = activity.window
            window.setFlags(67108864, 67108864)
            window.setFlags(134217728, 134217728)
            window.setFlags(128, 128)
            val resources = context.getResources()
            val identifier = resources.getIdentifier("config_showNavigationBar", "bool", "android")
            var z = false
            var z2 = if (identifier > 0) resources.getBoolean(identifier) else false
            try {
                val cls = Class.forName("android.os.SystemProperties")
                val str = cls.getMethod("get", String::class.java).invoke(cls, "qemu.hw.mainkeys") as String
                if ("1" != str) {
                    z = if ("0" == str) true else z2
                }
                z2 = z
            } catch (unused: Exception) {
            }
            if (z2) {
                activity.window.decorView.systemUiVisibility = 5894
            }
        }

        private fun intToIp(i: Int): String {
            return (i and 255).toString() + "." + (i shr 8 and 255) + "." + (i shr 16 and 255) + "." + (i shr 24 and 255)
        }

        fun CheckConnectedDevice(): Boolean {
            val intToIp = intToIp((instance!!.getSystemService(WIFI_SERVICE) as WifiManager).connectionInfo.ipAddress)
            val substring = intToIp.substring(0, intToIp.lastIndexOf("."))
            return if (substring.equals("192.168.33", ignoreCase = true) || substring.equals("192.168.34", ignoreCase = true)) {
                if (substring.equals("192.168.33", ignoreCase = true)) {
                    nModel = 3
                } else {
                    nModel = 2
                }
                true
            } else if (substring.equals("192.168.32", ignoreCase = true)) {
                nModel = 1
                true
            } else {
                false
            }
        }

        fun F_Init4225() {
            val i = nModel
            if (i == 2 || i == 3) {
                wifination.na4225_SetMode(0.toByte())
                wifination.naStartReadUdp(IjkMediaPlayer.FFP_PROP_INT64_SELECTED_VIDEO_STREAM)
                wifination.naStartRead20000_20001()
            }
        }

        fun F_OpenCamera(z: Boolean) {
            if (z) {
                if (bIsOpen) {
                    return
                }
                bIsOpen = true
                forceSendRequestByWifiData(false, null)
                SystemClock.sleep(150L)
                forceSendRequestByWifiData(true, null)
                SystemClock.sleep(220L)
                wifination.naSetRevBmp(true)
                wifination.naSet3DDenoiser(false)
                wifination.naInit("")
                F_Init4225()
                val i = nModel
                if (i == 2 || i == 3) {
                    wifination.na4225_SyncTime(F_GetDataTime(), 7)
                    wifination.na4225_ReadDeviceInfo()
                    wifination.na4225_ReadDeviceInfo()
                    wifination.na4225_ReadDeviceInfo()
                }
                Log.e("MyApp", "Open Camera")
            } else if (bIsOpen) {
                wifination.naStop()
                bIsOpen = false
                bIsConnect = false
                Log.e("MyApp", "Close Camera")
            }
        }

        fun F_GetDataTime(): ByteArray {
            val calendar = Calendar.getInstance()
            calendar.add(14, -(calendar[15] + calendar[16]))
            return byteArrayOf(
                    (calendar[1] - 2000).toByte(),
                    (calendar[2] + 1).toByte(),
                    calendar[5].toByte(),
                    calendar[11].toByte(),
                    calendar[12].toByte(),
                    calendar[13].toByte(),
                    (TimeZone.getDefault().getOffset(System.currentTimeMillis()) / 3600000).toByte()
            )
        }

        val isZH: Boolean
            get() = instance!!.resources.configuration.locale.language.uppercase(Locale.getDefault()).contains("ZH")
    }
}