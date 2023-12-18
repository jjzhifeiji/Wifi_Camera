package com.joyhonest.sports_camera

import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView

class MyVideoPlayView @JvmOverloads constructor(context: Context?, attributeSet: AttributeSet? = null, i: Int = 0, i2: Int = 0) : RelativeLayout(context, attributeSet, i, i2) {
    private var btn_back: Button? = null
    private var btn_pause: Button? = null
    private var disp_ImageView: ImageView? = null
    private var progressBar: ProgressBar? = null
    private var time_View1: TextView? = null
    private var time_View2: TextView? = null

    init {
        init()
    }

    private fun dip2px(context: Context, f: Float): Int {
        return (f * context.resources.displayMetrics.density + 0.5f).toInt()
    }

    private fun init() {
        val context = context
        disp_ImageView = ImageView(context)
        btn_back = Button(context)
        btn_pause = Button(context)
        progressBar = ProgressBar(context)
        time_View1 = TextView(context)
        time_View2 = TextView(context)
        val dip2px = dip2px(context, 40.0f)
        addView(disp_ImageView, LayoutParams(-1, -1))
        val layoutParams = LayoutParams(dip2px, dip2px)
        layoutParams.leftMargin = dip2px(context, 8.0f)
        layoutParams.topMargin = dip2px(context, 8.0f)
        addView(btn_back, layoutParams)
    }
}