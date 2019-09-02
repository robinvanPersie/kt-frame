package com.antimage.common.utils.android.rom

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.TextUtils
import com.antimage.common.utils.android.DeviceUtils

/**
 * Created by xuyuming on 2019-08-28
 */
object SmartisanUtil {

    private const val KEY_VERSION_SMARTISAN = "ro.smartisan.version"

    private var isInited = false
    private var isSmartisan = false

    private fun ensureInited() {
        if (!isInited) {
            isSmartisan = !TextUtils.isEmpty(DeviceUtils.getSystemProperty(KEY_VERSION_SMARTISAN))
            isInited = true
        }
    }

    /**
     * 是否为锤子
     */
    fun isSmartisan(): Boolean {
        ensureInited()
        return isSmartisan
    }

    /**
     * 悬浮窗授权页面
     */
    fun openDrawOverlaysSetting(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) return false

        var intent = Intent("com.smartisanos.security.action.SWITCHED_PERMISSIONS")
        intent.setClassName("com.smartisanos.security", "com.smartisanos.security.SwitchedPermissions")
        intent.putExtra("permission", Array(1){Manifest.permission.SYSTEM_ALERT_WINDOW})
        return Util.safeStart(context, intent)
    }
}