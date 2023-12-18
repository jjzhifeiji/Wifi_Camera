package com.joyhonest.sports_camera

import android.content.Context
import android.content.SharedPreferences
import kotlin.jvm.Synchronized

class AppPrefs private constructor(context: Context) {
    private val prefs: SharedPreferences

    init {
        prefs = context.getSharedPreferences(PREF_NAME, 0)
    }

    var privacyPolicyAgreement: Boolean
        get() = prefs.getBoolean(PREF_PRIVACY_POLICY_AGREEMENT, false)
        set(z) {
            prefs.edit().putBoolean(PREF_PRIVACY_POLICY_AGREEMENT, z).apply()
        }

    companion object {
        private const val DEFAULT_PRIVATE_POLICY_AGREEMENT = false
        private const val PREF_NAME = "Sports_Camera.pref"
        private const val PREF_PRIVACY_POLICY_AGREEMENT = "perf_privacy_policy_agreement"
        private var instance: AppPrefs? = null

        @Synchronized
        fun getInstance(context: Context): AppPrefs {
            var appPrefs: AppPrefs
            synchronized(AppPrefs::class.java) {
                if (instance == null) {
                    instance = AppPrefs(context)
                }
                appPrefs = instance!!
            }
            return appPrefs
        }
    }
}