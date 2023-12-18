package com.joyhonest.sports_camera

import android.content.Context
import android.os.Environment
import android.os.StatFs
import tv.danmaku.ijk.media.player.IjkMediaMeta
import java.io.BufferedReader
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader

object Storage {
    const val VENDOR = "JH-WIFI-FPV"
    val phoneCardPath: String
        get() = Environment.getDataDirectory().path
    val normalSDCardPath: String
        get() = Environment.getExternalStorageDirectory().path
    val sDCardPathEx: String
        get() {
            var str = String()
            try {
                val bufferedReader = BufferedReader(InputStreamReader(Runtime.getRuntime().exec("mount").inputStream))
                while (true) {
                    val readLine = bufferedReader.readLine()
                    if (readLine == null) {
                        break
                    } else if (!readLine.contains("secure") && !readLine.contains("asec") && (readLine.contains("fat") || readLine.contains("fuse"))) {
                        val split = readLine.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        if (split != null && split.size > 1 && getTotalSize(split[1]) > IjkMediaMeta.AV_CH_STEREO_RIGHT && normalSDCardPath != split[1]) {
                            str = str + split[1] + ";"
                        }
                    }
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e2: IOException) {
                e2.printStackTrace()
            }
            return str
        }

    fun getTotalSize(str: String?): Long {
        return try {
            val statFs = StatFs(File(str).path)
            statFs.blockSizeLong * statFs.blockCountLong
        } catch (e: Exception) {
            e.printStackTrace()
            0L
        }
    }

    fun getAvailableSize(str: String?): Long {
        return try {
            val statFs = StatFs(File(str).path)
            val blockSizeLong = statFs.blockSizeLong * statFs.availableBlocksLong
            statFs.blockSizeLong
            statFs.blockCountLong
            blockSizeLong
        } catch (e: Exception) {
            e.printStackTrace()
            0L
        }
    }

    fun dip2px(context: Context, f: Float): Int {
        return (f * context.resources.displayMetrics.density + 0.5f).toInt()
    }

    fun dip2pxF(context: Context, f: Float): Float {
        return f * context.resources.displayMetrics.density
    }

    fun px2dip(context: Context, f: Float): Int {
        return (f / context.resources.displayMetrics.density + 0.5f).toInt()
    }
}