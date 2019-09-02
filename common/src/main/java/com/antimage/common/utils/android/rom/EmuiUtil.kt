package com.antimage.common.utils.android.rom

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import com.antimage.common.utils.android.DeviceUtils

/**
 * Created by xuyuming on 2019-08-27
 */
object EmuiUtil {

    private const val KEY_VERSION_EMUI: String = "ro.build.version.emui";
    private const val HUAWEI_PACKAGE = "com.huawei.systemmanager";
    private var isInited: Boolean = false
    private var isEmui: Boolean = false;

    private fun ensureInited() {
        if (!isInited) {
            isEmui = !TextUtils.isEmpty(DeviceUtils.getSystemProperty(KEY_VERSION_EMUI))
            isInited = true
        }
    }

    /**
     * 判断是否为华为
     */
    fun isEmui(): Boolean {
        ensureInited()
        return isEmui
    }

    /**
     * 打开悬浮窗授权页面
     */
    fun openDrawOverlaysSetting(context: Context): Boolean {
        var intent = Intent()
        intent.setClassName(HUAWEI_PACKAGE, "com.huawei.systemmanager.addviewmonitor.AddViewMonitorActivity")
        if (Util.safeStart(context, intent)) return true

        intent.setClassName(HUAWEI_PACKAGE, "com.huawei.notificationmanager.ui.NotificationManagmentActivity")
        if (Util.safeStart(context, intent)) return true

        intent.setClassName(HUAWEI_PACKAGE, "com.huawei.permissionmanager.ui.MainActivity")
        if (Util.safeStart(context, intent)) return true

        return false
    }
}