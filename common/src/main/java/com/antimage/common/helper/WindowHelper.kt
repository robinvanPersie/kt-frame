package com.antimage.common.helper

import android.os.Build
import android.view.View
import android.view.Window

object WindowHelper {

    /**
     * 隐藏虚拟按键，并且全屏
     */
    fun hideBottomNav(window: Window) {
        if (Build.VERSION.SDK_INT < 19) {
            window.decorView.systemUiVisibility = View.GONE
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION and
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY and
                    View.SYSTEM_UI_FLAG_FULLSCREEN

        }
    }
}