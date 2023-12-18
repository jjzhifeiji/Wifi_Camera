package com.joyhonest.sports_camera

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.OverScroller
import android.widget.Scroller
import androidx.appcompat.widget.AppCompatImageView


class MyImageView : AppCompatImageView {

    var bCanScal: Boolean
    private var context: Context? = null
    private var delayedZoomVariables: ZoomVariables? = null
    private var doubleTapListener: GestureDetector.OnDoubleTapListener?
    private var fling: Fling? = null
    private var imageRenderedAtLeastOnce = false
    private var m: FloatArray? = null
    private var mGestureDetector: GestureDetector? = null
    private var mScaleDetector: ScaleGestureDetector? = null
    private var mScaleType: ScaleType? = null
    private var matchViewHeight = 0f
    private var matchViewWidth = 0f
    private var matrix: Matrix? = null
    private var maxScale = 0f
    private var minScale = 0f
    var currentZoom = 0f
        private set
    private var onDrawReady = false
    private var prevMatchViewHeight = 0f
    private var prevMatchViewWidth = 0f
    private var prevMatrix: Matrix? = null
    private var prevViewHeight = 0
    private var prevViewWidth = 0
    private var state: State? = null
    private var superMaxScale = 0f
    private var superMinScale = 0f
    private var touchImageViewListener: OnTouchImageViewListener?
    private var userTouchListener: OnTouchListener?
    private var viewHeight = 0
    private var viewWidth = 0

    interface OnTouchImageViewListener {
        fun onMove()
    }

    enum class State {
        NONE, DRAG, ZOOM, FLING, ANIMATE_ZOOM
    }

    fun getFixDragTrans(f: Float, f2: Float, f3: Float): Float {
        return if (f3 <= f2) {
            0.0f
        } else f
    }

    private fun getFixTrans(f: Float, f2: Float, f3: Float): Float {
        val f4: Float
        val f5: Float
        if (f3 <= f2) {
            f5 = f2 - f3
            f4 = 0.0f
        } else {
            f4 = f2 - f3
            f5 = 0.0f
        }
        if (f < f4) {
            return -f + f4
        }
        return if (f > f5) {
            -f + f5
        } else 0.0f
    }

