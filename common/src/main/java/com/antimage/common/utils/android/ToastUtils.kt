package com.antimage.common.utils.android

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

object ToastUtils {

    lateinit var context: Context

    fun install(ctx: Context) {
        this.context = ctx
    }

    fun showToast(@StringRes resId: Int) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show()
    }

    fun showToast(text: String): Unit {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    fun showLongToast(@StringRes resId: Int) {
        Toast.makeText(context, resId, Toast.LENGTH_LONG).show()
    }

    fun showLongToast(text: String): Unit {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }
}