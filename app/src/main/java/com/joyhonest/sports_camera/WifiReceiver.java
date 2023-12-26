package com.joyhonest.sports_camera;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.os.Parcelable;
import android.util.Log;

import org.simple.eventbus.EventBus;

public class WifiReceiver extends BroadcastReceiver {
    private static final String TAG = "WifiReceiver";

    private String getConnectionType(int i) {
        return i == 0 ? "3G网络数据" : i == 1 ? "WIFI网络" : "";
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        NetworkInfo networkInfo;
        Parcelable parcelableExtra;
        if ("android.net.wifi.WIFI_STATE_CHANGED".equals(intent.getAction())) {
            int intExtra = intent.getIntExtra("wifi_state", 0);
            Log.e("TAG", "wifiState:" + intExtra);
            if (intExtra == 1) {
                EventBus.getDefault().post("WiFi_Close", "WifiStatusChamged");
                Log.e(TAG, "断开0");
            }
        }
        if ("android.net.wifi.STATE_CHANGE".equals(intent.getAction()) && (parcelableExtra = intent.getParcelableExtra("networkInfo")) != null) {
            if (!(((NetworkInfo) parcelableExtra).getState() == NetworkInfo.State.CONNECTED)) {
                EventBus.getDefault().post("WiFi_NotConnected", "WifiStatusChamged");
                Log.e(TAG, "断开1");
            }
        }
        if (!"android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction()) || (networkInfo = (NetworkInfo) intent.getParcelableExtra("networkInfo")) == null) {
            return;
        }
        if (NetworkInfo.State.CONNECTED == networkInfo.getState() && networkInfo.isAvailable()) {
            if (networkInfo.getType() == 1 || networkInfo.getType() == 0) {
                Log.i("TAG", getConnectionType(networkInfo.getType()) + "连上");
                return;
            }
            return;
        }
        EventBus.getDefault().post("WiFi_DisConnected", "WifiStatusChamged");
        Log.e(TAG, "断开2");
    }
}
