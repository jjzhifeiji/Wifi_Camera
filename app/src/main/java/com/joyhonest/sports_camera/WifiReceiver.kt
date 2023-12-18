package com.joyhonest.sports_camera

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.NetworkInfo
import android.os.Parcelable
import android.util.Log
import org.simple.eventbus.EventBus

class WifiReceiver : BroadcastReceiver() {
    private fun getConnectionType(i: Int): String {
        return if (i == 0) "3G网络数据" else if (i == 1) "WIFI网络" else ""
    }

    override fun onReceive(context: Context, intent: Intent) {
        var networkInfo: NetworkInfo? = null
        var parcelableExtra: Parcelable? = null
        if ("android.net.wifi.WIFI_STATE_CHANGED" == intent.action) {
            val intExtra = intent.getIntExtra("wifi_state", 0)
            Log.e("TAG", "wifiState:$intExtra")
            if (intExtra == 1) {
                EventBus.getDefault().post("WiFi_Close", "WifiStatusChamged")
                Log.e(TAG, "断开0")
            }
        }
        if ("android.net.wifi.STATE_CHANGE" == intent.action && intent.getParcelableExtra<Parcelable>("networkInfo").also { parcelableExtra = it } != null) {
            if ((parcelableExtra as NetworkInfo).state != NetworkInfo.State.CONNECTED) {
                EventBus.getDefault().post("WiFi_NotConnected", "WifiStatusChamged")
                Log.e(TAG, "断开1")
            }
        }
        if ("android.net.conn.CONNECTIVITY_CHANGE" != intent.action || (intent.getParcelableExtra<Parcelable>("networkInfo") as NetworkInfo).also { networkInfo = it } == null) {
            return
        }
        if (NetworkInfo.State.CONNECTED == networkInfo?.state && networkInfo?.isAvailable == true) {
            if (networkInfo?.type == 1 || networkInfo?.type == 0) {
                Log.i("TAG", getConnectionType(networkInfo?.type!!) + "连上")
                return
            }
            return
        }
        EventBus.getDefault().post("WiFi_DisConnected", "WifiStatusChamged")
        Log.e(TAG, "断开2")
    }

    companion object {
        private const val TAG = "WifiReceiver"
    }
}