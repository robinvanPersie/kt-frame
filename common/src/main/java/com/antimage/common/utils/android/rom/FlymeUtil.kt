package com.antimage.common.utils.android.rom

import android.content.Context
import android.content.Intent
import android.os.Build
import java.lang.reflect.Method

/**
 * Created by xuyuming on 2019-08-27
 */
object FlymeUtil {

    private const val ROM_FLYME: String = "FLYME"

    private var isInited: Boolean = false
    private var isFlyme: Boolean = false
    private var hasSmartBar: Boolean = false

    private fun ensureInited() {
        if (!isInited) {
            isFlyme = Build.DISPLAY.toUpperCase().contains(ROM_FLYME)
            try {
                var method: Method = Class.forName("android.os.Build").getMethod("hasSmartBar")
                hasSmartBar = method.invoke(null) as Boolean
            } catch (e: Exception) {

            }
            if (Build.DEVICE == "mx2") {
                hasSmartBar = true
            } else if (Build.DEVICE == "mx" || Build.DEVICE == "m9") {
                hasSmartBar = false
            }
            isInited = true
        }
    }

    /**
     * 是否为魅族
     */
    fun isFlyme(): Boolean {
        ensureInited()
        return isFlyme
    }

    /**
     * 是否有smart bar
     */
    fun hasSmartBar(): Boolean {
        ensureInited()
        return hasSmartBar
    }

    /**
     * 悬浮窗授权页面
     */
    fun openDrawOverlaysSetting(context: Context): Boolean {
        var intent = Intent("com.meizu.safe.security.SHOW_APPSEC")
        intent.setClassName("com.meizu.safe", "com.meizu.safe.security.AppSecActivity")
        intent.putExtra("packageName", context.packageName)
        return Util.safeStart(context, intent)
    }
}