package com.antimage.basic.ui.view

import androidx.annotation.StringRes
import io.reactivex.ObservableTransformer

interface ComposeLoading {

    fun <T> bindLoading(msg: String): ObservableTransformer<T, T>

    fun <T> bindLoading(@StringRes resId: Int): ObservableTransformer<T, T>

    fun <T> bindLoading(): ObservableTransformer<T, T>
}