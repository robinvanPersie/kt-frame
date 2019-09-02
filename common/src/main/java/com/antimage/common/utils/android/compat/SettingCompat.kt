package com.antimage.common.utils.android.compat

import android.Manifest
import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.antimage.common.utils.android.rom.*
import java.lang.reflect.Method

/**
 * Created by xuyuming on 2019-08-28
 */
object SettingCompat {

    private val IMPL: SettingCompatImpl

    init {
        var version = Build.VERSION.SDK_INT
        when {
            version >= Build.VERSION_CODES.M -> IMPL = SettingCompatM()
            version >= Build.VERSION_CODES.KITKAT -> IMPL = SettingCompatKITKAT()
            else -> IMPL = BaseSettingCompatImpl()
        }
    }

    /**
     * 是否支持系统级悬浮窗
     */
    fun canSystemDrawOverlays(context: Context): Boolean =
            IMPL.canSystemDrawOverlays(context)

    /**
     * 设置系统悬浮窗
     * @param allowed 是否允许应用创建系统级悬浮窗
     */
    fun setSystemDrawOverlays(context: Context, allowed: Boolean): Boolean {
        if (allowed != canSystemDrawOverlays(context)) {
            if (MiuiUtil.isMiui()) {
                return MiuiUtil.openDrawOverlaysSetting(context)
            }
            if (FlymeUtil.isFlyme()) {
                return FlymeUtil.openDrawOverlaysSetting(context)
            }
            if (VivoUtil.isVivo()) {
                return VivoUtil.openDrawOverlaysSetting(context)
            }
            if (QihuUtil.isQihu()) {
                return QihuUtil.openDrawOverlaysSetting(context)
            }
            if (OppoUtil.isOppo()) {
                return OppoUtil.openDrawOverlaysSetting(context)
            }
            if (EmuiUtil.isEmui()) {
                return EmuiUtil.openDrawOverlaysSetting(context)
            }
            if (SmartisanUtil.isSmartisan()) {
                return SmartisanUtil.openDrawOverlaysSetting(context)
            }
            return IMPL.setSystemDrawOverlays(context, allowed)
        }
        return false
    }

    interface SettingCompatImpl {
        fun canSystemDrawOverlays(context: Context): Boolean
        fun setSystemDrawOverlays(context: Context, allowed: Boolean): Boolean
        fun canWriteSettings(context: Context): Boolean
        fun setWriteSettings(context: Context, allowed: Boolean): Boolean
    }

    private open class BaseSettingCompatImpl : SettingCompatImpl {

        override fun canSystemDrawOverlays(context: Context): Boolean =
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.SYSTEM_ALERT_WINDOW
            ) === PackageManager.PERMISSION_GRANTED

        override fun setSystemDrawOverlays(context: Context, allowed: Boolean): Boolean = false

        override fun canWriteSettings(context: Context): Boolean =
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_SETTINGS
            ) === PackageManager.PERMISSION_GRANTED

        override fun setWriteSettings(context: Context, allowed: Boolean): Boolean = false
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private class SettingCompatKITKAT: BaseSettingCompatImpl() {

        companion object {
            private const val OP_WRITE_SETTINGS = 23
            private const val OP_SYSTEM_ALERT_WINDOW = 24
        }

        private fun checkOp(context: Context, op: Int): Boolean {
            var manager: AppOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            try {
                var method: Method = AppOpsManager::class.java.getDeclaredMethod("checkOp", Int::class.java, Int::class.java, String::class.java)
                return AppOpsManager.MODE_ALLOWED == method.invoke(manager, op, Binder.getCallingUid(), context.packageName) as Int
            } catch (e: Exception) {
                Log.e("SettingCompatJELL", e.toString())
            }
            return false
        }

        private fun setMode(context: Context, op: Int, allowed: Boolean): Boolean {
            var manager: AppOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            try {
                var method: Method = AppOpsManager::class.java.getDeclaredMethod("setMode", Int::class.java, Int::class.java, String::class.java, Int::class.java)
                method.invoke(manager, op, Binder.getCallingUid(), context.packageName, if (allowed) AppOpsManager.MODE_ALLOWED else AppOpsManager.MODE_IGNORED)
                return true
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return false
        }

        override fun canSystemDrawOverlays(context: Context): Boolean =
            checkOp(context, OP_SYSTEM_ALERT_WINDOW)

        override fun setSystemDrawOverlays(context: Context, allowed: Boolean): Boolean =
            setMode(context, OP_SYSTEM_ALERT_WINDOW, allowed)

        override fun canWriteSettings(context: Context): Boolean =
            checkOp(context, OP_WRITE_SETTINGS)

        override fun setWriteSettings(context: Context, allowed: Boolean): Boolean =
            setMode(context, OP_WRITE_SETTINGS, allowed)
    }

    private class SettingCompatM: BaseSettingCompatImpl() {

        override fun canSystemDrawOverlays(context: Context): Boolean =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Settings.canDrawOverlays(context)
            } else {
                TODO("VERSION.SDK_INT < M")
            }

        override fun setSystemDrawOverlays(context: Context, allowed: Boolean): Boolean {
            var intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            intent.data = Uri.parse("package:" + context.packageName)
            context.startActivity(intent)
            return true
        }

        @SuppressLint("NewApi")
        override fun canWriteSettings(context: Context): Boolean =
            Settings.System.canWrite(context)

        override fun setWriteSettings(context: Context, allowed: Boolean): Boolean {
            var intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            intent.data = Uri.parse("package:" + context.packageName)
            context.startActivity(intent)
            return true
        }
    }
}