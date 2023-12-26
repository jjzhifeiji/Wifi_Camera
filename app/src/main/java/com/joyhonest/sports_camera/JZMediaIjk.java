package com.joyhonest.sports_camera;

import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.Surface;

import java.io.IOException;

import cn.jzvd.JZMediaInterface;
import cn.jzvd.Jzvd;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.IjkTimedText;

public class JZMediaIjk extends JZMediaInterface implements IMediaPlayer.OnPreparedListener, IMediaPlayer.OnVideoSizeChangedListener, IMediaPlayer.OnCompletionListener, IMediaPlayer.OnErrorListener, IMediaPlayer.OnInfoListener, IMediaPlayer.OnBufferingUpdateListener, IMediaPlayer.OnSeekCompleteListener, IMediaPlayer.OnTimedTextListener {
    IjkMediaPlayer ijkMediaPlayer;

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }

    @Override
    public void onTimedText(IMediaPlayer iMediaPlayer, IjkTimedText ijkTimedText) {
    }

    public JZMediaIjk(Jzvd jzvd) {
        super(jzvd);
    }

    @Override
    public void start() {
        IjkMediaPlayer ijkMediaPlayer = this.ijkMediaPlayer;
        if (ijkMediaPlayer != null) {
            ijkMediaPlayer.start();
        }
    }

    @Override
    public void prepare() {
        release();
        this.mMediaHandlerThread = new HandlerThread("JZVD");
        this.mMediaHandlerThread.start();
        this.mMediaHandler = new Handler(this.mMediaHandlerThread.getLooper());
        this.handler = new Handler();
        this.mMediaHandler.post(new Runnable() {
            @Override
            public final void run() {
                lambda$prepare$0$JZMediaIjk();
            }
        });
    }

    public void lambda$prepare$0$JZMediaIjk() {
        IjkMediaPlayer ijkMediaPlayer = new IjkMediaPlayer();
        this.ijkMediaPlayer = ijkMediaPlayer;
        ijkMediaPlayer.setOption(4, "mediacodec", 0L);
        this.ijkMediaPlayer.setOption(4, "opensles", 0L);
        this.ijkMediaPlayer.setOption(4, "overlay-format", 842225234L);
        this.ijkMediaPlayer.setOption(4, "framedrop", 1L);
        this.ijkMediaPlayer.setOption(4, "start-on-prepared", 0L);
        this.ijkMediaPlayer.setOption(1, "http-detect-range-support", 0L);
        this.ijkMediaPlayer.setOption(2, "skip_loop_filter", 48L);
        this.ijkMediaPlayer.setOption(4, "max-buffer-size", 8388608L);
        this.ijkMediaPlayer.setOption(4, "enable-accurate-seek", 1L);
        this.ijkMediaPlayer.setOption(1, "fflags", "fastseek");
        this.ijkMediaPlayer.setOnPreparedListener(this);
        this.ijkMediaPlayer.setOnVideoSizeChangedListener(this);
        this.ijkMediaPlayer.setOnCompletionListener(this);
        this.ijkMediaPlayer.setOnErrorListener(this);
        this.ijkMediaPlayer.setOnInfoListener(this);
        this.ijkMediaPlayer.setOnBufferingUpdateListener(this);
        this.ijkMediaPlayer.setOnSeekCompleteListener(this);
        this.ijkMediaPlayer.setOnTimedTextListener(this);
        try {
            this.ijkMediaPlayer.setDataSource(this.jzvd.jzDataSource.getCurrentUrl().toString());
            this.ijkMediaPlayer.setAudioStreamType(3);
            this.ijkMediaPlayer.setScreenOnWhilePlaying(true);
            this.ijkMediaPlayer.prepareAsync();
            this.ijkMediaPlayer.setSurface(new Surface(this.jzvd.textureView.getSurfaceTexture()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pause() {
        this.ijkMediaPlayer.pause();
    }

    @Override
    public boolean isPlaying() {
        return this.ijkMediaPlayer.isPlaying();
    }

    @Override
    public void seekTo(long j) {
        this.ijkMediaPlayer.seekTo(j);
    }

    @Override
    public void release() {
        if (this.mMediaHandler == null || this.mMediaHandlerThread == null || this.ijkMediaPlayer == null) {
            return;
        }
        final HandlerThread handlerThread = this.mMediaHandlerThread;
        final IjkMediaPlayer ijkMediaPlayer = this.ijkMediaPlayer;
        JZMediaInterface.SAVED_SURFACE = null;
        this.mMediaHandler.post(new Runnable() {
            @Override
            public final void run() {
                JZMediaIjk.lambda$release$1(ijkMediaPlayer, handlerThread);
            }
        });
        this.ijkMediaPlayer = null;
    }

    public static void lambda$release$1(IjkMediaPlayer ijkMediaPlayer, HandlerThread handlerThread) {
        ijkMediaPlayer.setSurface(null);
        ijkMediaPlayer.release();
        handlerThread.quit();
    }

    @Override
    public long getCurrentPosition() {
        return this.ijkMediaPlayer.getCurrentPosition();
    }

    @Override
    public long getDuration() {
        IjkMediaPlayer ijkMediaPlayer = this.ijkMediaPlayer;
        if (ijkMediaPlayer != null) {
            return ijkMediaPlayer.getDuration();
        }
        return 0L;
    }

    @Override
    public void setVolume(float f, float f2) {
        this.ijkMediaPlayer.setVolume(f, f2);
    }

    @Override
    public void setSpeed(float f) {
        this.ijkMediaPlayer.setSpeed(f);
    }

    public void lambda$onPrepared$2$JZMediaIjk() {
        this.jzvd.onPrepared();
    }

    @Override
    public void onPrepared(IMediaPlayer iMediaPlayer) {
        this.handler.post(new Runnable() {
            @Override
            public final void run() {
                lambda$onPrepared$2$JZMediaIjk();
            }
        });
    }

    public void lambda$onVideoSizeChanged$3$JZMediaIjk(IMediaPlayer iMediaPlayer) {
        this.jzvd.onVideoSizeChanged(iMediaPlayer.getVideoWidth(), iMediaPlayer.getVideoHeight());
    }

    @Override
    public void onVideoSizeChanged(final IMediaPlayer iMediaPlayer, int i, int i2, int i3, int i4) {
        this.handler.post(new Runnable() {
            @Override
            public final void run() {
                lambda$onVideoSizeChanged$3$JZMediaIjk(iMediaPlayer);
            }
        });
    }

    public void lambda$onError$4$JZMediaIjk(int i, int i2) {
        this.jzvd.onError(i, i2);
    }

    @Override
    public boolean onError(IMediaPlayer iMediaPlayer, final int i, final int i2) {
        this.handler.post(new Runnable() {
            @Override
            public final void run() {
                lambda$onError$4$JZMediaIjk(i, i2);
            }
        });
        return true;
    }

    public void lambda$onInfo$5$JZMediaIjk(int i, int i2) {
        this.jzvd.onInfo(i, i2);
    }

    @Override
    public boolean onInfo(IMediaPlayer iMediaPlayer, final int i, final int i2) {
        this.handler.post(new Runnable() {
            @Override
            public final void run() {
                lambda$onInfo$5$JZMediaIjk(i, i2);
            }
        });
        return false;
    }

    public void lambda$onBufferingUpdate$6$JZMediaIjk(int i) {
        this.jzvd.setBufferProgress(i);
    }

    @Override
    public void onBufferingUpdate(IMediaPlayer iMediaPlayer, final int i) {
        this.handler.post(new Runnable() {
            @Override
            public final void run() {
                lambda$onBufferingUpdate$6$JZMediaIjk(i);
            }
        });
    }

    public void lambda$onSeekComplete$7$JZMediaIjk() {
        this.jzvd.onSeekComplete();
    }

    @Override
    public void onSeekComplete(IMediaPlayer iMediaPlayer) {
        this.handler.post(new Runnable() {
            @Override
            public final void run() {
                lambda$onSeekComplete$7$JZMediaIjk();
            }
        });
    }

    @Override
    public void setSurface(Surface surface) {
        this.ijkMediaPlayer.setSurface(surface);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
        if (SAVED_SURFACE == null) {
            SAVED_SURFACE = surfaceTexture;
            prepare();
            return;
        }
        this.jzvd.textureView.setSurfaceTexture(SAVED_SURFACE);
    }

    public void lambda$onCompletion$8$JZMediaIjk() {
        this.jzvd.onCompletion();
    }

    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {
        this.handler.post(new Runnable() {
            @Override
            public final void run() {
                lambda$onCompletion$8$JZMediaIjk();
            }
        });
    }
}