    constructor(context: Context) : super(context) {
        bCanScal = false
        doubleTapListener = null
        userTouchListener = null
        touchImageViewListener = null
        sharedConstructing(context)
    }

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) {
        bCanScal = false
        doubleTapListener = null
        userTouchListener = null
        touchImageViewListener = null
        sharedConstructing(context)
    }

    constructor(context: Context, attributeSet: AttributeSet?, i: Int) : super(context, attributeSet, i) {
        bCanScal = false
        doubleTapListener = null
        userTouchListener = null
        touchImageViewListener = null
        sharedConstructing(context)
    }

    private fun sharedConstructing(context: Context) {
        super.setClickable(true)
        this.context = context
        mScaleDetector = ScaleGestureDetector(context, ScaleListener(this, null))
        mGestureDetector = GestureDetector(context, GestureListener(this, null))
        matrix = Matrix()
        prevMatrix = Matrix()
        m = FloatArray(9)
        currentZoom = 1.0f
        if (mScaleType == null) {
            mScaleType = ScaleType.FIT_CENTER
        }
        minScale = 1.0f
        maxScale = 4.0f
        superMinScale = 1.0f * 1.0f
        superMaxScale = 4.0f * 1.0f
        imageMatrix = matrix
        scaleType = ScaleType.MATRIX
        setState(State.NONE)
        onDrawReady = false
        super.setOnTouchListener(PrivateOnTouchListener(this, null))
    }

    override fun setOnTouchListener(onTouchListener: OnTouchListener) {
        userTouchListener = onTouchListener
    }

    fun setOnTouchImageViewListener(onTouchImageViewListener: OnTouchImageViewListener?) {
        touchImageViewListener = onTouchImageViewListener
    }

    fun setOnDoubleTapListener(onDoubleTapListener: GestureDetector.OnDoubleTapListener?) {
        doubleTapListener = onDoubleTapListener
    }

    override fun setImageResource(i: Int) {
        super.setImageResource(i)
        savePreviousImageValues()
        fitImageToView()
    }

    override fun setImageBitmap(bitmap: Bitmap) {
        super.setImageBitmap(bitmap)
        savePreviousImageValues()
        fitImageToView()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        savePreviousImageValues()
        fitImageToView()
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        savePreviousImageValues()
        fitImageToView()
    }

    override fun setScaleType(scaleType: ScaleType) {
        if (scaleType == ScaleType.FIT_START || scaleType == ScaleType.FIT_END) {
            throw UnsupportedOperationException("TouchImageView does not support FIT_START or FIT_END")
        }

        if (scaleType == ScaleType.MATRIX) {
            super.setScaleType(ScaleType.MATRIX)
            return
        }
        mScaleType = scaleType
        if (onDrawReady) {
            setZoom(this)
        }
    }

    override fun getScaleType(): ScaleType {
        return mScaleType!!
    }

    val isZoomed: Boolean
        get() = currentZoom != 1.0f
    val zoomedRect: RectF
        get() {
            if (mScaleType == ScaleType.FIT_XY) {
                throw UnsupportedOperationException("getZoomedRect() not supported with FIT_XY");
            }
            val transformCoordTouchToBitmap = transformCoordTouchToBitmap(0.0f, 0.0f, true)
            val transformCoordTouchToBitmap2 = transformCoordTouchToBitmap(viewWidth.toFloat(), viewHeight.toFloat(), true)
            val intrinsicWidth = drawable.intrinsicWidth.toFloat()
            val intrinsicHeight = drawable.intrinsicHeight.toFloat()
            return RectF(
                    transformCoordTouchToBitmap.x / intrinsicWidth,
                    transformCoordTouchToBitmap.y / intrinsicHeight,
                    transformCoordTouchToBitmap2.x / intrinsicWidth,
                    transformCoordTouchToBitmap2.y / intrinsicHeight)
        }

    private fun savePreviousImageValues() {
        val matrix = matrix
        if (matrix == null || viewHeight == 0 || viewWidth == 0) {
            return
        }
        matrix.getValues(m)
        prevMatrix!!.setValues(m)
        prevMatchViewHeight = matchViewHeight
        prevMatchViewWidth = matchViewWidth
        prevViewHeight = viewHeight
        prevViewWidth = viewWidth
    }

    public override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable("instanceState", super.onSaveInstanceState())
        bundle.putFloat("saveScale", currentZoom)
        bundle.putFloat("matchViewHeight", matchViewHeight)
        bundle.putFloat("matchViewWidth", matchViewWidth)
        bundle.putInt("viewWidth", viewWidth)
        bundle.putInt("viewHeight", viewHeight)
        matrix!!.getValues(m)
        bundle.putFloatArray("matrix", m)
        bundle.putBoolean("imageRendered", imageRenderedAtLeastOnce)
        return bundle
    }

    public override fun onRestoreInstanceState(parcelable: Parcelable) {
        if (parcelable is Bundle) {
            val bundle = parcelable
            currentZoom = bundle.getFloat("saveScale")
            val floatArray = bundle.getFloatArray("matrix")
            m = floatArray
            prevMatrix!!.setValues(floatArray)
            prevMatchViewHeight = bundle.getFloat("matchViewHeight")
            prevMatchViewWidth = bundle.getFloat("matchViewWidth")
            prevViewHeight = bundle.getInt("viewHeight")
            prevViewWidth = bundle.getInt("viewWidth")
            imageRenderedAtLeastOnce = bundle.getBoolean("imageRendered")
            super.onRestoreInstanceState(bundle.getParcelable("instanceState"))
            return
        }
        super.onRestoreInstanceState(parcelable)
    }

    override fun onDraw(canvas: Canvas) {
        onDrawReady = true
        imageRenderedAtLeastOnce = true
        val zoomVariables = delayedZoomVariables
        if (zoomVariables != null) {
            setZoom(zoomVariables.scale, delayedZoomVariables!!.focusX, delayedZoomVariables!!.focusY, delayedZoomVariables!!.scaleType)
            delayedZoomVariables = null
        }
        super.onDraw(canvas)
    }

    public override fun onConfigurationChanged(configuration: Configuration) {
        super.onConfigurationChanged(configuration)
        savePreviousImageValues()
    }

    var maxZoom: Float
        get() = maxScale
        set(f) {
            maxScale = f
            superMaxScale = f * 1.0f
        }
    var minZoom: Float
        get() = minScale
        set(f) {
            minScale = f
            superMinScale = f * 1.0f
        }

    fun resetZoom() {
        currentZoom = 1.0f
        fitImageToView()
    }

    fun setZoom(f: Float) {
        setZoom(f, 0.5f, 0.5f)
    }

    fun setZoom(f: Float, f2: Float, f3: Float) {
        setZoom(f, f2, f3, mScaleType)
    }

    fun setZoom(f: Float, f2: Float, f3: Float, scaleType: ScaleType?) {
        if (!onDrawReady) {
            delayedZoomVariables = ZoomVariables(f, f2, f3, scaleType)
            return
        }
        if (scaleType != mScaleType) {
            setScaleType(scaleType!!)
        }
        resetZoom()
        scaleImage(f.toDouble(), (viewWidth / 2).toFloat(), (viewHeight / 2).toFloat(), true)
        matrix!!.getValues(m)
        m!![2] = -(f2 * imageWidth - viewWidth * 0.5f)
        m!![5] = -(f3 * imageHeight - viewHeight * 0.5f)
        matrix!!.setValues(m)
        fixTrans()
        imageMatrix = matrix
    }

    fun setZoom(myImageView: MyImageView) {
        val scrollPosition = myImageView.scrollPosition
        setZoom(myImageView.currentZoom, scrollPosition!!.x, scrollPosition.y, myImageView.scaleType)
    }

    val scrollPosition: PointF?
        get() {
            val drawable = drawable ?: return null
            val intrinsicWidth = drawable.intrinsicWidth
            val intrinsicHeight = drawable.intrinsicHeight
            val transformCoordTouchToBitmap = transformCoordTouchToBitmap((viewWidth / 2).toFloat(), (viewHeight / 2).toFloat(), true)
            transformCoordTouchToBitmap.x /= intrinsicWidth.toFloat()
            transformCoordTouchToBitmap.y /= intrinsicHeight.toFloat()
            return transformCoordTouchToBitmap
        }

    fun setScrollPosition(f: Float, f2: Float) {
        setZoom(currentZoom, f, f2)
    }

    fun fixTrans() {
        matrix!!.getValues(m)
        val fArr = m
        val f = fArr!![2]
        val f2 = fArr[5]
        val fixTrans = getFixTrans(f, viewWidth.toFloat(), imageWidth)
        val fixTrans2 = getFixTrans(f2, viewHeight.toFloat(), imageHeight)
        if (fixTrans == 0.0f && fixTrans2 == 0.0f) {
            return
        }
        matrix!!.postTranslate(fixTrans, fixTrans2)
    }

    fun fixScaleTrans() {
        fixTrans()
        matrix!!.getValues(m)
        val imageWidth = imageWidth
        val i = viewWidth
        if (imageWidth < i) {
            m!![2] = (i - this.imageWidth) / 2.0f
        }
        val imageHeight = imageHeight
        val i2 = viewHeight
        if (imageHeight < i2) {
            m!![5] = (i2 - this.imageHeight) / 2.0f
        }
        matrix!!.setValues(m)
    }

    val imageWidth: Float
        get() = matchViewWidth * currentZoom
    val imageHeight: Float
        get() = matchViewHeight * currentZoom

    override fun onMeasure(i: Int, i2: Int) {
        val drawable = drawable
        if (drawable == null || drawable.intrinsicWidth == 0 || drawable.intrinsicHeight == 0) {
            setMeasuredDimension(0, 0)
            return
        }
        val intrinsicWidth = drawable.intrinsicWidth
        val intrinsicHeight = drawable.intrinsicHeight
        val size = MeasureSpec.getSize(i)
        val mode = MeasureSpec.getMode(i)
        val size2 = MeasureSpec.getSize(i2)
        val mode2 = MeasureSpec.getMode(i2)
        viewWidth = setViewSize(mode, size, intrinsicWidth)
        val viewSize = setViewSize(mode2, size2, intrinsicHeight)
        viewHeight = viewSize
        setMeasuredDimension(viewWidth, viewSize)
        fitImageToView()
    }

    private fun fitImageToView() {
        val drawable = drawable
        if (drawable == null || drawable.intrinsicWidth == 0 || drawable.intrinsicHeight == 0 || matrix == null || prevMatrix == null) {
            return
        }
        val intrinsicWidth = drawable.intrinsicWidth
        val intrinsicHeight = drawable.intrinsicHeight
        val f = intrinsicWidth.toFloat()
        var f2 = viewWidth / f
        val f3 = intrinsicHeight.toFloat()
        var f4 = viewHeight / f3
        val i = fun1.`$SwitchMap$android$widget$ImageView$ScaleType`[mScaleType!!.ordinal]
        if (i != 1) {
            if (i == 2) {
                f2 = Math.max(f2, f4)
            } else {
                if (i == 3) {
                    f2 = Math.min(1.0f, Math.min(f2, f4))
                    f4 = f2
                } else if (i != 4) {
                    if (i != 5) {
                        throw UnsupportedOperationException("TouchImageView does not support FIT_START or FIT_END");
                    }
                }
                f2 = Math.min(f2, f4)
            }
            f4 = f2
        } else {
            f2 = 1.0f
            f4 = 1.0f
        }
        val i2 = viewWidth
        val f5 = i2 - f2 * f
        val i3 = viewHeight
        val f6 = i3 - f4 * f3
        matchViewWidth = i2 - f5
        matchViewHeight = i3 - f6
        if (!isZoomed && !imageRenderedAtLeastOnce) {
            matrix!!.setScale(f2, f4)
            matrix!!.postTranslate(f5 / 2.0f, f6 / 2.0f)
            currentZoom = 1.0f
        } else {
            if (prevMatchViewWidth == 0.0f || prevMatchViewHeight == 0.0f) {
                savePreviousImageValues()
            }
            prevMatrix!!.getValues(m)
            val fArr = m
            val f7 = matchViewWidth / f
            val f8 = currentZoom
            fArr!![0] = f7 * f8
            fArr[4] = matchViewHeight / f3 * f8
            val f9 = fArr[2]
            val f10 = fArr[5]
            translateMatrixAfterRotate(2, f9, prevMatchViewWidth * f8, imageWidth, prevViewWidth, viewWidth, intrinsicWidth)
            translateMatrixAfterRotate(5, f10, prevMatchViewHeight * currentZoom, imageHeight, prevViewHeight, viewHeight, intrinsicHeight)
            matrix!!.setValues(m)
        }
        fixTrans()
        imageMatrix = matrix
    }

    object fun1 {
        val `$SwitchMap$android$widget$ImageView$ScaleType`: IntArray

        init {
            val iArr = IntArray(ScaleType.values().size)
            `$SwitchMap$android$widget$ImageView$ScaleType` = iArr
            try {
                iArr[ScaleType.CENTER.ordinal] = 1
            } catch (unused: NoSuchFieldError) {
            }
            try {
                `$SwitchMap$android$widget$ImageView$ScaleType`[ScaleType.CENTER_CROP.ordinal] = 2
            } catch (unused2: NoSuchFieldError) {
            }
            try {
                `$SwitchMap$android$widget$ImageView$ScaleType`[ScaleType.CENTER_INSIDE.ordinal] = 3
            } catch (unused3: NoSuchFieldError) {
            }
            try {
                `$SwitchMap$android$widget$ImageView$ScaleType`[ScaleType.FIT_CENTER.ordinal] = 4
            } catch (unused4: NoSuchFieldError) {
            }
            try {
                `$SwitchMap$android$widget$ImageView$ScaleType`[ScaleType.FIT_XY.ordinal] = 5
            } catch (unused5: NoSuchFieldError) {
            }
        }
    }

    private fun setViewSize(i: Int, i2: Int, i3: Int): Int {
        return if (i != Int.MIN_VALUE) {
            if (i != 0) i2 else i3
        } else Math.min(i3, i2)
    }

    private fun translateMatrixAfterRotate(i: Int, f: Float, f2: Float, f3: Float, i2: Int, i3: Int, i4: Int) {
        val f4 = i3.toFloat()
        if (f3 < f4) {
            val fArr = m
            fArr!![i] = (f4 - i4 * fArr[0]) * 0.5f
        } else if (f > 0.0f) {
            m!![i] = -((f3 - f4) * 0.5f)
        } else {
            m!![i] = -((Math.abs(f) + i2 * 0.5f) / f2 * f3 - f4 * 0.5f)
        }
    }

    fun setState(state: State?) {
        this.state = state
    }

    fun canScrollHorizontallyFroyo(i: Int): Boolean {
        return canScrollHorizontally(i)
    }

    override fun canScrollHorizontally(i: Int): Boolean {
        matrix!!.getValues(m)
        val f = m!![2]
        if (imageWidth < viewWidth) {
            return false
        }
        return if (f < -1.0f || i >= 0) {
            Math.abs(f) + viewWidth.toFloat() + 1.0f < imageWidth || i <= 0
        } else false
    }

    inner class GestureListener private constructor() : GestureDetector.SimpleOnGestureListener() {
        internal constructor(myImageView: MyImageView?, obj: Any?) : this()

        override fun onSingleTapConfirmed(motionEvent: MotionEvent): Boolean {
            return if (doubleTapListener != null) {
                doubleTapListener!!.onSingleTapConfirmed(motionEvent)
            } else performClick()
        }

        override fun onLongPress(motionEvent: MotionEvent) {
            performLongClick()
        }

        override fun onFling(motionEvent: MotionEvent, motionEvent2: MotionEvent, f: Float, f2: Float): Boolean {
            if (fling != null) {
                fling!!.cancelFling()
            }
            val myImageView = this@MyImageView
            myImageView.fling = Fling(f.toInt(), f2.toInt())
            val myImageView2 = this@MyImageView
            myImageView2.compatPostOnAnimation(myImageView2.fling)
            return super.onFling(motionEvent, motionEvent2, f, f2)
        }

        override fun onDoubleTap(motionEvent: MotionEvent): Boolean {
            val onDoubleTap = if (doubleTapListener != null) doubleTapListener!!.onDoubleTap(motionEvent) else false
            if (state == State.NONE) {
                compatPostOnAnimation(DoubleTapZoom(if (currentZoom == minScale) maxScale else minScale, motionEvent.x, motionEvent.y, false))
                return true
            }
            return onDoubleTap
        }

        override fun onDoubleTapEvent(motionEvent: MotionEvent): Boolean {
            return if (doubleTapListener != null) {
                doubleTapListener!!.onDoubleTapEvent(motionEvent)
            } else false
        }
    }

    inner class PrivateOnTouchListener private constructor() : OnTouchListener {
        private val last: PointF

        init {
            last = PointF()
        }

        internal constructor(myImageView: MyImageView?, obj: Any?) : this()

        override fun onTouch(r8: View, r9: MotionEvent): Boolean {
            throw UnsupportedOperationException("Method not decompiled: com.joyhonest.sports_camera.MyImageView.PrivateOnTouchListener.onTouch(android.view.View, android.view.MotionEvent):boolean");
        }
    }

    inner class ScaleListener private constructor() : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        internal constructor(myImageView: MyImageView?, obj: Any?) : this()

        override fun onScaleBegin(scaleGestureDetector: ScaleGestureDetector): Boolean {
            setState(State.ZOOM)
            return true
        }

        override fun onScale(scaleGestureDetector: ScaleGestureDetector): Boolean {
            scaleImage(scaleGestureDetector.scaleFactor.toDouble(), scaleGestureDetector.focusX, scaleGestureDetector.focusY, true)
            if (touchImageViewListener != null) {
                touchImageViewListener!!.onMove()
                return true
            }
            return true
        }

        override fun onScaleEnd(scaleGestureDetector: ScaleGestureDetector) {
            super.onScaleEnd(scaleGestureDetector)
            setState(State.NONE)
            var f: Float = currentZoom
            var z = true
            if (currentZoom > maxScale) {
                f = maxScale
            } else if (currentZoom < minScale) {
                f = minScale
            } else {
                z = false
            }
            val f2 = f
            if (z) {
                val myImageView = this@MyImageView
                compatPostOnAnimation(DoubleTapZoom(f2, (myImageView.viewWidth / 2).toFloat(), (viewHeight / 2).toFloat(), true))
            }
        }
    }

    fun scaleImage(d: Double, f: Float, f2: Float, z: Boolean) {
        var d = d
        val f3: Float
        val f4: Float
        if (z) {
            f3 = superMinScale
            f4 = superMaxScale
        } else {
            f3 = minScale
            f4 = maxScale
        }
        val f5 = currentZoom
        val f6 = (f5 * d).toFloat()
        currentZoom = f6
        if (f6 > f4) {
            currentZoom = f4
            d = (f4 / f5).toDouble()
        } else if (f6 < f3) {
            currentZoom = f3
            d = (f3 / f5).toDouble()
        }
        val f7 = d.toFloat()
        matrix!!.postScale(f7, f7, f, f2)
        fixScaleTrans()
    }

    private inner class DoubleTapZoom internal constructor(f: Float, f2: Float, f3: Float, z: Boolean) : Runnable {
        private val bitmapX: Float
        private val bitmapY: Float
        private val endTouch: PointF
        private val interpolator = AccelerateDecelerateInterpolator()
        private val startTime: Long
        private val startTouch: PointF
        private val startZoom: Float
        private val stretchImageToSuper: Boolean
        private val targetZoom: Float

        init {
            setState(State.ANIMATE_ZOOM)
            startTime = System.currentTimeMillis()
            startZoom = currentZoom
            targetZoom = f
            stretchImageToSuper = z
            val transformCoordTouchToBitmap = transformCoordTouchToBitmap(f2, f3, false)
            bitmapX = transformCoordTouchToBitmap.x
            val f4 = transformCoordTouchToBitmap.y
            bitmapY = f4
            startTouch = transformCoordBitmapToTouch(bitmapX, f4)
            endTouch = PointF((viewWidth / 2).toFloat(), (viewHeight / 2).toFloat())
        }

        override fun run() {
            val interpolate = interpolate()
            scaleImage(calculateDeltaScale(interpolate), bitmapX, bitmapY, stretchImageToSuper)
            translateImageToCenterTouchPosition(interpolate)
            fixScaleTrans()
            val myImageView = this@MyImageView
            myImageView.imageMatrix = myImageView.matrix
            if (touchImageViewListener != null) {
                touchImageViewListener!!.onMove()
            }
            if (interpolate < 1.0f) {
                compatPostOnAnimation(this)
            } else {
                setState(State.NONE)
            }
        }

        private fun translateImageToCenterTouchPosition(f: Float) {
            val f2 = startTouch.x + (endTouch.x - startTouch.x) * f
            val f3 = startTouch.y + f * (endTouch.y - startTouch.y)
            val transformCoordBitmapToTouch = transformCoordBitmapToTouch(bitmapX, bitmapY)
            matrix!!.postTranslate(f2 - transformCoordBitmapToTouch.x, f3 - transformCoordBitmapToTouch.y)
        }

        private fun interpolate(): Float {
            return interpolator.getInterpolation(Math.min(1.0f, (System.currentTimeMillis() - startTime).toFloat() / Companion.ZOOM_TIME))
        }

        private fun calculateDeltaScale(f: Float): Double {
            val f2 = startZoom
            return ((f2 + f * (targetZoom - f2)) / currentZoom).toDouble()
        }


    }

    fun transformCoordTouchToBitmap(f: Float, f2: Float, z: Boolean): PointF {
        matrix!!.getValues(m)
        val intrinsicWidth = drawable.intrinsicWidth.toFloat()
        val intrinsicHeight = drawable.intrinsicHeight.toFloat()
        val fArr = m
        val f3 = fArr!![2]
        val f4 = fArr[5]
        var imageWidth = (f - f3) * intrinsicWidth / imageWidth
        var imageHeight = (f2 - f4) * intrinsicHeight / imageHeight
        if (z) {
            imageWidth = imageWidth.coerceAtLeast(0.0f).coerceAtMost(intrinsicWidth)
            imageHeight = imageHeight.coerceAtLeast(0.0f).coerceAtMost(intrinsicHeight)
        }
        return PointF(imageWidth, imageHeight)
    }

    fun transformCoordBitmapToTouch(f: Float, f2: Float): PointF {
        matrix!!.getValues(m)
        return PointF(m!![2] + imageWidth * (f / drawable.intrinsicWidth), m!![5] + imageHeight * (f2 / drawable.intrinsicHeight))
    }

    private inner class Fling internal constructor(i: Int, i2: Int) : Runnable {
        var currX: Int
        var currY: Int
        var scroller: CompatScroller?

        init {
            val i3: Int
            val i4: Int
            val i5: Int
            val i6: Int
            setState(State.FLING)
            scroller = CompatScroller(context)
            matrix!!.getValues(m)
            val i7 = m!![2].toInt()
            val i8 = m!![5].toInt()
            if (imageWidth > viewWidth) {
                i3 = viewWidth - imageWidth.toInt()
                i4 = 0
            } else {
                i3 = i7
                i4 = i3
            }
            if (imageHeight > viewHeight) {
                i5 = viewHeight - imageHeight.toInt()
                i6 = 0
            } else {
                i5 = i8
                i6 = i5
            }
            scroller!!.fling(i7, i8, i, i2, i3, i4, i5, i6)
            currX = i7
            currY = i8
        }

        fun cancelFling() {
            if (scroller != null) {
                setState(State.NONE)
                scroller!!.forceFinished(true)
            }
        }

        override fun run() {
            if (touchImageViewListener != null) {
                touchImageViewListener!!.onMove()
            }
            if (scroller!!.isFinished) {
                scroller = null
            } else if (scroller!!.computeScrollOffset()) {
                val currX = scroller!!.currX
                val currY = scroller!!.currY
                val i = currX - this.currX
                val i2 = currY - this.currY
                this.currX = currX
                this.currY = currY
                matrix!!.postTranslate(i.toFloat(), i2.toFloat())
                fixTrans()
                val myImageView = this@MyImageView
                myImageView.imageMatrix = myImageView.matrix
                compatPostOnAnimation(this)
            }
        }
    }

    inner class CompatScroller(context: Context?) {
        var isPreGingerbread: Boolean
        var overScroller: OverScroller
        var scroller: Scroller? = null

        init {
            isPreGingerbread = false
            overScroller = OverScroller(context)
        }

        fun fling(i: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int) {
            if (isPreGingerbread) {
                scroller!!.fling(i, i2, i3, i4, i5, i6, i7, i8)
            } else {
                overScroller.fling(i, i2, i3, i4, i5, i6, i7, i8)
            }
        }

        fun forceFinished(z: Boolean) {
            if (isPreGingerbread) {
                scroller!!.forceFinished(z)
            } else {
                overScroller.forceFinished(z)
            }
        }

        val isFinished: Boolean
            get() = if (isPreGingerbread) {
                scroller!!.isFinished
            } else overScroller.isFinished

        fun computeScrollOffset(): Boolean {
            if (isPreGingerbread) {
                return scroller!!.computeScrollOffset()
            }
            overScroller.computeScrollOffset()
            return overScroller.computeScrollOffset()
        }

        val currX: Int
            get() = if (isPreGingerbread) {
                scroller!!.currX
            } else overScroller.currX
        val currY: Int
            get() = if (isPreGingerbread) {
                scroller!!.currY
            } else overScroller.currY
    }

    fun compatPostOnAnimation(runnable: Runnable?) {
        if (Build.VERSION.SDK_INT >= 16) {
            postOnAnimation(runnable)
        } else {
            postDelayed(runnable, 16L)
        }
    }

    inner class ZoomVariables(var scale: Float, var focusX: Float, var focusY: Float, var scaleType: ScaleType?)

    private fun printMatrixInfo() {
        val fArr = FloatArray(9)
        matrix!!.getValues(fArr)
        Log.d(DEBUG, "Scale: " + fArr[0] + " TransX: " + fArr[2] + " TransY: " + fArr[5])
    }

    companion object {
        private const val DEBUG = "DEBUG"
        private const val SUPER_MAX_MULTIPLIER = 1.0f
        private const val SUPER_MIN_MULTIPLIER = 1.0f
        private const val ZOOM_TIME = 500.0f

    }
}