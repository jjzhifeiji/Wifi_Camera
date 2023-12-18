package com.joyhonest.sports_camera

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.PointF
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.Button
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.joyhonest.wifination.wifination
import org.simple.eventbus.Subscriber
import java.util.Date

class PlayActivity : AppCompatActivity(), View.OnClickListener, OnTouchListener {
    private var DisplayImageView: ImageView? = null
    var PointX = 0f
    var PointY = 0f
    private var RecTime_Text: TextView? = null
    private var alertDialog: AlertDialog? = null
    private var bCheck = false
    private var btn_fillxy: Button? = null
    private var button1: Button? = null
    private var button2: Button? = null
    private var button3: Button? = null
    private var button4: Button? = null
    private var button5: Button? = null
    private var button6: Button? = null
    private var button7: Button? = null
    private var button8: Button? = null
    private var button_left0: Button? = null
    private var button_right0: Button? = null
    private var left_view: LinearLayout? = null
    private var mAsker: PermissionAsker? = null
    private val mCurrentClickTime: Long = 0
    private val mPrevClickThread: ClickPressedThread? = null
    private var right_view: LinearLayout? = null
    private var wifiReceiver: WifiReceiver? = null
    private var wifi_signal: ImageView? = null
    private val TAG = "PlayActivity"
    var bNeedAction = -1
    var nWidth = -1
    var nHeight = -1
    var bIREn = false
    var bStatusLEDEn = false
    var nIR_Status: Byte = 0
    var bLedStatus = false
    var bFillxy = true

