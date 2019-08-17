package com.antimage.basic.ui.view

import androidx.annotation.StringRes

interface ILoading {

    fun showLoading()

    fun showLoading(text: String)

    fun showLoading(@StringRes resId: Int)

    fun hideLoading()
}