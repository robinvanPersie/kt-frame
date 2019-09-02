package com.antimage.common.utils.android.rom

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.Window
import com.antimage.common.utils.android.DeviceUtils
import java.lang.reflect.Field

/**
 * Created by xuyuming on 2019-08-27
 */
object MiuiUtil {

    private var isInited: Boolean = false
    private var isMiui: Boolean = false
    private var sMiuiVersion = 0

    private fun ensureInited() {
        if (!isInited) {
            isMiui = !TextUtils.isEmpty(DeviceUtils.getSystemProperty("ro.miui.ui.version.name"))
            try {
                var version = DeviceUtils.getSystemProperty("ro.miui.ui.version.code")
                if (!TextUtils.isEmpty(version) && TextUtils.isDigitsOnly(version)) {
                    sMiuiVersion = version.toInt()
                }
            } catch (e: Exception) {
                Log.w("Miui", "get miui version failed")
                sMiuiVersion = 0
            }
            isInited = true
        }
    }

    /**
     * 是否小米rom
     */
    fun isMiui(): Boolean {
        ensureInited()
        return isMiui
    }

    /**
     * 获取小米版本
     */
    fun getMiUIVersion(): Int {
        ensureInited()
        return sMiuiVersion
    }

    /**
     * 设置状态栏是否为暗色
     */
    fun setMiUIStatusBarDarkMode(window: Window, darkMode: Boolean): Boolean {
        val clazz = window.javaClass
        try {
            var darkModeFlag = 0
            var layoutParams = Class.forName("android.view.MiuiWindowManager\$LayoutParams")
            var field: Field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
            darkModeFlag = field.getInt(layoutParams)
            var extraFlagField = clazz.getMethod("setExtraFlags", Int::class.java, Int::class.java)
            extraFlagField.invoke(window, if (darkMode) darkModeFlag else 0, darkModeFlag)
        } catch (e: Exception) {
            return false
        }
        return true
    }

    /**
     * 跳转到小米悬浮窗授权页面
     */
    fun openDrawOverlaysSetting(context: Context): Boolean {
        var intent = Intent("miui.intent.action.APP_PERM_EDITOR")
        intent.putExtra("extra_pkgname",context.packageName)
        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity")
        if (Util.safeStart(context, intent)) return true

        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity")
        if (Util.safeStart(context, intent)) return true

        // miui v5 的支持的android版本最高4.x
        // http://www.romzj.com/list/search?keyword=MIUI%20V5#search_result
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            var intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.fromParts("package", context.packageName, null)
            return Util.safeStart(context, intent)
        }
        return false
    }
}