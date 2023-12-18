package com.joyhonest.sports_camera

import android.app.Activity
import androidx.core.app.ActivityCompat

class PermissionAsker {
    private var bChecked: Boolean
    private var mDeniRun: Runnable
    private var mOkRun: Runnable
    private var mReqCode: Int

    constructor() {
        val runnable = RUN
        mOkRun = runnable
        mDeniRun = runnable
        mReqCode = 1
        bChecked = false
    }

    constructor(i: Int, runnable: Runnable, runnable2: Runnable) {
        val runnable3 = RUN
        mOkRun = runnable3
        mDeniRun = runnable3
        mReqCode = 1
        bChecked = false
        mReqCode = i
        mOkRun = runnable
        mDeniRun = runnable2
    }

    fun setReqCode(i: Int) {
        mReqCode = i
    }

    fun setSuccedCallback(runnable: Runnable) {
        mOkRun = runnable
    }

    fun setFailedCallback(runnable: Runnable) {
        mDeniRun = runnable
    }

    fun askPermission(activity: Activity?, vararg strArr: String?): PermissionAsker {
        var i = 0
        for (str in strArr) {
            i += ActivityCompat.checkSelfPermission(activity!!, str!!)
        }
        if (i == 0) {
            mOkRun.run()
        } else {
            ActivityCompat.requestPermissions(activity!!, strArr, mReqCode)
        }
        return this
    }

    fun onRequestPermissionsResult(iArr: IntArray) {
        var z = true
        for (i in iArr) {
            z = z and (i == 0)
        }
        if (iArr.size > 0 && z) {
            mOkRun.run()
        } else {
            mDeniRun.run()
        }
    }

    companion object {
        private val RUN = Runnable { }
    }
}