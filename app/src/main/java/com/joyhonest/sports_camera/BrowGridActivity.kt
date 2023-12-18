package com.joyhonest.sports_camera

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.PixelFormat
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.ScaleAnimation
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.GridView
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.joyhonest.wifination.GP4225_Device.GetFiles
import com.joyhonest.wifination.GP4225_Device.MyFile
import com.joyhonest.wifination.jh_dowload_callback
import com.joyhonest.wifination.wifination
import org.simple.eventbus.EventBus
import org.simple.eventbus.Subscriber
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.Collections
import java.util.concurrent.locks.ReentrantLock

class BrowGridActivity : AppCompatActivity(), View.OnClickListener {

    private val TAG = "BrowGridActivity"


    private var Delete_AlertView: ConstraintLayout? = null
    private var Title_textview: TextView? = null
    private var alertDialog: AlertDialog? = null
    private var btn_back: Button? = null
    private var btn_cancel1: Button? = null
    private var btn_del: Button? = null
    private var btn_edit: Button? = null
    private var btn_selectall: Button? = null
    private var downliad_node: MyNode? = null
    private var download_List: MutableList<MyNode> = mutableListOf()
    private var gridView: GridView? = null
    private var mAsker: PermissionAsker? = null
    private var mListPhoto: List<Uri>? = null
    private var myAdapter: MyAdapter? = null
    private var nodes: MutableList<MyNode> = mutableListOf()
    private var progressBar: ImageView? = null
    private var sLine2: TextView? = null
    private var sLine3: TextView? = null
    private var nodeAction: MyNode? = null
    private val lock = ReentrantLock()
    private val lock_down = ReentrantLock()
    private var bExit = false
    private var bDeletting = false
    private var bEdit = false
    var nReadStart = 1
    var nCountFile = 0
    var nReadFiles = 0
    var nReadFiles_A = 0
    private var nNeedAction = -1

    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_brow_grid)
        MyApp.F_makeFullScreen(this)
        Title_textview = findViewById<View>(R.id.Title_textview) as TextView
        sLine2 = findViewById<View>(R.id.sLine2) as TextView
        val textView = findViewById<View>(R.id.sLine3) as TextView
        sLine3 = textView
        textView.text = ""
        val imageView = findViewById<View>(R.id.progressBar) as ImageView
        progressBar = imageView
        imageView.visibility = View.VISIBLE
        (progressBar!!.drawable as AnimationDrawable).start()

        gridView = findViewById<View>(R.id.gridView) as GridView
        val constraintLayout = findViewById<View>(R.id.Delete_AlertView) as ConstraintLayout
        Delete_AlertView = constraintLayout
        constraintLayout.visibility = View.INVISIBLE
        btn_cancel1 = findViewById<View>(R.id.btn_cancel1) as Button
        btn_edit = findViewById<View>(R.id.btn_edit) as Button
        btn_del = findViewById<View>(R.id.btn_del) as Button
        btn_back = findViewById<View>(R.id.btn_back) as Button
        btn_selectall = findViewById<View>(R.id.btn_selectall) as Button
        btn_edit!!.setOnClickListener(this)
        btn_del!!.setOnClickListener(this)
        btn_back!!.setOnClickListener(this)
        btn_selectall!!.setOnClickListener(this)
        Delete_AlertView!!.setOnClickListener(this)
        btn_del!!.visibility = View.INVISIBLE
        btn_selectall!!.visibility = View.INVISIBLE
        btn_back!!.visibility = View.VISIBLE
        wifination.naStartRead20000_20001()
        findViewById<View>(R.id.btn_ok).setOnClickListener(this)
        btn_cancel1!!.setOnClickListener(this)
        if (MyApp.bBROW_SD && MyApp.BROW_TYPE == 1) {
            myAdapter = MyAdapter(this, R.layout.my_grid_node_sd, nodes)
            gridView!!.numColumns = 2
            gridView!!.horizontalSpacing = Storage.dip2px(this, 25.0f)
            _Init_Theard().start()
        } else {
            myAdapter = MyAdapter(this, R.layout.my_grid_node, nodes)
            gridView!!.post {
                gridView!!.numColumns = gridView!!.width / Storage.dip2px(this@BrowGridActivity, 80.0f) - 1
                _Init_Theard().start()
            }
        }
        gridView!!.adapter = myAdapter
        if (MyApp.BROW_TYPE == 0) {
            Title_textview!!.setText(R.string.PHOTOS)
        } else {
            Title_textview!!.setText(R.string.VIDEOS)
        }
        mAsker = PermissionAsker(10, {
            MyApp.F_CreateLocalDir("")
            DoAction()
        }) { F_DispDialg() }
        val string = resources.getString(R.string.warning)
        val string2 = resources.getString(R.string.The_necessary_permission_denied)
        alertDialog = AlertDialog.Builder(this)
                .setTitle(string)
                .setMessage(string2)
                .setNegativeButton(resources.getString(R.string.OK)) { dialogInterface, i -> PermissionPageUtils(this@BrowGridActivity).jumpPermissionPage() }
                .create()
    }

    fun DoAction() {
        if (nNeedAction == 0) {
            nNeedAction = -1
            val myNode = nodeAction
            if (myNode != null) {
                if (F_Add2DownloadList(myNode)) {
                    lock_down.lock()
                    val size = download_List!!.size
                    lock_down.unlock()
                    if (size == 1) {
                        F_DownLoad_Start()
                    }
                }
                myAdapter!!.notifyDataSetChanged()
            }
        }
        if (nNeedAction == 1) {
            nNeedAction = -1
            val myNode2 = nodeAction
            if (myNode2 != null) {
                if (F_Add2DownloadList(myNode2)) {
                    lock_down.lock()
                    val size2 = download_List!!.size
                    lock_down.unlock()
                    if (size2 == 1) {
                        F_DownLoad_Start()
                    }
                }
                myAdapter!!.notifyDataSetChanged()
            }
        }
    }

    fun F_DispDialg() {
        alertDialog!!.show()
    }

    override fun onRequestPermissionsResult(i: Int, strArr: Array<String>, iArr: IntArray) {
        super.onRequestPermissionsResult(i, strArr, iArr)
        mAsker!!.onRequestPermissionsResult(iArr)
    }

    private fun F_SetEditMode(z: Boolean) {
        bEdit = z
        if (z) {
            btn_del!!.visibility = View.VISIBLE
            btn_selectall!!.visibility = View.VISIBLE
            btn_back!!.visibility = View.INVISIBLE
            btn_edit!!.setText(R.string.Cancel)
            return
        }
        btn_del!!.visibility = View.INVISIBLE
        btn_selectall!!.visibility = View.INVISIBLE
        btn_back!!.visibility = View.VISIBLE
        if (nodes!!.size > 0) {
            for (myNode in nodes!!) {
                myNode!!.nOp = 0
            }
            myAdapter!!.notifyDataSetChanged()
        }
        Delete_AlertView!!.visibility = View.INVISIBLE
        bDeletting = false
        btn_edit!!.setText(R.string.Edit)
    }

    private fun F_AlertSetStyle(z: Boolean) {
        if (z) {
            sLine3!!.text = ""
            val layoutParams = btn_cancel1!!.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.matchConstraintPercentWidth = 0.49f
            btn_cancel1!!.layoutParams = layoutParams
            return
        }
        sLine3!!.text = ""
        val layoutParams2 = btn_cancel1!!.layoutParams as ConstraintLayout.LayoutParams
        layoutParams2.matchConstraintPercentWidth = 1.0f
        btn_cancel1!!.layoutParams = layoutParams2
    }

    public override fun onDestroy() {
        super.onDestroy()
        if (MyApp.bBROW_SD) {
            wifination.naDisConnectedTCP()
        }
    }

    override fun onClick(view: View) {
        MyApp.PlayBtnVoice()
        when (view.id) {
            R.id.btn_back -> {
                onBackPressed()
                return
            }

            R.id.btn_cancel1 -> {
                bExit = true
                F_SetEditMode(false)
                Delete_AlertView!!.visibility = View.INVISIBLE
                return
            }

            R.id.btn_del -> {
                if (F_GetSelectFiles() > 0) {
                    sLine2!!.setText(R.string.Do_you_sure_delete)
                    F_AlertSetStyle(true)
                    Delete_AlertView!!.visibility = View.VISIBLE
                    return
                }
                return
            }

            R.id.btn_edit -> {
                if (!bEdit) {
                    if (nodes!!.size == 0) {
                        return
                    }
                    F_SetEditMode(true)
                    for (myNode in nodes!!) {
                        myNode!!.nOp = 1
                    }
                    myAdapter!!.notifyDataSetChanged()
                    return
                }
                F_SetEditMode(false)
                return
            }

            R.id.btn_ok -> {
                sLine2!!.setText(R.string.deleting)
                F_AlertSetStyle(false)
                F_DeleteSelectedFiles()
                return
            }

            R.id.btn_selectall -> {
                if (F_isSelectAll()) {
                    for (myNode2 in nodes!!) {
                        myNode2!!.nOp = 1
                    }
                } else {
                    for (myNode3 in nodes!!) {
                        myNode3!!.nOp = 2
                    }
                }
                myAdapter!!.notifyDataSetChanged()
                return
            }

            else -> return
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        bExit = true
    }

    public override fun onResume() {
        super.onResume()
        MyApp.F_makeFullScreen(this)
    }

    private fun F_isSelectAll(): Boolean {
        var i = 0
        for (myNode in nodes!!) {
            if (myNode!!.nOp == 2) {
                i++
            }
        }
        return i != 0 && i == nodes!!.size
    }

    private fun F_DeleteSelectedFiles() {
        if (bDeletting) {
            return
        }
        F_DeleteSelectedFiles_A()
    }

    fun F_DeleteSelectedFiles_A() {
        bDeletting = true
        if (MyApp.bBROW_SD) {
            for (myNode in nodes!!) {
                if (myNode!!.nOp == 2) {
                    val str = myNode.sUrl
                    if (myNode.sPath.length > 5) {
                        MyApp.DeleteImage(myNode.sPath)
                    }
                    if (str.length > 2) {
                        wifination.na4225_DeleteFile("", str)
                        sLine3!!.text = str
                        return
                    }
                }
            }
            return
        }
        val it: Iterator<MyNode?> = nodes!!.iterator()
        while (true) {
            if (!it.hasNext()) {
                break
            } else if (bExit) {
                bExit = false
                break
            } else {
                val next = it.next()
                if (next!!.nOp == 2) {
                    val str2 = next.sPath
                    val F_GetTmpFileName = F_GetTmpFileName(str2)
                    try {
                        MyApp.DeleteImage(str2)
                        val file = File(F_GetTmpFileName)
                        if (file.exists() && file.isFile) {
                            file.delete()
                        }
                    } catch (unused: Exception) {
                    }
                }
            }
        }
        lock.lock()
        try {
            val it2 = nodes!!.iterator()
            while (it2.hasNext()) {
                if (it2.next()!!.nOp == 2) {
                    it2.remove()
                }
            }
            lock.unlock()
            EventBus.getDefault().post("", "Delete_OK")
            bDeletting = false
        } catch (th: Throwable) {
            lock.unlock()
            throw th
        }
    }

    private inner class Delete_Theard private constructor() : Thread() {
        override fun run() {
            F_DeleteSelectedFiles_A()
        }
    }

    private inner class _Init_Theard : Thread() {
        override fun run() {
            F_Init()
        }
    }

    fun F_Init() {
        if (MyApp.bBROW_SD) {
            MyApp.F_OpenCamera(false)
            SystemClock.sleep(200L)
            wifination.naStartRead20000_20001()
            wifination.na4225_SetMode(1.toByte())
            wifination.naStartRead20000_20001()
            wifination.na4225_ReadDeviceInfo()
            F_GetAllSD()
            return
        }
        F_GetAllLocal()
    }

    private fun getFileName(str: String): String? {
        val lastIndexOf = str.lastIndexOf("/")
        val lastIndexOf2 = str.lastIndexOf(".")
        return if (lastIndexOf == -1 || lastIndexOf2 == -1) {
            null
        } else str.substring(lastIndexOf + 1)
    }

    private fun F_SaveBitmap(bitmap: Bitmap?, str: String) {
        if (bitmap == null) {
            return
        }
        val file = File(str)
        if (file.exists()) {
            file.delete()
        }
        try {
            val fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e2: IOException) {
            e2.printStackTrace()
        }
    }

    private fun GetSuonuitu(uri: Uri): Bitmap? {
        return try {
            val openFileDescriptor = contentResolver.openFileDescriptor(uri, "r")
            val fileDescriptor = openFileDescriptor!!.fileDescriptor
            val options = BitmapFactory.Options()
            var i = 1
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options)
            options.inJustDecodeBounds = false
            val i2 = (options.outHeight / 100.0f).toInt()
            if (i2 > 0) {
                i = i2
            }
            options.inSampleSize = i
            val decodeFileDescriptor = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options)
            openFileDescriptor.close()
            decodeFileDescriptor
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getVideoThumbnail(uri: Uri): Bitmap? {
        val mediaMetadataRetriever = MediaMetadataRetriever()
        var bitmap: Bitmap? = null
        return try {
            try {
                try {
                    mediaMetadataRetriever.setDataSource(this, uri)
                    bitmap = ThumbnailUtils.extractThumbnail(mediaMetadataRetriever.getFrameAtTime(1L), 100, 100)
                    mediaMetadataRetriever.release()
                } catch (e: RuntimeException) {
                    e.printStackTrace()
                    mediaMetadataRetriever.release()
                }
            } catch (e2: RuntimeException) {
                e2.printStackTrace()
            }
            if (bitmap == null) {
                val str: String = MyApp.sSDPath + "/tad.tmp"
                if (MyApp.F_GetFile4Gallery(uri, str)) {
                    val naGetVideoThumbnail = wifination.naGetVideoThumbnail(str)
                    File(str).delete()
                    return naGetVideoThumbnail
                }
                return bitmap
            }
            bitmap
        } catch (th: Throwable) {
            try {
                mediaMetadataRetriever.release()
            } catch (e3: RuntimeException) {
                e3.printStackTrace()
            }
            throw th
        }
    }

    private fun getVideoThumbnail(str: String): Bitmap? {
        var bitmap: Bitmap?
        val str2 = cacheDir.toString() + "/" + getFileName(str) + ".v_thb.png"
        var decodeFile = BitmapFactory.decodeFile(str2)
        if (decodeFile != null) {
            return decodeFile
        }
        val mediaMetadataRetriever = MediaMetadataRetriever()
        return try {
            try {
                mediaMetadataRetriever.setDataSource(str)
                decodeFile = mediaMetadataRetriever.getFrameAtTime(1L)
                bitmap = ThumbnailUtils.extractThumbnail(decodeFile, 100, 100)
                try {
                    mediaMetadataRetriever.release()
                } catch (e: RuntimeException) {
                    e.printStackTrace()
                }
            } catch (e2: RuntimeException) {
                e2.printStackTrace()
                try {
                    mediaMetadataRetriever.release()
                } catch (e3: RuntimeException) {
                    e3.printStackTrace()
                }
                bitmap = decodeFile
            }
            F_SaveBitmap(bitmap, str2)
            bitmap
        } catch (th: Throwable) {
            try {
                mediaMetadataRetriever.release()
            } catch (e4: RuntimeException) {
                e4.printStackTrace()
            }
            throw th
        }
    }

    private fun F_GetTmpFileName(str: String): String {
        val fileName = getFileName(str)
        return "$cacheDir/$fileName.thb.png"
    }

    private fun GetSuonuitu_SD(str: String): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(str, options)
        options.inJustDecodeBounds = false
        val i = (options.outHeight / 100.0f).toInt() + 1
        options.inSampleSize = if (i > 0) i else 1
        return BitmapFactory.decodeFile(str, options)
    }

    private fun GetSuonuitu(str: String): Bitmap {
        val F_GetTmpFileName = F_GetTmpFileName(str)
        val decodeFile = BitmapFactory.decodeFile(F_GetTmpFileName)
        if (decodeFile != null) {
            return decodeFile
        }
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(str, options)
        options.inJustDecodeBounds = false
        val i = (options.outHeight / 100.0f).toInt() + 1
        options.inSampleSize = if (i > 0) i else 1
        val decodeFile2 = BitmapFactory.decodeFile(str, options)
        F_SaveBitmap(decodeFile2, F_GetTmpFileName)
        return decodeFile2
    }

    private fun F_GetAllSD() {
        nodes!!.clear()
        var i = 0
        while (wifination.gp4225_Device.nMode != 1 && i + 1.also { i = it } < 5) {
            wifination.na4225_SetMode(1.toByte())
            SystemClock.sleep(100L)
            wifination.na4225_ReadStatus()
        }
        runOnUiThread {
            progressBar!!.visibility = View.INVISIBLE
            (progressBar!!.drawable as AnimationDrawable).stop()
        }
        if (MyApp.BROW_TYPE == 1) {
            val i2 = wifination.gp4225_Device.VideosCount
            nCountFile = i2
            if (i2 > 0) {
                nReadStart = 1
                nReadFiles = 0
                nReadFiles_A = 0
                wifination.na4225_GetFileList(1, 1, 20)
                return
            }
            return
        }
        val i3 = wifination.gp4225_Device.PhotoCount
        nCountFile = i3
        if (i3 > 0) {
            nReadStart = 1
            nReadFiles = 0
            nReadFiles_A = 0
            wifination.na4225_GetFileList(3, 1, 20)
        }
    }

    private fun FindFile(str: String): Boolean {
        for (myNode in nodes!!) {
            if (myNode!!.sPath.equals(str, ignoreCase = true)) {
                return true
            }
        }
        return false
    }

    private fun F_Add2DownloadList(myNode: MyNode): Boolean {
        val z: Boolean
        var z2 = false
        if (download_List != null) {
            lock_down.lock()
            val it: Iterator<MyNode?> = download_List!!.iterator()
            while (true) {
                if (!it.hasNext()) {
                    z = false
                    break
                } else if (it.next()!!.sUrl.equals(myNode.sUrl, ignoreCase = true)) {
                    z = true
                    break
                }
            }
            if (!z) {
                myNode.nStatus = 3
                download_List!!.add(myNode)
                z2 = true
            } else if (myNode.nStatus == 3) {
                myNode.nStatus = 0
                download_List!!.remove(myNode)
            }
            lock_down.unlock()
        }
        return z2
    }

    private fun F_DownLoad_Start() {
        lock_down.lock()
        val myNode = if (download_List!!.size != 0) download_List!![0] else null
        lock_down.unlock()
        if (myNode != null && MyApp.sSDPath.length > 4) {
            val str = myNode.sUrl
            myNode.nStatus = 1
            downliad_node = myNode
            wifination.na4225StartDonwLoad("", str, myNode.nLength, MyApp.sSDPath + "/" + str)
        }
    }

    private fun F_Download_Next() {
        val myNode: MyNode?
        lock_down.lock()
        if (download_List!!.size > 1) {
            wifination.naDisConnectedTCP()
            myNode = download_List!![1]
            download_List!!.removeAt(0)
        } else {
            if (download_List!!.size == 1) {
                wifination.naDisConnectedTCP()
                download_List!!.removeAt(0)
            }
            myNode = null
        }
        lock_down.unlock()
        if (myNode == null) {
            return
        }
        val str = myNode.sUrl
        myNode.nStatus = 1
        downliad_node = myNode
        wifination.na4225StartDonwLoad("", str, myNode.nLength, MyApp.sSDPath + "/" + str)
    }

    @Subscriber(tag = "DownloadFile")
    private fun DownloadFile(jh_dowload_callbackVar: jh_dowload_callback) {
        if (jh_dowload_callbackVar.nError != 0) {
            val str = jh_dowload_callbackVar.sFileName
        } else {
            val str2 = jh_dowload_callbackVar.sFileName
            val i = jh_dowload_callbackVar.nPercentage
        }
        if (downliad_node != null) {
            if (jh_dowload_callbackVar.nError == 0) {
                if (downliad_node!!.nPre.toInt() != jh_dowload_callbackVar.nPercentage) {
                    downliad_node!!.nPre = jh_dowload_callbackVar.nPercentage.toLong()
                    if (jh_dowload_callbackVar.nPercentage >= 1000) {
                        downliad_node!!.nStatus = 2
                        F_SetSDFile_BitmpNode(downliad_node)
                        downliad_node = null
                        F_Download_Next()
                    }
                    updateItem(jh_dowload_callbackVar.sFileName)
                    return
                }
                return
            }
            downliad_node!!.nStatus = 0
            downliad_node = null
            F_Download_Next()
            myAdapter!!.notifyDataSetChanged()
        }
    }

    private fun updateItem(str: String) {
        val it: Iterator<MyNode?> = nodes!!.iterator()
        var i = 0
        while (true) {
            if (!it.hasNext()) {
                i = -1
                break
            } else if (it.next()!!.sUrl.equals(str, ignoreCase = true)) {
                break
            } else {
                i++
            }
        }
        if (i < 0) {
            return
        }
        val firstVisiblePosition = gridView!!.firstVisiblePosition
        val lastVisiblePosition = gridView!!.lastVisiblePosition
        if (i < firstVisiblePosition || i > lastVisiblePosition) {
            return
        }
        myAdapter!!.getView(i, gridView!!.getChildAt(i - firstVisiblePosition), gridView!!)
    }

    private fun F_SetSDFile_BitmpNode(myNode: MyNode?) {
        val i: Int
        val naGetVideoThumbnail: Bitmap?
        if (myNode == null) {
            return
        }
        val str: String = MyApp.sSDPath + "/" + myNode.sUrl
        val file = File(str)
        if (file.exists()) {
            if (file.length() == myNode.nLength.toLong()) {
                myNode.sPath = str
                if (MyApp.BROW_TYPE == 0) {
                    naGetVideoThumbnail = GetSuonuitu(str)
                    val str2: String = MyApp.sSDPath + "/" + myNode.sSDMp4name
                    myNode.sPath = ""
                    File(str).renameTo(File(str2))
                    MyApp.F_Save2ToGallery_A(str2, true, true)
                    val F_CheckIsExit: Uri? = MyApp.F_CheckIsExit(myNode.sSDMp4name)
                    if (F_CheckIsExit != null) {
                        myNode.sPath = F_CheckIsExit.toString()
                    }
                } else {
                    val str3: String = MyApp.sSDPath + "/" + myNode.sSDMp4name
                    i = if (MyApp.nModel == 2) {
                        wifination.F_Convert(str, str3)
                    } else {
                        File(str).renameTo(File(str3))
                        0
                    }
                    if (i == 0) {
                        try {
                            val file2 = File(str)
                            if (file2.exists()) {
                                file2.delete()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        myNode.sPath = str3
                    }
                    naGetVideoThumbnail = wifination.naGetVideoThumbnail(str3)
                    if (naGetVideoThumbnail != null) {
                        MyApp.F_Save2ToGallery_A(str3, false, true)
                        val F_CheckIsExit2: Uri? = MyApp.F_CheckIsExit(myNode.sSDMp4name)
                        if (F_CheckIsExit2 != null) {
                            myNode.sPath = F_CheckIsExit2.toString()
                        }
                    }
                }
                myNode.bitmap = naGetVideoThumbnail
                return
            }
            myNode.sPath = ""
            return
        }
        myNode.sPath = ""
    }

    @Subscriber(tag = "GP4225_RevFiles")
    private fun GP4225_RevFile(r14: GetFiles) {

        Log.d(TAG, "GP4225_RevFile: $r14")

        throw UnsupportedOperationException("Method not decompiled: com.joyhonest.sports_camera.BrowGridActivity.GP4225_RevFile(com.joyhonest.wifination.GP4225_Device.GetFiles):void");

    }

    private fun F_GetAllLocal() {
        val list = nodes
        list?.clear()
        if (MyApp.BROW_TYPE == 0) {
            val F_GetAllLocalFiles: List<Uri> = MyApp.F_GetAllLocalFiles(true)
            mListPhoto = F_GetAllLocalFiles
            Collections.sort(F_GetAllLocalFiles, MapComparator())
            val list2 = mListPhoto
            if (list2 != null) {
                for (uri in list2) {
                    if (bExit) {
                        break
                    }
                    val GetSuonuitu = GetSuonuitu(uri)
                    val myNode = MyNode(0)
                    myNode.bitmap = GetSuonuitu
                    myNode.sPath = uri.toString()
                    nodes!!.add(myNode)
                    EventBus.getDefault().post("", "Update_Grid")
                }
            }
        } else {
            val F_GetAllLocalFiles2: List<Uri> = MyApp.F_GetAllLocalFiles(false)
            mListPhoto = F_GetAllLocalFiles2
            Collections.sort(F_GetAllLocalFiles2, MapComparator())
            val list3 = mListPhoto
            if (list3 != null) {
                for (uri2 in list3) {
                    if (bExit) {
                        break
                    }
                    val videoThumbnail = getVideoThumbnail(uri2)
                    val myNode2 = MyNode(0)
                    myNode2.bitmap = videoThumbnail
                    myNode2.sPath = uri2.toString()
                    nodes!!.add(myNode2)
                    EventBus.getDefault().post("", "Update_Grid")
                }
            }
        }
        EventBus.getDefault().post("", "Update_Grid")
    }

    fun F_Checked() {
        if (MyApp.isAndroidQ) {
            DoAction()
        } else {
            mAsker!!.askPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE")
        }
    }

    inner class MapComparator internal constructor() : Comparator<Uri> {
        override fun compare(uri: Uri, uri2: Uri): Int {
            return uri2.compareTo(uri)
        }
    }

    inner class MyAdapter internal constructor(private val context: Context, private val viewResourceId: Int, private val mfilelist: List<MyNode?>?) : BaseAdapter() {
        private val mInflater: LayoutInflater
        override fun getItemId(i: Int): Long {
            return i.toLong()
        }

        init {
            mInflater = LayoutInflater.from(context)
        }

        override fun getCount(): Int {
            lock.lock()
            val size = mfilelist!!.size
            lock.unlock()
            return size
        }

        override fun getItem(i: Int): Any {
            return Integer.valueOf(i)
        }

        override fun getView(i: Int, view: View, viewGroup: ViewGroup): View {
            val myViewHolder: MyViewHolder
            val view2: View
            lock.lock()
            return try {
                val myNode = if (i < mfilelist!!.size) mfilelist[i] else null
                if (view == null) {
                    myViewHolder = MyViewHolder()
                    view2 = mInflater.inflate(viewResourceId, null as ViewGroup?)
                    myViewHolder.progressBar = view2.findViewById<View>(R.id.Grid_progressBar1) as ProgressBar
                    myViewHolder.item_view = view2.findViewById<View>(R.id.item_view) as RelativeLayout
                    myViewHolder.progressBar!!.progress = 0
                    myViewHolder.progressBar!!.max = 1000
                    myViewHolder.progressBar!!.visibility = View.INVISIBLE
                    myViewHolder.icon = view2.findViewById<View>(R.id.Grid_imageView1) as ImageView
                    myViewHolder.video_bg = view2.findViewById<View>(R.id.video_bg) as ImageView
                    myViewHolder.iv_delete = view2.findViewById<View>(R.id.iv_delete) as ImageView
                    myViewHolder.iv_delete!!.setBackgroundResource(R.mipmap.noselected_icon)
                    myViewHolder.tv_item = view2.findViewById<View>(R.id.tv_item) as TextView
                    myViewHolder.caption = view2.findViewById<View>(R.id.Grid_textView1) as TextView
                    myViewHolder.filename_TextView = view2.findViewById<View>(R.id.filename_TextView) as TextView
                    myViewHolder.btn_down = view2.findViewById<View>(R.id.btn_down) as Button
                    myViewHolder.sUrl = ""
                    view2.tag = myViewHolder
                } else {
                    myViewHolder = view.tag as MyViewHolder
                    view2 = view
                }
                if (myNode != null) {
                    myViewHolder.filename_TextView!!.text = myNode.sUrl
                    if (myNode.bitmap != null) {
                        myViewHolder.icon!!.setImageBitmap(myNode.bitmap)
                    } else if (MyApp.BROW_TYPE == 0) {
                        myViewHolder.icon!!.setImageBitmap(BitmapFactory.decodeResource(context.resources, R.mipmap.image_default))
                    } else {
                        myViewHolder.icon!!.setImageBitmap(BitmapFactory.decodeResource(context.resources, R.mipmap.video_default))
                    }
                    if (myNode.nType == MyNode.Companion.TYPE_SD) {
                        myViewHolder.progressBar!!.visibility = View.VISIBLE
                        if (myNode.nStatus == 2) {
                            myViewHolder.progressBar!!.progress = 1000
                            myViewHolder.caption!!.visibility = View.INVISIBLE
                        } else if (myNode.nStatus == 1) {
                            myViewHolder.progressBar!!.progress = myNode.nPre.toInt()
                            myViewHolder.caption!!.setTextColor(-16776961)
                            myViewHolder.caption!!.visibility = View.VISIBLE
                            val i2 = (myNode.nPre / 10).toInt()
                            val i3 = (myNode.nPre % 10).toInt()
                            val textView = myViewHolder.caption
                            textView!!.text = "$i2.$i3%"
                        } else if (myNode.nStatus == 3) {
                            myViewHolder.progressBar!!.progress = 0
                            myViewHolder.caption!!.text = "0.0%"
                            myViewHolder.caption!!.visibility = View.VISIBLE
                        } else {
                            myViewHolder.progressBar!!.progress = 0
                            myViewHolder.caption!!.visibility = View.INVISIBLE
                        }
                    } else {
                        myViewHolder.progressBar!!.visibility = View.GONE
                        myViewHolder.caption!!.visibility = View.INVISIBLE
                    }
                    if (myNode.nOp == 1) {
                        myViewHolder.iv_delete!!.visibility = View.VISIBLE
                        myViewHolder.iv_delete!!.setBackgroundResource(R.mipmap.noselected_icon)
                        val scaleAnimation = ScaleAnimation(1.0f, 1.0f, 1.0f, 1.0f, 1, 0.5f, 1, 0.5f)
                        scaleAnimation.duration = 1L
                        scaleAnimation.fillAfter = true
                        myViewHolder.icon!!.startAnimation(scaleAnimation)
                    } else if (myNode.nOp == 2) {
                        myViewHolder.iv_delete!!.visibility = View.VISIBLE
                        myViewHolder.iv_delete!!.setBackgroundResource(R.mipmap.selected_icon)
                        val scaleAnimation2 = ScaleAnimation(1.2f, 1.2f, 1.2f, 1.2f, 1, 0.5f, 1, 0.5f)
                        scaleAnimation2.duration = 1L
                        scaleAnimation2.fillAfter = true
                        myViewHolder.icon!!.startAnimation(scaleAnimation2)
                    } else {
                        myViewHolder.iv_delete!!.visibility = View.INVISIBLE
                    }
                    if (MyApp.BROW_TYPE == 1) {
                        if (myNode.bitmap == null) {
                            myViewHolder.video_bg!!.visibility = View.INVISIBLE
                        } else {
                            myViewHolder.video_bg!!.visibility = View.VISIBLE
                        }
                    } else {
                        myViewHolder.video_bg!!.visibility = View.INVISIBLE
                    }
                }
                myViewHolder.btn_down!!.setOnClickListener {
                    val myNode2 = nodes!![i]
                    if (myNode2!!.nStatus != 2) {
                        nNeedAction = 1
                        nodeAction = myNode2
                        F_Checked()
                    }
                }
                myViewHolder.item_view!!.setOnClickListener(View.OnClickListener {
                    val myNode2 = nodes!![i]
                    var i4 = 0
                    if (!bEdit) {
                        if (MyApp.bBROW_SD) {
                            if (myNode2!!.nStatus == 2) {
                                MyApp.dispList.clear()
                                if (MyApp.BROW_TYPE == 0) {
                                    MyApp.bPlayLocalVideo = true
                                    var i5 = 0
                                    for (myNode3 in nodes!!) {
                                        if (myNode3!!.nStatus == 2) {
                                            if (myNode2 === myNode3) {
                                                i4 = i5
                                            }
                                            i5++
                                            MyApp.dispList.add(myNode3.sPath)
                                        }
                                    }
                                    MyApp.PlayBtnVoice()
                                    MyApp.dispListInx = i4
                                    startActivity(Intent(this@BrowGridActivity, DispPhotoActivity::class.java))
                                    return@OnClickListener
                                }
                                MyApp.bPlayLocalVideo = false
                                MyApp.PlayBtnVoice()
                                MyApp.dispList.add(myNode2.sPath)
                                startActivity(Intent(this@BrowGridActivity, JoyDispVideoActivity::class.java))
                                return@OnClickListener
                            }
                            MyApp.bPlayLocalVideo = false
                            if (MyApp.BROW_TYPE == 0) {
                                nNeedAction = 0
                                nodeAction = myNode2
                                F_Checked()
                                return@OnClickListener
                            }
                            MyApp.PlayBtnVoice()
                            MyApp.OnPlayNode = myNode2
                            startActivity(Intent(this@BrowGridActivity, OnlinePlayActivity::class.java))
                            return@OnClickListener
                        }
                        MyApp.dispList.clear()
                        if (MyApp.BROW_TYPE == 0) {
                            for (myNode4 in nodes!!) {
                                MyApp.dispList.add(myNode4!!.sPath)
                            }
                            MyApp.PlayBtnVoice()
                            MyApp.dispListInx = i
                            startActivity(Intent(this@BrowGridActivity, DispPhotoActivity::class.java))
                            return@OnClickListener
                        }
                        MyApp.PlayBtnVoice()
                        MyApp.dispList.add(myNode2!!.sPath)
                        startActivity(Intent(this@BrowGridActivity, JoyDispVideoActivity::class.java))
                        return@OnClickListener
                    }
                    MyApp.PlayBtnVoice()
                    if (myNode2!!.nOp == 1) {
                        myNode2.nOp = 2
                    } else {
                        myNode2.nOp = 1
                    }
                    val F_GetSelectFiles = F_GetSelectFiles()
                    if (myNode2.nOp == 2) {
                        myViewHolder.setIv_delete(R.mipmap.selected_icon)
                        val textView2 = myViewHolder.tv_item
                        textView2!!.text = F_GetSelectFiles.toString() + ""
                        myViewHolder.tv_item!!.visibility = View.VISIBLE
                        Handler().postDelayed({ myViewHolder.tv_item!!.visibility = View.GONE }, 500L)
                        val scaleAnimation3 = ScaleAnimation(1.0f, 1.2f, 1.0f, 1.2f, 1, 0.5f, 1, 0.5f)
                        scaleAnimation3.duration = 250L
                        scaleAnimation3.fillAfter = true
                        myViewHolder.icon!!.startAnimation(scaleAnimation3)
                    }
                    if (myNode2.nOp == 1) {
                        myViewHolder.setIv_delete(R.mipmap.noselected_icon)
                        val scaleAnimation4 = ScaleAnimation(1.2f, 1.0f, 1.2f, 1.0f, 1, 0.5f, 1, 0.5f)
                        scaleAnimation4.duration = 250L
                        scaleAnimation4.fillAfter = true
                        myViewHolder.icon!!.startAnimation(scaleAnimation4)
                    }
                })
                view2
            } finally {
                lock.unlock()
            }
        }

        inner class MyViewHolder {
            var btn_down: Button? = null
            var caption: TextView? = null
            var filename_TextView: TextView? = null
            var icon: ImageView? = null
            var item_view: RelativeLayout? = null
            var iv_delete: ImageView? = null
            var progressBar: ProgressBar? = null
            var sUrl: String? = null
            var tv_item: TextView? = null
            var video_bg: ImageView? = null
            fun getIv_delete(): Drawable {
                return iv_delete!!.background
            }

            fun setIv_delete(i: Int) {
                iv_delete!!.setBackgroundResource(i)
            }
        }
    }

    fun F_GetSelectFiles(): Int {
        var i = 0
        for (myNode in nodes!!) {
            if (myNode!!.nOp == 2) {
                i++
            }
        }
        return i
    }

    @Subscriber(tag = "Delete_OK")
    private fun Delete_OK(str: String) {
        F_SetEditMode(false)
        myAdapter!!.notifyDataSetChanged()
    }

    @Subscriber(tag = "Update_Grid")
    private fun Update_Grid(str: String) {
        progressBar!!.visibility = View.INVISIBLE
        (progressBar!!.drawable as AnimationDrawable).stop()
        myAdapter!!.notifyDataSetChanged()
    }

    @Subscriber(tag = "GP4225_DeleteFile")
    private fun GP4225_DeleteFile(myFile: MyFile) {
        val str = myFile.sFileName
        var z = true
        var z2 = false
        if (myFile.nLength == 1) {
            val F_GetTmpFileName = F_GetTmpFileName(str)
            try {
                MyApp.DeleteImage(str)
                val file = File(F_GetTmpFileName)
                if (file.exists() && file.isFile) {
                    file.delete()
                }
            } catch (unused: Exception) {
            }
            val it: Iterator<MyNode?> = nodes!!.iterator()
            while (true) {
                if (!it.hasNext()) {
                    z = false
                    break
                }
                val next = it.next()
                if (str.equals(next!!.sUrl, ignoreCase = true)) {
                    val file2 = File(next.sPath)
                    if (file2.exists()) {
                        file2.delete()
                    }
                    nodes!!.remove(next)
                    myAdapter!!.notifyDataSetChanged()
                }
            }
        } else {
            val it2: Iterator<MyNode?> = nodes!!.iterator()
            while (true) {
                if (!it2.hasNext()) {
                    break
                }
                val next2 = it2.next()
                if (str.equals(next2!!.sUrl, ignoreCase = true)) {
                    MyApp.DeleteImage(next2.sPath)
                    nodes!!.remove(next2)
                    myAdapter!!.notifyDataSetChanged()
                    break
                }
            }
        }
        if (bExit) {
            bEdit = false
            F_SetEditMode(false)
            return
        }
        if (F_GetSelectFiles() == 0) {
            F_SetEditMode(false)
        } else {
            z2 = z
        }
        if (z2) {
            F_DeleteNextFile()
        }
    }

    private fun F_DeleteNextFile() {
        for (myNode in nodes!!) {
            if (myNode!!.nOp == 2) {
                val str = myNode.sUrl
                if (str.length > 2) {
                    wifination.na4225_DeleteFile("", str)
                    sLine3!!.text = str
                    return
                }
            }
        }
    }

    @Subscriber(tag = "Go2Background")
    private fun Go2Background(str: String) {
        bExit = true
        finish()
    }

    companion object {
        private const val D_Size = 20
        fun drawableToBitmap(drawable: Drawable): Bitmap {
            val intrinsicWidth = drawable.intrinsicWidth
            val intrinsicHeight = drawable.intrinsicHeight
            val createBitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565)
            val canvas = Canvas(createBitmap)
            drawable.setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            drawable.draw(canvas)
            return createBitmap
        }
    }
}