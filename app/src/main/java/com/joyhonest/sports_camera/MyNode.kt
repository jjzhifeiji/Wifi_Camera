package com.joyhonest.sports_camera

import android.graphics.Bitmap

class MyNode {
    var bitmap: Bitmap?
    var nGetType = 0
    var nLength: Int = 0
    var nOp: Int
    var nPre: Long = 0
    var nStatus: Int
    var nType: Int
    var sPath: String
    var sSDMp4name: String
    var sText: String
    var sUrl: String

    constructor() {
        nOp = 0
        nStatus = 0
        nType = TYPE_Local
        bitmap = null
        sPath = ""
        sUrl = ""
        sText = ""
        sSDMp4name = ""
        nOp = 0
        nLength = 0
    }

    constructor(i: Int) {
        nOp = 0
        nStatus = 0
        nType = i
        bitmap = null
        sPath = ""
        sUrl = ""
        sText = ""
        sSDMp4name = ""
        nOp = 0
        nLength = 0
    }

    companion object {
        const val Status_downloaded = 2
        const val Status_downloading = 1
        const val Status_waitdownload = 3
        const val Stauts_normal = 0
        var TYPE_Local = 0
        var TYPE_SD = 1
    }
}