package com.joyhonest.sports_camera

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout

class RoundConstraintLayout : ConstraintLayout {
    private var mHeight = 0
    private var mLastRadius = 0
    private var mPath: Path? = null
    private var mRadius = 0
    private var mRoundMode: Int
    private var mWidth = 0

    constructor(context: Context?) : super(context) {
        mRoundMode = 1
        init()
    }

    constructor(context: Context?, attributeSet: AttributeSet?) : super(context, attributeSet) {
        mRoundMode = 1
        init()
    }

    private fun init() {
        setBackgroundColor(-1)
        val path = Path()
        mPath = path
        path.fillType = Path.FillType.EVEN_ODD
        setCornerRadius(Storage.dip2px(context, 10.0f))
    }

    fun setRoundMode(i: Int) {
        mRoundMode = i
    }

    fun setCornerRadius(i: Int) {
        mRadius = i
    }

    private fun checkPathChanged() {
        if (width == mWidth && height == mHeight && mLastRadius == mRadius) {
            return
        }
        mWidth = width
        mHeight = height
        mLastRadius = mRadius
        mPath!!.reset()
        val i = mRoundMode
        if (i == 1) {
            val path = mPath
            val rectF = RectF(0.0f, 0.0f, mWidth.toFloat(), mHeight.toFloat())
            val i2 = mRadius
            path!!.addRoundRect(rectF, i2.toFloat(), i2.toFloat(), Path.Direction.CW)
        } else if (i == 2) {
            val path2 = mPath
            val rectF2 = RectF(0.0f, 0.0f, mWidth.toFloat(), mHeight.toFloat())
            val i3 = mRadius
            path2!!.addRoundRect(rectF2, floatArrayOf(i3.toFloat(), i3.toFloat(), 0.0f, 0.0f, 0.0f, 0.0f, i3.toFloat(), i3.toFloat()), Path.Direction.CW)
        } else if (i == 3) {
            val path3 = mPath
            val rectF3 = RectF(0.0f, 0.0f, mWidth.toFloat(), mHeight.toFloat())
            val i4 = mRadius
            path3!!.addRoundRect(rectF3, floatArrayOf(i4.toFloat(), i4.toFloat(), i4.toFloat(), i4.toFloat(), 0.0f, 0.0f, 0.0f, 0.0f), Path.Direction.CW)
        } else if (i == 4) {
            val path4 = mPath
            val rectF4 = RectF(0.0f, 0.0f, mWidth.toFloat(), mHeight.toFloat())
            val i5 = mRadius
            path4!!.addRoundRect(rectF4, floatArrayOf(0.0f, 0.0f, i5.toFloat(), i5.toFloat(), i5.toFloat(), i5.toFloat(), 0.0f, 0.0f), Path.Direction.CW)
        } else if (i != 5) {
        } else {
            val path5 = mPath
            val rectF5 = RectF(0.0f, 0.0f, mWidth.toFloat(), mHeight.toFloat())
            val i6 = mRadius
            path5!!.addRoundRect(rectF5, floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f, i6.toFloat(), i6.toFloat(), i6.toFloat(), i6.toFloat()), Path.Direction.CW)
        }
    }

    override fun draw(canvas: Canvas) {
        if (mRoundMode != 0) {
            val save = canvas.save()
            checkPathChanged()
            canvas.clipPath(mPath!!)
            super.draw(canvas)
            canvas.restoreToCount(save)
            return
        }
        super.draw(canvas)
    }

    companion object {
        const val MODE_ALL = 1
        const val MODE_BOTTOM = 5
        const val MODE_LEFT = 2
        const val MODE_NONE = 0
        const val MODE_RIGHT = 4
        const val MODE_TOP = 3
    }
}