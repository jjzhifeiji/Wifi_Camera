package com.joyhonest.sports_camera

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView

class StrokeTextView @JvmOverloads constructor(context: Context?, attributeSet: AttributeSet? = null, i: Int = 0) : AppCompatTextView(context, attributeSet, i) {
    private var outlineTextView: TextView

    init {
        outlineTextView = TextView(context, attributeSet, i)
        init(attributeSet)
    }

    private fun init(attributeSet: AttributeSet?) {
        val obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.StrokeTextView)
        val color = obtainStyledAttributes.getColor(0, -1)
        val dimension = obtainStyledAttributes.getDimension(1, 2.0f)
        val paint = outlineTextView.paint
        paint.strokeWidth = dimension
        paint.style = Paint.Style.STROKE
        outlineTextView.setTextColor(color)
        outlineTextView.gravity = gravity
    }

    override fun setLayoutParams(layoutParams: ViewGroup.LayoutParams) {
        super.setLayoutParams(layoutParams)
        outlineTextView!!.layoutParams = layoutParams
    }

    override fun onMeasure(i: Int, i2: Int) {
        super.onMeasure(i, i2)
        val text = outlineTextView!!.text
        if (text == null || text != getText()) {
            outlineTextView.text = getText()
            postInvalidate()
        }
        outlineTextView.measure(i, i2)
    }

    override fun onLayout(z: Boolean, i: Int, i2: Int, i3: Int, i4: Int) {
        super.onLayout(z, i, i2, i3, i4)
        outlineTextView!!.layout(i, i2, i3, i4)
    }

    override fun onDraw(canvas: Canvas) {
        outlineTextView!!.draw(canvas)
        super.onDraw(canvas)
    }
}