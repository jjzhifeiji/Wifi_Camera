package com.joyhonest.sports_camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

public class StrokeTextView extends androidx.appcompat.widget.AppCompatTextView {
    private TextView outlineTextView;

    public StrokeTextView(Context context) {
        this(context, null);
    }

    public StrokeTextView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public StrokeTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.outlineTextView = null;
        this.outlineTextView = new TextView(context, attributeSet, i);
        init(attributeSet);
    }

    private void init(AttributeSet attributeSet) {
        TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R.styleable.StrokeTextView);
        int color = obtainStyledAttributes.getColor(0, -1);
        @SuppressLint("ResourceType")
        float dimension = obtainStyledAttributes.getDimension(1, 2.0f);
        TextPaint paint = this.outlineTextView.getPaint();
        paint.setStrokeWidth(dimension);
        paint.setStyle(Paint.Style.STROKE);
        this.outlineTextView.setTextColor(color);
        this.outlineTextView.setGravity(getGravity());
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams layoutParams) {
        super.setLayoutParams(layoutParams);
        this.outlineTextView.setLayoutParams(layoutParams);
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        CharSequence text = this.outlineTextView.getText();
        if (text == null || !text.equals(getText())) {
            this.outlineTextView.setText(getText());
            postInvalidate();
        }
        this.outlineTextView.measure(i, i2);
    }

    @Override
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        this.outlineTextView.layout(i, i2, i3, i4);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        this.outlineTextView.draw(canvas);
        super.onDraw(canvas);
    }
}
