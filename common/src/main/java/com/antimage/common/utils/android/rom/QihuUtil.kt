package com.antimage.common.utils.android.rom

import android.content.Context
import android.content.Intent
import android.os.Build

/**
 * Created by xuyuming on 2019-08-28
 */
object QihuUtil {

    private const val ROM_QIHU: String = "QIHU"
    private var isInited = false
    private var isQihu = false

    private fun ensureInited() {
        if (!isInited) {
            isQihu = ROM_QIHU == Build.MANUFACTURER
            isInited = true
        }
    }

    /**
     * 是否为qihu rom
     */
    fun isQihu(): Boolean {
        ensureInited()
        return isQihu
    }

    /**
     * 跳转到qihu悬浮窗授权页面
     */
    fun openDrawOverlaysSetting(context: Context): Boolean {
        var intent = Intent()
        intent.setClassName("com.android.settings", "com.android.settings.Settings\$OverlaySettingsActivity")
        if (Util.safeStart(context, intent)) return true

        intent.setClassName("com.qihoo360.mobilesafe", "com.qihoo360.mobilesafe.ui.index.AppEnterActivity")
        return Util.safeStart(context, intent)
    }
}