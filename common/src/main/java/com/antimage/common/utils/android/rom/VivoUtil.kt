package com.antimage.common.utils.android.rom

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import com.antimage.common.utils.android.DeviceUtils

/**
 * Created by xuyuming on 2019-08-28
 */
object VivoUtil {
    private const val KEY_VERSION_VIVO = "ro.vivo.os.version"

    private var isinited = false
    private var isVivo =false

    private fun ensureInited() {
        if (!isinited) {
            isVivo = !TextUtils.isEmpty(DeviceUtils.getSystemProperty(KEY_VERSION_VIVO))
            isinited = true
        }
    }

    /**
     * 是否为vivo
     */
    fun isVivo(): Boolean {
        ensureInited()
        return isVivo
    }

    /**
     * 悬浮窗授权页面
     */
    fun openDrawOverlaysSetting(context: Context): Boolean {
        var intent = Intent("com.iqoo.secure")
        intent.setClassName("com.iqoo.secure", "com.iqoo.secure.MainGuideActivity")
        return Util.safeStart(context, intent)
    }

    /**
     * 跳转到Vivo安全中心
     */
    fun openPermissionSettings(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.fromParts("package", context.packageName, null)
            if (Util.safeStart(context, intent, false)) return true
        }
        var intent = context.packageManager.getLaunchIntentForPackage("com.iqoo.secure")
        if (intent != null) {
            return Util.safeStart(context, intent)
        }
        return false
    }
}