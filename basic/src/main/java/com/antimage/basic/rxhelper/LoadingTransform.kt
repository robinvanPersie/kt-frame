package com.antimage.basic.rxhelper

import androidx.annotation.StringRes
import com.antimage.basic.ui.view.ILoading
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer

class LoadingTransform<T> constructor(mV: ILoading, msg: String) : ObservableTransformer<T, T> {

    private val mView = mV
    private val message = msg
    private var resId: Int = 0

    constructor(mV: ILoading, @StringRes resId: Int) : this(mV, "") {
        this.resId = resId
    }

    override fun apply(upstream: Observable<T>): ObservableSource<T> {
        return upstream.apply {
            doOnSubscribe{
                if (resId == 0)
                    mView.showLoading(message)
                else
                    mView.showLoading(resId)
            }.doFinally {
                mView.hideLoading()
            }
        }
    }
}