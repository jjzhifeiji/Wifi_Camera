package com.joyhonest.sports_camera

import android.content.Context
import android.media.MediaPlayer
import android.util.AttributeSet
import android.view.KeyEvent
import android.widget.VideoView

class CustomVideoView : VideoView {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attributeSet: AttributeSet?) : super(context, attributeSet)
    constructor(context: Context?, attributeSet: AttributeSet?, i: Int) : super(context, attributeSet, i)

    override fun onMeasure(i: Int, i2: Int) {
        setMeasuredDimension(getDefaultSize(0, i), getDefaultSize(0, i2))
    }

    override fun setOnPreparedListener(onPreparedListener: MediaPlayer.OnPreparedListener) {
        super.setOnPreparedListener(onPreparedListener)
    }

    override fun onKeyDown(i: Int, keyEvent: KeyEvent): Boolean {
        return super.onKeyDown(i, keyEvent)
    }
}