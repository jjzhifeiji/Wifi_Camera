package com.joyhonest.sports_camera

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class boot_Activity : AppCompatActivity() {
    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_boot_)
        window.decorView.setBackgroundResource(R.mipmap.boot_interface)
        MyApp.F_makeFullScreen(this)
        Handler().postDelayed(Runnable {
            if (!AppPrefs.getInstance(applicationContext).privacyPolicyAgreement) {
                requestPrivacyPolicyAgreement()
                return@Runnable
            }
            startActivity(Intent(this@boot_Activity, SplashActivity::class.java))
            finish()
            overridePendingTransition(0, 0)
        }, 1500L)
    }

    public override fun onResume() {
        super.onResume()
        MyApp.F_makeFullScreen(this)
    }

    fun requestPrivacyPolicyAgreement() {
        val newInstance: PrivacyPolicyAgreementDialog =
                PrivacyPolicyAgreementDialog.newInstance(
                        this,
                        resources.getString(R.string.privacy_introduce_title),
                        resources.getString(R.string.privacy_introduce_content),
                        resources.getString(R.string.privacy_agree),
                        resources.getString(R.string.privacy_disagree),
                        object : PrivacyPolicyAgreementDialog.Listener {
                            override fun onYes() {
                                AppPrefs.getInstance(applicationContext).privacyPolicyAgreement = true
                                startActivity(Intent(this@boot_Activity, SplashActivity::class.java))
                                finish()
                                overridePendingTransition(0, 0)
                            }

                            override fun onNo() {
                                finish()
                            }
                        })
        newInstance.isCancelable = false
        newInstance.show(supportFragmentManager, "Privacy Policy Agreement")
    }
}