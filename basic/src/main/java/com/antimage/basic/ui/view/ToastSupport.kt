package com.antimage.basic.ui.view

import androidx.annotation.StringRes

interface ToastSupport {

    fun showToast(@StringRes resId: Int)

    fun showToast(text: String)

    fun showLongToast(@StringRes resId: Int)

    fun showLongToast(text: String)
}
