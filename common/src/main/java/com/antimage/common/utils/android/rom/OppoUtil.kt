package com.antimage.common.utils.android.rom

import android.content.ComponentName
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
object OppoUtil {

    private const val KEY_VERSION_OPPO = "ro.build.version.opporom"

    private var isInited = false
    private var isOppo = false

    private fun ensureInited() {
        if (!isInited) {
            isOppo = !TextUtils.isEmpty(DeviceUtils.getSystemProperty(KEY_VERSION_OPPO))
            isInited = true
        }
    }

    fun isOppo(): Boolean {
        ensureInited()
        return isOppo
    }

    /**
     * 跳转到oppo悬浮窗授权页面
     */
    fun openDrawOverlaysSetting(context: Context): Boolean {
        var intent = Intent()
        intent.putExtra("packageName", context.packageName)
        // OPPO A53|5.1.1|2.1
        intent.action = "com.oppo.safe"
        intent.setClassName("com.oppo.safe", "com.oppo.safe.permission.floatwindow.FloatWindowListActivity")
        if (Util.safeStart(context, intent)) return true

        // OPPO R7s|4.4.4|2.1
        intent.action = "com.color.safecenter"
        intent.setClassName("com.color.safecenter", "com.color.safecenter.permission.floatwindow.FloatWindowListActivity")
        if (Util.safeStart(context, intent)) return true

        intent.action = "com.coloros.safecenter"
        intent.setClassName("com.coloros.safecenter", "com.coloros.safecenter.sysfloatwindow.FloatWindowListActivity")
        return Util.safeStart(context, intent)
    }

    /**
     * 跳转到oppo权限设置页面
     */
    fun openPermissionSettings(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", context.packageName, null)
            intent.data = uri
            if (Util.safeStart(context, intent, false)) return true
        }

        var intent = context.packageManager.getLaunchIntentForPackage("com.oppo,.safe")
        if (intent != null) {
            return Util.safeStart(context, intent)
        }

        intent = context.packageManager.getLaunchIntentForPackage("com.color.safecenter")
        if (intent != null) {
            return Util.safeStart(context, intent)
        }

        val componentName = ComponentName("com.color.safecenter", "com.color.safecenter.permission.PermissionManagerActivity")
        return Util.safeStart(context, Intent().setComponent(componentName).putExtra("packageName", context.packageName))
    }
}