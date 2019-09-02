package com.antimage.common.utils.android.compat

import android.content.Context
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.RequiresApi
import com.antimage.common.utils.android.rom.MiuiUtil

/**
 * Created by xuyuming on 2019-08-28
 */
object StatusBarCompat {

    private val IMPL: StatusBarCompatImpl

    init {
        var version = Build.VERSION.SDK_INT
        if (version >= Build.VERSION_CODES.M) {
            if (MiuiUtil.getMiUIVersion() >= 4) {
                //miui 6 以上支持沉浸式状态栏
                IMPL = MiuiStatusBarCompat(MStatusBarCompat())
            } else {
                IMPL = MStatusBarCompat()
            }
        } else if (version >= Build.VERSION_CODES.LOLLIPOP) {
            if (MiuiUtil.getMiUIVersion() >= 4) {
                IMPL = MiuiStatusBarCompat(LollipopStatusBarCompat())
            } else {
                IMPL = LollipopStatusBarCompat()
            }
        } else if (version >= Build.VERSION_CODES.KITKAT) {
            if (MiuiUtil.getMiUIVersion() >= 4) {
                IMPL = MiuiStatusBarCompat(KitKatStatusBarCompat())
            } else {
                IMPL = KitKatStatusBarCompat()
            }
        } else {
            IMPL = BaseStatusBarCompat()
        }
    }

    interface StatusBarCompatImpl {

        fun isStatusBarHide(window: Window): Boolean

        fun setStatusBarLightMode(window: Window, lightMode: Boolean): Boolean

        fun layoutStatusBarSpace(window: Window)

        fun setStatusBarColor(window: Window, color: Int)

        fun getStatusBarHeight(context: Context): Int

    }

    private open class BaseStatusBarCompat : StatusBarCompatImpl {

        override fun isStatusBarHide(window: Window): Boolean = false

        override fun setStatusBarLightMode(window: Window, lightMode: Boolean): Boolean = false

        override fun layoutStatusBarSpace(window: Window) {
        }

        override fun setStatusBarColor(window: Window, color: Int) {
        }

        override fun getStatusBarHeight(context: Context): Int {
            var id = context.resources.getIdentifier("status_bar_height", "dimen", "android")
            return if (id > 0) context.resources.getDimensionPixelSize(id) else 0
        }
    }

    private open class KitKatStatusBarCompat : BaseStatusBarCompat() {

        override fun isStatusBarHide(window: Window): Boolean =
            window.attributes.flags and WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS != 0

        override fun layoutStatusBarSpace(window: Window) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }

    private open class LollipopStatusBarCompat : KitKatStatusBarCompat() {

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun setStatusBarColor(window: Window, color: Int) {
            window.statusBarColor = color
        }
    }

    private class MiuiStatusBarCompat(val mDelegate: StatusBarCompatImpl) : BaseStatusBarCompat() {

        override fun isStatusBarHide(window: Window): Boolean =
            mDelegate.isStatusBarHide(window)

        override fun setStatusBarLightMode(window: Window, lightMode: Boolean): Boolean =
            MiuiUtil.setMiUIStatusBarDarkMode(window, lightMode)

        override fun layoutStatusBarSpace(window: Window) =
            mDelegate.layoutStatusBarSpace(window)

        override fun setStatusBarColor(window: Window, color: Int) =
            mDelegate.setStatusBarColor(window, color)

        override fun getStatusBarHeight(context: Context): Int =
            mDelegate.getStatusBarHeight(context)
    }

    private class MStatusBarCompat : LollipopStatusBarCompat() {

        override fun isStatusBarHide(window: Window): Boolean =
            window.decorView.systemUiVisibility and (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_FULLSCREEN) != 0

        override fun setStatusBarLightMode(window: Window, lightMode: Boolean): Boolean {
            var decorView = window.decorView
            if (decorView != null) {
                if (lightMode) {
                    if ((View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR and decorView.systemUiVisibility) != 0) {
                        // 当前为亮色模式
                        return true
                    }
                    decorView.systemUiVisibility = decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    if (View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR and decorView.systemUiVisibility == 0) {
                        // 当前不是亮色模式
                        return true
                    }
                    decorView.systemUiVisibility = decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                }
                return true
            }
            return false
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun layoutStatusBarSpace(window: Window) {
            var decorView = window.decorView
            if (decorView != null) {
                setStatusBarColor(window, 0)
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                decorView.systemUiVisibility = decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }
        }
    }

    /**
     * 是否占用状态栏区域
     */
    fun isStatusBarHide(window: Window): Boolean =
            IMPL.isStatusBarHide(window)

    /**
     * 扩展显示区域到状态栏
     */
    fun layoutStatusSpace(window: Window) =
        IMPL.layoutStatusBarSpace(window)

    /**
     * 设置状态栏模式
     */
    fun setStatusBarLightMode(window: Window, lightMode: Boolean): Boolean =
            IMPL.setStatusBarLightMode(window, lightMode)

    /**
     * 设置状态栏颜色
     */
    fun setStatusBarColor(window: Window, color: Int) =
            IMPL.setStatusBarColor(window, color)

    /**
     * 返回状态栏高度
     */
    fun getStatusBarHeight(context: Context): Int =
            IMPL.getStatusBarHeight(context)

    /**
     * 适配各类rom支持theme
     */
    fun adapterStatusBarTheme(window: Window) {
        if (IMPL is MiuiStatusBarCompat) {
            var decorView = window.decorView
            if ((decorView?.systemUiVisibility?.and(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)) != 0) {
                IMPL.setStatusBarLightMode(window, true)
            } else {
                IMPL.setStatusBarLightMode(window, false)
            }
        }
    }

    /**
     * 适配6.0以上miui
     */
    fun setMiuiSupport(window: Window) {
        if (IMPL is MiuiStatusBarCompat) {
            val decorView = window.decorView
            if (decorView != null && decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR != 0) {
                //theme light mode
                IMPL.setStatusBarLightMode(window, true)
            }
        }
    }
}