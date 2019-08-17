package com.antimage.basic.presenter.base

import android.content.Context
import com.antimage.basic.rxhelper.LoadingTransform
import com.antimage.basic.ui.view.ComposeLoading
import com.antimage.basic.ui.view.IBaseView
import com.antimage.basic.ui.view.ILoading
import com.trello.rxlifecycle2.LifecycleProvider
import com.trello.rxlifecycle2.LifecycleTransformer
import com.trello.rxlifecycle2.RxLifecycle
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.subjects.BehaviorSubject

open class BasePresenter<E, V> : LifecycleProvider<E>, ComposeLoading {

    protected var context: Context? = null
    var mV: V? = null
    private var lifecycleProvider: LifecycleProvider<E>? = null
    private var isAttach: Boolean = false

    open fun onCreate(context: Context, lifecycleProvider: LifecycleProvider<E>) {
        this.context = context
        this.lifecycleProvider = lifecycleProvider
    }

    open fun onAttachView(v: V) {
        mV = v
        isAttach = true
    }

    open fun onDetachViw() {
        isAttach = false
        mV = null
    }

    open fun onDestroy() {
        lifecycleProvider = null
        context = null
    }

//  ================ override ================

    override fun <T> bindLoading(msg: String): ObservableTransformer<T, T> {
        if (mV is ILoading) {
            return LoadingTransform(mV as ILoading, msg)
        }
        return ObservableTransformer { it }
    }

    override fun <T> bindLoading(resId: Int): ObservableTransformer<T, T> {
        if (mV is ILoading) {
            return LoadingTransform(mV as ILoading, resId)
        }
        return ObservableTransformer { it }
    }

    override fun <T> bindLoading(): ObservableTransformer<T, T> {
        return this.bindLoading("loading...")
    }

    override fun lifecycle(): Observable<E> {
        return if (lifecycleProvider == null) BehaviorSubject.create() else lifecycleProvider!!.lifecycle()
    }

    override fun <T> bindUntilEvent(event: E): LifecycleTransformer<T> {
        return lifecycleProvider?.bindUntilEvent(event)
            ?: RxLifecycle.bind(BehaviorSubject.create<T>())
    }

    override fun <T> bindToLifecycle(): LifecycleTransformer<T> {
        return lifecycleProvider?.bindToLifecycle()
            ?: RxLifecycle.bind(BehaviorSubject.create<T>())
    }
}