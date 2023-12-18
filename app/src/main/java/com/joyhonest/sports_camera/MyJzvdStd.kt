package com.joyhonest.sports_camera

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import android.widget.SeekBar
import cn.jzvd.JzvdStd

class MyJzvdStd : JzvdStd {
    override fun getLayoutId(): Int {
        return R.layout.jz_layout_std
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attributeSet: AttributeSet?) : super(context, attributeSet)

    override fun init(context: Context) {
        super.init(context)
        fullscreenButton.visibility = GONE
        replayTextView.text = ""
        val layoutParams = backButton.layoutParams as RelativeLayout.LayoutParams
        layoutParams.width = 0
        layoutParams.height = 0
        backButton.layoutParams = layoutParams
    }

    override fun onClick(view: View) {
        super.onClick(view)
        val id = view.id
        if (id == R.id.fullscreen) {
            Log.i("JZVD", "onClick: fullscreen button")
        } else if (id == R.id.start) {
            Log.i("JZVD", "onClick: start button")
        }
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        super.onTouch(view, motionEvent)
        if (view.id == R.id.surface_container && motionEvent.action == 1) {
            if (mChangePosition) {
                Log.i("JZVD", "Touch screen seek position")
            }
            if (mChangeVolume) {
                Log.i("JZVD", "Touch screen change volume")
                return false
            }
            return false
        }
        return false
    }

    override fun startVideo() {
        super.startVideo()
        Log.i("JZVD", "startVideo")
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        super.onStopTrackingTouch(seekBar)
        Log.i("JZVD", "Seek position ")
    }

    override fun gotoFullscreen() {
        super.gotoFullscreen()
        Log.i("JZVD", "goto Fullscreen")
    }

    override fun gotoNormalScreen() {
        super.gotoNormalScreen()
        Log.i("JZVD", "quit Fullscreen")
    }

    override fun autoFullscreen(f: Float) {
        super.autoFullscreen(f)
        Log.i("JZVD", "auto Fullscreen")
    }

    override fun onClickUiToggle() {
        super.onClickUiToggle()
        Log.i("JZVD", "click blank")
    }

    override fun onStateNormal() {
        super.onStateNormal()
    }

    override fun onStatePreparing() {
        super.onStatePreparing()
    }

    override fun onStatePlaying() {
        super.onStatePlaying()
    }

    override fun onStatePause() {
        super.onStatePause()
    }

    override fun onStateError() {
        super.onStateError()
    }

    override fun onCompletion() {
        super.onCompletion()
    }

    override fun onStateAutoComplete() {
        super.onStateAutoComplete()
        Log.i("JZVD", "Auto complete")
    }

    override fun changeUiToNormal() {
        super.changeUiToNormal()
    }

    override fun changeUiToPreparing() {
        super.changeUiToPreparing()
    }

    override fun changeUiToPlayingShow() {
        super.changeUiToPlayingShow()
    }

    override fun changeUiToPlayingClear() {
        super.changeUiToPlayingClear()
    }

    override fun changeUiToPauseShow() {
        super.changeUiToPauseShow()
    }

    override fun changeUiToPauseClear() {
        super.changeUiToPauseClear()
    }

    override fun changeUiToComplete() {
        super.changeUiToComplete()
    }

    override fun changeUiToError() {
        super.changeUiToError()
    }

    override fun onInfo(i: Int, i2: Int) {
        super.onInfo(i, i2)
    }

    override fun onError(i: Int, i2: Int) {
        super.onError(i, i2)
    }
}