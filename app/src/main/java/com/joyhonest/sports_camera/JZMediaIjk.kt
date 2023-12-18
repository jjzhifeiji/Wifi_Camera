package com.joyhonest.sports_camera

import android.graphics.SurfaceTexture
import android.os.Handler
import android.os.HandlerThread
import android.view.Surface
import cn.jzvd.JZMediaInterface
import cn.jzvd.Jzvd
import tv.danmaku.ijk.media.player.IMediaPlayer
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import tv.danmaku.ijk.media.player.IjkTimedText
import java.io.IOException

class JZMediaIjk(jzvd: Jzvd?) : JZMediaInterface(jzvd), IMediaPlayer.OnPreparedListener, IMediaPlayer.OnVideoSizeChangedListener, IMediaPlayer.OnCompletionListener, IMediaPlayer.OnErrorListener, IMediaPlayer.OnInfoListener, IMediaPlayer.OnBufferingUpdateListener, IMediaPlayer.OnSeekCompleteListener, IMediaPlayer.OnTimedTextListener {
    var ijkMediaPlayer: IjkMediaPlayer? = null
    override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
        return false
    }

    override fun onSurfaceTextureSizeChanged(surfaceTexture: SurfaceTexture, i: Int, i2: Int) {}
    override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {}
    override fun onTimedText(iMediaPlayer: IMediaPlayer, ijkTimedText: IjkTimedText) {}
    override fun start() {
        val ijkMediaPlayer = ijkMediaPlayer
        ijkMediaPlayer?.start()
    }

    override fun prepare() {
        release()
        mMediaHandlerThread = HandlerThread("JZVD")
        mMediaHandlerThread.start()
        mMediaHandler = Handler(mMediaHandlerThread.looper)
        handler = Handler()
        mMediaHandler.post { `lambda$prepare$0$JZMediaIjk`() }
    }

    fun `lambda$prepare$0$JZMediaIjk`() {
        val ijkMediaPlayer = IjkMediaPlayer()
        this.ijkMediaPlayer = ijkMediaPlayer
        ijkMediaPlayer.setOption(4, "mediacodec", 0L)
        this.ijkMediaPlayer!!.setOption(4, "opensles", 0L)
        this.ijkMediaPlayer!!.setOption(4, "overlay-format", 842225234L)
        this.ijkMediaPlayer!!.setOption(4, "framedrop", 1L)
        this.ijkMediaPlayer!!.setOption(4, "start-on-prepared", 0L)
        this.ijkMediaPlayer!!.setOption(1, "http-detect-range-support", 0L)
        this.ijkMediaPlayer!!.setOption(2, "skip_loop_filter", 48L)
        this.ijkMediaPlayer!!.setOption(4, "max-buffer-size", 8388608L)
        this.ijkMediaPlayer!!.setOption(4, "enable-accurate-seek", 1L)
        this.ijkMediaPlayer!!.setOption(1, "fflags", "fastseek")
        this.ijkMediaPlayer!!.setOnPreparedListener(this)
        this.ijkMediaPlayer!!.setOnVideoSizeChangedListener(this)
        this.ijkMediaPlayer!!.setOnCompletionListener(this)
        this.ijkMediaPlayer!!.setOnErrorListener(this)
        this.ijkMediaPlayer!!.setOnInfoListener(this)
        this.ijkMediaPlayer!!.setOnBufferingUpdateListener(this)
        this.ijkMediaPlayer!!.setOnSeekCompleteListener(this)
        this.ijkMediaPlayer!!.setOnTimedTextListener(this)
        try {
            this.ijkMediaPlayer!!.dataSource = jzvd.jzDataSource.currentUrl.toString()
            this.ijkMediaPlayer!!.setAudioStreamType(3)
            this.ijkMediaPlayer!!.setScreenOnWhilePlaying(true)
            this.ijkMediaPlayer!!.prepareAsync()
            this.ijkMediaPlayer!!.setSurface(Surface(jzvd.textureView.surfaceTexture))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun pause() {
        ijkMediaPlayer!!.pause()
    }

    override fun isPlaying(): Boolean {
        return ijkMediaPlayer!!.isPlaying
    }

    override fun seekTo(j: Long) {
        ijkMediaPlayer!!.seekTo(j)
    }

    override fun release() {
        if (mMediaHandler == null || mMediaHandlerThread == null || ijkMediaPlayer == null) {
            return
        }
        val handlerThread = mMediaHandlerThread
        val ijkMediaPlayer = ijkMediaPlayer
        SAVED_SURFACE = null
        mMediaHandler.post { `lambda$release$1`(ijkMediaPlayer, handlerThread) }
        this.ijkMediaPlayer = null
    }

    override fun getCurrentPosition(): Long {
        return ijkMediaPlayer!!.currentPosition
    }

    override fun getDuration(): Long {
        val ijkMediaPlayer = ijkMediaPlayer
        return ijkMediaPlayer?.duration ?: 0L
    }

    override fun setVolume(f: Float, f2: Float) {
        ijkMediaPlayer!!.setVolume(f, f2)
    }

    override fun setSpeed(f: Float) {
        ijkMediaPlayer!!.setSpeed(f)
    }

    fun `lambda$onPrepared$2$JZMediaIjk`() {
        jzvd.onPrepared()
    }

    override fun onPrepared(iMediaPlayer: IMediaPlayer) {
        handler.post { `lambda$onPrepared$2$JZMediaIjk`() }
    }

    fun `lambda$onVideoSizeChanged$3$JZMediaIjk`(iMediaPlayer: IMediaPlayer) {
        jzvd.onVideoSizeChanged(iMediaPlayer.videoWidth, iMediaPlayer.videoHeight)
    }

    override fun onVideoSizeChanged(iMediaPlayer: IMediaPlayer, i: Int, i2: Int, i3: Int, i4: Int) {
        handler.post { `lambda$onVideoSizeChanged$3$JZMediaIjk`(iMediaPlayer) }
    }

    fun `lambda$onError$4$JZMediaIjk`(i: Int, i2: Int) {
        jzvd.onError(i, i2)
    }

    override fun onError(iMediaPlayer: IMediaPlayer, i: Int, i2: Int): Boolean {
        handler.post { `lambda$onError$4$JZMediaIjk`(i, i2) }
        return true
    }

    fun `lambda$onInfo$5$JZMediaIjk`(i: Int, i2: Int) {
        jzvd.onInfo(i, i2)
    }

    override fun onInfo(iMediaPlayer: IMediaPlayer, i: Int, i2: Int): Boolean {
        handler.post { `lambda$onInfo$5$JZMediaIjk`(i, i2) }
        return false
    }

    fun `lambda$onBufferingUpdate$6$JZMediaIjk`(i: Int) {
        jzvd.setBufferProgress(i)
    }

    override fun onBufferingUpdate(iMediaPlayer: IMediaPlayer, i: Int) {
        handler.post { `lambda$onBufferingUpdate$6$JZMediaIjk`(i) }
    }

    fun `lambda$onSeekComplete$7$JZMediaIjk`() {
        jzvd.onSeekComplete()
    }

    override fun onSeekComplete(iMediaPlayer: IMediaPlayer) {
        handler.post { `lambda$onSeekComplete$7$JZMediaIjk`() }
    }

    override fun setSurface(surface: Surface) {
        ijkMediaPlayer!!.setSurface(surface)
    }

    override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, i: Int, i2: Int) {
        if (SAVED_SURFACE == null) {
            SAVED_SURFACE = surfaceTexture
            prepare()
            return
        }
        jzvd.textureView.surfaceTexture = SAVED_SURFACE
    }

    fun `lambda$onCompletion$8$JZMediaIjk`() {
        jzvd.onCompletion()
    }

    override fun onCompletion(iMediaPlayer: IMediaPlayer) {
        handler.post { `lambda$onCompletion$8$JZMediaIjk`() }
    }

    companion object {
        fun `lambda$release$1`(ijkMediaPlayer: IjkMediaPlayer?, handlerThread: HandlerThread) {
            ijkMediaPlayer!!.setSurface(null)
            ijkMediaPlayer.release()
            handlerThread.quit()
        }
    }
}