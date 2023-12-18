package com.joyhonest.sports_camera

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.internal.view.SupportMenu
import androidx.core.view.ViewCompat
import com.joyhonest.wifination.wifination
import org.simple.eventbus.Subscriber

class SettingActivity : AppCompatActivity(), View.OnClickListener {
    var AlertView: ConstraintLayout? = null
    var Ver_View: TextView? = null
    var btn_1min: Button? = null
    var btn_3min: Button? = null
    var btn_5min: Button? = null
    var btn_osd: Button? = null
    var btn_rev: Button? = null
    var format_b: Button? = null
    var format_v: TextView? = null
    var rest_b: Button? = null
    var rest_v: TextView? = null
    var sLine1: TextView? = null
    var sLine2: TextView? = null
    private val TAG = "SettingActivity"
    var bRev = false
    var bOsd = false
    var nRecT = 0
    var bReadOK = false
    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_setting)
        MyApp.Companion.F_makeFullScreen(this)
        (findViewById<View>(R.id.APP_Ver_View) as TextView).text = "1.6-24"
        val constraintLayout = findViewById<View>(R.id.AlertView) as ConstraintLayout
        AlertView = constraintLayout
        constraintLayout.visibility = View.INVISIBLE
        val textView = findViewById<View>(R.id.sLine2) as TextView
        sLine2 = textView
        textView.setTextColor(SupportMenu.CATEGORY_MASK)
        sLine1 = findViewById<View>(R.id.sLine1) as TextView
        btn_rev = findViewById<View>(R.id.btn_rev) as Button
        btn_osd = findViewById<View>(R.id.btn_osd) as Button
        btn_1min = findViewById<View>(R.id.btn_1min) as Button
        btn_3min = findViewById<View>(R.id.btn_3min) as Button
        btn_5min = findViewById<View>(R.id.btn_5min) as Button
        Ver_View = findViewById<View>(R.id.Ver_View) as TextView
        format_v = findViewById<View>(R.id.format_v) as TextView
        format_b = findViewById<View>(R.id.format_b) as Button
        rest_v = findViewById<View>(R.id.rest_v) as TextView
        rest_b = findViewById<View>(R.id.rest_b) as Button
        btn_rev!!.setOnClickListener(this)
        btn_osd!!.setOnClickListener(this)
        btn_1min!!.setOnClickListener(this)
        btn_3min!!.setOnClickListener(this)
        btn_5min!!.setOnClickListener(this)
        format_v!!.setOnClickListener(this)
        format_b!!.setOnClickListener(this)
        rest_v!!.setOnClickListener(this)
        rest_b!!.setOnClickListener(this)
        AlertView!!.setOnClickListener(this)
        findViewById<View>(R.id.btn_ok).setOnClickListener(this)
        findViewById<View>(R.id.btn_ok_a).setOnClickListener(this)
        findViewById<View>(R.id.btn_cancel).setOnClickListener(this)
        findViewById<View>(R.id.btn_back).setOnClickListener {
            MyApp.PlayBtnVoice()
            onBackPressed()
        }
        MyApp.forceSendRequestByWifiData(true, object : RequestWifi_Interface {
            override fun onResult() {
                MyApp.F_Init4225()
                wifination.na4225_ReadReversal()
                wifination.na4225_ReadOsd()
                wifination.na4225_ReadRecordTime()
                wifination.na4225_ReadFireWareVer()
            }
        })
        Handler().postDelayed({ }, 100L)
    }

    override fun onClick(view: View) {
        MyApp.PlayBtnVoice()
        when (view.id) {
            R.id.btn_1min -> {
                nRecT = 0
                F_DispStatus()
                wifination.na4225_SetRecordTime(nRecT)
                return
            }

            R.id.btn_3min -> {
                nRecT = 1
                F_DispStatus()
                wifination.na4225_SetRecordTime(nRecT)
                return
            }

            R.id.btn_5min -> {
                nRecT = 2
                F_DispStatus()
                wifination.na4225_SetRecordTime(nRecT)
                return
            }

            R.id.btn_cancel -> {
                DispAlertView(false, false)
                return
            }

            R.id.btn_ok -> {
                DispAlertView(false, false)
                wifination.na4225_FormatSD()
                AlertView!!.visibility = View.GONE
                return
            }

            R.id.btn_ok_a -> {
                DispAlertView(false, false)
                return
            }

            R.id.btn_osd -> {
                bOsd = !bOsd
                F_DispStatus()
                wifination.na4225_SetOsd(bOsd)
                return
            }

            R.id.btn_rev -> {
                bRev = !bRev
                F_DispStatus()
                wifination.na4225_SetReversal(bRev)
                DispAlertView(false, true)
                return
            }

            R.id.format_b, R.id.format_v -> {
                DispAlertView(true, true)
                return
            }

            R.id.rest_b, R.id.rest_v -> {
                wifination.na4225_ResetDevice()
                DispAlertView(false, true)
                return
            }

            else -> return
        }
    }

    private fun DispAlertView(z: Boolean, z2: Boolean) {
        if (z2) {
            if (z) {
                sLine1!!.setText(R.string.Warring)
                sLine2!!.setText(R.string.All_Data_will_be_deleted)
                sLine2!!.setTextColor(SupportMenu.CATEGORY_MASK)
                findViewById<View>(R.id.btn_ok).visibility = View.VISIBLE
                findViewById<View>(R.id.btn_cancel).visibility = View.VISIBLE
                findViewById<View>(R.id.btn_ok_a).visibility = View.GONE
            } else {
                sLine1!!.text = ""
                sLine2!!.setText(R.string.Please_reset_device)
                sLine2!!.setTextColor(ViewCompat.MEASURED_STATE_MASK)
                findViewById<View>(R.id.btn_ok).visibility = View.INVISIBLE
                findViewById<View>(R.id.btn_cancel).visibility = View.INVISIBLE
                findViewById<View>(R.id.btn_ok_a).visibility = View.VISIBLE
            }
            AlertView!!.visibility = View.VISIBLE
            return
        }
        AlertView!!.visibility = View.GONE
    }

    private fun F_DispStatus() {
        if (bOsd) {
            btn_osd!!.setBackgroundResource(R.mipmap.on)
        } else {
            btn_osd!!.setBackgroundResource(R.mipmap.off)
        }
        if (bRev) {
            btn_rev!!.setBackgroundResource(R.mipmap.on)
        } else {
            btn_rev!!.setBackgroundResource(R.mipmap.off)
        }
        val i = nRecT
        if (i == 0) {
            btn_1min!!.setBackgroundResource(R.mipmap.selected_button)
            btn_3min!!.setBackgroundResource(R.mipmap.no_selected_button)
            btn_5min!!.setBackgroundResource(R.mipmap.no_selected_button)
        } else if (i == 1) {
            btn_1min!!.setBackgroundResource(R.mipmap.no_selected_button)
            btn_3min!!.setBackgroundResource(R.mipmap.selected_button)
            btn_5min!!.setBackgroundResource(R.mipmap.no_selected_button)
        } else {
            btn_1min!!.setBackgroundResource(R.mipmap.no_selected_button)
            btn_3min!!.setBackgroundResource(R.mipmap.no_selected_button)
            btn_5min!!.setBackgroundResource(R.mipmap.selected_button)
        }
    }

    @Subscriber(tag = "GP4225_GetDeviceOsdStatus")
    private fun GP4225_GetDeviceOsdStatus(i: Int) {
        bReadOK = true
        Log.e("SettingActivity", "Osd = $i")
        if (i == 0) {
            bOsd = false
        } else {
            bOsd = true
        }
        F_DispStatus()
    }

    @Subscriber(tag = "GP4225_GetDeviceReversaltatus")
    private fun GP4225_GetDeviceReversaltatus(i: Int) {
        bReadOK = true
        Log.e("SettingActivity", "Reversal = $i")
        if (i == 0) {
            bRev = false
        } else {
            bRev = true
        }
        F_DispStatus()
    }

    @Subscriber(tag = "GP4225_GetDeviceRecordTime")
    private fun GP4225_GetDeviceRecordTime(i: Int) {
        bReadOK = true
        Log.e("SettingActivity", "RecordTime = $i")
        if (i == 0) {
            nRecT = 0
        } else if (i == 1) {
            nRecT = 1
        } else {
            nRecT = 2
        }
        F_DispStatus()
    }

    @Subscriber(tag = "GP4225_GetFireWareVersion")
    private fun GP4225_GetFireWareVersion(str: String) {
        bReadOK = true
        Ver_View!!.text = str
    }

    public override fun onResume() {
        super.onResume()
        MyApp.F_makeFullScreen(this)
    }

    public override fun onDestroy() {
        super.onDestroy()
    }

    @Subscriber(tag = "Go2Background")
    private fun Go2Background(str: String) {
        finish()
    }
}