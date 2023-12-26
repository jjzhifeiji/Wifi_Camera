package com.joyhonest.sports_camera;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import cn.jzvd.JzvdStd;
public class MyJzvdStd extends JzvdStd {
    @Override
    public int getLayoutId() {
        return R.layout.jz_layout_std;
    }

    public MyJzvdStd(Context context) {
        super(context);
    }

    public MyJzvdStd(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    public void init(Context context) {
        super.init(context);
        this.fullscreenButton.setVisibility(View.GONE);
        this.replayTextView.setText("");
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.backButton.getLayoutParams();
        layoutParams.width = 0;
        layoutParams.height = 0;
        this.backButton.setLayoutParams(layoutParams);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.fullscreen) {
            Log.i("JZVD", "onClick: fullscreen button");
        } else if (id == R.id.start) {
            Log.i("JZVD", "onClick: start button");
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        super.onTouch(view, motionEvent);
        if (view.getId() == R.id.surface_container && motionEvent.getAction() == 1) {
            if (this.mChangePosition) {
                Log.i("JZVD", "Touch screen seek position");
            }
            if (this.mChangeVolume) {
                Log.i("JZVD", "Touch screen change volume");
                return false;
            }
            return false;
        }
        return false;
    }

    @Override
    public void startVideo() {
        super.startVideo();
        Log.i("JZVD", "startVideo");
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        super.onStopTrackingTouch(seekBar);
        Log.i("JZVD", "Seek position ");
    }

    @Override
    public void gotoFullscreen() {
        super.gotoFullscreen();
        Log.i("JZVD", "goto Fullscreen");
    }

    @Override
    public void gotoNormalScreen() {
        super.gotoNormalScreen();
        Log.i("JZVD", "quit Fullscreen");
    }

    @Override
    public void autoFullscreen(float f) {
        super.autoFullscreen(f);
        Log.i("JZVD", "auto Fullscreen");
    }

    @Override
    public void onClickUiToggle() {
        super.onClickUiToggle();
        Log.i("JZVD", "click blank");
    }

    @Override
    public void onStateNormal() {
        super.onStateNormal();
    }

    @Override
    public void onStatePreparing() {
        super.onStatePreparing();
    }

    @Override
    public void onStatePlaying() {
        super.onStatePlaying();
    }

    @Override
    public void onStatePause() {
        super.onStatePause();
    }

    @Override
    public void onStateError() {
        super.onStateError();
    }

    @Override
    public void onCompletion() {
        super.onCompletion();
    }

    @Override
    public void onStateAutoComplete() {
        super.onStateAutoComplete();
        Log.i("JZVD", "Auto complete");
    }

    @Override
    public void changeUiToNormal() {
        super.changeUiToNormal();
    }

    @Override
    public void changeUiToPreparing() {
        super.changeUiToPreparing();
    }

    @Override
    public void changeUiToPlayingShow() {
        super.changeUiToPlayingShow();
    }

    @Override
    public void changeUiToPlayingClear() {
        super.changeUiToPlayingClear();
    }

    @Override
    public void changeUiToPauseShow() {
        super.changeUiToPauseShow();
    }

    @Override
    public void changeUiToPauseClear() {
        super.changeUiToPauseClear();
    }

    @Override
    public void changeUiToComplete() {
        super.changeUiToComplete();
    }

    @Override
    public void changeUiToError() {
        super.changeUiToError();
    }

    @Override
    public void onInfo(int i, int i2) {
        super.onInfo(i, i2);
    }

    @Override
    public void onError(int i, int i2) {
        super.onError(i, i2);
    }
}
