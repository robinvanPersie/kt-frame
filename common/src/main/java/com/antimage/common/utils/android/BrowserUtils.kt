package com.antimage.common.utils.android

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.util.Log

/**
 * Created by xuyuming on 2019-08-28
 */
object BrowserUtils {

    fun openBrowser(context: Context, url: String) {
        if (TextUtils.isEmpty(url)) {
            Log.e(javaClass.simpleName, "open browser url cannot be empty")
            return
        }
        if (!openDefaultBrowser(context, url)) {
            openOtherBrowser(context, url)
        }
    }

    /**
     * 直接打开浏览器
     * @return
     */
    private fun openDefaultBrowser(context: Context, url: String): Boolean {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity")
        if (intentUseful(context, intent)) {
            context.startActivity(intent)
            return true
        }
        return false
    }

    /**
     * 弹出选择浏览器框
     */
    private fun openOtherBrowser(context: Context, url: String): Boolean {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        if (intentUseful(context, intent)) {
            context.startActivity(intent)
            return true
        }
        return false
    }

    /**
     * intent跳转是否可用
     * @return true: 可用 false: 不可用
     */
    private fun intentUseful(context: Context, intent: Intent): Boolean {
        return intent.resolveActivity(context.packageManager) != null
    }
}