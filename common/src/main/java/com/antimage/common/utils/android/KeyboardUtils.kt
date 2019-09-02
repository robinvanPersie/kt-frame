package com.antimage.common.utils.android

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import java.util.*

/**
 * Created by xuyuming on 2019-08-28
 */
object KeyboardUtils {

    //隐藏虚拟键盘
    fun hideKeyboard(v: View) {
        val imm = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (imm.isActive) {
            imm.hideSoftInputFromWindow(v.applicationWindowToken, 0)
        }
    }

    fun hideKeyboard(activity: Activity, flags: Int) {
        try {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (imm.isActive) {
                imm.hideSoftInputFromWindow(activity.window.decorView.windowToken, flags)
            }
        } catch (e: Exception) {
            Log.e(javaClass.simpleName, e.toString())
        }

    }

    //显示虚拟键盘
    fun showKeyboard(v: View, flags: Int) {
        try {
            val imm = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            imm.showSoftInput(v, flags)
        } catch (e: Exception) {
            Log.e(javaClass.simpleName, e.toString())
        }
    }

    //强制显示或者关闭系统键盘
    fun keyBoard(txtSearchKey: EditText, status: String) {
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                val m = txtSearchKey.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                if (status == "open") {
                    m.showSoftInput(txtSearchKey, InputMethodManager.SHOW_FORCED)
                } else {
                    m.hideSoftInputFromWindow(txtSearchKey.windowToken, 0)
                }
            }
        }, 300)
    }

    //通过定时器强制隐藏虚拟键盘
    fun timerHideKeyboard(v: View) {
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                val imm = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                if (imm.isActive) {
                    imm.hideSoftInputFromWindow(v.applicationWindowToken, 0)
                }
            }
        }, 10)
    }
}