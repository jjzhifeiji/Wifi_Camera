package com.joyhonest.sports_camera;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
public class MyVideoPlayView extends RelativeLayout {
    private Button btn_back;
    private Button btn_pause;
    private ImageView disp_ImageView;
    private ProgressBar progressBar;
    private TextView time_View1;
    private TextView time_View2;

    public MyVideoPlayView(Context context) {
        this(context, null);
    }

    public MyVideoPlayView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public MyVideoPlayView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public MyVideoPlayView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        init();
    }

    private int dip2px(Context context, float f) {
        return (int) ((f * context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    private void init() {
        Context context = getContext();
        this.disp_ImageView = new ImageView(context);
        this.btn_back = new Button(context);
        this.btn_pause = new Button(context);
        this.progressBar = new ProgressBar(context);
        this.time_View1 = new TextView(context);
        this.time_View2 = new TextView(context);
        int dip2px = dip2px(context, 40.0f);
        addView(this.disp_ImageView, new LayoutParams(-1, -1));
        LayoutParams layoutParams = new LayoutParams(dip2px, dip2px);
        layoutParams.leftMargin = dip2px(context, 8.0f);
        layoutParams.topMargin = dip2px(context, 8.0f);
        addView(this.btn_back, layoutParams);
    }
}
