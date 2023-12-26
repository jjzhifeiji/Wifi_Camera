package com.joyhonest.sports_camera;

import static android.graphics.PixelFormat.OPAQUE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.joyhonest.wifination.GP4225_Device;
import com.joyhonest.wifination.jh_dowload_callback;
import com.joyhonest.wifination.wifination;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class BrowGridActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int D_Size = 20;
    private ConstraintLayout Delete_AlertView;
    private TextView Title_textview;
    private AlertDialog alertDialog;
    private Button btn_back;
    private Button btn_cancel1;
    private Button btn_del;
    private Button btn_edit;
    private Button btn_selectall;
    private MyNode downliad_node;
    private List<MyNode> download_List;
    private GridView gridView;
    private PermissionAsker mAsker;
    private List<Uri> mListPhoto;
    private MyAdapter myAdapter;
    private List<MyNode> nodes;
    private ImageView progressBar;
    private TextView sLine2;
    private TextView sLine3;
    private final String TAG = "BrowGridActivity";
    private MyNode nodeAction = null;
    private final ReentrantLock lock = new ReentrantLock();
    private final ReentrantLock lock_down = new ReentrantLock();
    private boolean bExit = false;
    private boolean bDeletting = false;
    private boolean bEdit = false;
    int nReadStart = 1;
    int nCountFile = 0;
    int nReadFiles = 0;
    int nReadFiles_A = 0;
    private int nNeedAction = -1;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_brow_grid);
        MyApp.F_makeFullScreen(this);
        this.Title_textview = (TextView) findViewById(R.id.Title_textview);
        this.sLine2 = (TextView) findViewById(R.id.sLine2);
        TextView textView = (TextView) findViewById(R.id.sLine3);
        this.sLine3 = textView;
        textView.setText("");
        ImageView imageView = (ImageView) findViewById(R.id.progressBar);
        this.progressBar = imageView;
        imageView.setVisibility(View.VISIBLE);
        ((AnimationDrawable) this.progressBar.getDrawable()).start();
        this.download_List = new ArrayList();
        this.nodes = new ArrayList();
        this.gridView = (GridView) findViewById(R.id.gridView);
        ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.Delete_AlertView);
        this.Delete_AlertView = constraintLayout;
        constraintLayout.setVisibility(View.INVISIBLE);
        this.btn_cancel1 = (Button) findViewById(R.id.btn_cancel1);
        this.btn_edit = (Button) findViewById(R.id.btn_edit);
        this.btn_del = (Button) findViewById(R.id.btn_del);
        this.btn_back = (Button) findViewById(R.id.btn_back);
        this.btn_selectall = (Button) findViewById(R.id.btn_selectall);
        this.btn_edit.setOnClickListener(this);
        this.btn_del.setOnClickListener(this);
        this.btn_back.setOnClickListener(this);
        this.btn_selectall.setOnClickListener(this);
        this.Delete_AlertView.setOnClickListener(this);
        this.btn_del.setVisibility(View.INVISIBLE);
        this.btn_selectall.setVisibility(View.INVISIBLE);
        this.btn_back.setVisibility(View.VISIBLE);
        wifination.naStartRead20000_20001();
        findViewById(R.id.btn_ok).setOnClickListener(this);
        this.btn_cancel1.setOnClickListener(this);
        if (MyApp.bBROW_SD && MyApp.BROW_TYPE == 1) {
            this.myAdapter = new MyAdapter(this, R.layout.my_grid_node_sd, this.nodes);
            this.gridView.setNumColumns(2);
            this.gridView.setHorizontalSpacing(Storage.dip2px(this, 25.0f));
            new _Init_Theard().start();
        } else {
            this.myAdapter = new MyAdapter(this, R.layout.my_grid_node, this.nodes);
            this.gridView.post(new Runnable() {
                @Override
                public void run() {
                    gridView.setNumColumns((gridView.getWidth() / Storage.dip2px(BrowGridActivity.this, 80.0f)) - 1);
                    new _Init_Theard().start();
                }
            });
        }
        this.gridView.setAdapter((ListAdapter) this.myAdapter);
        if (MyApp.BROW_TYPE == 0) {
            this.Title_textview.setText(R.string.PHOTOS);
        } else {
            this.Title_textview.setText(R.string.VIDEOS);
        }
        this.mAsker = new PermissionAsker(10, new Runnable() {
            @Override
            public void run() {
                MyApp.F_CreateLocalDir("");
                DoAction();
            }
        }, new Runnable() {
            @Override
            public void run() {
                F_DispDialg();
            }
        });
        String string = getResources().getString(R.string.warning);
        String string2 = getResources().getString(R.string.The_necessary_permission_denied);
        this.alertDialog = new AlertDialog.Builder(this).setTitle(string).setMessage(string2).setNegativeButton(getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new PermissionPageUtils(BrowGridActivity.this).jumpPermissionPage();
            }
        }).create();
    }

    public void DoAction() {
        if (this.nNeedAction == 0) {
            this.nNeedAction = -1;
            MyNode myNode = this.nodeAction;
            if (myNode != null) {
                if (F_Add2DownloadList(myNode)) {
                    this.lock_down.lock();
                    int size = this.download_List.size();
                    this.lock_down.unlock();
                    if (size == 1) {
                        F_DownLoad_Start();
                    }
                }
                this.myAdapter.notifyDataSetChanged();
            }
        }
        if (this.nNeedAction == 1) {
            this.nNeedAction = -1;
            MyNode myNode2 = this.nodeAction;
            if (myNode2 != null) {
                if (F_Add2DownloadList(myNode2)) {
                    this.lock_down.lock();
                    int size2 = this.download_List.size();
                    this.lock_down.unlock();
                    if (size2 == 1) {
                        F_DownLoad_Start();
                    }
                }
                this.myAdapter.notifyDataSetChanged();
            }
        }
    }

    public void F_DispDialg() {
        this.alertDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        this.mAsker.onRequestPermissionsResult(iArr);
    }

    private void F_SetEditMode(boolean z) {
        this.bEdit = z;
        if (z) {
            this.btn_del.setVisibility(View.VISIBLE);
            this.btn_selectall.setVisibility(View.VISIBLE);
            this.btn_back.setVisibility(View.INVISIBLE);
            this.btn_edit.setText(R.string.Cancel);
            return;
        }
        this.btn_del.setVisibility(View.INVISIBLE);
        this.btn_selectall.setVisibility(View.INVISIBLE);
        this.btn_back.setVisibility(View.VISIBLE);
        if (this.nodes.size() > 0) {
            for (MyNode myNode : this.nodes) {
                myNode.nOp = 0;
            }
            this.myAdapter.notifyDataSetChanged();
        }
        this.Delete_AlertView.setVisibility(View.INVISIBLE);
        this.bDeletting = false;
        this.btn_edit.setText(R.string.Edit);
    }

    private void F_AlertSetStyle(boolean z) {
        if (z) {
            this.sLine3.setText("");
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) this.btn_cancel1.getLayoutParams();
            layoutParams.matchConstraintPercentWidth = 0.49f;
            this.btn_cancel1.setLayoutParams(layoutParams);
            return;
        }
        this.sLine3.setText("");
        ConstraintLayout.LayoutParams layoutParams2 = (ConstraintLayout.LayoutParams) this.btn_cancel1.getLayoutParams();
        layoutParams2.matchConstraintPercentWidth = 1.0f;
        this.btn_cancel1.setLayoutParams(layoutParams2);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (MyApp.bBROW_SD) {
            wifination.naDisConnectedTCP();
        }
    }

    @Override
    public void onClick(View view) {
        MyApp.PlayBtnVoice();
        switch (view.getId()) {
            case R.id.btn_back:
                onBackPressed();
                return;
            case R.id.btn_cancel1:
                this.bExit = true;
                F_SetEditMode(false);
                this.Delete_AlertView.setVisibility(View.INVISIBLE);
                return;
            case R.id.btn_del:
                if (F_GetSelectFiles() > 0) {
                    this.sLine2.setText(R.string.Do_you_sure_delete);
                    F_AlertSetStyle(true);
                    this.Delete_AlertView.setVisibility(View.VISIBLE);
                    return;
                }
                return;
            case R.id.btn_edit:
                if (!this.bEdit) {
                    if (this.nodes.size() == 0) {
                        return;
                    }
                    F_SetEditMode(true);
                    for (MyNode myNode : this.nodes) {
                        myNode.nOp = 1;
                    }
                    this.myAdapter.notifyDataSetChanged();
                    return;
                }
                F_SetEditMode(false);
                return;
            case R.id.btn_ok:
                this.sLine2.setText(R.string.deleting);
                F_AlertSetStyle(false);
                F_DeleteSelectedFiles();
                return;
            case R.id.btn_selectall:
                if (F_isSelectAll()) {
                    for (MyNode myNode2 : this.nodes) {
                        myNode2.nOp = 1;
                    }
                } else {
                    for (MyNode myNode3 : this.nodes) {
                        myNode3.nOp = 2;
                    }
                }
                this.myAdapter.notifyDataSetChanged();
                return;
            default:
                return;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.bExit = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        MyApp.F_makeFullScreen(this);
    }

    private boolean F_isSelectAll() {
        int i = 0;
        for (MyNode myNode : this.nodes) {
            if (myNode.nOp == 2) {
                i++;
            }
        }
        return i != 0 && i == this.nodes.size();
    }

    private void F_DeleteSelectedFiles() {
        if (this.bDeletting) {
            return;
        }
        F_DeleteSelectedFiles_A();
    }

    public void F_DeleteSelectedFiles_A() {
        this.bDeletting = true;
        if (MyApp.bBROW_SD) {
            for (MyNode myNode : this.nodes) {
                if (myNode.nOp == 2) {
                    String str = myNode.sUrl;
                    if (myNode.sPath.length() > 5) {
                        MyApp.DeleteImage(myNode.sPath);
                    }
                    if (str.length() > 2) {
                        wifination.na4225_DeleteFile("", str);
                        this.sLine3.setText(str);
                        return;
                    }
                }
            }
            return;
        }
        Iterator<MyNode> it = this.nodes.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            } else if (this.bExit) {
                this.bExit = false;
                break;
            } else {
                MyNode next = it.next();
                if (next.nOp == 2) {
                    String str2 = next.sPath;
                    String F_GetTmpFileName = F_GetTmpFileName(str2);
                    try {
                        MyApp.DeleteImage(str2);
                        File file = new File(F_GetTmpFileName);
                        if (file.exists() && file.isFile()) {
                            file.delete();
                        }
                    } catch (Exception unused) {
                    }
                }
            }
        }
        this.lock.lock();
        try {
            Iterator<MyNode> it2 = this.nodes.iterator();
            while (it2.hasNext()) {
                if (it2.next().nOp == 2) {
                    it2.remove();
                }
            }
            this.lock.unlock();
            EventBus.getDefault().post("", "Delete_OK");
            this.bDeletting = false;
        } catch (Throwable th) {
            this.lock.unlock();
            throw th;
        }
    }

    private class Delete_Theard extends Thread {
        private Delete_Theard() {
        }

        @Override
        public void run() {
            F_DeleteSelectedFiles_A();
        }
    }

    private class _Init_Theard extends Thread {
        private _Init_Theard() {
        }

        @Override
        public void run() {
            F_Init();
        }
    }

    public void F_Init() {
        if (MyApp.bBROW_SD) {
            MyApp.F_OpenCamera(false);
            SystemClock.sleep(200L);
            wifination.naStartRead20000_20001();
            wifination.na4225_SetMode((byte) 1);
            wifination.naStartRead20000_20001();
            wifination.na4225_ReadDeviceInfo();
            F_GetAllSD();
            return;
        }
        F_GetAllLocal();
    }

    private String getFileName(String str) {
        int lastIndexOf = str.lastIndexOf("/");
        int lastIndexOf2 = str.lastIndexOf(".");
        if (lastIndexOf == -1 || lastIndexOf2 == -1) {
            return null;
        }
        return str.substring(lastIndexOf + 1);
    }

    private void F_SaveBitmap(Bitmap bitmap, String str) {
        if (bitmap == null) {
            return;
        }
        File file = new File(str);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    private Bitmap GetSuonuitu(Uri uri) {
        try {
            ParcelFileDescriptor openFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = openFileDescriptor.getFileDescriptor();
            BitmapFactory.Options options = new BitmapFactory.Options();
            int i = 1;
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
            options.inJustDecodeBounds = false;
            int i2 = (int) (options.outHeight / 100.0f);
            if (i2 > 0) {
                i = i2;
            }
            options.inSampleSize = i;
            Bitmap decodeFileDescriptor = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
            openFileDescriptor.close();
            return decodeFileDescriptor;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Bitmap getVideoThumbnail(Uri uri) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        Bitmap bitmap = null;
        try {
            try {
                try {
                    mediaMetadataRetriever.setDataSource(this, uri);
                    bitmap = ThumbnailUtils.extractThumbnail(mediaMetadataRetriever.getFrameAtTime(1L), 100, 100);
                    mediaMetadataRetriever.release();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    mediaMetadataRetriever.release();
                }
            } catch (RuntimeException e2) {
                e2.printStackTrace();
            }
            if (bitmap == null) {
                String str = MyApp.sSDPath + "/tad.tmp";
                if (MyApp.F_GetFile4Gallery(uri, str)) {
                    Bitmap naGetVideoThumbnail = wifination.naGetVideoThumbnail(str);
                    new File(str).delete();
                    return naGetVideoThumbnail;
                }
                return bitmap;
            }
            return bitmap;
        } catch (Throwable th) {
            try {
                mediaMetadataRetriever.release();
            } catch (RuntimeException e3) {
                e3.printStackTrace();
            }
            throw th;
        }
    }

    private Bitmap getVideoThumbnail(String str) {
        Bitmap bitmap;
        String str2 = getCacheDir() + "/" + getFileName(str) + ".v_thb.png";
        Bitmap decodeFile = BitmapFactory.decodeFile(str2);
        if (decodeFile != null) {
            return decodeFile;
        }
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        try {
            try {
                mediaMetadataRetriever.setDataSource(str);
                decodeFile = mediaMetadataRetriever.getFrameAtTime(1L);
                bitmap = ThumbnailUtils.extractThumbnail(decodeFile, 100, 100);
                try {
                    mediaMetadataRetriever.release();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            } catch (RuntimeException e2) {
                e2.printStackTrace();
                try {
                    mediaMetadataRetriever.release();
                } catch (RuntimeException e3) {
                    e3.printStackTrace();
                }
                bitmap = decodeFile;
            }
            F_SaveBitmap(bitmap, str2);
            return bitmap;
        } catch (Throwable th) {
            try {
                mediaMetadataRetriever.release();
            } catch (RuntimeException e4) {
                e4.printStackTrace();
            }
            throw th;
        }
    }

    private String F_GetTmpFileName(String str) {
        String fileName = getFileName(str);
        return getCacheDir() + "/" + fileName + ".thb.png";
    }

    private Bitmap GetSuonuitu_SD(String str) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(str, options);
        options.inJustDecodeBounds = false;
        int i = ((int) (options.outHeight / 100.0f)) + 1;
        options.inSampleSize = i > 0 ? i : 1;
        return BitmapFactory.decodeFile(str, options);
    }

    private Bitmap GetSuonuitu(String str) {
        String F_GetTmpFileName = F_GetTmpFileName(str);
        Bitmap decodeFile = BitmapFactory.decodeFile(F_GetTmpFileName);
        if (decodeFile != null) {
            return decodeFile;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(str, options);
        options.inJustDecodeBounds = false;
        int i = ((int) (options.outHeight / 100.0f)) + 1;
        options.inSampleSize = i > 0 ? i : 1;
        Bitmap decodeFile2 = BitmapFactory.decodeFile(str, options);
        F_SaveBitmap(decodeFile2, F_GetTmpFileName);
        return decodeFile2;
    }

    private void F_GetAllSD() {
        this.nodes.clear();
        int i = 0;
        while (wifination.gp4225_Device.nMode != 1 && (i = i + 1) < 5) {
            wifination.na4225_SetMode((byte) 1);
            SystemClock.sleep(100L);
            wifination.na4225_ReadStatus();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.INVISIBLE);
                ((AnimationDrawable) progressBar.getDrawable()).stop();
            }
        });
        if (MyApp.BROW_TYPE == 1) {
            int i2 = wifination.gp4225_Device.VideosCount;
            this.nCountFile = i2;
            if (i2 > 0) {
                this.nReadStart = 1;
                this.nReadFiles = 0;
                this.nReadFiles_A = 0;
                wifination.na4225_GetFileList(1, 1, 20);
                return;
            }
            return;
        }
        int i3 = wifination.gp4225_Device.PhotoCount;
        this.nCountFile = i3;
        if (i3 > 0) {
            this.nReadStart = 1;
            this.nReadFiles = 0;
            this.nReadFiles_A = 0;
            wifination.na4225_GetFileList(3, 1, 20);
        }
    }

    private boolean FindFile(String str) {
        for (MyNode myNode : this.nodes) {
            if (myNode.sPath.equalsIgnoreCase(str)) {
                return true;
            }
        }
        return false;
    }

    private boolean F_Add2DownloadList(MyNode myNode) {
        boolean z;
        boolean z2 = false;
        if (this.download_List != null) {
            this.lock_down.lock();
            Iterator<MyNode> it = this.download_List.iterator();
            while (true) {
                if (!it.hasNext()) {
                    z = false;
                    break;
                } else if (it.next().sUrl.equalsIgnoreCase(myNode.sUrl)) {
                    z = true;
                    break;
                }
            }
            if (!z) {
                myNode.nStatus = 3;
                this.download_List.add(myNode);
                z2 = true;
            } else if (myNode.nStatus == 3) {
                myNode.nStatus = 0;
                this.download_List.remove(myNode);
            }
            this.lock_down.unlock();
        }
        return z2;
    }

    private void F_DownLoad_Start() {
        this.lock_down.lock();
        MyNode myNode = this.download_List.size() != 0 ? this.download_List.get(0) : null;
        this.lock_down.unlock();
        if (myNode != null && MyApp.sSDPath.length() > 4) {
            String str = myNode.sUrl;
            myNode.nStatus = 1;
            this.downliad_node = myNode;
            wifination.na4225StartDonwLoad("", str, myNode.nLength, MyApp.sSDPath + "/" + str);
        }
    }

    private void F_Download_Next() {
        MyNode myNode;
        this.lock_down.lock();
        if (this.download_List.size() > 1) {
            wifination.naDisConnectedTCP();
            myNode = this.download_List.get(1);
            this.download_List.remove(0);
        } else {
            if (this.download_List.size() == 1) {
                wifination.naDisConnectedTCP();
                this.download_List.remove(0);
            }
            myNode = null;
        }
        this.lock_down.unlock();
        if (myNode == null) {
            return;
        }
        String str = myNode.sUrl;
        myNode.nStatus = 1;
        this.downliad_node = myNode;
        wifination.na4225StartDonwLoad("", str, myNode.nLength, MyApp.sSDPath + "/" + str);
    }

    @Subscriber(tag = "DownloadFile")
    private void DownloadFile(jh_dowload_callback jh_dowload_callbackVar) {
        if (jh_dowload_callbackVar.nError != 0) {
            String str = jh_dowload_callbackVar.sFileName;
        } else {
            String str2 = jh_dowload_callbackVar.sFileName;
            int i = jh_dowload_callbackVar.nPercentage;
        }
        if (this.downliad_node != null) {
            if (jh_dowload_callbackVar.nError == 0) {
                if (((int) this.downliad_node.nPre) != jh_dowload_callbackVar.nPercentage) {
                    this.downliad_node.nPre = jh_dowload_callbackVar.nPercentage;
                    if (jh_dowload_callbackVar.nPercentage >= 1000) {
                        this.downliad_node.nStatus = 2;
                        F_SetSDFile_BitmpNode(this.downliad_node);
                        this.downliad_node = null;
                        F_Download_Next();
                    }
                    updateItem(jh_dowload_callbackVar.sFileName);
                    return;
                }
                return;
            }
            this.downliad_node.nStatus = 0;
            this.downliad_node = null;
            F_Download_Next();
            this.myAdapter.notifyDataSetChanged();
        }
    }

    private void updateItem(String str) {
        Iterator<MyNode> it = this.nodes.iterator();
        int i = 0;
        while (true) {
            if (!it.hasNext()) {
                i = -1;
                break;
            } else if (it.next().sUrl.equalsIgnoreCase(str)) {
                break;
            } else {
                i++;
            }
        }
        if (i < 0) {
            return;
        }
        int firstVisiblePosition = this.gridView.getFirstVisiblePosition();
        int lastVisiblePosition = this.gridView.getLastVisiblePosition();
        if (i < firstVisiblePosition || i > lastVisiblePosition) {
            return;
        }
        this.myAdapter.getView(i, this.gridView.getChildAt(i - firstVisiblePosition), this.gridView);
    }

    private void F_SetSDFile_BitmpNode(MyNode myNode) {
        int i;
        Bitmap naGetVideoThumbnail;
        if (myNode == null) {
            return;
        }
        String str = MyApp.sSDPath + "/" + myNode.sUrl;
        File file = new File(str);
        if (file.exists()) {
            if (file.length() == myNode.nLength) {
                myNode.sPath = str;
                if (MyApp.BROW_TYPE == 0) {
                    naGetVideoThumbnail = GetSuonuitu(str);
                    String str2 = MyApp.sSDPath + "/" + myNode.sSDMp4name;
                    myNode.sPath = "";
                    new File(str).renameTo(new File(str2));
                    MyApp.F_Save2ToGallery_A(str2, true, true);
                    Uri F_CheckIsExit = MyApp.F_CheckIsExit(myNode.sSDMp4name);
                    if (F_CheckIsExit != null) {
                        myNode.sPath = F_CheckIsExit.toString();
                    }
                } else {
                    String str3 = MyApp.sSDPath + "/" + myNode.sSDMp4name;
                    if (MyApp.nModel == 2) {
                        i = wifination.F_Convert(str, str3);
                    } else {
                        new File(str).renameTo(new File(str3));
                        i = 0;
                    }
                    if (i == 0) {
                        try {
                            File file2 = new File(str);
                            if (file2.exists()) {
                                file2.delete();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        myNode.sPath = str3;
                    }
                    naGetVideoThumbnail = wifination.naGetVideoThumbnail(str3);
                    if (naGetVideoThumbnail != null) {
                        MyApp.F_Save2ToGallery_A(str3, false, true);
                        Uri F_CheckIsExit2 = MyApp.F_CheckIsExit(myNode.sSDMp4name);
                        if (F_CheckIsExit2 != null) {
                            myNode.sPath = F_CheckIsExit2.toString();
                        }
                    }
                }
                myNode.bitmap = naGetVideoThumbnail;
                return;
            }
            myNode.sPath = "";
            return;
        }
        myNode.sPath = "";
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        int intrinsicWidth = drawable.getIntrinsicWidth();
        int intrinsicHeight = drawable.getIntrinsicHeight();
        Bitmap createBitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, drawable.getOpacity() != OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(createBitmap);
        drawable.setBounds(0, 0, intrinsicWidth, intrinsicHeight);
        drawable.draw(canvas);
        return createBitmap;
    }

    @Subscriber(tag = "GP4225_RevFiles")
    private void GP4225_RevFile(GP4225_Device.GetFiles r14) {
//        \r\n//本方法所在的代码反编译失败，请在反编译界面按照提示打开jeb编译器，找到当前对应的类的相应方法，替换到这里，然后进行适当的代码修复工作\r\n\r\nreturn;//这行代码是为了保证方法体完整性额外添加的，请按照上面的方法补充完善代码\r\n\r\n//throw new UnsupportedOperationException(\nMethod not decompiled: com.joyhonest.sports_camera.BrowGridActivity.GP4225_RevFile(com.joyhonest.wifination.GP4225_Device$GetFiles):void");
    }

    private void F_GetAllLocal() {
        List<MyNode> list = this.nodes;
        if (list != null) {
            list.clear();
        }
        if (MyApp.BROW_TYPE == 0) {
            List<Uri> F_GetAllLocalFiles = MyApp.F_GetAllLocalFiles(true);
            this.mListPhoto = F_GetAllLocalFiles;
            Collections.sort(F_GetAllLocalFiles, new MapComparator());
            List<Uri> list2 = this.mListPhoto;
            if (list2 != null) {
                for (Uri uri : list2) {
                    if (this.bExit) {
                        break;
                    }
                    Bitmap GetSuonuitu = GetSuonuitu(uri);
                    MyNode myNode = new MyNode(0);
                    myNode.bitmap = GetSuonuitu;
                    myNode.sPath = uri.toString();
                    this.nodes.add(myNode);
                    EventBus.getDefault().post("", "Update_Grid");
                }
            }
        } else {
            List<Uri> F_GetAllLocalFiles2 = MyApp.F_GetAllLocalFiles(false);
            this.mListPhoto = F_GetAllLocalFiles2;
            Collections.sort(F_GetAllLocalFiles2, new MapComparator());
            List<Uri> list3 = this.mListPhoto;
            if (list3 != null) {
                for (Uri uri2 : list3) {
                    if (this.bExit) {
                        break;
                    }
                    Bitmap videoThumbnail = getVideoThumbnail(uri2);
                    MyNode myNode2 = new MyNode(0);
                    myNode2.bitmap = videoThumbnail;
                    myNode2.sPath = uri2.toString();
                    this.nodes.add(myNode2);
                    EventBus.getDefault().post("", "Update_Grid");
                }
            }
        }
        EventBus.getDefault().post("", "Update_Grid");
    }

    public void F_Checked() {
        if (MyApp.isAndroidQ()) {
            DoAction();
        } else {
            this.mAsker.askPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE");
        }
    }

    public class MapComparator implements Comparator<Uri> {
        MapComparator() {
        }

        @Override
        public int compare(Uri uri, Uri uri2) {
            return uri2.compareTo(uri);
        }
    }

    public class MyAdapter extends BaseAdapter {
        private Context context;
        private LayoutInflater mInflater;
        private List<MyNode> mfilelist;
        private int viewResourceId;

        @Override
        public long getItemId(int i) {
            return i;
        }

        MyAdapter(Context context, int i, List<MyNode> list) {
            this.context = context;
            this.viewResourceId = i;
            this.mfilelist = list;
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            lock.lock();
            int size = this.mfilelist.size();
            lock.unlock();
            return size;
        }

        @Override
        public Object getItem(int i) {
            return Integer.valueOf(i);
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            final MyViewHolder myViewHolder;
            View view2;
            lock.lock();
            try {
                MyNode myNode = i < this.mfilelist.size() ? this.mfilelist.get(i) : null;
                if (view == null) {
                    myViewHolder = new MyViewHolder();
                    view2 = this.mInflater.inflate(this.viewResourceId, (ViewGroup) null);
                    myViewHolder.progressBar = (ProgressBar) view2.findViewById(R.id.Grid_progressBar1);
                    myViewHolder.item_view = (RelativeLayout) view2.findViewById(R.id.item_view);
                    myViewHolder.progressBar.setProgress(0);
                    myViewHolder.progressBar.setMax(1000);
                    myViewHolder.progressBar.setVisibility(View.INVISIBLE);
                    myViewHolder.icon = (ImageView) view2.findViewById(R.id.Grid_imageView1);
                    myViewHolder.video_bg = (ImageView) view2.findViewById(R.id.video_bg);
                    myViewHolder.iv_delete = (ImageView) view2.findViewById(R.id.iv_delete);
                    myViewHolder.iv_delete.setBackgroundResource(R.mipmap.noselected_icon);
                    myViewHolder.tv_item = (TextView) view2.findViewById(R.id.tv_item);
                    myViewHolder.caption = (TextView) view2.findViewById(R.id.Grid_textView1);
                    myViewHolder.filename_TextView = (TextView) view2.findViewById(R.id.filename_TextView);
                    myViewHolder.btn_down = (Button) view2.findViewById(R.id.btn_down);
                    myViewHolder.sUrl = "";
                    view2.setTag(myViewHolder);
                } else {
                    myViewHolder = (MyViewHolder) view.getTag();
                    view2 = view;
                }
                if (myNode != null) {
                    myViewHolder.filename_TextView.setText(myNode.sUrl);
                    if (myNode.bitmap != null) {
                        myViewHolder.icon.setImageBitmap(myNode.bitmap);
                    } else if (MyApp.BROW_TYPE == 0) {
                        myViewHolder.icon.setImageBitmap(BitmapFactory.decodeResource(this.context.getResources(), R.mipmap.image_default));
                    } else {
                        myViewHolder.icon.setImageBitmap(BitmapFactory.decodeResource(this.context.getResources(), R.mipmap.video_default));
                    }
                    if (myNode.nType == MyNode.TYPE_SD) {
                        myViewHolder.progressBar.setVisibility(View.VISIBLE);
                        if (myNode.nStatus == 2) {
                            myViewHolder.progressBar.setProgress(1000);
                            myViewHolder.caption.setVisibility(View.INVISIBLE);
                        } else if (myNode.nStatus == 1) {
                            myViewHolder.progressBar.setProgress((int) myNode.nPre);
                            myViewHolder.caption.setTextColor(-16776961);
                            myViewHolder.caption.setVisibility(View.VISIBLE);
                            int i2 = (int) (myNode.nPre / 10);
                            int i3 = (int) (myNode.nPre % 10);
                            TextView textView = myViewHolder.caption;
                            textView.setText(i2 + "." + i3 + "%");
                        } else if (myNode.nStatus == 3) {
                            myViewHolder.progressBar.setProgress(0);
                            myViewHolder.caption.setText("0.0%");
                            myViewHolder.caption.setVisibility(View.VISIBLE);
                        } else {
                            myViewHolder.progressBar.setProgress(0);
                            myViewHolder.caption.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        myViewHolder.progressBar.setVisibility(View.GONE);
                        myViewHolder.caption.setVisibility(View.INVISIBLE);
                    }
                    if (myNode.nOp == 1) {
                        myViewHolder.iv_delete.setVisibility(View.VISIBLE);
                        myViewHolder.iv_delete.setBackgroundResource(R.mipmap.noselected_icon);
                        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.0f, 1.0f, 1.0f, 1, 0.5f, 1, 0.5f);
                        scaleAnimation.setDuration(1L);
                        scaleAnimation.setFillAfter(true);
                        myViewHolder.icon.startAnimation(scaleAnimation);
                    } else if (myNode.nOp == 2) {
                        myViewHolder.iv_delete.setVisibility(View.VISIBLE);
                        myViewHolder.iv_delete.setBackgroundResource(R.mipmap.selected_icon);
                        ScaleAnimation scaleAnimation2 = new ScaleAnimation(1.2f, 1.2f, 1.2f, 1.2f, 1, 0.5f, 1, 0.5f);
                        scaleAnimation2.setDuration(1L);
                        scaleAnimation2.setFillAfter(true);
                        myViewHolder.icon.startAnimation(scaleAnimation2);
                    } else {
                        myViewHolder.iv_delete.setVisibility(View.INVISIBLE);
                    }
                    if (MyApp.BROW_TYPE == 1) {
                        if (myNode.bitmap == null) {
                            myViewHolder.video_bg.setVisibility(View.INVISIBLE);
                        } else {
                            myViewHolder.video_bg.setVisibility(View.VISIBLE);
                        }
                    } else {
                        myViewHolder.video_bg.setVisibility(View.INVISIBLE);
                    }
                }
                myViewHolder.btn_down.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view3) {
                        MyNode myNode2 = (MyNode) nodes.get(i);
                        if (myNode2.nStatus != 2) {
                            nNeedAction = 1;
                            nodeAction = myNode2;
                            F_Checked();
                        }
                    }
                });
                myViewHolder.item_view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view3) {
                        MyNode myNode2 = (MyNode) nodes.get(i);
                        int i4 = 0;
                        if (!bEdit) {
                            if (MyApp.bBROW_SD) {
                                if (myNode2.nStatus == 2) {
                                    MyApp.dispList.clear();
                                    if (MyApp.BROW_TYPE == 0) {
                                        MyApp.bPlayLocalVideo = true;
                                        int i5 = 0;
                                        for (MyNode myNode3 : nodes) {
                                            if (myNode3.nStatus == 2) {
                                                if (myNode2 == myNode3) {
                                                    i4 = i5;
                                                }
                                                i5++;
                                                MyApp.dispList.add(myNode3.sPath);
                                            }
                                        }
                                        MyApp.PlayBtnVoice();
                                        MyApp.dispListInx = i4;
                                        startActivity(new Intent(BrowGridActivity.this, DispPhotoActivity.class));
                                        return;
                                    }
                                    MyApp.bPlayLocalVideo = false;
                                    MyApp.PlayBtnVoice();
                                    MyApp.dispList.add(myNode2.sPath);
                                    startActivity(new Intent(BrowGridActivity.this, JoyDispVideoActivity.class));
                                    return;
                                }
                                MyApp.bPlayLocalVideo = false;
                                if (MyApp.BROW_TYPE == 0) {
                                    nNeedAction = 0;
                                    nodeAction = myNode2;
                                    F_Checked();
                                    return;
                                }
                                MyApp.PlayBtnVoice();
                                MyApp.OnPlayNode = myNode2;
                                startActivity(new Intent(BrowGridActivity.this, OnlinePlayActivity.class));
                                return;
                            }
                            MyApp.dispList.clear();
                            if (MyApp.BROW_TYPE == 0) {
                                for (MyNode myNode4 : nodes) {
                                    MyApp.dispList.add(myNode4.sPath);
                                }
                                MyApp.PlayBtnVoice();
                                MyApp.dispListInx = i;
                                startActivity(new Intent(BrowGridActivity.this, DispPhotoActivity.class));
                                return;
                            }
                            MyApp.PlayBtnVoice();
                            MyApp.dispList.add(myNode2.sPath);
                            startActivity(new Intent(BrowGridActivity.this, JoyDispVideoActivity.class));
                            return;
                        }
                        MyApp.PlayBtnVoice();
                        if (myNode2.nOp == 1) {
                            myNode2.nOp = 2;
                        } else {
                            myNode2.nOp = 1;
                        }
                        int F_GetSelectFiles = F_GetSelectFiles();
                        if (myNode2.nOp == 2) {
                            myViewHolder.setIv_delete(R.mipmap.selected_icon);
                            TextView textView2 = myViewHolder.tv_item;
                            textView2.setText(F_GetSelectFiles + "");
                            myViewHolder.tv_item.setVisibility(View.VISIBLE);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    myViewHolder.tv_item.setVisibility(View.GONE);
                                }
                            }, 500L);
                            ScaleAnimation scaleAnimation3 = new ScaleAnimation(1.0f, 1.2f, 1.0f, 1.2f, 1, 0.5f, 1, 0.5f);
                            scaleAnimation3.setDuration(250L);
                            scaleAnimation3.setFillAfter(true);
                            myViewHolder.icon.startAnimation(scaleAnimation3);
                        }
                        if (myNode2.nOp == 1) {
                            myViewHolder.setIv_delete(R.mipmap.noselected_icon);
                            ScaleAnimation scaleAnimation4 = new ScaleAnimation(1.2f, 1.0f, 1.2f, 1.0f, 1, 0.5f, 1, 0.5f);
                            scaleAnimation4.setDuration(250L);
                            scaleAnimation4.setFillAfter(true);
                            myViewHolder.icon.startAnimation(scaleAnimation4);
                        }
                    }
                });
                return view2;
            } finally {
                lock.unlock();
            }
        }

        public class MyViewHolder {
            Button btn_down;
            TextView caption;
            TextView filename_TextView;
            ImageView icon;
            RelativeLayout item_view;
            ImageView iv_delete;
            ProgressBar progressBar;
            String sUrl;
            TextView tv_item;
            ImageView video_bg;

            public MyViewHolder() {
            }

            public Drawable getIv_delete() {
                return this.iv_delete.getBackground();
            }

            public void setIv_delete(int i) {
                this.iv_delete.setBackgroundResource(i);
            }
        }
    }

    public int F_GetSelectFiles() {
        int i = 0;
        for (MyNode myNode : this.nodes) {
            if (myNode.nOp == 2) {
                i++;
            }
        }
        return i;
    }

    @Subscriber(tag = "Delete_OK")
    private void Delete_OK(String str) {
        F_SetEditMode(false);
        this.myAdapter.notifyDataSetChanged();
    }

    @Subscriber(tag = "Update_Grid")
    private void Update_Grid(String str) {
        this.progressBar.setVisibility(View.INVISIBLE);
        ((AnimationDrawable) this.progressBar.getDrawable()).stop();
        this.myAdapter.notifyDataSetChanged();
    }

    @Subscriber(tag = "GP4225_DeleteFile")
    private void GP4225_DeleteFile(GP4225_Device.MyFile myFile) {
        String str = myFile.sFileName;
        boolean z = true;
        boolean z2 = false;
        if (myFile.nLength == 1) {
            String F_GetTmpFileName = F_GetTmpFileName(str);
            try {
                MyApp.DeleteImage(str);
                File file = new File(F_GetTmpFileName);
                if (file.exists() && file.isFile()) {
                    file.delete();
                }
            } catch (Exception unused) {
            }
            Iterator<MyNode> it = this.nodes.iterator();
            while (true) {
                if (!it.hasNext()) {
                    z = false;
                    break;
                }
                MyNode next = it.next();
                if (str.equalsIgnoreCase(next.sUrl)) {
                    File file2 = new File(next.sPath);
                    if (file2.exists()) {
                        file2.delete();
                    }
                    this.nodes.remove(next);
                    this.myAdapter.notifyDataSetChanged();
                }
            }
        } else {
            Iterator<MyNode> it2 = this.nodes.iterator();
            while (true) {
                if (!it2.hasNext()) {
                    break;
                }
                MyNode next2 = it2.next();
                if (str.equalsIgnoreCase(next2.sUrl)) {
                    MyApp.DeleteImage(next2.sPath);
                    this.nodes.remove(next2);
                    this.myAdapter.notifyDataSetChanged();
                    break;
                }
            }
        }
        if (this.bExit) {
            this.bEdit = false;
            F_SetEditMode(false);
            return;
        }
        if (F_GetSelectFiles() == 0) {
            F_SetEditMode(false);
        } else {
            z2 = z;
        }
        if (z2) {
            F_DeleteNextFile();
        }
    }

    private void F_DeleteNextFile() {
        for (MyNode myNode : this.nodes) {
            if (myNode.nOp == 2) {
                String str = myNode.sUrl;
                if (str.length() > 2) {
                    wifination.na4225_DeleteFile("", str);
                    this.sLine3.setText(str);
                    return;
                }
            }
        }
    }

    @Subscriber(tag = "Go2Background")
    private void Go2Background(String str) {
        this.bExit = true;
        finish();
    }
}