    @Volatile
    var nCMes: Byte = 0
    var nTP1 = 0
    var Handler_Read4225 = Handler()
    var Runnable_Read4225: Runnable = object : Runnable {
        override fun run() {
            val b = nCMes
            Log.e("PlayActivity", "Read4225 Status")
            var b2 = (b + 1).toByte()
            if (b2 > 40) {
                nCMes = 40.toByte()
                b2 = 40
            }
            nCMes = b2
            if (nTP1 and 3 == 0) {
                F_DispRssi()
            }
            nTP1++
            wifination.na4225_ReadStatus()
            Handler_Read4225.postDelayed(this, 250L)
        }
    }
    private val wifiicon = intArrayOf(R.mipmap.jhd_wifi0, R.mipmap.jhd_wifi1, R.mipmap.jhd_wifi2, R.mipmap.jhd_wifi3, R.mipmap.jhd_wifi4)
    private val point2 = PointF()
    private val point1 = PointF()
    private val touchListener = OnTouchListener { view, motionEvent ->
        if (motionEvent.action == 0) {
            point1.x = motionEvent.x
            point1.y = motionEvent.y
        }
        if (motionEvent.action == 1) {
            point2.x = motionEvent.x
            point2.y = motionEvent.y
            val abs = Math.abs(point1.y - point2.y)
            Log.e("PlayActivity", "dy = $abs")
            if (abs < 10.0) {
                MyApp.Companion.PlayBtnVoice()
                Show_hid_toolbar()
            }
        }
        true
    }
    var bt6 = true
    var bt7 = true
    var bTest = false
    var bCanPlay = true
    var Hand_DispRec = Handler()
    var Runnable_DispRec: Runnable = object : Runnable {
        var bhalf = false
        var bhalf1 = false
        override fun run() {
            val naGetRecordTime = wifination.naGetRecordTime() / 1000
            val i = naGetRecordTime / 60
            val i2 = naGetRecordTime % 60
            val i3 = i / 60
            val i4 = i % 60
            RecTime_Text!!.text = if (i3 == 0) String.format("%02d:%02d", Integer.valueOf(i4), Integer.valueOf(i2)) else String.format("%02d:%02d:%02d", Integer.valueOf(i3), Integer.valueOf(i4), Integer.valueOf(i2))
            val z = !bhalf
            bhalf = z
            if (z) {
                val z2 = !bhalf1
                bhalf1 = z2
                if (z2) {
                    button3!!.setBackgroundResource(R.mipmap.video_icon_recording)
                } else {
                    button3!!.setBackgroundResource(R.mipmap.video_icon)
                }
            }
            Hand_DispRec.postDelayed(this, 250L)
        }
    }
    var Hand_DispRec_SD = Handler()
    var Runnable_DispRec_SD: Runnable = object : Runnable {
        var bhalf = false
        var bhalf1 = false
        override fun run() {
            val z = !bhalf
            bhalf = z
            if (z) {
                val z2 = !bhalf1
                bhalf1 = z2
                if (z2) {
                    button7!!.setBackgroundResource(R.mipmap.video_device_recording)
                } else {
                    button7!!.setBackgroundResource(R.mipmap.video_device)
                }
            }
            Hand_DispRec_SD.postDelayed(this, 250L)
        }
    }
    var nCheckConnected = 0
    var CheckHander = Handler()
    var CheckRunnable: Runnable = object : Runnable {
        override fun run() {
            if (nCheckConnected < 20) {
                nCheckConnected++
            } else {
                MyApp.Companion.F_OpenCamera(false)
                SystemClock.sleep(250L)
                MyApp.Companion.F_OpenCamera(true)
                nCheckConnected = 0
            }
            CheckHander.postDelayed(this, 100L)
        }
    }
    var bSdRecording = false
    var bMultFing = false
    var prePointF = PointF(0.0f, 0.0f)
    private var mClickCount = 0
    private val mBaseHandler = Handler()
    var bAdjIng = false
    private fun DispIrIcon() {}
    private fun DispPIrIcon() {}
    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        return true
    }

    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_play)
        MyApp.Companion.F_makeFullScreen(this)
        wifi_signal = findViewById<View>(R.id.wifi_signal) as ImageView
        btn_fillxy = findViewById<View>(R.id.btn_fillxy) as Button
        DisplayImageView = findViewById<View>(R.id.DisplayImageView) as ImageView
        left_view = findViewById<View>(R.id.left_view) as LinearLayout
        right_view = findViewById<View>(R.id.right_view) as LinearLayout
        button1 = findViewById<View>(R.id.button1) as Button
        button2 = findViewById<View>(R.id.button2) as Button
        button3 = findViewById<View>(R.id.button3) as Button
        button4 = findViewById<View>(R.id.button4) as Button
        button5 = findViewById<View>(R.id.button5) as Button
        button6 = findViewById<View>(R.id.button6) as Button
        button7 = findViewById<View>(R.id.button7) as Button
        button8 = findViewById<View>(R.id.button8) as Button
        val textView = findViewById<View>(R.id.RecTime_Text) as TextView
        RecTime_Text = textView
        textView.visibility = View.INVISIBLE
        button6!!.isEnabled = false
        button7!!.isEnabled = false
        button_left0 = findViewById<View>(R.id.button_left0) as Button
        button_right0 = findViewById<View>(R.id.button_right0) as Button
        F_SetFitXY()
        DisplayImageView!!.setOnClickListener(this)
        button1!!.setOnClickListener(this)
        button2!!.setOnClickListener(this)
        button3!!.setOnClickListener(this)
        button4!!.setOnClickListener(this)
        button5!!.setOnClickListener(this)
        button6!!.setOnClickListener(this)
        button7!!.setOnClickListener(this)
        button8!!.setOnClickListener(this)
        left_view!!.setOnClickListener(this)
        btn_fillxy!!.setOnClickListener(this)
        button_left0!!.setOnClickListener(this)
        button_right0!!.setOnClickListener(this)
        val string = resources.getString(R.string.warning)
        val string2 = resources.getString(R.string.The_necessary_permission_denied)
        alertDialog = AlertDialog.Builder(this).setTitle(string).setMessage(string2).setNegativeButton(resources.getString(R.string.OK)) { dialogInterface, i -> PermissionPageUtils(this@PlayActivity).jumpPermissionPage() }.create()
        if (MyApp.isAndroidQ) {
            MyApp.F_CreateLocalDir("")
        }
        mAsker = PermissionAsker(10, {
            MyApp.F_CreateLocalDir("")
            DoAction()
        }) { F_DispDialg() }
        if (MyApp.nModel == 2 || MyApp.Companion.nModel == 3) {
            button6!!.visibility = View.VISIBLE
            button7!!.visibility = View.VISIBLE
        }
        wifiReceiver = WifiReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction("android.net.wifi.STATE_CHANGE")
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED")
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(wifiReceiver, intentFilter)
        F_AdjStatusLed_Button()
        F_AdjIR_Button()
    }

    private fun F_SetFitXY() {
        if (bFillxy) {
            DisplayImageView!!.scaleType = ScaleType.FIT_XY
            btn_fillxy!!.setBackgroundResource(R.mipmap.fitcenter)
            return
        }
        DisplayImageView!!.scaleType = ScaleType.FIT_CENTER
        btn_fillxy!!.setBackgroundResource(R.mipmap.fillxy)
    }

    fun F_DispRssi() {
        var wifiRssi = wifiRssi
        if (wifiRssi > 4) {
            wifiRssi = 4
        }
        if (wifiRssi < 0) {
            wifiRssi = 0
        }
        wifi_signal!!.setBackgroundResource(wifiicon[wifiRssi])
    }

    fun F_DispDialg() {
        alertDialog!!.show()
    }

    private fun F_Checked() {
        if (MyApp.isAndroidQ) {
            DoAction()
        } else {
            mAsker!!.askPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE")
        }
    }

    fun DoAction() {
        if (bNeedAction == 0) {
            TakePhoto()
            bNeedAction = -1
        }
        if (bNeedAction == 1) {
            StartRecordVideo()
            bNeedAction = -1
        }
        if (bNeedAction == 2) {
            bNeedAction = -1
            startActivity(Intent(this, BrowSelectActivity::class.java))
        }
    }

    @SuppressLint("ObjectAnimatorBinding")
    fun Show_hid_toolbar() {
        if (MyApp.bIsConnect) {
            val width = left_view!!.width + 2
            val width2 = right_view!!.width + 2
            if (left_view!!.translationX < 0.0f) {
                ObjectAnimator.ofFloat(left_view, "TranslationX", 0.0f).setDuration(300L).start()
                ObjectAnimator.ofFloat(right_view, "TranslationX", 0.0f).setDuration(300L).start()
                return
            }
            ObjectAnimator.ofFloat(left_view, "TranslationX", -width.toFloat()).setDuration(300L).start()
            ObjectAnimator.ofFloat(right_view, "TranslationX", width2.toFloat()).setDuration(300L).start()
        } else if (left_view!!.translationX < 0.0f) {
            ObjectAnimator.ofFloat(left_view, "TranslationX", 0.0f).setDuration(300L).start()
            ObjectAnimator.ofFloat(right_view, "TranslationX", 0.0f).setDuration(300L).start()
        }
    }

    private fun StartRecordVideo() {
        if (MyApp.bisRecording) {
            wifination.naStopRecord(0)
        } else {
            wifination.naStartRecord(MyApp.getFileNameFromDate(true, true), 0)
        }
    }

    private fun TakePhoto() {
        MyApp.PlayPhotoMusic()
        wifination.naSnapPhoto(MyApp.getFileNameFromDate(false, true), 0)
        button2!!.isClickable = false
        Handler().postDelayed({ button2!!.isClickable = true }, 200L)
    }

    override fun onClick(view: View) {
        if (view.id != R.id.button2 && view.id != R.id.button6 && view.id != R.id.button7) {
            MyApp.PlayBtnVoice()
        }
        when (view.id) {
            R.id.DisplayImageView -> {
                Log.e("PlayActivity", "clicked!")
                Show_hid_toolbar(true)
                return
            }

            R.id.btn_fillxy -> {
                bFillxy = !bFillxy
                F_SetFitXY()
                return
            }

            R.id.button1 -> {
                onBackPressed()
                return
            }

            R.id.button2 -> {
                MyApp.bBROW_SD = false
                if (MyApp.bIsConnect) {
                    bNeedAction = 0
                    F_Checked()
                    return
                }
                MyApp.PlayBtnVoice()
                return
            }

            R.id.button3 -> {
                MyApp.bBROW_SD = false
                if (MyApp.bIsConnect) {
                    bNeedAction = 1
                    F_Checked()
                    return
                }
                return
            }

            R.id.button4 -> {
                MyApp.bBROW_SD = false
                bNeedAction = 2
                F_Checked()
                return
            }

            R.id.button6 -> {
                MyApp.bBROW_SD = true
                if (MyApp.bIsConnect && wifination.gp4225_Device.bSD && !bSdRecording && bt6) {
                    MyApp.PlayPhotoMusic()
                    wifination.naSnapPhoto("", 1)
                    bt6 = false
                    Handler().postDelayed({ bt6 = true }, 500L)
                    return
                }
                return
            }

            R.id.button7 -> {
                MyApp.bBROW_SD = true
                if (MyApp.bIsConnect && wifination.gp4225_Device.bSD && bt7) {
                    bt7 = false
                    MyApp.PlayBtnVoice()
                    if (bSdRecording) {
                        wifination.naStopRecord(1)
                    } else {
                        wifination.naStartRecord("", 1)
                    }
                    Handler().postDelayed({ bt7 = true }, 2000L)
                    return
                }
                return
            }

            R.id.button8 -> {
                if (MyApp.bisRecording) {
                    wifination.naStopRecord(0)
                }
                MyApp.bBROW_SD = true
                bNeedAction = 2
                F_Checked()
                return
            }

            R.id.button_left0 -> {
                val b = nIR_Status
                if (b.toInt() == 0) {
                    wifination.naSetIR(1)
                    return
                } else if (b.toInt() == 1) {
                    wifination.naSetIR(2)
                    return
                } else {
                    wifination.naSetIR(0)
                    return
                }
            }

            R.id.button_right0 -> if (bLedStatus) {
                wifination.naSetStatusLedDisp(false)
                return
            } else {
                wifination.naSetStatusLedDisp(true)
                return
            }

            else -> return
        }
    }

    override fun onRequestPermissionsResult(i: Int, strArr: Array<String>, iArr: IntArray) {
        super.onRequestPermissionsResult(i, strArr, iArr)
        mAsker!!.onRequestPermissionsResult(iArr)
    }

    public override fun onPause() {
        super.onPause()
        bCanPlay = false
    }

    public override fun onResume() {
        bCanPlay = true
        super.onResume()
        if (!MyApp.CheckConnectedDevice()) {
            onBackPressed()
            return
        }
        MyApp.F_makeFullScreen(this)
        if (!MyApp.bIsOpen) {
            nWidth = -1
            nHeight = -1
            MyApp.F_OpenCamera(true)
            nCheckConnected = 0
        }
        Handler_Read4225.removeCallbacksAndMessages(null)
        Handler_Read4225.post(Runnable_Read4225)
    }

    public override fun onStart() {
        super.onStart()
    }

    public override fun onStop() {
        super.onStop()
        Handler_Read4225.removeCallbacksAndMessages(null)
        bCheck = false
    }

    public override fun onDestroy() {
        super.onDestroy()
        Hand_DispRec.removeCallbacksAndMessages(null)
        Handler_Read4225.removeCallbacksAndMessages(null)
        Hand_DispRec_SD.removeCallbacksAndMessages(null)
        MyApp.F_OpenCamera(false)
        unregisterReceiver(wifiReceiver)
        Log.e("PlayActivity", "PlayActivity Destroy")
    }

    private fun F_DispRecordTime(z: Boolean) {
        if (z) {
            RecTime_Text!!.visibility = View.VISIBLE
            Hand_DispRec.removeCallbacksAndMessages(null)
            Hand_DispRec.post(Runnable_DispRec)
            return
        }
        RecTime_Text!!.visibility = View.INVISIBLE
        Hand_DispRec.removeCallbacksAndMessages(null)
        button3!!.setBackgroundResource(R.mipmap.video_icon)
    }

    private fun F_DispRecordTime_SD(z: Boolean) {
        if (z) {
            Hand_DispRec_SD.removeCallbacksAndMessages(null)
            Hand_DispRec_SD.post(Runnable_DispRec_SD)
            return
        }
        Hand_DispRec_SD.removeCallbacksAndMessages(null)
        button7!!.setBackgroundResource(R.mipmap.video_device)
    }

    private fun F_AdjIR_Button() {
        if (!bIREn) {
            button_left0!!.visibility = View.GONE
            val findViewById = findViewById<View>(R.id.space_1)
            if (findViewById != null) {
                findViewById.visibility = View.GONE
                return
            }
            return
        }
        button_left0!!.visibility = View.VISIBLE
        val findViewById2 = findViewById<View>(R.id.space_1)
        if (findViewById2 != null) {
            findViewById2.visibility = View.VISIBLE
        }
    }

    private fun F_AdjStatusLed_Button() {
        if (!bStatusLEDEn) {
            button_right0!!.visibility = View.GONE
            val findViewById = findViewById<View>(R.id.space_2)
            if (findViewById != null) {
                findViewById.visibility = View.GONE
                return
            }
            return
        }
        button_right0!!.visibility = View.VISIBLE
        val findViewById2 = findViewById<View>(R.id.space_2)
        if (findViewById2 != null) {
            findViewById2.visibility = View.VISIBLE
        }
    }

    @Subscriber(tag = "SavePhotoOK")
    private fun SavePhotoOK(str: String) {
        if (str.length < 5) {
            return
        }
        val substring = str.substring(0, 2)
        val substring2 = str.substring(2, str.length)
        if (substring.toInt() == 0) {
            MyApp.F_Save2ToGallery(substring2, true)
        } else {
            MyApp.F_Save2ToGallery(substring2, false)
        }
    }

    @Subscriber(tag = "SDStatus_Changed")
    private fun _OnStatusChanged(i: Int) {
        Log.d("   Status", "" + i)
        if (i and 2 != 0) {
            if (MyApp.bisRecording) {
                return
            }
            MyApp.bisRecording = true
            F_DispRecordTime(true)
        } else if (MyApp.bisRecording) {
            MyApp.bisRecording = false
            F_DispRecordTime(false)
        }
    }

    @Subscriber(tag = "ReceiveBMP")
    private fun ReviceBMP(bitmap: Bitmap) {
        if (bCanPlay) {
            nCMes = 0.toByte()
            MyApp.bIsConnect = true
            nCheckConnected = 0
            if (nWidth < 0) {
                nWidth = bitmap.width
                nHeight = bitmap.height
            }
            DisplayImageView!!.setImageBitmap(bitmap)
        }
    }

    private fun Show_hid_toolbar(z: Boolean) {
        val linearLayout = findViewById<View>(R.id.left_view) as LinearLayout
        val linearLayout2 = findViewById<View>(R.id.right_view) as LinearLayout
        if (linearLayout == null || linearLayout2 == null) {
            return
        }
        val width = linearLayout2.width
        val translationX = linearLayout2.translationX.toInt()
        if (!z) {
            if (translationX != 0) {
                ObjectAnimator.ofFloat(linearLayout2, "TranslationX", 0.0f).setDuration(2L).start()
                ObjectAnimator.ofFloat(linearLayout, "TranslationX", 0.0f).setDuration(200L).start()
            }
        } else if (translationX == 0) {
            ObjectAnimator.ofFloat(linearLayout2, "TranslationX", width.toFloat()).setDuration(200L).start()
            ObjectAnimator.ofFloat(linearLayout, "TranslationX", -width.toFloat()).setDuration(200L).start()
        } else {
            ObjectAnimator.ofFloat(linearLayout2, "TranslationX", 0.0f).setDuration(200L).start()
            ObjectAnimator.ofFloat(linearLayout, "TranslationX", 0.0f).setDuration(200L).start()
        }
    }

    @Subscriber(tag = "GP4225_GetStatus")
    private fun GP4225_GetStatus(str: String) {
        if (wifination.gp4225_Device.bSD) {
            if (!button6!!.isEnabled) {
                button6!!.isEnabled = true
                button7!!.isEnabled = true
                button8!!.isEnabled = true
                button6!!.setBackgroundResource(R.mipmap.photo_device)
                button7!!.setBackgroundResource(R.mipmap.video_device)
                button8!!.setBackgroundResource(R.mipmap.gallery_device)
            }
        } else if (button6!!.isEnabled) {
            button6!!.isEnabled = false
            button7!!.isEnabled = false
            button8!!.isEnabled = false
            button6!!.setBackgroundResource(R.mipmap.photo_device_dis)
            button7!!.setBackgroundResource(R.mipmap.video_device_dis)
            button8!!.setBackgroundResource(R.mipmap.gallery_device_dis)
        }
        if (wifination.gp4225_Device.bSDRecording) {
            if (!bSdRecording) {
                bSdRecording = true
                F_DispRecordTime_SD(true)
            }
        } else if (bSdRecording) {
            bSdRecording = false
            F_DispRecordTime_SD(false)
        }
        val iArr = intArrayOf(R.mipmap.battery_0, R.mipmap.battery_1, R.mipmap.battery_2, R.mipmap.battery_3, R.mipmap.battery_4)
        var i = wifination.gp4225_Device.nBattery
        if (i > 4) {
            i = 4
        }
        if (i < 0) {
            i = 3
        }
        button5!!.setBackgroundResource(iArr[i])
    }

    private val system: Long
        private get() = java.lang.Long.valueOf(Date().time)

    private inner class ClickPressedThread private constructor() : Runnable {
        override fun run() {
            mClickCount = 0
        }
    }

    @Subscriber(tag = "onAdjFocus")
    private fun onAdjFocus(i: Int) {
        bAdjIng = false
    }

    private fun F_DispIrStatus() {
        val b = nIR_Status
        if (b.toInt() == 1) {
            button_left0!!.setBackgroundResource(R.mipmap.ir_on)
        } else if (b.toInt() == 2) {
            button_left0!!.setBackgroundResource(R.mipmap.ir_auto)
        } else {
            button_left0!!.setBackgroundResource(R.mipmap.ir_off)
        }
    }

    private fun F_DispLedStatus() {
        if (bLedStatus) {
            button_right0!!.setBackgroundResource(R.mipmap.led_status_on)
        } else {
            button_right0!!.setBackgroundResource(R.mipmap.led_status_off)
        }
    }

    @Subscriber(tag = "GP4225_GetIR_Status")
    private fun GP4225_GetIR_Status(num: Int) {
        nIR_Status = num.toByte()
        F_DispIrStatus()
        Log.e("GetStatus", "IR = $num")
    }

    @Subscriber(tag = "GP4225_GetPIR_Status")
    private fun GP4225_GetPIR_Status(num: Int) {
        Log.e("GetStatus", "PIR = $num")
    }

    @Subscriber(tag = "GP4225_GetLed_Status")
    private fun GP4225_GetLed_Status(num: Int) {
        bLedStatus = num != 0
        F_DispLedStatus()
        Log.e("GetStatus", "LEDStatus = $num")
    }

    @Subscriber(tag = "Go2Background")
    private fun Go2Background(str: String) {
        finish()
    }

    @Subscriber(tag = "GP4225_GetDeviceInfo")
    private fun GP4225_GetDeviceInfo(bArr: ByteArray?) {
        if (bArr != null) {
            if (bArr.size >= 56) {
                val i = (bArr[55].toInt() and -1) * 16777216 or (bArr[52].toInt() and -1) or (bArr[53].toInt() and -1) * 256 or (bArr[54].toInt() and -1) * 65536
                bIREn = i and 8 != 0
                bStatusLEDEn = i and 16 != 0
                F_AdjIR_Button()
                F_AdjStatusLed_Button()
                if (bIREn) {
                    wifination.naReadIR()
                }
                if (bStatusLEDEn) {
                    wifination.naReadStatusLedDisp()
                    return
                }
                return
            }
            bIREn = false
            bStatusLEDEn = false
            F_AdjIR_Button()
            F_AdjStatusLed_Button()
            return
        }
        bIREn = false
        bStatusLEDEn = false
        F_AdjIR_Button()
        F_AdjStatusLed_Button()
    }

    private val wifiRssi: Int
        private get() {
            val connectionInfo = (applicationContext.getSystemService(WIFI_SERVICE) as WifiManager).connectionInfo
            return WifiManager.calculateSignalLevel(connectionInfo?.rssi ?: -200, 5)
        }
}