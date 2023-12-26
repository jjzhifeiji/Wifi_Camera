package com.joyhonest.sports_camera;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPrefs {
    private static final boolean DEFAULT_PRIVATE_POLICY_AGREEMENT = false;
    private static final String PREF_NAME = "Sports_Camera.pref";
    private static final String PREF_PRIVACY_POLICY_AGREEMENT = "perf_privacy_policy_agreement";
    private static AppPrefs instance;
    private final SharedPreferences prefs;

    private AppPrefs(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, 0);
    }

    public static synchronized AppPrefs getInstance(Context context) {
        AppPrefs appPrefs;
        synchronized (AppPrefs.class) {
            if (instance == null) {
                instance = new AppPrefs(context);
            }
            appPrefs = instance;
        }
        return appPrefs;
    }

    public boolean getPrivacyPolicyAgreement() {
        return this.prefs.getBoolean(PREF_PRIVACY_POLICY_AGREEMENT, false);
    }

    public void setPrivacyPolicyAgreement(boolean z) {
        this.prefs.edit().putBoolean(PREF_PRIVACY_POLICY_AGREEMENT, z).apply();
    }
}
